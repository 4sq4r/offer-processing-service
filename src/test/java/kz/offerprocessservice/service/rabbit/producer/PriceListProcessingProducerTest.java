package kz.offerprocessservice.service.rabbit.producer;

import kz.offerprocessservice.model.dto.rabbit.RabbitMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static kz.offerprocessservice.configuration.RabbitConfiguration.PROCESSING_EXCHANGE;
import static kz.offerprocessservice.configuration.RabbitConfiguration.PROCESSING_ROUTING_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static util.Data.PRICE_LIST_ID;

@ExtendWith(MockitoExtension.class)
class PriceListProcessingProducerTest {

    @Mock
    private RabbitTemplate template;

    @InjectMocks
    private PriceListProcessingProducer producer;

    @Test
    void sendToProcessing_sendsMessage() {
        //when
        ArgumentCaptor<RabbitMessage> messageArgumentCaptor = ArgumentCaptor.forClass(RabbitMessage.class);
        //then
        producer.sendToProcessing(PRICE_LIST_ID);
        //when
        verify(template).convertAndSend(
                eq(PROCESSING_EXCHANGE),
                eq(PROCESSING_ROUTING_KEY),
                messageArgumentCaptor.capture()
        );
        RabbitMessage sendMessage = messageArgumentCaptor.getValue();
        assertThat(sendMessage).isNotNull();
        assertThat(sendMessage.getPriceListId()).isEqualTo(PRICE_LIST_ID);
    }

}