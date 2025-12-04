package kz.offerprocessservice.file.templating.impl;

import kz.offerprocessservice.file.templating.AbstractTemplatingStrategyTest;
import kz.offerprocessservice.model.enums.FileFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

class ExcelTemplatingStrategyImplTest extends AbstractTemplatingStrategyTest<ExcelTemplatingStrategyImpl> {

    @Override
    protected ExcelTemplatingStrategyImpl createStrategy() {
        return new ExcelTemplatingStrategyImpl();
    }

    @Test
    void getFileFormat_returnsExcel() {
        FileFormat fileFormat = strategy.getFileFormat();
        AssertionsForInterfaceTypes.assertThat(fileFormat).isEqualTo(FileFormat.EXCEL);
    }

    @Test
    void generate_createsExcelTemplate() throws IOException {
        ResponseEntity<byte[]> response = strategy.generate(warehouseNames);

        assertBasicResponse(response, APPLICATION_OCTET_STREAM_VALUE);

        try (XSSFWorkbook workbook = new XSSFWorkbook(
                new ByteArrayInputStream(Objects.requireNonNull(response.getBody())))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(1);

            var sheet = workbook.getSheetAt(0);
            var headerRow = sheet.getRow(0);

            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("OfferCode");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("OfferName");

            for (int i = 0; i < warehouseNames.size(); i++) {
                assertThat(headerRow.getCell(i + 2).getStringCellValue()).isEqualTo(warehouseNames.toArray()[i]);
            }
        }
    }
}