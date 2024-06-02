package ar.edu.itba.pod.tpe2.client.utils.parsing;


import lombok.Getter;
import org.apache.commons.cli.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public abstract class BaseParser implements QueryParser {
    private BaseArguments arguments;

    @Override
    public Options getOptions() {
        Options options = new Options();
        options.addRequiredOption("Daddresses","Daddresses",  true, "Addresses of Hazelcast nodes");
        options.addRequiredOption("DinPath", "DinPath",true, "Input file path");
        options.addRequiredOption("DoutPath","DoutPath", true, "Output file path");
        options.addRequiredOption("Dcity","Dcity", true, "City to process");
        addCustomOptions(options);

        return options;
    }
    protected abstract void addCustomOptions(Options options);

    @Override
    public void parse(CommandLine cmd) throws ParseException {
        String addresses = cmd.getOptionValue("Daddresses");
        String city = cmd.getOptionValue("Dcity");
        String inPathStr = cmd.getOptionValue("DinPath");
        String outPathStr = cmd.getOptionValue("DoutPath");


        validateAddresses(addresses);
        validateCity(city);

        Path inPath = validateAndConvertPath(inPathStr, "Input");
        Path outPath = validateAndConvertPath(outPathStr, "Output");


        arguments = new BaseArguments(addresses, city, inPath, outPath);
        parseCustomArguments(cmd);
    }

    protected abstract void parseCustomArguments(CommandLine cmd) throws ParseException;


    private void validateAddresses(String addresses) throws ParseException {
        if (addresses == null || addresses.isEmpty()) {
            throw new ParseException("Addresses must not be empty");
        }
        String[] addressArray = addresses.split(";");
        for (String address : addressArray) {
            if (!address.matches("^([0-9]{1,3}\\.){3}[0-9]{1,3}:[0-9]+$")) {
                throw new ParseException("Invalid address format: " + address);
            }
        }
    }

    private void validateCity(String city) throws ParseException {
        if (city == null || city.isEmpty()) {
            throw new ParseException("City must not be empty");
        }
        if (!city.equals("NYC") && !city.equals("CHI")) {
            throw new ParseException("Invalid city. Only 'NYC' and 'CHI' are allowed");
        }
    }

    private Path validateAndConvertPath(String pathStr, String pathType) throws ParseException {
        if (pathStr == null || pathStr.isEmpty()) {
            throw new ParseException(pathType + " path must not be empty");
        }

        Path path = Paths.get(pathStr);
        if (!Files.exists(path)) {
            throw new ParseException(pathType + " directory path does not exist: " + pathStr);
        }
        if (!Files.isDirectory(path)) {
            throw new ParseException(pathType + " path is not a directory: " + pathStr);
        }
        return path;
    }


    @Override
    public BaseArguments getArguments(String[] args) throws ParseException{
        Options options = this.getOptions();
        CommandLineParser cliParser = new DefaultParser();
        CommandLine cmd = cliParser.parse(options, args);
        parse(cmd);
        return new BaseArguments(arguments.getAddresses(), arguments.getCity(), arguments.getInPath(), arguments.getOutPath());
    }

}

