package kz.offerprocessservice.service.rabbit;

import kz.offerprocessservice.configuration.RabbitConfiguration;
import kz.offerprocessservice.model.dto.rabbit.PriceListMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceListProducer {

    private final RabbitTemplate template;

    public void sendToValidation(UUID priceListId) {
        log.info("Sending price list {} to validation queue.", priceListId);

        template.convertAndSend(
                RabbitConfiguration.PRICE_LIST_EXCHANGE,
                RabbitConfiguration.VALIDATION_ROUTING_KEY,
                new PriceListMessage(priceListId)
        );
    }

    public void sendToProcessing(UUID priceListId) {
        log.info("Sending price list {} to processing queue.", priceListId);

        template.convertAndSend(
                RabbitConfiguration.PRICE_LIST_EXCHANGE,
                RabbitConfiguration.PROCESSING_ROUTING_KEY,
                new PriceListMessage(priceListId)
        );
    }
}
