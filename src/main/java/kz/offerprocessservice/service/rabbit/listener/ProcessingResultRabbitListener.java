package kz.offerprocessservice.service.rabbit.listener;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.file.processing.ProcessingResultStatus;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.dto.rabbit.ProcessingResultMessage;
import kz.offerprocessservice.service.statemachine.PriceListStateMachineService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

import static kz.offerprocessservice.configuration.rabbit.RabbitConfiguration.PROCESSING_RESULT_QUEUE;

@Component
public class ProcessingResultRabbitListener extends AbstractPriceListRabbitListener {
    public ProcessingResultRabbitListener(PriceListStateMachineService priceListStateMachineService) {
        super(priceListStateMachineService);
    }

    private static final Map<ProcessingResultStatus, PriceListEvent> STATUS_EVENT_MAP = Map.of(
            ProcessingResultStatus.SUCCESS, PriceListEvent.PROCESSING_SUCCESS,
            ProcessingResultStatus.PARTIALLY, PriceListEvent.PROCESSING_PARTIALLY_SUCCESS,
            ProcessingResultStatus.FAIL, PriceListEvent.PROCESSING_ERROR
    );

    @RabbitListener(queues = PROCESSING_RESULT_QUEUE)
    void handle(ProcessingResultMessage message) throws CustomException {
        log.info("Received processing result message: {} and {}", message.getPriceListId(), message.getStatus());
        PriceListEvent event = STATUS_EVENT_MAP.get(message.getStatus());

        if (event != null) {
            priceListStateMachineService.sendEvent(message.getPriceListId(), event);
        }
    }
}
