package kz.offerprocessservice.file;

import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.file.processing.FileProcessingStrategy;
import kz.offerprocessservice.file.templating.FileTemplatingStrategy;
import kz.offerprocessservice.file.validation.FileValidationStrategy;

public interface FileStrategyProvider {

    FileTemplatingStrategy getTemplatingStrategy(FileFormat ff);

    FileValidationStrategy getValidationStrategy(FileFormat ff);

    FileProcessingStrategy getProcessingStrategy(FileFormat ff);
}
