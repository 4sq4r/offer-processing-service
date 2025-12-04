package kz.offerprocessservice.file.validation.impl;

import kz.offerprocessservice.file.validation.AbstractValidationStrategyTest;
import kz.offerprocessservice.model.enums.FileFormat;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CsvValidationStrategyImplTest extends AbstractValidationStrategyTest<CsvValidationStrategyImpl> {

    @Override
    protected CsvValidationStrategyImpl createStrategy() {
        return new CsvValidationStrategyImpl();
    }

    @Test
    void getFileFormat_returnsCSV() {
        FileFormat fileFormat = strategy.getFileFormat();
        AssertionsForInterfaceTypes.assertThat(fileFormat).isEqualTo(FileFormat.CSV);
    }

    @Test
    void validate_returnsTrue_whenHeadersMatch() throws Exception {
        String csv = "wh1,wh2\nvalue1,value2";
        InputStream inputStream = createInputStream(csv);

        boolean valid = strategy.validate(inputStream, warehouseNames);

        assertThat(valid).isTrue();
    }

    @Test
    void validate_returnsFalse_whenHeadersMismatch() throws Exception {
        String csv = "wh5,wh4\nvalue1,value2";
        InputStream inputStream = createInputStream(csv);

        boolean valid = strategy.validate(inputStream, warehouseNames);

        assertThat(valid).isFalse();
    }

    @Test
    void validate_returnsFalse_whenNoHeaders() throws Exception {
        String csv = "";
        InputStream inputStream = createInputStream(csv);

        boolean valid = strategy.validate(inputStream, warehouseNames);

        assertThat(valid).isFalse();
    }
}