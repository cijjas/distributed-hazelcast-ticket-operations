package ar.edu.itba.pod.tpe2.client.query2;

import ar.edu.itba.pod.tpe2.client.query1.Query1Client;
import ar.edu.itba.pod.tpe2.client.utils.HazelcastConfig;
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


        try {
            logger.info("Start reading and storing CSV files");

            String city = arguments.getCity();


            // Load infractions from CSV
            Map<String, Infraction> infractions = new HashMap<>();
            parseInfractions(arguments.getInPath(), city, infractions);

            // Load tickets from CSV
            IList<Ticket> ticketList = hazelcastInstance.getList(CNP + "ticketList");
            parseTickets(arguments.getInPath(), city, ticketList, infractions);



            JobTracker jobTracker = hazelcastInstance.getJobTracker(CNP + QUERY_NAME + "jobTracker");
            KeyValueSource<String, Ticket> source = KeyValueSource.fromList(ticketList);

            Job<String, Ticket> job = jobTracker.newJob(source);
            Map<String, List<String>> result = job
                    .mapper(new Query2Mapper())
                    .combiner(new Query2CombinerFactory())
                    .reducer(new Query2ReducerFactory())
                    .submit(new Query2Collator(infractions))
                    .get();

            List<String> output = result.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + ";" + String.join(";", entry.getValue()))
                    .toList();


           // writeQueryResults(arguments.getOutPath(), QUERY_NAME, QUERY_RESULT_HEADER, output);

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
