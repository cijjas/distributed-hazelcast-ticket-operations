package ar.edu.itba.pod.tpe2.server.utils.parsing;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.Collection;

public class ServerParser {
    private int port;
    private String clusterName;
    private String clusterPassword;
    private Collection<String> interfaces;
    private static final String DEFAULT_CLUSTER_NAME = "g7";
    private static final String DEFAULT_CLUSTER_PASSWORD = "g7-pass";
    private static final String DEFAULT_INTERFACE = "192.168.0.*";
    private static final String DEFAULT_PORT = "5701";

    private Options getOptions() {
        Options options = new Options();
        options.addOption("Dname", "Dname", true, "Hazelcast cluster name");
        options.addOption("Dpass", "Dpass", true, "Hazelcast cluster password");
        options.addOption("Dinterfaces",  "Dinterfaces", true, "Addresses of Hazelcast nodes");
        options.addOption("Dport", "Dport", true, "Hazelcast cluster port");
        return options;
    }

    private void parse(CommandLine cmd) throws ParseException {
        this.clusterName = cmd.getOptionValue("Dname", DEFAULT_CLUSTER_NAME);
        this.clusterPassword = cmd.getOptionValue("Dpass", DEFAULT_CLUSTER_PASSWORD);
        this.port = validatePort(cmd.getOptionValue("Dport", DEFAULT_PORT));

        String interfaceOption = cmd.getOptionValue("Dinterfaces", DEFAULT_INTERFACE);
        this.interfaces = validateAndGetInterfaces(interfaceOption);
    }

   private int validatePort(String port) throws ParseException {
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid port: " + port);
        }
    }

    public ServerArguments getArguments(String[] args) throws ParseException{
        Options options = this.getOptions();
        CommandLineParser cliParser = new DefaultParser();
        CommandLine cmd = cliParser.parse(options, args);

        parse(cmd);
        return new ServerArguments(clusterName, clusterPassword, interfaces, port);
    }


    private Collection<String> validateAndGetInterfaces(String interfaceOption) throws ParseException {
        String[] interfaces = interfaceOption.split(";");
        String pattern = "^(localhost|eth0|eth1|(\\d{1,3}|\\*)\\.(\\d{1,3}|\\*)\\.(\\d{1,3}|\\*)\\.(\\d{1,3}|\\*)(/\\d{1,2})?)$";

        for (String iface : interfaces) {
            if (!iface.matches(pattern)) {
                throw new ParseException("Invalid interface: " + iface);
            }
        }
        return Arrays.asList(interfaces);
    }
}
