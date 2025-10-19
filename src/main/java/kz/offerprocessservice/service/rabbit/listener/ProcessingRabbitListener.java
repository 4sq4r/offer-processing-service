package kz.offerprocessservice.service.rabbit.listener;

import kz.offerprocessservice.configuration.RabbitConfiguration;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.dto.rabbit.RabbitMessage;
import kz.offerprocessservice.service.statemachine.PriceListStateMachineService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProcessingRabbitListener extends AbstractPriceListRabbitListener {

    public ProcessingRabbitListener(PriceListStateMachineService priceListStateMachineService) {
        super(priceListStateMachineService);
    }

    @RabbitListener(queues = RabbitConfiguration.PROCESSING_QUEUE)
    void handle(RabbitMessage message) throws CustomException {
        priceListStateMachineService.sendEvent(message.getPriceListId(), PriceListEvent.START_PROCESSING);
    }
}
