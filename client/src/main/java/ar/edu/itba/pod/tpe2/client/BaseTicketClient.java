package ar.edu.itba.pod.tpe2.client;

import ar.edu.itba.pod.tpe2.client.utils.HazelcastConfig;
import ar.edu.itba.pod.tpe2.client.utils.QueryConfig;
import ar.edu.itba.pod.tpe2.client.utils.TimestampLogger;
import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParser;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParserFactory;
import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.*;

public abstract class BaseTicketClient<T extends BaseArguments, K extends Map> {
    protected static final String CNP = "g7-"; // Cluster Name Prefix
    protected abstract QueryConfigEnum getQueryConfig();
    protected String getTicketMapName() {
        return CNP + getQueryConfig().getQueryName() + "ticketMap";
    }
    protected String getJobTrackerName() {
        return CNP + getQueryConfig().getQueryName() + "jobTracker";
    }
    protected abstract void parseData(Path inPath, City city, HazelcastInstance hazelcastInstance, IMap<Long, Ticket> ticketMap) throws IOException;
    protected abstract K mapReduce(Job<Long, Ticket> job) throws InterruptedException, ExecutionException;
    protected abstract List<String> generateOutputFromResults(K result);
    protected T arguments;
    protected JobTracker jobTracker;
    protected HazelcastInstance hazelcastInstance;
    protected void run(String[] args) {
        QueryConfigEnum query = getQueryConfig();
        QueryParser parser = QueryParserFactory.getParser(query.getQueryName());

        try {
            arguments = (T) parser.getArguments(args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return;
        }

        QueryConfig queryConfig = new QueryConfig(query.getQueryOutputFile(), query.getTimeOutputFile());
        City city = arguments.getCity();

        // Hazelcast client Config
        hazelcastInstance = HazelcastConfig.configureHazelcastClient(arguments);
        TimestampLogger timeLog = new TimestampLogger(arguments.getOutPath(), queryConfig.getTimeOutputFile());

        try {
            IMap<Long, Ticket> ticketIMap = hazelcastInstance.getMap(getTicketMapName());
            ticketIMap.clear();

            timeLog.logStartReading();
            parseData(arguments.getInPath(), city, hazelcastInstance, ticketIMap);
            timeLog.logEndReading();

            jobTracker = hazelcastInstance.getJobTracker(getJobTrackerName());
            KeyValueSource<Long, Ticket> source = KeyValueSource.fromMap(ticketIMap);
            Job<Long, Ticket> job = jobTracker.newJob(source);

            timeLog.logStartMapReduce();
            K result = mapReduce(job);
            timeLog.logEndMapReduce();

            List<String> outputLines = generateOutputFromResults(result);
            writeQueryResults(arguments.getOutPath(), queryConfig.getQueryOutputFile(), query.getResultHeader(), outputLines);

            timeLog.writeTimestamps();
        } catch (IOException e) {
            System.out.println("Error reading CSV files or processing MapReduce job");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            IMap<Long, Ticket> ticketIMap = hazelcastInstance.getMap(getTicketMapName());
            ticketIMap.clear();
            HazelcastClient.shutdownAll();
        }
    }
}
