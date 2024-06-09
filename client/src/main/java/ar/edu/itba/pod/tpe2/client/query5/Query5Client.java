package ar.edu.itba.pod.tpe2.client.query5;

import ar.edu.itba.pod.tpe2.client.BaseTicketClient;
import ar.edu.itba.pod.tpe2.client.QueryConfigEnum;
import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import ar.edu.itba.pod.tpe2.query5.*;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.KeyValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.parseInfractions;
import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.parseTicketsToMap;

public class Query5Client extends BaseTicketClient<BaseArguments, Map<Integer, List<String>>> {
    private final Map<String, Infraction> infractions;

    public Query5Client() {
        this.infractions = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        new Query5Client().run(args);
    }

    @Override
    protected QueryConfigEnum getQueryConfig() {
        return QueryConfigEnum.QUERY5;
    }

    @Override
    protected void parseData(Path inPath, City city, HazelcastInstance hazelcastInstance, IMap<Long, Ticket> ticketMap) throws IOException {
        parseInfractions(arguments.getInPath(), city, infractions);
        parseTicketsToMap(inPath, city, hazelcastInstance, ticketMap, ticket -> hasInfraction(ticket, infractions));
    }

    @Override
    protected Map<Integer, List<String>> mapReduce(Job<Long, Ticket> job) throws InterruptedException, ExecutionException {
        Map<String, String> job1Result = job
                .mapper(new Query5aMapper())
                .combiner(new Query5aCombinerFactory())
                .reducer(new Query5aReducerFactory())
                .submit()
                .get();

        // Prepare the input for the second job
        IMap<String, String> averageFineMap = hazelcastInstance.getMap(getAverageFineMapName());
        averageFineMap.clear();
        averageFineMap.putAll(job1Result);

        // Second Job: Group Infractions by Hundreds Range and Form Pairs
        KeyValueSource<String, String> source2 = KeyValueSource.fromMap(averageFineMap);
        Job<String, String> job2 = jobTracker.newJob(source2);
        return job2
                .mapper(new Query5bMapper())
                .reducer(new Query5bReducerFactory())
                .submit(new Query5bCollator(infractions))
                .get();

    }

    @Override
    protected List<String> generateOutputFromResults(Map<Integer, List<String>> result) {
        return result.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
    }

    private String getAverageFineMapName() {
        return CNP + getQueryConfig().getQueryName() + "averageFineMap";
    }

    public static boolean hasInfraction(Ticket ticket, Map<String, Infraction> infractions) {
        return infractions.containsKey(ticket.getInfractionCode());
    }
}
