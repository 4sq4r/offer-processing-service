package kz.offerprocessservice.file;

import kz.offerprocessservice.file.processing.impl.CsvProcessingStrategyImpl;
import kz.offerprocessservice.file.processing.impl.ExcelProcessingStrategyImpl;
import kz.offerprocessservice.file.processing.impl.XmlProcessingStrategyImpl;
import kz.offerprocessservice.file.templating.impl.CsvTemplatingStrategyImpl;
import kz.offerprocessservice.file.templating.impl.ExcelTemplatingStrategyImpl;
import kz.offerprocessservice.file.templating.impl.XmlTemplatingStrategyImpl;
import kz.offerprocessservice.file.validation.impl.CsvValidationStrategyImpl;
import kz.offerprocessservice.file.validation.impl.ExcelValidationStrategyImpl;
import kz.offerprocessservice.file.validation.impl.XmlValidationStrategyImpl;
import kz.offerprocessservice.model.enums.FileFormat;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileStrategyProviderImplTest {

    private FileStrategyProviderImpl provider;

    @BeforeEach
    void setUp() {
        provider = new FileStrategyProviderImpl(
                List.of(
                        new CsvTemplatingStrategyImpl(),
                        new XmlTemplatingStrategyImpl(),
                        new ExcelTemplatingStrategyImpl()
                ),
                List.of(
                        new CsvValidationStrategyImpl(),
                        new XmlValidationStrategyImpl(),
                        new ExcelValidationStrategyImpl()
                ),
                List.of(
                        new CsvProcessingStrategyImpl(),
                        new XmlProcessingStrategyImpl(),
                        new ExcelProcessingStrategyImpl()
                )
        );
    }

    @Test
    void getTemplatingStrategy_throwsException_whenStrategyNotFound() {
        FileStrategyProviderImpl testProvider = new FileStrategyProviderImpl(
                List.of(),
                List.of(),
                List.of()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testProvider.getTemplatingStrategy(FileFormat.XML)
        );

        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Strategy not found for format: XML");
    }

    @ParameterizedTest
    @MethodSource("argumentsFor_getTemplatingStrategy")
    void getTemplatingStrategy_returnsStrategy(Class<T> tClass, FileFormat format) {
        assertInstanceOf(tClass, provider.getTemplatingStrategy(format));
    }

    private static Stream<Arguments> argumentsFor_getTemplatingStrategy() {
        return Stream.of(
                Arguments.of(CsvTemplatingStrategyImpl.class, FileFormat.CSV),
                Arguments.of(XmlTemplatingStrategyImpl.class, FileFormat.XML),
                Arguments.of(ExcelTemplatingStrategyImpl.class, FileFormat.EXCEL)
        );
    }

    @Test
    void getValidationStrategy_throwsException_whenStrategyNotFound() {
        FileStrategyProviderImpl testProvider = new FileStrategyProviderImpl(
                List.of(),
                List.of(),
                List.of()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testProvider.getValidationStrategy(FileFormat.XML)
        );

        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Strategy not found for format: XML");
    }

    @ParameterizedTest
    @MethodSource("argumentsFor_getValidationStrategy")
    void getValidationStrategy(Class<T> tClass, FileFormat format) {
        assertInstanceOf(tClass, provider.getValidationStrategy(format));
    }

    private static Stream<Arguments> argumentsFor_getValidationStrategy() {
        return Stream.of(
                Arguments.of(CsvValidationStrategyImpl.class, FileFormat.CSV),
                Arguments.of(XmlValidationStrategyImpl.class, FileFormat.XML),
                Arguments.of(ExcelValidationStrategyImpl.class, FileFormat.EXCEL)
        );
    }

    @Test
    void getProcessingStrategy_throwsException_whenStrategyNotFound() {
        FileStrategyProviderImpl testProvider = new FileStrategyProviderImpl(
                List.of(),
                List.of(),
                List.of()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testProvider.getProcessingStrategy(FileFormat.XML)
        );

        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Strategy not found for format: XML");
    }

    @ParameterizedTest
    @MethodSource("argumentsFor_getProcessingStrategy")
    void getProcessingStrategy(Class<T> tClass, FileFormat format) {
        assertInstanceOf(tClass, provider.getProcessingStrategy(format));
    }

    private static Stream<Arguments> argumentsFor_getProcessingStrategy() {
        return Stream.of(
                Arguments.of(CsvProcessingStrategyImpl.class, FileFormat.CSV),
                Arguments.of(XmlProcessingStrategyImpl.class, FileFormat.XML),
                Arguments.of(ExcelProcessingStrategyImpl.class, FileFormat.EXCEL)
        );
    }
}