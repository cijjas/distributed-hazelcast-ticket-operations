package ar.edu.itba.pod.tpe2.client.query1;

import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.client.utils.HazelcastConfigurator;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParser;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParserFactory;
import ar.edu.itba.pod.tpe2.models.Infraction;
import ar.edu.itba.pod.tpe2.models.Ticket;
import ar.edu.itba.pod.tpe2.query1.Q1Collator;
import ar.edu.itba.pod.tpe2.query1.Q1CombinerFactory;
import ar.edu.itba.pod.tpe2.query1.Q1Mapper;
import ar.edu.itba.pod.tpe2.query1.Q1ReducerFactory;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.*;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.*;

public class Q1Client {

    private static final Logger logger = LoggerFactory.getLogger(Q1Client.class);

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
                    .toList(); // list de multas que tienen infracciones del archivo de infracciones

            System.out.println("Infractions: " + infractions);
            logger.info("Finished reading and storing CSV files");

            IList<Ticket> ticketList = hazelcastInstance.getList(CNP +"ticketList");
            ticketList.addAll(tickets);

            JobTracker jobTracker = hazelcastInstance.getJobTracker(CNP + "jobTracker");
            KeyValueSource<String, Ticket> source = KeyValueSource.fromList(ticketList);

            Job<String, Ticket> job = jobTracker.newJob(source);
            Map<String, Integer> result = job
                    .mapper(new Q1Mapper())
                    .combiner(new Q1CombinerFactory())
                    .reducer(new Q1ReducerFactory())
                    .submit(new Q1Collator())
                    .get();

            List<String> output = result.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                            .thenComparing(entry -> infractions.get(entry.getKey()).getDescription()))
                    .map(entry -> infractions.get(entry.getKey()).getDescription() + ";" + entry.getValue())
                    .toList();


            writeQueryResults(arguments.getOutPath(), QUERY_NAME, QUERY_RESULT_HEADER, output);

        } catch (IOException  e) {
            logger.error("Error processing MapReduce job", e);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            HazelcastClient.shutdownAll();
        }
    }



}
