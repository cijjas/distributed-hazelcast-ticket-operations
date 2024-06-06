package ar.edu.itba.pod.tpe2.client.utils;

import ar.edu.itba.pod.tpe2.client.utils.parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.models.ticket.TicketFactory;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastConfig {

    public static HazelcastInstance configureHazelcastClient(BaseArguments arguments) {
        ClientConfig clientConfig = new ClientConfig();

        GroupConfig groupConfig = new GroupConfig()
                .setName(arguments.getClusterName())
                .setPassword(arguments.getClusterPass());

        clientConfig.setGroupConfig(groupConfig);

        ClientNetworkConfig clientNetworkConfig = new ClientNetworkConfig();

        clientNetworkConfig.addAddress(arguments.getAddresses().split(";"));

        clientConfig.setNetworkConfig(clientNetworkConfig);

        // Nitro hazelast
        SerializationConfig serializationConfig = clientConfig.getSerializationConfig();
        System.out.println("City: " + arguments.getCity().ordinal());
        serializationConfig.addDataSerializableFactory(arguments.getCity().ordinal(), new TicketFactory());

        clientConfig.getSerializationConfig().setAllowUnsafe(true);
        clientConfig.setProperty("hazelcast.client.max.concurrent.invocations", "1000");
        //clientConfig.setProperty("hazelcast.client.invocation.timeout.seconds", "120");

        return HazelcastClient.newHazelcastClient(clientConfig);
    }


}
