package ar.edu.itba.pod.tpe2.client.query5;

import ar.edu.itba.pod.tpe2.client.utils.QueryConfig;
import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.client.utils.HazelcastConfig;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParser;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParserFactory;
import ar.edu.itba.pod.tpe2.client.utils.TimestampLogger;
import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import ar.edu.itba.pod.tpe2.query5.*;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.*;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.*;

@Slf4j
public class Query5Client {
    private static final String QUERY_NAME = "query5";
    private static final String QUERY_RESULT_HEADER = "Group;Infraction A;Infraction B";
    private static final String CNP = "g7-"; // Cluster Name Prefix
    private static final String TIME_OUTPUT_FILE = "time5.txt";
    private static final String QUERY_OUTPUT_FILE = "query5.csv";

    public static void main(String[] args) {

        QueryParser parser = QueryParserFactory.getParser(QUERY_NAME);

        BaseArguments arguments;
        try {
            arguments = parser.getArguments(args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return;
        }

        QueryConfig queryConfig = new QueryConfig(QUERY_OUTPUT_FILE, TIME_OUTPUT_FILE);
        City city = arguments.getCity();

        // Hazelcast client Config
        HazelcastInstance hazelcastInstance = HazelcastConfig.configureHazelcastClient(arguments);
        TimestampLogger timeLog = new TimestampLogger(arguments.getOutPath(), queryConfig.getTimeOutputFile());

        try {
            timeLog.logStartReading();
            // Parse infractions
            Map<String, Infraction> infractions = new ConcurrentHashMap<>();
            parseInfractions(arguments.getInPath(), city, infractions);

            // Parse tickets
            IList<Ticket> ticketList = hazelcastInstance.getList(CNP + QUERY_NAME + "ticketList");
            ticketList.clear();
            parseTickets(arguments.getInPath(), city, ticketList, ticket -> hasInfraction(ticket, infractions));

            timeLog.logEndReading();

            JobTracker jobTracker = hazelcastInstance.getJobTracker(CNP + QUERY_NAME + "jobTracker");
            KeyValueSource<String, Ticket> source = KeyValueSource.fromList(ticketList);

            // First Job: Calculate Total Amount, Count, and Average Fine for Each Infraction
            Job<String, Ticket> job1 = jobTracker.newJob(source);
            Map<String, String> job1Result = job1
                    .mapper(new Query5aMapper())
                    .combiner(new Query5aCombinerFactory())
                    .reducer(new Query5aReducerFactory())
                    .submit()
                    .get();

            // Prepare the input for the second job
            IMap<String, String> averageFineMap = hazelcastInstance.getMap(CNP + QUERY_NAME + "averageFineMap");
            averageFineMap.clear();
            averageFineMap.putAll(job1Result);

            // Second Job: Group Infractions by Hundreds Range and Form Pairs
            KeyValueSource<String, String> source2 = KeyValueSource.fromMap(averageFineMap);
            Job<String, String> job2 = jobTracker.newJob(source2);
            Map<Integer, List<String>> result = job2
                    .mapper(new Query5bMapper())
                    .reducer(new Query5bReducerFactory())
                    .submit(new Query5bCollator(infractions))
                    .get();

            timeLog.logEndMapReduce();

            // Generate output lines
            List<String> outputLines = result.entrySet().stream()
                    .flatMap(entry -> entry.getValue().stream())
                    .collect(Collectors.toList());

            writeQueryResults(arguments.getOutPath(), queryConfig.getQueryOutputFile(), QUERY_RESULT_HEADER, outputLines);
            timeLog.writeTimestamps();
            ticketList.clear();
            averageFineMap.clear();
        } catch (IOException e) {
            System.out.println("Error reading CSV files or processing MapReduce job");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            HazelcastClient.shutdownAll();
        }
    }

    public static boolean hasInfraction(Ticket ticket, Map<String, Infraction> infractions) {
        return infractions.containsKey(ticket.getInfractionCode());
    }
}
