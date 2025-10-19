package kz.offerprocessservice.service.rabbit.listener;

import kz.offerprocessservice.configuration.RabbitConfiguration;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.dto.rabbit.ValidationResultMessage;
import kz.offerprocessservice.service.statemachine.PriceListStateMachineService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ValidationResultRabbitListener extends AbstractPriceListRabbitListener {

    public ValidationResultRabbitListener(PriceListStateMachineService priceListStateMachineService) {
        super(priceListStateMachineService);
    }

    @RabbitListener(queues = RabbitConfiguration.VALIDATION_RESULT_QUEUE)
    void handle(ValidationResultMessage message) throws CustomException {
        String id = message.getPriceListId();
        if (message.isSuccess()) {
            priceListStateMachineService.sendEvent(id, PriceListEvent.VALIDATION_SUCCESS);
        } else {
            priceListStateMachineService.sendEvent(id, PriceListEvent.VALIDATION_ERROR);
        }
    }
}
