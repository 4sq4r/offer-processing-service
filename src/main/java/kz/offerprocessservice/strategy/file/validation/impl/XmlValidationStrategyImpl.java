package kz.offerprocessservice.strategy.file.validation.impl;

import kz.offerprocessservice.strategy.file.validation.FileValidationStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class XmlValidationStrategyImpl implements FileValidationStrategy {
    @Override
    public boolean validate(InputStream inputStream, Set<String> warehouseNames) throws IOException {
        return false;
    }
}
