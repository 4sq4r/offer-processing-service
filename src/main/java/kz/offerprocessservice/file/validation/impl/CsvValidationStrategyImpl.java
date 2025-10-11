package kz.offerprocessservice.file.validation.impl;

import kz.offerprocessservice.file.validation.FileValidationStrategy;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

public class CsvValidationStrategyImpl implements FileValidationStrategy {
    @Override
    public boolean validate(InputStream inputStream, Set<String> warehouseNames) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            List<String> headerNames = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(false)
                    .build()
                    .parse(reader)
                    .getHeaderNames();

            if (headerNames == null || headerNames.isEmpty()) {
                return false;
            }

            for (String header : headerNames) {
                if (!warehouseNames.contains(header)) {
                    return false;
                }
            }

            return true;
        }
    }
}
