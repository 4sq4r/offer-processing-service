package kz.offerprocessservice.strategy.file.processing;

import kz.offerprocessservice.strategy.file.processing.impl.CsvFileProcessorStrategyImpl;
import kz.offerprocessservice.strategy.file.processing.impl.ExcelFileProcessorStrategyImpl;

public class FileProcessorStrategyFactory {

    public static FileProcessStrategy getStrategy(String fileName) {
        return switch (fileName) {
            case String f when f.endsWith(".xlsx") -> new ExcelFileProcessorStrategyImpl();
            case String f when f.endsWith(".csv") -> new CsvFileProcessorStrategyImpl();
            default -> throw new IllegalArgumentException("Unsupported file format: " + fileName);
        };
    }
}
