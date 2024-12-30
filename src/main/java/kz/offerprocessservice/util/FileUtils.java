package kz.offerprocessservice.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

@Slf4j
@NoArgsConstructor
public class FileUtils {

    public static final String OFFER_CODE = "offer_code";
    public static final String OFFER_NAME = "offer_name";

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

    public static boolean isRowEmpty(Row r) {
        if (r == null) {
            return true;
        }

        for (int i = 0; i < r.getLastCellNum(); i++) {
            Cell cell = r.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                if (StringUtils.isBlank(getStringCellValue(cell))) {
                    return false;
                }
            }
        }

        return false;
    }


    public static String getStringCellValue(Cell c) {
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
