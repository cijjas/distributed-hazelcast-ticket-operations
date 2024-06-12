package ar.edu.itba.pod.tpe2.server;

import ar.edu.itba.pod.tpe2.server.utils.parsing.ServerArguments;
import ar.edu.itba.pod.tpe2.server.utils.parsing.ServerParser;
import com.hazelcast.config.*;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import com.hazelcast.core.Hazelcast;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);


    public static void main(String[] args) {

        ServerParser parser = new ServerParser();
        ServerArguments arguments;

        try {
            arguments = parser.getArguments(args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return;
        }

        logger.info("hz-config Server Starting ...");

        try {
            Config config = getHazelcastConfig(arguments);
            Hazelcast.newHazelcastInstance(config);
        } catch (Exception e) {
            logger.error("Error starting cluster", e);
        }
    }


    private static Config getHazelcastConfig(ServerArguments arguments) throws SocketException {
        // Group Config
        Config config = new Config();
        GroupConfig groupConfig = new GroupConfig()
                .setName(arguments.getClusterName())
                .setPassword(arguments.getClusterPassword());

        config.setGroupConfig(groupConfig);
        // Network Config
        MulticastConfig multicastConfig = new MulticastConfig();

        JoinConfig joinConfig = new JoinConfig()
                .setMulticastConfig(multicastConfig);

        InterfacesConfig interfacesConfig = new InterfacesConfig()
                .setInterfaces(arguments.getAddresses())
                .setEnabled(true);

        NetworkConfig networkConfig = new NetworkConfig()
                .setPort(arguments.getPort())
                .setInterfaces(interfacesConfig)
                .setJoin(joinConfig);

        config.setNetworkConfig(networkConfig);

        // Management Center Config
//        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig()
//                .setUrl("http://localhost:8080/mancenter-3.8.5/")
//                .setEnabled(true);
//        config.setManagementCenterConfig(managementCenterConfig);

        return config;
    }

}
