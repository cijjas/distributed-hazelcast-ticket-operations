package ar.edu.itba.pod.tpe2.client.utils;

import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastConfigurator {

    public static HazelcastInstance configureHazelcastClient(BaseArguments arguments) {
        ClientConfig clientConfig = new ClientConfig();

        GroupConfig groupConfig = new GroupConfig()
                .setName(arguments.getClusterName())
                .setPassword(arguments.getClusterPass());

        clientConfig.setGroupConfig(groupConfig);

        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();

        clientNetworkConfig.addAddress(arguments.getAddresses().split(";"));

        clientConfig.setNetworkConfig(clientNetworkConfig);
        return HazelcastClient.newHazelcastClient(clientConfig);
    }
}
