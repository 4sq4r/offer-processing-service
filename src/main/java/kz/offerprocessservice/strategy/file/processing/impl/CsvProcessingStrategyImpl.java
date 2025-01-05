package kz.offerprocessservice.strategy.file.processing.impl;

import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.strategy.file.processing.FileProcessingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static kz.offerprocessservice.util.FileUtils.OFFER_CODE;
import static kz.offerprocessservice.util.FileUtils.OFFER_NAME;

@Slf4j
public class CsvProcessingStrategyImpl implements FileProcessingStrategy {
    @Override
    public Set<PriceListItemDTO> extract(InputStream inputStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(false)
                    .build()
                    .parse(reader);
            List<String> headerNames = csvParser.getHeaderNames();
            List<CSVRecord> records = csvParser.getRecords();

            if (headerNames == null || headerNames.isEmpty()) {
                throw new IllegalArgumentException("CSV file is empty or does not contain headers");
            }

            Set<PriceListItemDTO> result = new HashSet<>();

            for (CSVRecord record : records) {
                if (record == null) {
                    break;
                }

                PriceListItemDTO priceListItemDTO = new PriceListItemDTO();
                Map<String, Integer> stocks = new HashMap<>();

                for (String header : headerNames) {
                    String cellValue = record.get(header);

                    if (Objects.equals(header, OFFER_CODE)) {
                        priceListItemDTO.setOfferCode(cellValue);
                    } else if (Objects.equals(header, OFFER_NAME)) {
                        priceListItemDTO.setOfferName(cellValue);
                    } else {
                        int stock = Integer.parseInt(cellValue);
                        stocks.put(header, stock);
                    }
                }

                priceListItemDTO.setStocks(stocks);
                result.add(priceListItemDTO);
            }
            return result;
        }
    }
}
