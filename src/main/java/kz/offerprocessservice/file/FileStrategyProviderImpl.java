package kz.offerprocessservice.file;

import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.file.processing.FileProcessingStrategy;
import kz.offerprocessservice.file.processing.impl.CsvProcessingStrategyImpl;
import kz.offerprocessservice.file.processing.impl.ExcelProcessingStrategyImpl;
import kz.offerprocessservice.file.processing.impl.XmlProcessingStrategyImpl;
import kz.offerprocessservice.file.templating.FileTemplatingStrategy;
import kz.offerprocessservice.file.templating.impl.CsvTemplatingStrategyImpl;
import kz.offerprocessservice.file.templating.impl.ExcelTemplatingStrategyImpl;
import kz.offerprocessservice.file.templating.impl.XmlTemplatingStrategyImpl;
import kz.offerprocessservice.file.validation.FileValidationStrategy;
import kz.offerprocessservice.file.validation.impl.CsvValidationStrategyImpl;
import kz.offerprocessservice.file.validation.impl.ExcelValidationStrategyImpl;
import kz.offerprocessservice.file.validation.impl.XmlValidationStrategyImpl;
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
