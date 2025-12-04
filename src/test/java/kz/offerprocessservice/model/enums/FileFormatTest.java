package kz.offerprocessservice.model.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileFormatTest {

    @ParameterizedTest
    @MethodSource("argumentsFor_fromExtension_throwsException_whenExtensionNotFound")
    void fromExtension_throwsException_whenExtensionNotFound(String extension) {
        assertThrows(IllegalArgumentException.class, () -> FileFormat.fromExtension(extension));
    }

    private static Stream<String> argumentsFor_fromExtension_throwsException_whenExtensionNotFound() {
        return Stream.of(
                null,
                " ",
                "asd"
        );
    }

    @ParameterizedTest
    @MethodSource("argumentsFor_fromExtension_returnsFileFormat")
    void fromExtension_returnsFileFormat(String extension, FileFormat expected) {
        FileFormat actual = FileFormat.fromExtension(extension);
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> argumentsFor_fromExtension_returnsFileFormat() {
        return Stream.of(
                Arguments.of(".xlsx", FileFormat.EXCEL),
                Arguments.of(".xml", FileFormat.XML),
                Arguments.of(".csv", FileFormat.CSV)
        );
    }
}