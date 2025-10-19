package kz.offerprocessservice.service.statemachine.action;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActionNames {

    public static final String START_VALIDATION = "START_VALIDATION";

    public static final String VALIDATION_SUCCESS = "VALIDATION_SUCCESS";

    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";

    public static final String START_PROCESSING = "START_PROCESSING";

    public static final String PARTIALLY_PROCESSED = "PARTIALLY_PROCESSED";

    public static final String PROCESSING_FAILED = "PROCESSING_FAILED";

    public static final String PROCESSED = "PROCESSED";
}
