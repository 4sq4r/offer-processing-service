package kz.offerprocessservice.file.templating.impl;


import kz.offerprocessservice.file.templating.AbstractTemplatingStrategyTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

class CsvTemplatingStrategyImplTest extends AbstractTemplatingStrategyTest<CsvTemplatingStrategyImpl> {

    @Override
    protected CsvTemplatingStrategyImpl createStrategy() {
        return new CsvTemplatingStrategyImpl();
    }

    @Test
    void generate_createsCsvTemplate() {
        ResponseEntity<byte[]> response = strategy.generate(warehouseNames);

        assertBasicResponse(response, TEXT_PLAIN_VALUE);

        String content = new String(Objects.requireNonNull(response.getBody()), UTF_8);
        assertThat(content).startsWith("OfferCode,OfferName");
        for (String wh : warehouseNames) {
            assertThat(content).contains(wh);
        }
        assertThat(content).endsWith("\n");
    }
}