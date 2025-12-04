package kz.offerprocessservice.file;

import kz.offerprocessservice.file.processing.FileProcessingStrategy;
import kz.offerprocessservice.file.templating.FileTemplatingStrategy;
import kz.offerprocessservice.file.validation.FileValidationStrategy;
import kz.offerprocessservice.model.enums.FileFormat;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class FileStrategyProviderImpl implements FileStrategyProvider {

    private final Map<FileFormat, FileTemplatingStrategy> templatingStrategies;

    private final Map<FileFormat, FileValidationStrategy> validationStrategies;

    private final Map<FileFormat, FileProcessingStrategy> processingStrategies;

    public FileStrategyProviderImpl(
            List<FileTemplatingStrategy> templatingStrategies,
            List<FileValidationStrategy> validationStrategies,
            List<FileProcessingStrategy> processingStrategies
    ) {
        this.templatingStrategies = templatingStrategies.stream()
                .collect(toMap(FileTemplatingStrategy::getFileFormat, identity()));
        this.validationStrategies = validationStrategies.stream()
                .collect(toMap(FileValidationStrategy::getFileFormat, identity()));
        this.processingStrategies = processingStrategies.stream()
                .collect(toMap(FileProcessingStrategy::getFileFormat, identity()));
    }

    @Override
    public FileTemplatingStrategy getTemplatingStrategy(FileFormat ff) {
        return getOrThrow(templatingStrategies, ff);
    }

    @Override
    public FileValidationStrategy getValidationStrategy(FileFormat ff) {
        return getOrThrow(validationStrategies, ff);
    }

    @Override
    public FileProcessingStrategy getProcessingStrategy(FileFormat ff) {
        return getOrThrow(processingStrategies, ff);
    }

    private <T> T getOrThrow(Map<FileFormat, T> map, FileFormat fileFormat) {
        T strategy = map.get(fileFormat);
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy not found for format: " + fileFormat);
        }
        return strategy;
    }

}
