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
import java.util.List;

@NoArgsConstructor
public class FileUtils {

    public static ResponseEntity<byte[]> getPriceListTemplate(List<String> posNames) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet s = wb.createSheet("sheet");
            Row header = s.createRow(0);
            createCellAndValue(0, header, "offer_code");
            createCellAndValue(1, header, "offer_name");

            for (int i = 0; i < posNames.size(); i++) {
                createCellAndValue(i + 2, header, posNames.get(i));
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

    private static void createCellAndValue(int index, Row row, String value) {
        Cell c = row.createCell(index);
        c.setCellValue(value);
    }
}
