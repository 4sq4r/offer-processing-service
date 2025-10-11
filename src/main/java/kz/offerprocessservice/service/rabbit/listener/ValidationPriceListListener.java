package kz.offerprocessservice.service.rabbit.listener;

import kz.offerprocessservice.configuration.RabbitConfiguration;
import kz.offerprocessservice.model.dto.rabbit.PriceListMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class ValidationPriceListListener extends AbstractPriceListListener {

    @Override
    @RabbitListener(queues = RabbitConfiguration.VALIDATION_QUEUE)
    public void handle(PriceListMessage message) {
        log.info("Received validation request for {}", message.getPriceListId());
    }
}
