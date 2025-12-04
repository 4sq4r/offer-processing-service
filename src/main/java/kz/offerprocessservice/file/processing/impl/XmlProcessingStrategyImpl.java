package kz.offerprocessservice.file.processing.impl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import kz.offerprocessservice.file.processing.FileProcessingStrategy;
import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.model.xml.XmlPriceListTemplate;
import kz.offerprocessservice.model.xml.XmlStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static kz.offerprocessservice.model.enums.FileFormat.XML;

@Slf4j
@Component
public class XmlProcessingStrategyImpl implements FileProcessingStrategy {

    @Override
    public FileFormat getFileFormat() {
        return XML;
    }

    @Override
    public Set<PriceListItemDTO> extract(InputStream inputStream) throws SAXException {
        try {
            JAXBContext context = JAXBContext.newInstance(XmlPriceListTemplate.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            XmlPriceListTemplate priceList = (XmlPriceListTemplate) unmarshaller.unmarshal(inputStream);

            if (!priceList.getOffers().isEmpty()) {
                return priceList.getOffers().stream()
                        .map(offer -> {
                            PriceListItemDTO priceListItemDTO = new PriceListItemDTO();
                            priceListItemDTO.setOfferName(offer.getOfferName());
                            priceListItemDTO.setOfferCode(offer.getOfferCode());
                            Map<String, Integer> stocks = offer.getStocks().stream()
                                    .collect(Collectors.toMap(
                                            XmlStock::getWarehouseName,
                                            XmlStock::getStock
                                    ));
                            priceListItemDTO.setStocks(stocks);

                            return priceListItemDTO;
                        }).collect(Collectors.toSet());
            }
        } catch (Exception e) {
            throw new SAXException(e);
        }

        return Set.of();
    }
}
