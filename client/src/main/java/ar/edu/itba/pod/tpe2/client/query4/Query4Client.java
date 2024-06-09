package ar.edu.itba.pod.tpe2.client.query4;

import ar.edu.itba.pod.tpe2.client.BaseTicketClient;
import ar.edu.itba.pod.tpe2.client.QueryConfigEnum;
import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import ar.edu.itba.pod.tpe2.query4.Query4Collator;
import ar.edu.itba.pod.tpe2.query4.Query4CombinerFactory;
import ar.edu.itba.pod.tpe2.query4.Query4Mapper;
import ar.edu.itba.pod.tpe2.query4.Query4ReducerFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.parseTicketsToMap;

public class Query4Client extends BaseTicketClient<Query4Arguments, Map<String, Map.Entry<String, Integer>>> {

    public static void main(String[] args) {
        new Query4Client().run(args);
    }

    @Override
    protected QueryConfigEnum getQueryConfig() {
        return QueryConfigEnum.QUERY4;
    }

    @Override
    protected void parseData(Path inPath, City city, HazelcastInstance hazelcastInstance, IMap<Long, Ticket> ticketMap) throws IOException {
        parseTicketsToMap(inPath, city, hazelcastInstance, ticketMap, ticket -> isWithinRange(ticket.getIssueDate(), arguments));
    }

    @Override
    protected Map<String, Map.Entry<String, Integer>> mapReduce(Job<Long, Ticket> job) throws InterruptedException, ExecutionException {
        return job
                .mapper(new Query4Mapper())
                .combiner(new Query4CombinerFactory())
                .reducer(new Query4ReducerFactory())
                .submit(new Query4Collator())
                .get();
    }

    @Override
    protected List<String> generateOutputFromResults(Map<String, Map.Entry<String, Integer>> result) {
        return result
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ";" + entry.getValue().getKey() + ';' + entry.getValue().getValue())
                .toList();
    }

    public static boolean isWithinRange(LocalDate date, Query4Arguments arguments) {
        return date.isAfter(arguments.getFrom()) && date.isBefore(arguments.getTo());
    }
}
