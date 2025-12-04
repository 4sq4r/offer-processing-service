package kz.offerprocessservice.file.templating.impl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import kz.offerprocessservice.file.templating.AbstractTemplatingStrategyTest;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.model.xml.XmlPriceListTemplate;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

class XmlTemplatingStrategyImplTest extends AbstractTemplatingStrategyTest<XmlTemplatingStrategyImpl> {

    @Override
    protected XmlTemplatingStrategyImpl createStrategy() {
        return new XmlTemplatingStrategyImpl();
    }

    @Test
    void getFileFormat_returnsXml() {
        FileFormat fileFormat = strategy.getFileFormat();
        AssertionsForInterfaceTypes.assertThat(fileFormat).isEqualTo(FileFormat.XML);
    }


    @Test
    void generate_createsXmlTemplate() throws Exception {
        ResponseEntity<byte[]> response = strategy.generate(warehouseNames);

        assertBasicResponse(response, APPLICATION_XML_VALUE);

        JAXBContext context = JAXBContext.newInstance(XmlPriceListTemplate.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        XmlPriceListTemplate template = (XmlPriceListTemplate) unmarshaller.unmarshal(
                new ByteArrayInputStream(Objects.requireNonNull(response.getBody())));

        assertThat(template.getOffers()).hasSize(1);

        var offer = template.getOffers().get(0);
        assertThat(offer.getOfferCode()).isEqualTo("Put your offer code here");
        assertThat(offer.getOfferName()).isEqualTo("Put your offer name here");

        assertThat(offer.getStocks()).hasSize(warehouseNames.size());
        for (var stock : offer.getStocks()) {
            assertThat(warehouseNames).contains(stock.getWarehouseName());
            assertThat(stock.getStock()).isZero();
        }
    }
}