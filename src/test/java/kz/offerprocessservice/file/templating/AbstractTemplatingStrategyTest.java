package kz.offerprocessservice.file.templating;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public abstract class AbstractTemplatingStrategyTest<T extends FileTemplatingStrategy> {

    protected T strategy;

    protected Set<String> warehouseNames = Set.of("wh1", "wh2", "wh3");

    @BeforeEach
    void setUp() {
        strategy = createStrategy();
    }

    protected abstract T createStrategy();

    protected void assertBasicResponse(ResponseEntity<byte[]> response, String expectedContentType) {
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getHeaders().getFirst("Content-Disposition")).contains("attachment");
        assertThat(Objects.requireNonNull(response.getHeaders().getContentType()).toString()).isEqualTo(expectedContentType);
    }
}
