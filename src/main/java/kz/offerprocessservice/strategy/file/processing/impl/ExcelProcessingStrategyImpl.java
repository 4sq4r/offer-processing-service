package kz.offerprocessservice.strategy.file.processing.impl;

import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.strategy.file.processing.FileProcessingStrategy;
import kz.offerprocessservice.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class ExcelProcessingStrategyImpl implements FileProcessingStrategy {
    @Override
    public Set<PriceListItemDTO> extract(InputStream inputStream) throws IOException {
        try (Workbook wb = new XSSFWorkbook(inputStream)) {
            Sheet sheet = wb.getSheetAt(0);
            Map<Integer, String> headerMap = new HashMap<>();
            Set<PriceListItemDTO> result = new HashSet<>();

            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (FileUtils.isRowEmpty(row)) {
                    continue;
                }

                if (rowIndex == 0) {
                    for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                        headerMap.put(cellIndex, FileUtils.getStringCellValue(row.getCell(cellIndex)));
                    }
                } else {
                    PriceListItemDTO priceListItemDTO = new PriceListItemDTO();
                    Map<String, Integer> stocks = new HashMap<>();

                    for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                        String headerCellName = headerMap.get(cellIndex);
                        String cellValue = FileUtils.getStringCellValue(row.getCell(cellIndex));

                        if (Objects.equals(headerCellName, FileUtils.OFFER_CODE)) {
                            priceListItemDTO.setOfferCode(FileUtils.getStringCellValue(row.getCell(cellIndex)));
                        } else if (Objects.equals(headerCellName, FileUtils.OFFER_NAME)) {
                            priceListItemDTO.setOfferName(FileUtils.getStringCellValue(row.getCell(cellIndex)));
                        } else {
                            try {
                                int stock = Integer.parseInt(cellValue);
                                stocks.put(headerCellName, stock);
                            } catch (NumberFormatException e) {
                                log.info(e.getMessage());
                            }
                        }
                    }

                    priceListItemDTO.setStocks(stocks);
                    result.add(priceListItemDTO);
                }
            }

            return result;
        }
    }
}
