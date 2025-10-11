package kz.offerprocessservice.statemachine;

public enum PriceListState {
    UPLOADED,
    VALIDATION,
    VALIDATED,
    PROCESSING,
    PARTIALLY_PROCESSED,
    PROCESSED,
    VALIDATION_FAILED,
    PROCESSING_FAILED
}