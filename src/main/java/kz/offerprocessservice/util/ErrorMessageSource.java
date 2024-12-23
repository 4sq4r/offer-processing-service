package kz.offerprocessservice.util;

public enum ErrorMessageSource {
    MINIO_BUCKET_NOT_EXIST("Minio bucket not exists."),
    CITY_ALREADY_EXISTS("City already exists: %s"),
    CITY_NOT_FOUND("City not found: %s");

    private String text;

    ErrorMessageSource(String text) {
        this.text = text;
    }

    public String getText(String... params) {
        return String.format(this.text, params);
    }
}
