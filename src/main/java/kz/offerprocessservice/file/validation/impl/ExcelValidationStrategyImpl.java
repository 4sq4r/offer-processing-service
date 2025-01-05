package kz.offerprocessservice.file.validation.impl;

import kz.offerprocessservice.file.validation.FileValidationStrategy;
import kz.offerprocessservice.util.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ExcelValidationStrategyImpl implements FileValidationStrategy {

    @Override
    public boolean validate(InputStream inputStream, Set<String> warehouseNames) throws IOException {
        try (Workbook wb = new XSSFWorkbook(inputStream)) {
            Sheet sheet = wb.getSheetAt(0);
            Row row = sheet.getRow(0);

            if (row == null) {
                return false;
            }

            for (int i = 0; i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);

                if (cell != null) {
                    String pointOfSaleName = FileUtils.getStringCellValue(cell);

                    if (!pointOfSaleName.isEmpty() && !warehouseNames.contains(pointOfSaleName)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            return true;
        }
    }
}
