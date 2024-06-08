package ar.edu.itba.pod.tpe2.client.utils;

import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import ar.edu.itba.pod.tpe2.models.ticket.adapters.Ticket;
import ar.edu.itba.pod.tpe2.models.ticket.services.TicketAdapterFactory;
import com.hazelcast.core.*;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CSVUtils {
    private static final String INFRACTIONS = "infractions";
    private static final String TICKETS = "tickets";
    private static final String CSV_FORMAT = ".csv";
    private static final String SEPARATOR = ";";
    private static final Integer BATCH_SIZE = 50000;

    public static void parseInfractions(Path filePath, City city, Map<String, Infraction> infractions) throws IOException {
        Path realPath = filePath.resolve(INFRACTIONS + city.name() + CSV_FORMAT);
        try (Stream<String> lines = Files.lines(realPath)) {
            lines.skip(1)
                    .parallel()  // Turbo
                    .map(line -> line.split(SEPARATOR))
                    .map(fields -> new Infraction(fields[0], fields[1]))
                    .forEach(infraction -> infractions.put(infraction.getCode(), infraction));
        }
    }

    public static void parseTicketsToMap(Path filePath, City city, IMap<Long, Ticket> ticketMap, Predicate<Ticket> shouldAddToBatch) throws IOException {
        Path realPath = filePath.resolve("tickets" + city + ".csv");
        Ticket ticketAdapter = TicketAdapterFactory.getAdapter(city);
        int batchSize = BATCH_SIZE;
        long id = 0;
        Map<Long, Ticket> batchMap = new ConcurrentHashMap<>(batchSize);
        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        settings.getFormat().setDelimiter(';');
        CsvParser parser = new CsvParser(settings);

        try (BufferedReader reader = new BufferedReader(new FileReader(realPath.toFile()), 8192)) {
            parser.beginParsing(reader);

            com.univocity.parsers.common.record.Record record;
            while ((record = parser.parseNextRecord()) != null) {
                Ticket ticket = ticketAdapter.createTicket(record.getValues());
                if (!shouldAddToBatch.test(ticket)) {
                    continue;
                }
                id++;
                batchMap.put(id, ticket);
                if (batchMap.size() >= batchSize) {
                    ticketMap.putAll(batchMap);
                    batchMap.clear();
                }
            }

            if (!batchMap.isEmpty()) {
                ticketMap.putAll(batchMap);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }


        public static void parseTickets(Path filePath, City city, IList<Ticket> ticketList, Predicate<Ticket> shouldAddToBatch) throws IOException {
        Path realPath = filePath.resolve(TICKETS + city + CSV_FORMAT);
        Ticket ticketAdapter = TicketAdapterFactory.getAdapter(city);

        try (BufferedReader br = Files.newBufferedReader(realPath)) {
            String line;
            br.readLine(); // Saltar encabezado
            List<Ticket> batch = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(SEPARATOR);
                Ticket ticket = ticketAdapter.createTicket(fields);
                if (shouldAddToBatch.test(ticket)) {
                    batch.add(ticket);
                    if (batch.size() == BATCH_SIZE) {  // Ajustar tamaño del lote según sea necesario
                        ticketList.addAll(batch);
                        batch.clear();
                    }
                }
            }
            if (!batch.isEmpty()) {
                ticketList.addAll(batch);
            }
        }
    }



    public static void writeQueryResults(Path outPath, String queryOutFile, String CSVHeader, List<String> outputLines) throws IOException {
        Path realPath = outPath.resolve(queryOutFile);
        try (BufferedWriter writer = Files.newBufferedWriter(realPath, StandardCharsets.UTF_8)) {
            writer.write(CSVHeader);
            writer.newLine();
            outputLines.forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }


}
