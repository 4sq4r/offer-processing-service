package kz.offerprocessservice.service.statemachine.action;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActionNames {

    public static final String START_VALIDATION = "START_VALIDATION";

    public static final String VALIDATION_SUCCESS = "VALIDATION_SUCCESS";

}
