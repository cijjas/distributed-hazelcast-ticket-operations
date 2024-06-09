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
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static ar.edu.itba.pod.tpe2.client.utils.CSVUtils.*;

public abstract class BaseTicketClient<T extends BaseArguments> {
    private static final String CNP = "g7-"; // Cluster Name Prefix
    protected abstract String getQueryName();
    protected String getQueryOutputFile() {
        return getQueryName() + ".csv";
    }
    protected String getTicketMapName() {
        return CNP + getQueryName() + "ticketMap";
    }
    protected String getJobTrackerName() {
        return CNP + getQueryName() + "jobTracker";
    }
    protected abstract String getTimeOutputFile();
    protected abstract String getQueryResultHeader();
    protected abstract void parseData(Path inPath, City city, IMap<Long, Ticket> ticketMap) throws IOException;
    protected abstract Map<String, Integer> mapReduce(Job<Long, Ticket> job) throws InterruptedException, ExecutionException;
    protected abstract List<String> generateOutputFromResults(Map<String, Integer> result);
    protected void run(String[] args) {
        QueryParser parser = QueryParserFactory.getParser(getQueryName());

        T arguments;
        try {
            arguments = (T) parser.getArguments(args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return;
        }

        QueryConfig queryConfig = new QueryConfig(getQueryOutputFile(), getTimeOutputFile());
        City city = arguments.getCity();

        // Hazelcast client Config
        HazelcastInstance hazelcastInstance = HazelcastConfig.configureHazelcastClient(arguments);
        TimestampLogger timeLog = new TimestampLogger(arguments.getOutPath(), queryConfig.getTimeOutputFile(), this.getClass());

        try {
            IMap<Long, Ticket> ticketIMap = hazelcastInstance.getMap(getTicketMapName());
            ticketIMap.clear();

            timeLog.logStartReading();
            parseData(arguments.getInPath(), city, ticketIMap);
            timeLog.logEndReading();

            JobTracker jobTracker = hazelcastInstance.getJobTracker(getJobTrackerName());
            KeyValueSource<Long, Ticket> source = KeyValueSource.fromMap(ticketIMap);;
            Job<Long, Ticket> job = jobTracker.newJob(source);

            timeLog.logStartMapReduce();
            Map<String, Integer> result = mapReduce(job);
            timeLog.logEndMapReduce();

            List<String> outputLines = generateOutputFromResults(result);
            writeQueryResults(arguments.getOutPath(), queryConfig.getQueryOutputFile(), getQueryResultHeader(), outputLines);

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
