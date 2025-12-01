package kz.offerprocessservice.file.validation.impl;

import kz.offerprocessservice.file.validation.AbstractValidationStrategyTest;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class XmlValidationStrategyImplTest extends AbstractValidationStrategyTest<XmlValidationStrategyImpl> {

    @Override
    protected XmlValidationStrategyImpl createStrategy() {
        return new XmlValidationStrategyImpl();
    }

    @Test
    void validate_returnsTrue_whenAllWarehouseNamesMatch() throws Exception {
        String xml = """
                <offers>
                    <offer>
                        <OfferCode>code1</OfferCode>
                        <OfferName>name1</OfferName>
                        <stocks>
                            <stock>
                                <warehouseName>wh1</warehouseName>
                                <stock>10</stock>
                            </stock>
                            <stock>
                                <warehouseName>wh2</warehouseName>
                                <stock>5</stock>
                            </stock>
                        </stocks>
                    </offer>
                </offers>
                """;

        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        boolean valid = strategy.validate(inputStream, warehouseNames);

        assertThat(valid).isTrue();
    }

    @Test
    void validate_returnsFalse_whenWarehouseNamesMismatch() throws Exception {
        String xml = """
                <offers>
                    <offer>
                        <OfferCode>code1</OfferCode>
                        <OfferName>name1</OfferName>
                        <stocks>
                            <stock>
                                <warehouseName>wh1</warehouseName>
                                <stock>10</stock>
                            </stock>
                            <stock>
                                <warehouseName>wh3</warehouseName>
                                <stock>5</stock>
                            </stock>
                        </stocks>
                    </offer>
                </offers>
                """;

        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        boolean valid = strategy.validate(inputStream, warehouseNames);

        assertThat(valid).isFalse();
    }

    @Test
    void validate_returnsTrue_forMultipleOffers() throws Exception {
        String xml = """
                <offers>
                    <offer>
                        <OfferCode>code1</OfferCode>
                        <OfferName>name1</OfferName>
                        <stocks>
                            <stock>
                                <warehouseName>wh1</warehouseName>
                                <stock>10</stock>
                            </stock>
                            <stock>
                                <warehouseName>wh2</warehouseName>
                                <stock>5</stock>
                            </stock>
                        </stocks>
                    </offer>
                    <offer>
                        <OfferCode>code2</OfferCode>
                        <OfferName>name2</OfferName>
                        <stocks>
                            <stock>
                                <warehouseName>wh1</warehouseName>
                                <stock>3</stock>
                            </stock>
                            <stock>
                                <warehouseName>wh2</warehouseName>
                                <stock>7</stock>
                            </stock>
                        </stocks>
                    </offer>
                </offers>
                """;

        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        boolean valid = strategy.validate(inputStream, warehouseNames);

        assertThat(valid).isTrue();
    }

    @Test
    void validate_throwsIOException_whenXmlIsInvalid() {
        String xml = "<offers><offer></offers>";
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());

        try {
            strategy.validate(inputStream, warehouseNames);
        } catch (IOException e) {
            assertThat(e.getMessage()).contains("Error parsing XML file");
        }
    }
}