package kz.offerprocessservice.model;

public enum PriceListStatus {
    UPLOADED,
    VALIDATION,
    VALIDATED,
    PROCESSING,
    PARTIALLY_PROCESSED,
    PROCESSED,
    VALIDATION_FAILED,
    PROCESSING_FAILED
}