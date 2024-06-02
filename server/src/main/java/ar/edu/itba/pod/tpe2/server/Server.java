package ar.edu.itba.pod.tpe2.server;

import ar.edu.itba.pod.tpe2.server.utils.parsing.ServerArguments;
import ar.edu.itba.pod.tpe2.server.utils.parsing.ServerParser;
import com.hazelcast.config.*;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;

import com.hazelcast.core.Hazelcast;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);


    public static void main(String[] args) {
        logger.info("hz-config Server Starting ...");

        ServerParser parser = new ServerParser();
        ServerArguments arguments;

        try{
             arguments = parser.getArguments(args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Config
        Config config = getHazelcastConfig(arguments);

        // Start cluster
        Hazelcast.newHazelcastInstance(config);
    }


    private static Config getHazelcastConfig(ServerArguments arguments) {
        // Group Config
        Config config = new Config();
        GroupConfig groupConfig = new GroupConfig()
                .setName(arguments.getClusterName())
                .setPassword(arguments.getClusterPassword());

        config.setGroupConfig(groupConfig);

        // Network Config
        MulticastConfig multicastConfig = new MulticastConfig();

        JoinConfig joinConfig = new JoinConfig().setMulticastConfig(multicastConfig);

        InterfacesConfig interfacesConfig = new InterfacesConfig()
                .setInterfaces(arguments.getInterfaces())
                .setEnabled(true);

        NetworkConfig networkConfig = new NetworkConfig()
                .setInterfaces(interfacesConfig)
                .setJoin(joinConfig);

        config.setNetworkConfig(networkConfig);

        // Management Center Config
        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig()
                .setUrl("http://localhost:8080/mancenter-3.8.5/")
                .setEnabled(true);
        config.setManagementCenterConfig(managementCenterConfig);

        return config;
    }

}
