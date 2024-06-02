package ar.edu.itba.pod.tpe2.client.utils;

import ar.edu.itba.pod.tpe2.models.Ticket;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVUtils {

    // Mapa de InfractionCode a Linea
    public static Map<String, String> parseInfractions(String filePath) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines
                    .skip(1)
                    .collect(Collectors.toMap(
                            line -> line.split(";")[2],
                            line -> line,
                            (existing, replacement) -> existing
                    ));
        }
    }

    public static List<Ticket> parseTickets(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            return lines.skip(1)
                    .map(line -> {
                        String[] parts = line.split(";");
                        return new Ticket(
                                parts[0],
                                LocalDate.parse(parts[1], DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                parts[2],
                                Double.parseDouble(parts[3]),
                                parts[4],
                                parts[5]
                        );
                    }).toList();
        }
    }


}
