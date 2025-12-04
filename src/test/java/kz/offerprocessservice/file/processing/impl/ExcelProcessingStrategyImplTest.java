package kz.offerprocessservice.file.processing.impl;

import kz.offerprocessservice.file.processing.AbstractProcessingStrategyTest;
import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.enums.FileFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelProcessingStrategyImplTest extends AbstractProcessingStrategyTest<ExcelProcessingStrategyImpl> {

    @Override
    protected ExcelProcessingStrategyImpl createStrategy() {
        return new ExcelProcessingStrategyImpl();
    }

    @Test
    void getFileFormat_returnsExcel() {
        FileFormat fileFormat = strategy.getFileFormat();
        AssertionsForInterfaceTypes.assertThat(fileFormat).isEqualTo(FileFormat.EXCEL);
    }

    @Test
    void extract_parsesExcelCorrectly() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("OfferCode");
        header.createCell(1).setCellValue("OfferName");
        header.createCell(2).setCellValue("StockA");
        header.createCell(3).setCellValue("StockB");

        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("code1");
        row1.createCell(1).setCellValue("name1");
        row1.createCell(2).setCellValue(10);
        row1.createCell(3).setCellValue(20);

        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("code2");
        row2.createCell(1).setCellValue("name2");
        row2.createCell(2).setCellValue(5);
        row2.createCell(3).setCellValue(15);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
        // when
        Set<PriceListItemDTO> result = strategy.extract(inputStream);
        // then
        assertBasicExtraction(result, 2);

        PriceListItemDTO item1 = result.stream()
                .filter(i -> i.getOfferCode().equals("code1"))
                .findFirst().orElseThrow();
        assertThat(item1.getOfferName()).isEqualTo("name1");
        assertThat(item1.getStocks()).containsExactlyInAnyOrderEntriesOf(Map.of("StockA", 10, "StockB", 20));
    }

    @Test
    void extract_handlesEmptyStockValues() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("OfferCode");
        header.createCell(1).setCellValue("OfferName");
        header.createCell(2).setCellValue("StockA");

        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("code1");
        row1.createCell(1).setCellValue("name1");
        row1.createCell(2).setCellValue("");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

        // when
        Set<PriceListItemDTO> result = strategy.extract(inputStream);

        // then
        assertBasicExtraction(result, 1);
        PriceListItemDTO item = result.iterator().next();
        assertThat(item.getStocks()).isEmpty();
    }
}