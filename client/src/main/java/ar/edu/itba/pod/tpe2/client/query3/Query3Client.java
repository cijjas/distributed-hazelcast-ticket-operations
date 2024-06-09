package ar.edu.itba.pod.tpe2.client.query3;

import ar.edu.itba.pod.tpe2.client.BaseTicketClient;
import ar.edu.itba.pod.tpe2.client.utils.QueryConfigEnum;
import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import ar.edu.itba.pod.tpe2.query3.Query3Collator;
import ar.edu.itba.pod.tpe2.query3.Query3CombinerFactory;
import ar.edu.itba.pod.tpe2.query3.Query3Mapper;
import ar.edu.itba.pod.tpe2.query3.Query3ReducerFactory;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.parseTicketsToMap;

public class Query3Client extends BaseTicketClient<Query3Arguments, Map<String, String>> {

    public static void main(String[] args) {
        new Query3Client().run(args);
    }

    @Override
    protected QueryConfigEnum getQueryConfig() {
        return QueryConfigEnum.QUERY3;
    }

    @Override
    protected void parseData(Path inPath, City city, IMap<Long, Ticket> ticketMap) throws IOException {
        parseTicketsToMap(inPath, city, ticketMap, ticket -> true);
    }

    @Override
    protected Map<String, String> mapReduce(Job<Long, Ticket> job) throws InterruptedException, ExecutionException {
        return job
                .mapper(new Query3Mapper())
                .combiner(new Query3CombinerFactory())
                .reducer(new Query3ReducerFactory())
                .submit(new Query3Collator(arguments.getN()))
                .get();
    }

    @Override
    protected List<String> generateOutputFromResults(Map<String, String> result) {
        return result
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ";" + entry.getValue())
                .toList();
    }
}
