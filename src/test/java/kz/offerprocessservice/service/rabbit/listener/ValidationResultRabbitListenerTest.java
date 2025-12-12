package kz.offerprocessservice.service.rabbit.listener;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.dto.rabbit.ResultMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static util.Data.PRICE_LIST_ID;

class ValidationResultRabbitListenerTest extends AbstractRabbitListenerTest<ValidationResultRabbitListener, ResultMessage> {

    @Override
    protected ValidationResultRabbitListener createListener() {
        return new ValidationResultRabbitListener(stateMachineService);
    }

    @Override
    protected void handle(ResultMessage validationResultMessage) {
        listener.handle(validationResultMessage);
    }

    @ParameterizedTest
    @MethodSource("argumentsFor_handle")
    void handle(boolean isSuccess, PriceListEvent sendEvent, PriceListEvent neverSendEvent) throws CustomException {
        // given
        ResultMessage message = new ResultMessage(PRICE_LIST_ID, isSuccess);
        // when
        listener.handle(message);
        // then
        verify(stateMachineService, times(1))
                .sendEvent(PRICE_LIST_ID, sendEvent);
        verify(stateMachineService, never())
                .sendEvent(PRICE_LIST_ID, neverSendEvent);
    }

    private static Stream<Arguments> argumentsFor_handle() {
        return Stream.of(
                Arguments.of(true, PriceListEvent.VALIDATION_SUCCESS, PriceListEvent.VALIDATION_ERROR),
                Arguments.of(false, PriceListEvent.VALIDATION_ERROR, PriceListEvent.VALIDATION_SUCCESS)
        );
    }

    @Test
    void handle_throwsCustomException() throws CustomException {
        // given
        ResultMessage message = new ResultMessage(PRICE_LIST_ID, true);
        doThrow(new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "fail"))
                .when(stateMachineService).sendEvent(PRICE_LIST_ID, PriceListEvent.VALIDATION_SUCCESS);

        // then
        assertThrows(CustomException.class, () -> listener.handle(message));
    }

}