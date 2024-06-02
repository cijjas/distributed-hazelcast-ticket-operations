package ar.edu.itba.pod.tpe2.client.query1;

import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.client.utils.HazelcastConfigurator;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParser;
import ar.edu.itba.pod.tpe2.client.utils.parsing.QueryParserFactory;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.*;

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


//        try {
//
//
//        } finally {
//            // Shutdown
//            HazelcastClient.shutdownAll();
//        }
    }



}
