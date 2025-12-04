package kz.offerprocessservice.file.validation.impl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import kz.offerprocessservice.file.validation.FileValidationStrategy;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.model.xml.XmlOffer;
import kz.offerprocessservice.model.xml.XmlPriceListTemplate;
import kz.offerprocessservice.model.xml.XmlStock;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

import static kz.offerprocessservice.model.enums.FileFormat.XML;

@Component
public class XmlValidationStrategyImpl implements FileValidationStrategy {

    @Override
    public FileFormat getFileFormat() {
        return XML;
    }

    @Override
    public boolean validate(InputStream inputStream, Set<String> warehouseNames) throws IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(XmlPriceListTemplate.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            XmlPriceListTemplate priceList = (XmlPriceListTemplate) unmarshaller.unmarshal(inputStream);

            for (XmlOffer offer : priceList.getOffers()) {
                Set<String> tempNames = offer.getStocks().stream()
                        .map(XmlStock::getWarehouseName)
                        .collect(Collectors.toSet());

                if (!warehouseNames.equals(tempNames)) {
                    return false;
                }
            }

            return true;
        } catch (JAXBException e) {
            throw new IOException("Error parsing XML file", e);
        }
    }
}
