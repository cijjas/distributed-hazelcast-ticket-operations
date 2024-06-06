package ar.edu.itba.pod.tpe2.client.query1;

import ar.edu.itba.pod.tpe2.client.utils.QueryConfig;
import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.client.utils.HazelcastConfig;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParser;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParserFactory;
import ar.edu.itba.pod.tpe2.client.utils.TimestampLogger;
import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import ar.edu.itba.pod.tpe2.models.ticket.Ticket;
import ar.edu.itba.pod.tpe2.query1.Query1Collator;
import ar.edu.itba.pod.tpe2.query1.Query1CombinerFactory;
import ar.edu.itba.pod.tpe2.query1.Query1Mapper;
import ar.edu.itba.pod.tpe2.query1.Query1ReducerFactory;
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

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.*;

@Slf4j
public class Query1Client {


    private static final String QUERY_NAME = "query1";
    private static final String QUERY_RESULT_HEADER = "Infraction;Tickets";
    private static final String CNP = "g7-"; // Cluster Name Prefix

    public static void main(String[] args) {

        QueryParser parser = QueryParserFactory.getParser(QUERY_NAME);

        BaseArguments arguments;
        try{
            arguments = parser.getArguments(args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return;
        }
        QueryConfig queryConfig = new QueryConfig(QUERY_NAME + ".csv", "time1.txt");

        // Hazelcast client Config
        HazelcastInstance hazelcastInstance = HazelcastConfig.configureHazelcastClient(arguments);

        TimestampLogger timeLog = new TimestampLogger(arguments.getOutPath(), queryConfig.getTimeOutputFile());

        try {

            String city = arguments.getCity();

            // Load infractions from CSV
            timeLog.logStartReading();

            Map<String, Infraction> infractions = new ConcurrentHashMap<>();
            parseInfractions(arguments.getInPath(), city, infractions);

            // Load tickets from CSV
            MultiMap<String, Ticket> ticketMultiMap = hazelcastInstance.getMultiMap(CNP + QUERY_NAME + "tickets");
            MultiMap<String, Ticket> ticketMultiMap2 = hazelcastInstance.getMultiMap(CNP + QUERY_NAME + "tickets2");
            IList<Ticket> ticketList = hazelcastInstance.getList(CNP + QUERY_NAME + "ticketList");
            ticketMultiMap2.clear();
            ticketMultiMap.clear();
            ticketList.clear();


            long startTimeList = System.currentTimeMillis();
            parseTicketsToList(arguments.getInPath(), city,ticketList, infractions);
            long endTimeList = System.currentTimeMillis();
            System.out.println("List processing took: " + (endTimeList - startTimeList ) + " ms");


            long startTimeBatch = System.currentTimeMillis();
            parseTicketsToMultiMapBatch(arguments.getInPath(), city,ticketMultiMap2, infractions);
            long endTimeBatch = System.currentTimeMillis();
            System.out.println("Batch processing took: " + (endTimeBatch - startTimeBatch) + " ms");

            long startTimeStream = System.currentTimeMillis();
            parseTicketsToMultiMapStream(arguments.getInPath(), city,ticketMultiMap, infractions);
            long endTimeStream = System.currentTimeMillis();
            System.out.println("Stream.parallel() took: " + (endTimeStream - startTimeStream) + " ms");


            timeLog.logEndReading();

            JobTracker jobTracker = hazelcastInstance.getJobTracker(CNP + QUERY_NAME +"jobTracker");
            KeyValueSource<String, Ticket> source = KeyValueSource.fromMultiMap(ticketMultiMap);

            Job<String, Ticket> job = jobTracker.newJob(source);
            timeLog.logStartMapReduce();
            Map<String, Integer> result = job
                    .mapper(new Query1Mapper())
                    .combiner(new Query1CombinerFactory())
                    .reducer(new Query1ReducerFactory())
                    .submit(new Query1Collator(infractions)) // TODO MIRAR infractions
                    .get();
            timeLog.logEndMapReduce();

            List<String> outputLines = result
                    .entrySet()
                    .stream()
                    .map(entry -> infractions.get(entry.getKey()).getDescription() + ";" + entry.getValue())
                    .toList();

            writeQueryResults(arguments.getOutPath(), queryConfig.getQueryOutputFile(), QUERY_RESULT_HEADER, outputLines);
            timeLog.writeTimestamps();
        } catch (IOException  e) {
            System.out.println("Error reading CSV files or processing MapReduce job");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            MultiMap<String, Ticket> ticketMultiMap = hazelcastInstance.getMultiMap(CNP + QUERY_NAME + "tickets");
            ticketMultiMap.clear();
            HazelcastClient.shutdownAll();
        }
    }


}
