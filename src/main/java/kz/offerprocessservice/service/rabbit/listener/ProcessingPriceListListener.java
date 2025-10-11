package kz.offerprocessservice.service.rabbit.listener;

import kz.offerprocessservice.configuration.RabbitConfiguration;
import kz.offerprocessservice.model.dto.rabbit.PriceListMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingPriceListListener extends AbstractPriceListListener {

    @Override
    @RabbitListener(queues = RabbitConfiguration.PROCESSING_QUEUE)
    void handle(PriceListMessage message) {
        log.info("Received processing request for {}", message.getPriceListId());
    }
}
