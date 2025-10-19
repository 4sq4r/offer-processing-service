package kz.offerprocessservice.service.rabbit.producer;

import kz.offerprocessservice.configuration.RabbitConfiguration;
import kz.offerprocessservice.model.dto.rabbit.RabbitMessage;
import kz.offerprocessservice.model.dto.rabbit.ValidationResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceListValidationRabbitProducer {

    private final RabbitTemplate template;

    public void sendToValidation(String priceListId) {
        log.info("Sending price list {} to validation queue.", priceListId);
        template.convertAndSend(
                RabbitConfiguration.VALIDATION_EXCHANGE,
                RabbitConfiguration.VALIDATION_ROUTING_KEY,
                new RabbitMessage(priceListId)
        );
    }

    public void sendValidationResult(String priceListId, boolean success) {
        log.info("Sending price list validation result: {} to validation result queue.", priceListId);
        template.convertAndSend(
                RabbitConfiguration.VALIDATION_RESULT_EXCHANGE,
                RabbitConfiguration.VALIDATION_RESULT_ROUTING_KEY,
                new ValidationResultMessage(priceListId, success)
        );
    }
}
