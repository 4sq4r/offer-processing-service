package kz.offerprocessservice.file.templating.impl;

import kz.offerprocessservice.file.templating.FileTemplatingStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static kz.offerprocessservice.model.enums.FileFormat.CSV;
import static kz.offerprocessservice.util.FileUtils.*;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.TEXT_PLAIN;

public class CsvTemplatingStrategyImpl implements FileTemplatingStrategy {

    @Override
    public ResponseEntity<byte[]> generate(Set<String> warehouseNames) {
        StringBuilder content = new StringBuilder();
        content.append(OFFER_CODE).append(COMA).append(OFFER_NAME);

        for (String name : warehouseNames) {
            content.append(COMA).append(name);
        }

        content.append("\n");
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_DISPOSITION, getContentDisposition(CSV));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(TEXT_PLAIN)
                .body(content.toString().getBytes(StandardCharsets.UTF_8));
    }
}

