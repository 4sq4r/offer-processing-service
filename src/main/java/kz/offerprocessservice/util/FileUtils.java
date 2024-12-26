package kz.offerprocessservice.util;

import kz.offerprocessservice.model.dto.PriceListItemDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@NoArgsConstructor
public class FileUtils {

    private static final String OFFER_CODE = "offer_code";
    private static final String OFFER_NAME = "offer_name";

    public static ResponseEntity<byte[]> getPriceListTemplate(Set<String> warehouseNames) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ArrayList<String> tempNames = new ArrayList<>(warehouseNames);
            Sheet s = wb.createSheet("sheet");
            Row header = s.createRow(0);
            createCellAndValue(0, header, OFFER_CODE);
            createCellAndValue(1, header, OFFER_NAME);

            for (int i = 0; i < warehouseNames.size(); i++) {
                createCellAndValue(i + 2, header, tempNames.get(i));
            }

            wb.write(out);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "template.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new ByteArrayInputStream(out.toByteArray()).readAllBytes());
        }
    }

    public static boolean validatePriceList(InputStream is, Set<String> headers) throws IOException {
        try (Workbook wb = new XSSFWorkbook(is)) {
            headers.add(OFFER_CODE);
            headers.add(OFFER_NAME);
            Sheet sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(0);


            if (row == null) {
                return false;
            }

            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);

                if (cell != null) {
                    String pointOfSaleName = getStringCellValue(cell);

                    if (!pointOfSaleName.isEmpty() && !headers.contains(pointOfSaleName)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            return true;
        }
    }

    public static Set<PriceListItemDTO> extractPriceListItems(InputStream is) throws IOException {
        try (Workbook wb = new XSSFWorkbook(is)) {
            Sheet sheet = wb.getSheetAt(0);
            Map<Integer, String> headerCellMap = new HashMap<>();
            Set<PriceListItemDTO> result = new HashSet<>();

            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (isRowEmpty(row)) {
                    continue;
                }

                if (rowIndex == 0) {
                    for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                        headerCellMap.put(cellIndex, getStringCellValue(row.getCell(cellIndex)));
                    }
                } else {
                    PriceListItemDTO priceListItemDTO = new PriceListItemDTO();
                    Map<String, Integer> stocks = new HashMap<>();

                    for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                        String headerCellName = headerCellMap.get(cellIndex);
                        String cellValue = getStringCellValue(row.getCell(cellIndex));

                        if (Objects.equals(headerCellName, OFFER_CODE)) {
                            priceListItemDTO.setOfferCode(getStringCellValue(row.getCell(cellIndex)));
                        } else if (Objects.equals(headerCellName, OFFER_NAME)) {
                            priceListItemDTO.setOfferName(getStringCellValue(row.getCell(cellIndex)));
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

    private static boolean isRowEmpty(Row r) {
        if (r == null) {
            return true;
        }

        for (int i = 0; i < r.getLastCellNum(); i++) {
            Cell c = r.getCell(i);
            if (c != null && !StringUtils.isBlank(getStringCellValue(c))) {
                return false;
            }
        }

        return false;
    }

    private static String getStringCellValue(Cell c) {
        if (c == null) {
            return null;
        }

        return switch (c.getCellType()) {
            case STRING -> c.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(Math.round(c.getNumericCellValue()));
            default -> null;
        };
    }

    private static void createCellAndValue(int index, Row row, String value) {
        Cell c = row.createCell(index);
        c.setCellValue(value);
    }
}
