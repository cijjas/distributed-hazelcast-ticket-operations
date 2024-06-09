package ar.edu.itba.pod.tpe2.client.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
@Slf4j
public class TimestampLogger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSSS");

    private final StringBuilder logBuilder = new StringBuilder();
    private final Path outFile;
    private final Object callingClass;

    public TimestampLogger(Path outPath, String queryOutFile, Object callingClass) {
        this.outFile = outPath.resolve(queryOutFile);
        this.callingClass  = callingClass;
    }

    public void logStartReading() {
        LocalDateTime startReading = LocalDateTime.now();
        logEvent("Inicio de la lectura del archivo", startReading);
    }

    public void logEndReading() {
        LocalDateTime endReading = LocalDateTime.now();
        logEvent("Fin de lectura del archivo", endReading);
    }

    public void logStartMapReduce() {
        LocalDateTime startMapReduce = LocalDateTime.now();
        logEvent("Inicio del trabajo map/reduce", startMapReduce);
    }

    public void logEndMapReduce() {
        LocalDateTime endMapReduce = LocalDateTime.now();
        logEvent("Fin del trabajo map/reduce", endMapReduce);
    }

    private void logEvent(String message, LocalDateTime timestamp) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement logCaller = stackTrace[3];
        String methodName = logCaller.getMethodName();
        int lineNumber = logCaller.getLineNumber();

        String logMessage = String.format("%s INFO [%s] %s (%s:%d) - %s",
                timestamp.format(formatter), Thread.currentThread().getName(), callingClass, methodName, lineNumber, message);

        log.info(logMessage);
        logBuilder.append(logMessage).append("\n");
    }

    public void writeTimestamps() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.toString()))) {
            writer.write(logBuilder.toString());
        } catch (IOException e) {
            log.error("Error writing time log", e);
        }
    }
}
