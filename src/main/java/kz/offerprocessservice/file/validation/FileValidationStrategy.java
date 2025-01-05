package kz.offerprocessservice.file.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public interface FileValidationStrategy {

    boolean validate(InputStream inputStream, Set<String> warehouseNames) throws IOException;
}
