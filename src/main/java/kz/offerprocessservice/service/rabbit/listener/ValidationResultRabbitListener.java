package kz.offerprocessservice.service.rabbit.listener;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.rabbit.ValidationResultMessage;
import kz.offerprocessservice.service.statemachine.PriceListStateMachineService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static kz.offerprocessservice.configuration.rabbit.RabbitConfiguration.VALIDATION_RESULT_QUEUE;
import static kz.offerprocessservice.model.PriceListEvent.VALIDATION_ERROR;
import static kz.offerprocessservice.model.PriceListEvent.VALIDATION_SUCCESS;

@Component
public class ValidationResultRabbitListener extends AbstractPriceListRabbitListener {

    public ValidationResultRabbitListener(PriceListStateMachineService priceListStateMachineService) {
        super(priceListStateMachineService);
    }

    @RabbitListener(queues = VALIDATION_RESULT_QUEUE)
    void handle(ValidationResultMessage message) throws CustomException {
        String id = message.getPriceListId();
        if (message.isSuccess()) {
            priceListStateMachineService.sendEvent(id, VALIDATION_SUCCESS);
        } else {
            priceListStateMachineService.sendEvent(id, VALIDATION_ERROR);
        }
    }
}
