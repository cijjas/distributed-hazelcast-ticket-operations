package ar.edu.itba.pod.tpe2.client.query4;

import ar.edu.itba.pod.tpe2.client.utils.BaseParser;
import ar.edu.itba.pod.tpe2.client.utils.QueryParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Q4Parser extends BaseParser {
    private Q4Arguments arguments;


    @Override
    protected void addCustomOptions(Options options) {
        options.addRequiredOption("Dfrom", "Dfrom", true, "From date");
        options.addRequiredOption("Dto", "Dto", true, "To date");
    }

    @Override
    protected void parseCustomArguments(CommandLine cmd) throws ParseException {
        String from = cmd.getOptionValue("Dfrom");
        String to = cmd.getOptionValue("Dto");

        LocalDate fromDate = validateAndParseDate(from, "Dfrom");
        LocalDate toDate = validateAndParseDate(to, "Dto");

        arguments = new Q4Arguments(super.getArguments().getAddresses(), super.getArguments().getCity(), super.getArguments().getInPath(), super.getArguments().getOutPath(), fromDate, toDate);
    }
    private LocalDate validateAndParseDate(String dateStr, String dateType) throws ParseException {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new ParseException(dateType + " must not be empty");
        }
        if (!dateStr.matches("^\\d{2}/\\d{2}/\\d{2}$")) {
            throw new ParseException("Invalid date format for " + dateType + ". Expected format is DD/MM/YY");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new ParseException("Invalid date for " + dateType + ". Expected format is DD/MM/YY");
        }
    }
    @Override
    public Q4Arguments getArguments() {
        return arguments;
    }


}
