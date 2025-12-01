package kz.offerprocessservice.file.processing;

import kz.offerprocessservice.model.dto.PriceListItemDTO;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public abstract class AbstractProcessingStrategyTest<T extends FileProcessingStrategy> {

    protected T strategy;

    @BeforeEach
    void setUp() {
        strategy = createStrategy();
    }

    protected abstract T createStrategy();

    protected InputStream createInputStream(String file) {
        return new ByteArrayInputStream(file.getBytes(StandardCharsets.UTF_8));
    }
    protected void assertBasicExtraction(Set<PriceListItemDTO> extracted, int expectedCount) {
        assertThat(extracted).isNotNull();
        assertThat(extracted.size()).isEqualTo(expectedCount);
        extracted.forEach(item -> {
            assertThat(item.getOfferCode()).isNotNull();
            assertThat(item.getOfferName()).isNotNull();
            assertThat(item.getStocks()).isNotNull();
        });
    }
}
