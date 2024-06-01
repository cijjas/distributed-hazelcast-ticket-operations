package ar.edu.itba.pod.tpe2.client.query1;

import ar.edu.itba.pod.tpe2.client.utils.BaseArguments;
import ar.edu.itba.pod.tpe2.client.utils.QueryParser;
import ar.edu.itba.pod.tpe2.client.utils.QueryParserFactory;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.*;

public class Q1Client {

    private static final Logger logger = LoggerFactory.getLogger(Q1Client.class);

    public static void main(String[] args) {
        logger.info("hz-config Client Starting ...");

        QueryParser parser = QueryParserFactory.getParser("q1");
        Options options = parser.getOptions();
        CommandLineParser cliParser = new DefaultParser();
        try {
            CommandLine cmd = cliParser.parse(options, args);
            parser.parse(cmd);
        } catch (ParseException e) {
            logger.error("Error parsing arguments", e);
        }
        BaseArguments arguments = (BaseArguments) parser.getArguments();


        // Client Config
        ClientConfig clientConfig = new ClientConfig();

        // Group Config
        GroupConfig groupConfig = new
                GroupConfig().setName("g0").setPassword("g0-pass");
        clientConfig.setGroupConfig(groupConfig);

        // Client Network Config
        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();
        clientNetworkConfig.addAddress(arguments.getAddresses().split(";"));

        clientConfig.setNetworkConfig(clientNetworkConfig);
        HazelcastInstance hazelcastInstance =
                HazelcastClient.newHazelcastClient(clientConfig);
        String mapName = "testMap";
        IMap<Integer, String> testMapFromMember = hazelcastInstance.getMap(mapName);
        testMapFromMember.set(1, "test1");

        IMap<Integer, String> testMap = hazelcastInstance.getMap(mapName);
        System.out.println(testMap.get(1));

        // Shutdown
        HazelcastClient.shutdownAll();
    }
}
