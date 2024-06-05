package ar.edu.itba.pod.tpe2.client.query2;

import ar.edu.itba.pod.tpe2.client.query1.Query1Client;
import ar.edu.itba.pod.tpe2.client.utils.HazelcastConfig;
import ar.edu.itba.pod.tpe2.client.utils.QueryConfig;
import ar.edu.itba.pod.tpe2.client.utils.TimestampLogger;
import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParser;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParserFactory;
import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import ar.edu.itba.pod.tpe2.models.ticket.Ticket;
import ar.edu.itba.pod.tpe2.query2.*;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.*;

public class Query2Client {

    private static final Logger logger = LoggerFactory.getLogger(Query1Client.class);

    private static final String QUERY_NAME = "query2";
    private static final String QUERY_RESULT_HEADER = "County;InfractionTop1;InfractionTop2;InfractionTop3";
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

        // Hazelcast client Config
        HazelcastInstance hazelcastInstance = HazelcastConfig.configureHazelcastClient(arguments);

        QueryConfig queryConfig = new QueryConfig(QUERY_NAME + ".csv", "time2.txt");

        TimestampLogger timeLog = new TimestampLogger(arguments.getOutPath(), queryConfig.getTimeOutputFile());

        try {

            String city = arguments.getCity();
            timeLog.logStartReading();


            // Load infractions from CSV
            Map<String, Infraction> infractions = new ConcurrentHashMap<>();
            parseInfractions(arguments.getInPath(), city, infractions);

            // Load tickets from CSV
            IList<Ticket> ticketList = hazelcastInstance.getList(CNP + "ticketList");
            ticketList.clear();
            parseTickets(arguments.getInPath(), city, ticketList, infractions);

            timeLog.logEndReading();


            JobTracker jobTracker = hazelcastInstance.getJobTracker(CNP + QUERY_NAME + "jobTracker");
            KeyValueSource<String, Ticket> source = KeyValueSource.fromList(ticketList);

            Job<String, Ticket> job = jobTracker.newJob(source);
            timeLog.logStartMapReduce();

            Map<String, List<String>> result = job
                    .mapper(new Query2Mapper())
                    .combiner(new Query2CombinerFactory())
                    .reducer(new Query2ReducerFactory())
                    .submit(new Query2Collator(infractions))
                    .get();
            timeLog.logEndMapReduce();

            List<String> output = result.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + ";" + String.join(";", entry.getValue()))
                    .toList();


            writeQueryResults(arguments.getOutPath(),  queryConfig.getQueryOutputFile(), QUERY_RESULT_HEADER, output);
            timeLog.writeTimestamps();

        } catch (IOException e) {
            logger.error("Error processing MapReduce job", e);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            IList<Ticket> ticketList = hazelcastInstance.getList(CNP + "ticketList");
            ticketList.clear();
            HazelcastClient.shutdownAll();
        }
    }
}
