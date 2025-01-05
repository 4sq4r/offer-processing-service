package kz.offerprocessservice.file.templating.impl;

import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.file.templating.FileTemplatingStrategy;
import kz.offerprocessservice.util.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class CsvTemplatingStrategyImpl implements FileTemplatingStrategy {

    @Override
    public ResponseEntity<byte[]> generate(Set<String> warehouseNames) {
        StringBuilder content = new StringBuilder();
        content.append(FileUtils.OFFER_CODE).append(FileUtils.COMA).append(FileUtils.OFFER_NAME);

        for (String name : warehouseNames) {
            content.append(FileUtils.COMA).append(name);
        }

        content.append("\n");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, FileUtils.getContentDisposition(FileFormat.CSV));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.TEXT_PLAIN)
                .body(content.toString().getBytes(StandardCharsets.UTF_8));
    }
}

