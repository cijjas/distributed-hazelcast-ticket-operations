package ar.edu.itba.pod.tpe2.client.utils;

import ar.edu.itba.pod.tpe2.models.Infraction;
import ar.edu.itba.pod.tpe2.models.Ticket;
import ar.edu.itba.pod.tpe2.models.TicketAdapterFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVUtils {
    private static final String INFRACTIONS = "infractions";
    private static final String TICKETS = "tickets";
    private static final String CSV_FORMAT = ".csv";
    private static final String SEPARATOR = ";";

    public static List<Infraction> parseInfractions(Path filePath, String city) throws IOException {
        Path realPath = filePath.resolve(INFRACTIONS + city + CSV_FORMAT);
        return Files.readAllLines(realPath).stream()
                .skip(1)
                .map(line -> line.split(SEPARATOR))
                .map(parts ->new Infraction(parts[0], parts[1]))
                .toList();
    }

    public static List<Ticket> parseTickets(Path filePath, String city) throws IOException, IllegalArgumentException {
        Path realPath = filePath.resolve(TICKETS  + city + CSV_FORMAT);
        return Files.readAllLines(realPath).stream()
                .skip(1)
                .map(line -> line.split(SEPARATOR))
                .map(fields -> TicketAdapterFactory.createAdapter(city, fields))
                .toList();
    }

    public static void writeQueryResults(Path outPath, String queryName, String CSVHeader,  List<String> outputLines) throws IOException {
        Path realPath = outPath.resolve(queryName + CSV_FORMAT);
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
