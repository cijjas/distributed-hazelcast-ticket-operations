package ar.edu.itba.pod.tpe2.client.utils;

import ar.edu.itba.pod.tpe2.models.Infraction;
import ar.edu.itba.pod.tpe2.models.Ticket;
import ar.edu.itba.pod.tpe2.models.TicketAdapterFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVUtils {

    // Mapa de InfractionCode a Linea
    public static List<Infraction> parseInfractions(Path filePath, String city) throws IOException {
        Path realPath = filePath.resolve("infractions" + city + ".csv");
        return Files.readAllLines(realPath).stream()
                .skip(1)
                .map(line -> line.split(";"))
                .map(parts ->new Infraction(parts[0], parts[1]))
                .toList();
    }

    public static List<Ticket> parseTickets(Path filePath, String city) throws IOException, IllegalArgumentException {
        Path realPath = filePath.resolve("tickets" + city + ".csv");
        return Files.readAllLines(realPath).stream()
                .skip(1)
                .map(line -> line.split(";"))
                .map(fields -> TicketAdapterFactory.createAdapter(city, fields))
                .toList();
    }

}
