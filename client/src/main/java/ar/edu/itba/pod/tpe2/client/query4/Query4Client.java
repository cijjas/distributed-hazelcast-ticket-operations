package ar.edu.itba.pod.tpe2.client.query4;

import ar.edu.itba.pod.tpe2.client.utils.HazelcastConfig;
import ar.edu.itba.pod.tpe2.client.utils.QueryConfig;
import ar.edu.itba.pod.tpe2.client.utils.TimestampLogger;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParser;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParserFactory;
import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import ar.edu.itba.pod.tpe2.models.ticket.Ticket;
import ar.edu.itba.pod.tpe2.query4.*;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.*;

public class Query4Client {

    private static final String QUERY_NAME = "query4";
    private static final String QUERY_RESULT_HEADER = "County;Plate;Tickets";
    private static final String CNP = "g7-"; // Cluster Name Prefix

    public static void main(String[] args) {

        QueryParser parser = QueryParserFactory.getParser(QUERY_NAME);

        Query4Arguments arguments;
        try {
            arguments = (Query4Arguments) parser.getArguments(args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return;
        }
        QueryConfig queryConfig = new QueryConfig(QUERY_NAME + ".csv", "time4.txt");

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
            IList<Ticket> ticketList = hazelcastInstance.getList(CNP + "ticketList");
            ticketList.clear();
            parseTickets(arguments.getInPath(), city, ticketList, ticket -> isWithinRange(ticket.getIssueDate(), arguments));

            timeLog.logEndReading();

            JobTracker jobTracker = hazelcastInstance.getJobTracker(CNP + QUERY_NAME + "jobTracker");
            KeyValueSource<String, Ticket> source = KeyValueSource.fromList(ticketList);

            Job<String, Ticket> job = jobTracker.newJob(source);
            timeLog.logStartMapReduce();
            Map<String, Map.Entry<String, Integer>> result = job
                    .mapper(new Query4Mapper())
                    .combiner(new Query4CombinerFactory())
                    .reducer(new Query4ReducerFactory())
                    .submit(new Query4Collator())
                    .get();
            timeLog.logEndMapReduce();

            List<String> outputLines = result
                    .entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + ";" + entry.getValue().getKey() + ';' + entry.getValue().getValue())
                    .toList();

            writeQueryResults(arguments.getOutPath(), queryConfig.getQueryOutputFile(), QUERY_RESULT_HEADER, outputLines);
            timeLog.writeTimestamps();
            ticketList.clear();
        } catch (IOException e) {
            System.out.println("Error reading CSV files or processing MapReduce job");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {

            HazelcastClient.shutdownAll();
        }
    }

    public static boolean isWithinRange(LocalDate date, Query4Arguments arguments) {
        return date.isAfter(arguments.getFrom()) && date.isBefore(arguments.getTo());
    }

}
