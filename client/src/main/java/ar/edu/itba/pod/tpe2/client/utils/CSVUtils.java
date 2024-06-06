package ar.edu.itba.pod.tpe2.client.utils;

import ar.edu.itba.pod.tpe2.models.City;
import ar.edu.itba.pod.tpe2.models.infraction.Infraction;
import ar.edu.itba.pod.tpe2.models.ticket.Ticket;
import ar.edu.itba.pod.tpe2.models.ticket.TicketAdapterFactory;
import com.hazelcast.core.IList;
import com.hazelcast.core.MultiMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVUtils {
    private static final String INFRACTIONS = "infractions";
    private static final String TICKETS = "tickets";
    private static final String CSV_FORMAT = ".csv";
    private static final String SEPARATOR = ";";
    private static final Integer BATCH_SIZE = 1000;

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
//
//    public static void parseTicketsToMultiMapStream(Path filePath, String city, MultiMap<String, Ticket> ticketMultiMap, Map<String, Infraction> infractions) throws IOException {
//        Path realPath = filePath.resolve(TICKETS + city + CSV_FORMAT);
//        TicketAdapter ticketAdapter = TicketAdapterFactory.getAdapter(city);
//
//        try (Stream<String> lines = Files.lines(realPath)) {
//            lines.skip(1) // Skip header line
//                    .parallel() // Process lines in parallel
//                    .forEach(line -> {
//                        String[] fields = line.split(";");
//                        Ticket ticket = ticketAdapter.createTicket(fields);
//
//                        if (ticket != null) {
//                            ticketMultiMap.put(ticket.getPlate(), ticket);
//                        }
//                    });
//        }
//    }

//
//    public static void parseTicketsToMultiMapBatch(Path filePath, String city,
//                                                   MultiMap<String, Ticket> ticketMultiMap, Map<String, Infraction> infractions) throws IOException {
//        Path realPath = filePath.resolve(TICKETS + city + CSV_FORMAT);
//        TicketAdapter ticketAdapter = TicketAdapterFactory.getAdapter(city);
//
//        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//
//        try (Stream<String> lines = Files.lines(realPath).skip(1)) { // Skip header line
//            List<String> batch = new ArrayList<>(BATCH_SIZE);
//            for (String line : (Iterable<String>) lines::iterator) {
//                batch.add(line);
//                if (batch.size() == BATCH_SIZE) {
//                    List<String> batchCopy = new ArrayList<>(batch);
//                    executorService.submit(() -> processBatch(batchCopy, ticketAdapter, ticketMultiMap));
//                    batch.clear();
//                }
//            }
//            if (!batch.isEmpty()) {
//                List<String> batchCopy = new ArrayList<>(batch);
//                executorService.submit(() -> processBatch(batchCopy, ticketAdapter, ticketMultiMap));
//            }
//        } finally {
//            executorService.shutdown();
//            try {
//                if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
//                    executorService.shutdownNow();
//                }
//            } catch (InterruptedException e) {
//                executorService.shutdownNow();
//                Thread.currentThread().interrupt();
//            }
//        }
//    }

    public static void parseTicketsToList(Path filePath, City city, IList<Ticket> ticketList, Map<String, Infraction> infractions) throws IOException {
        Path realPath = filePath.resolve(TICKETS + city + CSV_FORMAT);
        Ticket ticketAdapter = TicketAdapterFactory.getAdapter(city);

        try (BufferedReader br = Files.newBufferedReader(realPath)) {
            String line;
            br.readLine(); // Saltar encabezado
            List<Ticket> batch = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(SEPARATOR);
                Ticket ticket = ticketAdapter.createTicket(fields);
                    batch.add(ticket);
                    if (batch.size() == 1000) {  // Ajustar tamaño del lote según sea necesario
                        ticketList.addAll(batch);
                        batch.clear();
                    }
            }
            if (!batch.isEmpty()) {
                ticketList.addAll(batch);
            }
        }
    }

//
//    public static void parseTicketsToNormalList(Path filePath, String city, List<Ticket> ticketList, Map<String, Infraction> infractions) throws IOException {
//        Path realPath = filePath.resolve(TICKETS + city + CSV_FORMAT);
//        TicketAdapter ticketAdapter = TicketAdapterFactory.getAdapter(city);
//
//        try (BufferedReader br = Files.newBufferedReader(realPath)) {
//            String line;
//            br.readLine();
//            List<Ticket> batch = new ArrayList<>();
//            while ((line = br.readLine()) != null) {
//                String[] fields = line.split(SEPARATOR);
//                Ticket ticket = ticketAdapter.createTicket(fields);
//                if(infractions.containsKey(ticket.getInfractionCode())){
//                    batch.add(ticket);
//                    if (batch.size() == 1000) {  // Ajustar tamaño del lote según sea necesario
//                        ticketList.addAll(batch);
//                        batch.clear();
//                    }
//                }
//
//            }
//            if (!batch.isEmpty()) {
//                ticketList.addAll(batch);
//            }
//        }
//    }

//
//    private static void processBatch(List<String> batch, TicketAdapter ticketAdapter,
//                                     MultiMap<String, Ticket> ticketMultiMap) {
//        batch.parallelStream().forEach(line -> {
//            String[] fields = line.split(";");
//            Ticket ticket = ticketAdapter.createTicket(fields);
//            if (ticket != null) {
//                ticketMultiMap.put(ticket.getPlate(), ticket);
//            }
//        });
//    }


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
