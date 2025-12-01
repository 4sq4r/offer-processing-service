package kz.offerprocessservice.file.validation.impl;

import kz.offerprocessservice.file.validation.AbstractValidationStrategyTest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ExcelValidationStrategyImplTest extends AbstractValidationStrategyTest<ExcelValidationStrategyImpl> {

    @Override
    protected ExcelValidationStrategyImpl createStrategy() {
        return new ExcelValidationStrategyImpl();
    }

    @Test
    void validate_returnsTrue_whenHeadersMatch() throws Exception {
        InputStream inputStream = createWorkbook(warehouseNames);
        boolean valid = strategy.validate(inputStream, warehouseNames);
        assertThat(valid).isTrue();
    }

    @Test
    void validate_returnsFalse_whenHeadersMismatch() throws Exception {
        InputStream inputStream = createWorkbook(Set.of("wh1", "wh3"));
        boolean valid = strategy.validate(inputStream, warehouseNames);
        assertThat(valid).isFalse();
    }

    @Test
    void validate_returnsFalse_whenRowIsEmpty() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        workbook.createSheet().createRow(0);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
        boolean valid = strategy.validate(inputStream, warehouseNames);
        assertThat(valid).isFalse();
    }

    private InputStream createWorkbook(Set<String> headers) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row headerRow = sheet.createRow(0);
        int i = 0;
        for (String h : headers) {
            headerRow.createCell(i++).setCellValue(h);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return new ByteArrayInputStream(baos.toByteArray());
    }
}