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

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.parseInfractions;
import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.parseTickets;

public class Q1Client {

    private static final Logger logger = LoggerFactory.getLogger(Q1Client.class);

    public static void main(String[] args) {
        logger.info("hz-config Client Starting ...");

        QueryParser parser = QueryParserFactory.getParser("q1");



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


            logger.info("Finished reading and storing CSV files");

            IList<Ticket> ticketList = hazelcastInstance.getList("g7-ticketList");
            ticketList.addAll(tickets);

            JobTracker jobTracker = hazelcastInstance.getJobTracker("g7-jobTracker");
            KeyValueSource<String, Ticket> source = KeyValueSource.fromList(ticketList);

            Job<String, Ticket> job = jobTracker.newJob(source);
            Map<String, Integer> result = job.mapper(new Q1Mapper())
                    .combiner(new Q1CombinerFactory())
                    .reducer(new Q1ReducerFactory())
                    .submit(new Q1Collator())
                    .get();

            // Procesar los resultados y guardarlos en un archivo de salida
            List<String> output = result.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                            .thenComparing(Map.Entry::getKey))
                    .map(entry -> entry.getKey() + ";" + entry.getValue())
                    .collect(Collectors.toList());

            Files.write(Paths.get(String.valueOf(arguments.getOutPath()), "query1.csv"), output, StandardCharsets.UTF_8);


        } catch (IOException  e) {
            logger.error("Error processing MapReduce job", e);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            HazelcastClient.shutdownAll();
        }
    }



}
