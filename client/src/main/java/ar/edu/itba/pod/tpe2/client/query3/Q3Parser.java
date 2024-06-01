package ar.edu.itba.pod.tpe2.client.query3;


import ar.edu.itba.pod.tpe2.client.utils.BaseArguments;
import ar.edu.itba.pod.tpe2.client.utils.BaseParser;
import ar.edu.itba.pod.tpe2.client.utils.QueryParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Q3Parser extends BaseParser {
    private Q3Arguments arguments;

    @Override
    protected void addCustomOptions(Options options) {
        options.addRequiredOption("Dn", "Dn", true, "Number of top agencies");
    }

    @Override
    protected void parseCustomArguments(CommandLine cmd) throws ParseException {
        String nValue = cmd.getOptionValue("Dn");
        int n;
        try {
            n = Integer.parseInt(nValue);
        } catch (NumberFormatException e) {
            throw new ParseException("The value of Dn must be a valid integer: " + nValue);
        }
        BaseArguments baseArgs = super.getArguments();
        arguments = new Q3Arguments(baseArgs.getAddresses(), baseArgs.getCity(), baseArgs.getInPath(), baseArgs.getOutPath(), n);
    }


    @Override
    public Q3Arguments getArguments() {
        return arguments;
    }
}
