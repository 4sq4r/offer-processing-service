package kz.offerprocessservice.strategy.file;

import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.strategy.file.processing.FileProcessingStrategy;
import kz.offerprocessservice.strategy.file.processing.impl.CsvProcessingStrategyImpl;
import kz.offerprocessservice.strategy.file.processing.impl.ExcelProcessingStrategyImpl;
import kz.offerprocessservice.strategy.file.processing.impl.XmlProcessingStrategyImpl;
import kz.offerprocessservice.strategy.file.templating.FileTemplatingStrategy;
import kz.offerprocessservice.strategy.file.templating.impl.CsvTemplatingStrategyImpl;
import kz.offerprocessservice.strategy.file.templating.impl.ExcelTemplatingStrategyImpl;
import kz.offerprocessservice.strategy.file.templating.impl.XmlTemplatingStrategyImpl;
import kz.offerprocessservice.strategy.file.validation.FileValidationStrategy;
import kz.offerprocessservice.strategy.file.validation.impl.CsvValidationStrategyImpl;
import kz.offerprocessservice.strategy.file.validation.impl.ExcelValidationStrategyImpl;
import kz.offerprocessservice.strategy.file.validation.impl.XmlValidationStrategyImpl;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FileStrategyProviderImpl implements FileStrategyProvider {

    private final Map<FileFormat, FileTemplatingStrategy> templatingStrategies = Map.of(
            FileFormat.CSV, new CsvTemplatingStrategyImpl(),
            FileFormat.XML, new XmlTemplatingStrategyImpl(),
            FileFormat.EXCEL, new ExcelTemplatingStrategyImpl()
    );

    private final Map<FileFormat, FileValidationStrategy> validationStrategies = Map.of(
            FileFormat.CSV, new CsvValidationStrategyImpl(),
            FileFormat.XML, new XmlValidationStrategyImpl(),
            FileFormat.EXCEL, new ExcelValidationStrategyImpl()
    );

    private final Map<FileFormat, FileProcessingStrategy> processingStrategies = Map.of(
            FileFormat.CSV, new CsvProcessingStrategyImpl(),
            FileFormat.XML, new XmlProcessingStrategyImpl(),
            FileFormat.EXCEL, new ExcelProcessingStrategyImpl()
    );

    @Override
    public FileTemplatingStrategy getTemplatingStrategy(FileFormat ff) {
        return templatingStrategies.get(ff);
    }

    @Override
    public FileValidationStrategy getValidationStrategy(FileFormat ff) {
        return validationStrategies.get(ff);
    }

    @Override
    public FileProcessingStrategy getProcessingStrategy(FileFormat ff) {
        return processingStrategies.get(ff);
    }
}
