package kz.offerprocessservice.strategy.file.validation;

import kz.offerprocessservice.strategy.file.validation.impl.CsvFileValidationStrategyImpl;
import kz.offerprocessservice.strategy.file.validation.impl.ExcelFileValidationStrategyImpl;

public class FileValidationStrategyFactory {

    public static FileValidationStrategy getStrategy(String fileName) {
        return switch (fileName) {
            case String f when f.endsWith(".xlsx") -> new ExcelFileValidationStrategyImpl();
            case String f when f.endsWith(".csv") -> new CsvFileValidationStrategyImpl();
            default -> throw new IllegalArgumentException("Unsupported file format: " + fileName);
        };
    }
}
