package kz.offerprocessservice.service.rabbit.producer;

import kz.offerprocessservice.file.processing.ProcessingResultStatus;
import kz.offerprocessservice.model.dto.rabbit.ProcessingResultMessage;
import kz.offerprocessservice.model.dto.rabbit.RabbitMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static kz.offerprocessservice.configuration.rabbit.RabbitConfiguration.PROCESSING_EXCHANGE;
import static kz.offerprocessservice.configuration.rabbit.RabbitConfiguration.PROCESSING_RESULT_EXCHANGE;
import static kz.offerprocessservice.configuration.rabbit.RabbitConfiguration.PROCESSING_RESULT_ROUTING_KEY;
import static kz.offerprocessservice.configuration.rabbit.RabbitConfiguration.PROCESSING_ROUTING_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceListProcessingProducer {

    private final RabbitTemplate template;

    public void sendToProcessing(String priceListId) {
        log.info("Sending price list {} to processing queue.", priceListId);
        template.convertAndSend(
                PROCESSING_EXCHANGE,
                PROCESSING_ROUTING_KEY,
                new RabbitMessage(priceListId)
        );
    }

    public void sendProcessingResult(String priceListId, ProcessingResultStatus status) {
        log.info("Sending price list processing result: {} to processing result queue.", priceListId);
        template.convertAndSend(
                PROCESSING_RESULT_EXCHANGE,
                PROCESSING_RESULT_ROUTING_KEY,
                new ProcessingResultMessage(priceListId, status)
        );
    }
}
