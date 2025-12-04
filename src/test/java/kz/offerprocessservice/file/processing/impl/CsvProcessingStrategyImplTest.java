package kz.offerprocessservice.file.processing.impl;

import kz.offerprocessservice.file.processing.AbstractProcessingStrategyTest;
import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.enums.FileFormat;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class CsvProcessingStrategyImplTest extends AbstractProcessingStrategyTest<CsvProcessingStrategyImpl> {

    @Override
    protected CsvProcessingStrategyImpl createStrategy() {
        return new CsvProcessingStrategyImpl();
    }

    @Test
    void getFileFormat_returnsCSV() {
        FileFormat fileFormat = strategy.getFileFormat();
        assertThat(fileFormat).isEqualTo(FileFormat.CSV);
    }

    @Test
    void extract_throwsException_whenNoHeaders() {
        String csv = "";
        InputStream inputStream = createInputStream(csv);

        assertThatThrownBy(() -> strategy.extract(inputStream))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("CSV file is empty or does not contain headers");
    }

    @Test
    void extract_handlesEmptyValues() throws Exception {
        //given
        String csv = "OfferCode,OfferName,StockA\n" +
                     "code1,name1,\n";
        InputStream inputStream = createInputStream(csv);

        //when
        Set<PriceListItemDTO> result = strategy.extract(inputStream);

        //then
        assertBasicExtraction(result, result.size());
        PriceListItemDTO item = result.iterator().next();
        assertThat(item.getStocks()).doesNotContainKey("StockA");
    }

    @Test
    void extract_parsesCsvCorrectly() throws Exception {
        //given
        String csv = "OfferCode,OfferName,StockA,StockB\n" +
                     "code1,name1,10,20\n" +
                     "code2,name2,5,15";
        InputStream inputStream = createInputStream(csv);
        //when
        Set<PriceListItemDTO> result = strategy.extract(inputStream);
        //then
        assertBasicExtraction(result, result.size());
        PriceListItemDTO item1 = result.stream()
                .filter(i -> i.getOfferCode().equals("code1"))
                .findFirst().orElseThrow();
        assertThat(item1.getOfferName()).isEqualTo("name1");
        assertThat(item1.getStocks()).containsExactlyInAnyOrderEntriesOf(Map.of("StockA", 10, "StockB", 20));
    }
}