package kz.offerprocessservice.strategy.file.templating.impl;

import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.strategy.file.templating.FileTemplatingStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static kz.offerprocessservice.util.FileUtils.*;

public class CsvFileTemplatingStrategyImpl implements FileTemplatingStrategy {

    @Override
    public ResponseEntity<byte[]> generate(Set<String> warehouseNames) {
        StringBuilder content = new StringBuilder();
        content.append(OFFER_CODE).append(COMA).append(OFFER_NAME);

        for (String name : warehouseNames) {
            content.append(COMA).append(name);
        }

        content.append("\n");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, getContentDisposition(FileFormat.CSV));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.TEXT_PLAIN)
                .body(content.toString().getBytes(StandardCharsets.UTF_8));
    }
}

