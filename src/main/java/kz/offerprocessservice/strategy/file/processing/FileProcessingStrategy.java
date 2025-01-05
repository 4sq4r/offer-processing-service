package kz.offerprocessservice.strategy.file.processing;

import kz.offerprocessservice.model.dto.PriceListItemDTO;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public interface FileProcessingStrategy {

    Set<PriceListItemDTO> extract(InputStream inputStream) throws IOException;
}
