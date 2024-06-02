package ar.edu.itba.pod.tpe2.client.query1;

import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.client.utils.HazelcastConfigurator;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParser;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParserFactory;
import ar.edu.itba.pod.tpe2.models.Ticket;
import ar.edu.itba.pod.tpe2.query1.Q1Collator;
import ar.edu.itba.pod.tpe2.query1.Q1Mapper;
import ar.edu.itba.pod.tpe2.query1.Q1ReducerFactory;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
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
import java.util.stream.Collectors;

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

        JobTracker jobTracker = hazelcastInstance.getJobTracker("g7-jobtracker");

        List<Ticket> tickets;
        try {
            tickets = parseTickets(arguments.getInPath().resolve("ticketsNYC.csv"));
        } catch (IOException e) {
            logger.error("Error reading input file", e);
            return;
        }

        MultiMap<String, Ticket> ticketMultiMap = hazelcastInstance.getMultiMap("g5-tickets");
        for (Ticket ticket : tickets) {
            ticketMultiMap.put(ticket.getInfractionCode(), ticket);
        }
        Job<String, Ticket> job = jobTracker.newJob(KeyValueSource.fromMultiMap(ticketMultiMap));
        ICompletableFuture<Map<String, Long>> future = job
                .mapper(new Q1Mapper())
                .reducer(new Q1ReducerFactory())
                .submit(new Q1Collator());

        try {
            Map<String, Long> result = future.get();
            result.forEach((infraction, count) -> {
                System.out.println(infraction + ";" + count);
            });
        } catch (Exception e) {
            logger.error("Error executing job", e);
        }

        HazelcastClient.shutdownAll();
    }



}
