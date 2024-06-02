package ar.edu.itba.pod.tpe2.client.utils.parsing;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public interface QueryParser {
    Options getOptions();
    void parse(CommandLine cmd) throws ParseException;
    BaseArguments getArguments(String[] args) throws ParseException;
}
