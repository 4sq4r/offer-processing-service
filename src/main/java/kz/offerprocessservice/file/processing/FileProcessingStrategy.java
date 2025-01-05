package kz.offerprocessservice.file.processing;

import kz.offerprocessservice.model.dto.PriceListItemDTO;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Set;

public interface FileProcessingStrategy {

    Set<PriceListItemDTO> extract(InputStream inputStream) throws IOException, URISyntaxException, SAXException;
}
