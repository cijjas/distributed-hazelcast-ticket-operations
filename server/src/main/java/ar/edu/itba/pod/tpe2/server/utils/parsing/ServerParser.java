package ar.edu.itba.pod.tpe2.server.utils.parsing;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.Collection;

public class ServerParser {

    private String clusterName;
    private String clusterPassword;
    private Collection<String> interfaces;
    private static final String DEFAULT_CLUSTER_NAME = "g7";
    private static final String DEFAULT_CLUSTER_PASSWORD = "g7-pass";
    private static final String DEFAULT_INTERFACES = "127.0.0.*";

    private Options getOptions() {
        Options options = new Options();
        options.addOption("Dname", "Dname", true, "Hazelcast cluster name");
        options.addOption("Dpass", "Dpass", true, "Hazelcast cluster password");
        options.addOption("Dinterfaces",  "Dinterfaces", true, "Addresses of Hazelcast nodes");
        return options;
    }

    private void parse(CommandLine cmd) throws ParseException {
        this.clusterName = cmd.getOptionValue("Dname", DEFAULT_CLUSTER_NAME);
        this.clusterPassword = cmd.getOptionValue("Dpass", DEFAULT_CLUSTER_PASSWORD);


        String interfaceOption = cmd.getOptionValue("Dinterfaces", DEFAULT_INTERFACES);
        this.interfaces = validateAndGetInterfaces(interfaceOption);
    }

    public ServerArguments getArguments(String[] args) throws ParseException{
        Options options = this.getOptions();
        CommandLineParser cliParser = new DefaultParser();
        CommandLine cmd = cliParser.parse(options, args);
        parse(cmd);
        return new ServerArguments(clusterName, clusterPassword, interfaces);
    }


    private Collection<String> validateAndGetInterfaces(String interfaceOption) throws ParseException {
        String[] interfaces = interfaceOption.split(";");
        String pattern = "^(localhost|(\\d{1,3}|\\*)\\.(\\d{1,3}|\\*)\\.(\\d{1,3}|\\*)\\.(\\d{1,3}|\\*)(/\\d{1,2})?)$";

        for (String iface : interfaces) {
            if (!iface.matches(pattern)) {
                throw new ParseException("Invalid interface: " + iface);
            }
        }
        return Arrays.asList(interfaces);
    }
}
