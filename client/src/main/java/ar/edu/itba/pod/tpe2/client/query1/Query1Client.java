package ar.edu.itba.pod.tpe2.client.query1;

import ar.edu.itba.pod.tpe2.client.BaseTicketClient;
import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import ar.edu.itba.pod.tpe2.query1.Query1Collator;
import ar.edu.itba.pod.tpe2.query1.Query1CombinerFactory;
import ar.edu.itba.pod.tpe2.query1.Query1Mapper;
import ar.edu.itba.pod.tpe2.query1.Query1ReducerFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.parseInfractions;
import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.parseTicketsToMap;

@Getter
public class Query1Client extends BaseTicketClient<BaseArguments> {
    private final Map<String, Infraction> infractions;
    protected final String queryName = "query1";
    protected final String timeOutputFile = "time1.txt";
    protected final String queryResultHeader = "Infraction;Tickets";

    public Query1Client() {
        this.infractions = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        new Query1Client().run(args);
    }

    @Override
    protected void parseData(Path inPath, City city, IMap<Long, Ticket> ticketMap) throws IOException {
        parseInfractions(inPath, city, infractions);
        parseTicketsToMap(inPath, city, ticketMap, ticket -> hasInfraction(ticket, infractions));
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
