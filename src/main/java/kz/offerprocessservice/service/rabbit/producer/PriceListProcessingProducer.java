package kz.offerprocessservice.service.rabbit.producer;

import kz.offerprocessservice.configuration.RabbitConfiguration;
import kz.offerprocessservice.model.dto.rabbit.RabbitMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceListProcessingProducer {

    private final RabbitTemplate template;

    public void sendToProcessing(String priceListId) {
        log.info("Sending price list {} to processing queue.", priceListId);
        template.convertAndSend(
                RabbitConfiguration.PROCESSING_EXCHANGE,
                RabbitConfiguration.PROCESSING_ROUTING_KEY,
                new RabbitMessage(priceListId)
        );
    }
}
