package kz.offerprocessservice.file.validation;

import kz.offerprocessservice.model.enums.FileFormat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public interface FileValidationStrategy {

    FileFormat getFileFormat();

    boolean validate(InputStream inputStream, Set<String> warehouseNames) throws IOException;
}
