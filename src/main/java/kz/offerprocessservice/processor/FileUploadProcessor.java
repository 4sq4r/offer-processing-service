package kz.offerprocessservice.processor;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class FileUploadProcessor {

    private final ExecutorService es = Executors.newFixedThreadPool(2);

    public void processFileAsync(Runnable task) {
        es.submit(task);
    }

    @PreDestroy
    public void shutdown() {
        es.shutdown();
        try {
            if (!es.awaitTermination(60, TimeUnit.SECONDS)) {
                es.shutdownNow();
            }
        } catch (InterruptedException e) {
            es.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
