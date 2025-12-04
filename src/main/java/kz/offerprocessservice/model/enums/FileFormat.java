package kz.offerprocessservice.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FileFormat {

    XML(".xml"),
    CSV(".csv"),
    EXCEL(".xlsx"),
    ;
    private final String extension;

    FileFormat(String extension) {
        this.extension = extension;
    }

    public static FileFormat fromExtension(String extension) {
        return Arrays.stream(values())
                .filter(ext -> ext.getExtension().equals(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported file: " + extension));
    }
}
