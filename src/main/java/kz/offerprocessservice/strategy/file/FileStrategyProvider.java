package kz.offerprocessservice.strategy.file;

import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.strategy.file.processing.FileProcessingStrategy;
import kz.offerprocessservice.strategy.file.templating.FileTemplatingStrategy;
import kz.offerprocessservice.strategy.file.validation.FileValidationStrategy;

public interface FileStrategyProvider {

    FileTemplatingStrategy getTemplatingStrategy(FileFormat ff);

    FileValidationStrategy getValidationStrategy(FileFormat ff);

    FileProcessingStrategy getProcessingStrategy(FileFormat ff);
}
