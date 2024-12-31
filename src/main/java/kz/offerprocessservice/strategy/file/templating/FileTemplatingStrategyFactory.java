package kz.offerprocessservice.strategy.file.templating;

import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.strategy.file.templating.impl.CsvFileTemplatingStrategyImpl;
import kz.offerprocessservice.strategy.file.templating.impl.ExcelFileTemplatingStrategyImpl;
import kz.offerprocessservice.strategy.file.templating.impl.XmlFileTemplatingStrategyImpl;

public class FileTemplatingStrategyFactory {

    public static FileTemplatingStrategy getStrategy(FileFormat format) {
        return switch (format) {
            case FileFormat.CSV -> new CsvFileTemplatingStrategyImpl();
            case FileFormat.XML -> new XmlFileTemplatingStrategyImpl();
            case FileFormat.EXCEL -> new ExcelFileTemplatingStrategyImpl();
        };
    }
}
