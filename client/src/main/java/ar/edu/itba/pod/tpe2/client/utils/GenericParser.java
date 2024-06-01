package ar.edu.itba.pod.tpe2.client.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class GenericParser extends BaseParser {

    @Override
    protected void parseCustomArguments(CommandLine cmd) throws ParseException {
        // No custom arguments to parse
    }

    @Override
    protected void addCustomOptions(Options options) {
        // No custom options to add
    }
}
