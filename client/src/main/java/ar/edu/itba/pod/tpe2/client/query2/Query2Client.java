package ar.edu.itba.pod.tpe2.client.query2;

import ar.edu.itba.pod.tpe2.client.BaseTicketClient;
import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import ar.edu.itba.pod.tpe2.query2.Query2Collator;
import ar.edu.itba.pod.tpe2.query2.Query2CombinerFactory;
import ar.edu.itba.pod.tpe2.query2.Query2Mapper;
import ar.edu.itba.pod.tpe2.query2.Query2ReducerFactory;
import com.hazelcast.core.HazelcastInstance;
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

public class Query2Client extends BaseTicketClient<BaseArguments, List<String>> {
    private final Map<String, Infraction> infractions;

    public Query2Client() {
        this.infractions = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        new Query2Client().run(args);
    }

    @Override
    protected String getQueryName() {
        return "query2";
    }

    @Override
    protected String getTimeOutputFile() {
        return "time2.txt";
    }

    @Override
    protected String getQueryResultHeader() {
        return "County;InfractionTop1;InfractionTop2;InfractionTop3";
    }

    @Override
    protected void parseData(Path inPath, City city, HazelcastInstance hazelcastInstance, IMap<Long, Ticket> ticketMap) throws IOException {
        parseInfractions(inPath, city, infractions);
        parseTicketsToMap(inPath, city, hazelcastInstance, ticketMap, ticket -> hasInfraction(ticket, infractions));
    }

    @Override
    protected Map<String, List<String>> mapReduce(Job<Long, Ticket> job) throws InterruptedException, ExecutionException {
        return job
                .mapper(new Query2Mapper())
                .combiner(new Query2CombinerFactory())
                .reducer(new Query2ReducerFactory())
                .submit(new Query2Collator(infractions))
                .get();
    }

    @Override
    protected List<String> generateOutputFromResults(Map<String, List<String>> result) {
        return result
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ";" + String.join(";", entry.getValue()))
                .toList();
    }

    private static boolean hasInfraction(Ticket ticket, Map<String, Infraction> infractions) {
        return infractions.containsKey(ticket.getInfractionCode());
    }
}
