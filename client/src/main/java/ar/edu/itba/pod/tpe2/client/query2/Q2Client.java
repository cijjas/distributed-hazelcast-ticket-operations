package ar.edu.itba.pod.tpe2.client.query2;

import ar.edu.itba.pod.tpe2.client.query1.Q1Client;
import ar.edu.itba.pod.tpe2.client.utils.HazelcastConfigurator;
import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParser;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParserFactory;
import ar.edu.itba.pod.tpe2.models.Infraction;
import ar.edu.itba.pod.tpe2.models.Ticket;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.*;

public class Q2Client {

    private static final Logger logger = LoggerFactory.getLogger(Q1Client.class);

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
        HazelcastInstance hazelcastInstance = HazelcastConfigurator.configureHazelcastClient(arguments);


        try {
            logger.info("Start reading and storing CSV files");

            String city = arguments.getCity();

            Map<String, Infraction> infractions = parseInfractions(arguments.getInPath(), city)
                    .stream()
                    .collect(Collectors.toMap(Infraction::getCode, infraction -> infraction));
            List<Ticket> tickets = parseTickets(arguments.getInPath(), city)
                    .stream()
                    .filter(ticket -> infractions.containsKey(ticket.getInfractionCode()))
                    .toList();

            logger.info("Finished reading and storing CSV files");

            IList<Ticket> ticketList = hazelcastInstance.getList(CNP +"ticketList");
            ticketList.addAll(tickets);

            JobTracker jobTracker = hazelcastInstance.getJobTracker(CNP + "jobQ2Tracker");
            KeyValueSource<String, Ticket> source = KeyValueSource.fromList(ticketList);

            Job<String, Ticket> job = jobTracker.newJob(source);
            Map<String, List<String>> result = job
                    .mapper(new Q2Mapper())
                    .combiner(new Q2CombinerFactory())
                    .reducer(new Q2ReducerFactory())
                    .submit(new Q2Collator(infractions))
                    .get();

            List<String> output = result.entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + ";" + String.join(";", entry.getValue()))
                    .toList();

            writeQueryResults(arguments.getOutPath(), QUERY_NAME, QUERY_RESULT_HEADER, output);

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
