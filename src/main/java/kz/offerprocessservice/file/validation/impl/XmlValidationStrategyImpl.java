package kz.offerprocessservice.file.validation.impl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import kz.offerprocessservice.file.validation.FileValidationStrategy;
import kz.offerprocessservice.model.xml.XmlOffer;
import kz.offerprocessservice.model.xml.XmlPriceListTemplate;
import kz.offerprocessservice.model.xml.XmlStock;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class XmlValidationStrategyImpl implements FileValidationStrategy {
    @Override
    public boolean validate(InputStream inputStream, Set<String> warehouseNames) throws IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(XmlPriceListTemplate.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            XmlPriceListTemplate priceList = (XmlPriceListTemplate) unmarshaller.unmarshal(inputStream);
            List<XmlOffer> offers = priceList.getOffers();

            for (XmlOffer offer : offers) {
                List<XmlStock> stocks = offer.getStocks();
                Set<String> tempNames = stocks.stream()
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
