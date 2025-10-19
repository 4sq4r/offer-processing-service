package kz.offerprocessservice.util;

public enum ErrorMessageSource {
    MINIO_BUCKET_NOT_EXIST("Minio bucket not exists."),
    CITY_ALREADY_EXISTS("City already exists: %s"),
    CITY_NOT_FOUND("City not found: %s"),
    MERCHANT_NOT_FOUND("Merchant not found: %s"),
    MERCHANT_ALREADY_EXISTS("Merchant already exists: %s"),
    POINT_OF_SALE_ALREADY_EXISTS("Point of sale already exists: %s"),
    POINT_OF_SALE_NOT_FOUND("Point of sale not found: %s"),
    SKU_NOT_FOUND("Sku not found: %s"),
    PRICE_LIST_NOT_FOUND("Price list not found: %s");

    private final String text;

    ErrorMessageSource(String text) {
        this.text = text;
    }

    public String getText(String... params) {
        return String.format(this.text, (Object[]) params);
    }
}
