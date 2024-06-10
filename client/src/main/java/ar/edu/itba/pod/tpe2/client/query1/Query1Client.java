package ar.edu.itba.pod.tpe2.client.query1;

import ar.edu.itba.pod.tpe2.client.BaseTicketClient;
import ar.edu.itba.pod.tpe2.client.utils.QueryConfigEnum;
import ar.edu.itba.pod.tpe2.client.utils.cli_parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import ar.edu.itba.pod.tpe2.query1.Query1Collator;
import ar.edu.itba.pod.tpe2.query1.Query1CombinerFactory;
import ar.edu.itba.pod.tpe2.query1.Query1Mapper;
import ar.edu.itba.pod.tpe2.query1.Query1ReducerFactory;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.parseInfractions;
import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.parseTicketsToMap;

public class Query1Client extends BaseTicketClient<BaseArguments, Map<String, Integer>> {
    private final Map<String, Infraction> infractions;

    public Query1Client() {
        this.infractions = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        new Query1Client().run(args);
    }

    @Override
    protected QueryConfigEnum getQueryConfig() {
        return QueryConfigEnum.QUERY1;
    }

    @Override
    protected void parseData(Path inPath, City city, IMap<Long, Ticket> ticketMap) throws IOException {
        parseInfractions(inPath, city, infractions);
        parseTicketsToMap(inPath, city, ticketMap, ticket -> hasInfraction(ticket, infractions), 20000);

        /*long start, end;
        start = System.currentTimeMillis();
        parseInfractions(inPath, city, infractions);
        end = System.currentTimeMillis();
        System.out.println("Infractions parsed in " + (end - start) + " ms");
        int[] batchSizes = generateSequentialNumbers(20, 10000, 10);
        for (int batchSize : batchSizes) {
            IMap<Long, Ticket> ticketIMap = hazelcastInstance.getMap("g7-ticketMap");
            ticketIMap.clear();
            start = System.currentTimeMillis();
            parseTicketsToMap(inPath, city, ticketIMap, ticket -> hasInfraction(ticket, infractions), batchSize);
            end = System.currentTimeMillis();
            System.out.println("Batch size: " + batchSize + " Time: " + (end - start) + " ms");
            ticketIMap.destroy();
        }*/
    }
    public static int[] generateSequentialNumbers(int size, int min, int max) {
        int[] numbers = new int[size];
        int increment = (max - min) / (size - 1);
        for (int i = 0; i < size; i++) {
            numbers[i] = min + (i * increment);
        }
        return numbers;
    }

    @Override
    protected Map<String, Integer> mapReduce(Job<Long, Ticket> job) throws InterruptedException, ExecutionException {
        return job
                .mapper(new Query1Mapper())
                .combiner(new Query1CombinerFactory())
                .reducer(new Query1ReducerFactory())
                .submit(new Query1Collator(infractions))
                .get();
    }

    @Override
    protected List<String> generateOutputFromResults(Map<String, Integer> result) {
        return result
                .entrySet()
                .stream()
                .map(entry -> infractions.get(entry.getKey()).getDescription() + ";" + entry.getValue())
                .toList();
    }

    private static boolean hasInfraction(Ticket ticket, Map<String, Infraction> infractions) {
        return infractions.containsKey(ticket.getInfractionCode());
    }
}
