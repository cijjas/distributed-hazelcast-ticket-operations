package ar.edu.itba.pod.tpe2.client.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public interface QueryParser {
    Options getOptions();
    void parse(CommandLine cmd) throws ParseException;
    Object getArguments();

}
