package kz.offerprocessservice.strategy.file.validation.impl;

import kz.offerprocessservice.strategy.file.validation.FileValidationStrategy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import static kz.offerprocessservice.util.FileUtils.OFFER_CODE;
import static kz.offerprocessservice.util.FileUtils.OFFER_NAME;

public class CsvValidationStrategyImpl implements FileValidationStrategy {
    @Override
    public boolean validate(InputStream inputStream, Set<String> warehouseNames) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            CSVParser parser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(false)
                    .build()
                    .parse(reader);
            List<String> headerNames = parser.getHeaderNames();
            warehouseNames.add(OFFER_CODE);
            warehouseNames.add(OFFER_NAME);

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
