package kz.offerprocessservice.util;

import lombok.NoArgsConstructor;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

@NoArgsConstructor
public class FileUtils {

    private static final String OFFER_CODE = "offer_code";
    private static final String OFFER_NAME = "offer_name";

    public static ResponseEntity<byte[]> getPriceListTemplate(Set<String> posNames) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ArrayList<String> tempNames = new ArrayList<>(posNames);
            Sheet s = wb.createSheet("sheet");
            Row header = s.createRow(0);
            createCellAndValue(0, header, OFFER_CODE);
            createCellAndValue(1, header, OFFER_NAME);

            for (int i = 0; i < posNames.size(); i++) {
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

    private static String getStringCellValue(Cell c) {
        if (c == null) {
            return null;
        }

        return switch (c.getCellType()) {
            case STRING -> c.getStringCellValue().trim();
            case NUMERIC -> BigDecimal.valueOf(c.getNumericCellValue()).toPlainString();
            default -> null;
        };
    }

    private static void createCellAndValue(int index, Row row, String value) {
        Cell c = row.createCell(index);
        c.setCellValue(value);
    }
}
