package kz.offerprocessservice.file.processing.impl;

import kz.offerprocessservice.file.processing.FileProcessingStrategy;
import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.enums.FileFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static kz.offerprocessservice.model.enums.FileFormat.CSV;
import static kz.offerprocessservice.util.FileUtils.OFFER_CODE;
import static kz.offerprocessservice.util.FileUtils.OFFER_NAME;

@Slf4j
@Component
public class CsvProcessingStrategyImpl implements FileProcessingStrategy {

    @Override
    public FileFormat getFileFormat() {
        return CSV;
    }

    @Override
    public Set<PriceListItemDTO> extract(InputStream inputStream) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(false)
                    .build()
                    .parse(reader);
            List<String> headerNames = csvParser.getHeaderNames();

            if (headerNames == null || headerNames.isEmpty()) {
                throw new IllegalArgumentException("CSV file is empty or does not contain headers");
            }

            Set<PriceListItemDTO> result = new HashSet<>();

            for (CSVRecord csvRecord : csvParser.getRecords()) {
                if (csvRecord == null) {
                    break;
                }

                PriceListItemDTO priceListItemDTO = new PriceListItemDTO();
                Map<String, Integer> stocks = new HashMap<>();

                for (String header : headerNames) {
                    String cellValue = csvRecord.get(header);

                    if (Objects.equals(header, OFFER_CODE)) {
                        priceListItemDTO.setOfferCode(cellValue);
                    } else if (Objects.equals(header, OFFER_NAME)) {
                        priceListItemDTO.setOfferName(cellValue);
                    } else if (cellValue != null && !cellValue.isEmpty()) {
                        stocks.put(header, Integer.parseInt(cellValue));
                    }
                }

                priceListItemDTO.setStocks(stocks);
                result.add(priceListItemDTO);
            }
            return result;
        }
    }
}
