package kz.offerprocessservice.file.templating;

import jakarta.xml.bind.JAXBException;
import kz.offerprocessservice.model.enums.FileFormat;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Set;

public interface FileTemplatingStrategy {

    FileFormat getFileFormat();

    ResponseEntity<byte[]> generate(Set<String> warehouseNames) throws IOException, JAXBException;
}
