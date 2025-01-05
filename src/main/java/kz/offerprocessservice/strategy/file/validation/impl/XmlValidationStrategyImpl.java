package kz.offerprocessservice.strategy.file.validation.impl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import kz.offerprocessservice.model.xml.XmlPriceListTemplate;
import kz.offerprocessservice.strategy.file.validation.FileValidationStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class XmlValidationStrategyImpl implements FileValidationStrategy {
    @Override
    public boolean validate(InputStream inputStream, Set<String> warehouseNames) throws IOException {
        try {
            JAXBContext context = JAXBContext.newInstance(XmlPriceListTemplate.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            XmlPriceListTemplate xmlPriceListTemplate = (XmlPriceListTemplate) unmarshaller.unmarshal(inputStream);

            for (String headerValue : xmlPriceListTemplate.getHeaders()) {
                if (!warehouseNames.contains(headerValue)) {
                    return false;
                }
            }

            return true;
        } catch (JAXBException e) {
            throw new IOException("Error parsing XML file", e);
        }
    }
}
