package kz.offerprocessservice.strategy.file.templating;

import jakarta.xml.bind.JAXBException;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Set;

public interface FileTemplatingStrategy {
    ResponseEntity<byte[]> generate(Set<String> warehouseNames) throws IOException, JAXBException;
}
