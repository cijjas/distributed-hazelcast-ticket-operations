package ar.edu.itba.pod.tpe2.client.query3;


import ar.edu.itba.pod.tpe2.client.utils.cli_parsing.BaseArguments;
import ar.edu.itba.pod.tpe2.client.utils.cli_parsing.BaseParser;
import lombok.Getter;
import org.apache.commons.cli.*;

@Getter
public class Query3Parser extends BaseParser {
    private Query3Arguments arguments;

    @Override
    public Options getOptions() {
        Options options = super.getOptions();
        addCustomOptions(options);
        return options;
    }

    private void addCustomOptions(Options options) {
        options.addRequiredOption("Dn", "Dn", true, "Number of top agencies");
    }

    @Override
    public void parse(CommandLine cmd) throws ParseException {
        super.parse(cmd);
        parseCustomArguments(cmd);
    }

    private void parseCustomArguments(CommandLine cmd) throws ParseException {
        String nValue = cmd.getOptionValue("Dn");
        int n;
        try {
            n = Integer.parseInt(nValue);
        } catch (NumberFormatException e) {
            throw new ParseException("The value of Dn must be a valid integer: " + nValue);
        }
        BaseArguments baseArgs = super.getArguments();
        arguments = new Query3Arguments(baseArgs.getAddresses(), baseArgs.getCity(), baseArgs.getInPath(), baseArgs.getOutPath(), baseArgs.getClusterName(), baseArgs.getClusterPass(), n);
    }

    @Override
    public BaseArguments getArguments(String[] args) throws ParseException {
        CommandLineParser cliParser = new DefaultParser();
        Options options = getOptions();
        CommandLine cmd = cliParser.parse(options, args);
        parse(cmd);
        return arguments;
    }

}
