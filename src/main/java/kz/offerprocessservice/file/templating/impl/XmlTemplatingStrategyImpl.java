package kz.offerprocessservice.file.templating.impl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import kz.offerprocessservice.file.templating.FileTemplatingStrategy;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.model.xml.XmlOffer;
import kz.offerprocessservice.model.xml.XmlPriceListTemplate;
import kz.offerprocessservice.model.xml.XmlStock;
import kz.offerprocessservice.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Set;

@Slf4j
public class XmlTemplatingStrategyImpl implements FileTemplatingStrategy {
    @Override
    public ResponseEntity<byte[]> generate(Set<String> warehouseNames) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            XmlPriceListTemplate template = new XmlPriceListTemplate();
            XmlOffer offer = new XmlOffer();
            offer.setOfferName("Put your offer name here");
            offer.setOfferCode("Put your offer code here");
            offer.setStocks(warehouseNames.stream()
                    .map(warehouseName -> {
                        XmlStock stock = new XmlStock();
                        stock.setStock(0);
                        stock.setWarehouseName(warehouseName);

                        return stock;
                    }).toList());
            template.setOffers(List.of(offer));
            JAXBContext context = JAXBContext.newInstance(XmlPriceListTemplate.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(template, out);
        } catch (JAXBException e) {
            log.error("Error generating XML template: {}", e.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, FileUtils.getContentDisposition(FileFormat.XML));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_XML)
                .body(out.toByteArray());
    }
}
