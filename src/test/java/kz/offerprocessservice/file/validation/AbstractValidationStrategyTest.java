package kz.offerprocessservice.file.validation;

import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class AbstractValidationStrategyTest<T extends FileValidationStrategy> {

    protected T strategy;

    protected Set<String> warehouseNames = Set.of("wh1", "wh2");

    @BeforeEach
    void setUp() {
        strategy = createStrategy();
    }

    protected abstract T createStrategy();

    protected InputStream createInputStream(String content) {
        return new ByteArrayInputStream(content.getBytes(UTF_8));
    }
}
