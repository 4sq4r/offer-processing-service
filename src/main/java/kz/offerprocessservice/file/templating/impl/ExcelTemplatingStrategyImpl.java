package kz.offerprocessservice.file.templating.impl;

import kz.offerprocessservice.file.templating.FileTemplatingStrategy;
import kz.offerprocessservice.model.enums.FileFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static kz.offerprocessservice.model.enums.FileFormat.EXCEL;
import static kz.offerprocessservice.util.FileUtils.OFFER_CODE;
import static kz.offerprocessservice.util.FileUtils.OFFER_NAME;
import static kz.offerprocessservice.util.FileUtils.createCellAndValue;
import static kz.offerprocessservice.util.FileUtils.getContentDisposition;

@Component
public class ExcelTemplatingStrategyImpl implements FileTemplatingStrategy {

    @Override
    public FileFormat getFileFormat() {
        return FileFormat.EXCEL;
    }

    @Override
    public ResponseEntity<byte[]> generate(Set<String> warehouseNames) throws IOException {
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
            headers.add(HttpHeaders.CONTENT_DISPOSITION, getContentDisposition(EXCEL));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new ByteArrayInputStream(out.toByteArray()).readAllBytes());
        }
    }
}
