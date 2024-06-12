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
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CSVUtils {
    private static final String INFRACTIONS = "infractions";
    private static final String TICKETS = "tickets";
    private static final String CSV_FORMAT = ".csv";
    private static final String SEPARATOR = ";";
    private static final Integer BATCH_SIZE = 35000;

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
        long id = 0;
        Map<Long, Ticket> batchMap = new ConcurrentHashMap<>(BATCH_SIZE);

        CsvParserSettings settings = new CsvParserSettings();
        settings.setHeaderExtractionEnabled(true);
        settings.getFormat().setDelimiter(SEPARATOR);

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try (BufferedReader reader = new BufferedReader(new FileReader(realPath.toFile()))) {
            CsvParser parser = new CsvParser(settings);
            parser.beginParsing(reader);

            String[] row;
            while ((row = parser.parseNext()) != null) {
                Ticket ticket = ticketAdapter.createTicket(row);
                if (!shouldAddToBatch.test(ticket)) {
                    continue;
                }
                batchMap.put(id++, ticket);
                if (batchMap.size() >= BATCH_SIZE) {
                    Map<Long, Ticket> batchToPut = new ConcurrentHashMap<>(batchMap);
                    batchMap.clear();
                    CompletableFuture.runAsync(() -> ticketMap.putAll(batchToPut), executorService).exceptionally(ex -> {
                        System.err.println("Failed to put batch: " + ex.getMessage());
                        return null;
                    });
                }
            }

            if (!batchMap.isEmpty()) {
                CompletableFuture.runAsync(() -> ticketMap.putAll(new ConcurrentHashMap<>(batchMap)), executorService).exceptionally(ex -> {
                    System.err.println("Failed to put remaining batch: " + ex.getMessage());
                    return null;
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file", e);
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException ex) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }


    public static void parseTickets(Path filePath, City city, IList<Ticket> ticketList, Predicate<Ticket> shouldAddToBatch) throws IOException {
        Path realPath = filePath.resolve(TICKETS + city + CSV_FORMAT);
        Ticket ticketAdapter = TicketAdapterFactory.getAdapter(city);

        try (BufferedReader br = Files.newBufferedReader(realPath)) {
            String line;
            br.readLine();
            List<Ticket> batch = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(SEPARATOR);
                Ticket ticket = ticketAdapter.createTicket(fields);
                if (shouldAddToBatch.test(ticket)) {
                    batch.add(ticket);
                    if (batch.size() == BATCH_SIZE) {
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
