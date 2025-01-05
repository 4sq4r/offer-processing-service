package kz.offerprocessservice.strategy.file.templating.impl;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.model.xml.XmlPriceListTemplate;
import kz.offerprocessservice.strategy.file.templating.FileTemplatingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Set;

import static kz.offerprocessservice.util.FileUtils.getContentDisposition;

@Slf4j
public class XmlTemplatingStrategyImpl implements FileTemplatingStrategy {
    @Override
    public ResponseEntity<byte[]> generate(Set<String> warehouseNames) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            XmlPriceListTemplate template = new XmlPriceListTemplate();
            template.setHeaders(new ArrayList<>(warehouseNames));
            JAXBContext context = JAXBContext.newInstance(XmlPriceListTemplate.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(template, out);
        } catch (JAXBException e) {
            log.error("Error generating XML template: {}", e.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, getContentDisposition(FileFormat.XML));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_XML)
                .body(out.toByteArray());
    }
}
