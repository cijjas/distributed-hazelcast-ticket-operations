package ar.edu.itba.pod.tpe2.client.utils;

import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import ar.edu.itba.pod.tpe2.models.ticket.Ticket;
import ar.edu.itba.pod.tpe2.models.ticket.TicketAdapterFactory;
import com.hazelcast.core.IList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class CSVUtils {
    private static final String INFRACTIONS = "infractions";
    private static final String TICKETS = "tickets";
    private static final String CSV_FORMAT = ".csv";
    private static final String SEPARATOR = ";";

    public static List<Infraction> parseInfractions(Path filePath, String city, Map<String, Infraction> infractions) throws IOException {
        Path realPath = filePath.resolve(INFRACTIONS + city + CSV_FORMAT);
        try (Stream<String> lines = Files.lines(realPath)) {
            return lines.skip(1)
                    .map(line -> line.split(SEPARATOR))
                    .map(fields -> new Infraction(fields[0], fields[1]))
                    .peek(infraction -> infractions.put(infraction.getCode(), infraction))
                    .toList();
        }
    }

    public static void parseTickets(Path filePath, String city, IList<Ticket> ticketList, Map<String, Infraction> infractions) throws IOException, IllegalArgumentException {
        Path realPath = filePath.resolve(TICKETS + city + CSV_FORMAT);
        try (BufferedReader br = Files.newBufferedReader(realPath)) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(SEPARATOR);
                Ticket ticket = TicketAdapterFactory.createAdapter(city, fields);
                if (infractions.containsKey(ticket.getInfractionCode())) {
                    ticketList.add(ticket);
                }
            }
        }
    }

    public static void writeQueryResults(Path outPath, String queryOutFile, String CSVHeader,  List<String> outputLines) throws IOException {
        Path realPath = outPath.resolve(queryOutFile);
        try (BufferedWriter writer = Files.newBufferedWriter(realPath, StandardCharsets.UTF_8)) {
            writer.write(CSVHeader);
            writer.newLine();
            for (String line : outputLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }


}
