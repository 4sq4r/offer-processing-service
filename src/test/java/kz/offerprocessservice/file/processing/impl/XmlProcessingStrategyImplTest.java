package kz.offerprocessservice.file.processing.impl;

import kz.offerprocessservice.file.processing.AbstractProcessingStrategyTest;
import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.enums.FileFormat;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class XmlProcessingStrategyImplTest extends AbstractProcessingStrategyTest<XmlProcessingStrategyImpl> {

    @Override
    protected XmlProcessingStrategyImpl createStrategy() {
        return new XmlProcessingStrategyImpl();
    }

    @Test
    void getFileFormat_returnsXml() {
        FileFormat fileFormat = strategy.getFileFormat();
        AssertionsForInterfaceTypes.assertThat(fileFormat).isEqualTo(FileFormat.XML);
    }

    @Test
    void extract_parsesXmlCorrectly() throws Exception {
        String xml = """
                <offers>
                    <offer>
                        <OfferCode>code1</OfferCode>
                        <OfferName>name1</OfferName>
                        <stocks>
                            <stock>
                                <warehouseName>StockA</warehouseName>
                                <stock>10</stock>
                            </stock>
                            <stock>
                                <warehouseName>StockB</warehouseName>
                                <stock>20</stock>
                            </stock>
                        </stocks>
                    </offer>
                </offers>
                """;

        InputStream inputStream = createInputStream(xml);
        Set<PriceListItemDTO> result = strategy.extract(inputStream);

        assertBasicExtraction(result, 1);

        PriceListItemDTO item1 = result.stream()
                .filter(i -> i.getOfferCode().equals("code1"))
                .findFirst()
                .orElseThrow();

        assertThat(item1.getOfferName()).isEqualTo("name1");
        assertThat(item1.getStocks()).containsExactlyInAnyOrderEntriesOf(Map.of("StockA", 10, "StockB", 20));
    }

    @Test
    void extract_returnsEmptySet_whenNoOffers() throws Exception {
        String xml = "<offers><offers/></offers>";
        InputStream inputStream = createInputStream(xml);

        Set<PriceListItemDTO> result = strategy.extract(inputStream);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_throwsSAXException_forInvalidXml() {
        String xml = "<offers><offers><offer></offers>"; // некорректный
        InputStream inputStream = createInputStream(xml);

        assertThatThrownBy(() -> strategy.extract(inputStream)).isInstanceOf(SAXException.class);
    }
}