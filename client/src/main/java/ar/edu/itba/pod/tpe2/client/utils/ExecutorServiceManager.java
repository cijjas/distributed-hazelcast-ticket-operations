package ar.edu.itba.pod.tpe2.client.utils;

import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class ExecutorServiceManager implements AutoCloseable {
    private final ExecutorService executorService;

    public ExecutorServiceManager(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void close() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
