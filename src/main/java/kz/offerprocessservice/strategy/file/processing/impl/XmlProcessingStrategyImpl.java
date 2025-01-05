package kz.offerprocessservice.strategy.file.processing.impl;

import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.strategy.file.processing.FileProcessingStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class XmlProcessingStrategyImpl implements FileProcessingStrategy {
    @Override
    public Set<PriceListItemDTO> extract(InputStream inputStream) throws IOException {
        return Set.of();
    }
}
