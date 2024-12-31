package kz.offerprocessservice.strategy.file.templating.impl;

import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.strategy.file.templating.FileTemplatingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import static kz.offerprocessservice.util.FileUtils.*;

@Slf4j
public class XmlFileTemplatingStrategyImpl implements FileTemplatingStrategy {
    @Override
    public ResponseEntity<byte[]> generate(Set<String> warehouseNames) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement("Template");
            document.appendChild(root);
            Element headers = document.createElement("Headers");
            root.appendChild(headers);
            Element offerCode = document.createElement("Header");
            offerCode.setTextContent(OFFER_CODE);
            headers.appendChild(offerCode);
            Element offerName = document.createElement("Header");
            offerName.setTextContent(OFFER_NAME);
            headers.appendChild(offerName);

            for (String warehouse : warehouseNames) {
                Element header = document.createElement("Header");
                header.setTextContent(warehouse);
                headers.appendChild(header);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(document), new StreamResult(out));
        } catch (Exception e) {
            throw new IOException("Error generating XML template", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, getContentDisposition(FileFormat.XML));

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_XML)
                .body(out.toByteArray());
    }
}
