package kz.offerprocessservice.service.rabbit.listener;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.dto.rabbit.RabbitMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static util.Data.PRICE_LIST_ID;

class ValidationRabbitListenerTest extends AbstractRabbitListenerTest<ValidationRabbitListener, RabbitMessage> {

    @Override
    protected ValidationRabbitListener createListener() {
        return new ValidationRabbitListener(stateMachineService);
    }

    @Override
    protected void handle(RabbitMessage message) throws Exception {
        listener.handle(message);
    }

    @Test
    void handle_throwsCustomException() throws CustomException {
        // given
        RabbitMessage message = new RabbitMessage();
        message.setPriceListId(PRICE_LIST_ID);
        doThrow(CustomException.builder()
                .message("fail")
                .build()).when(stateMachineService)
                .sendEvent(PRICE_LIST_ID, PriceListEvent.START_VALIDATION);
        // then
        assertThrows(CustomException.class, () -> listener.handle(message));
    }

    @Test
    void handle_sendsEvent() throws CustomException {
        // given
        RabbitMessage message = new RabbitMessage();
        message.setPriceListId(PRICE_LIST_ID);
        // when
        listener.handle(message);
        // then
        verify(stateMachineService, times(1)).sendEvent(PRICE_LIST_ID, PriceListEvent.START_VALIDATION);
    }

}