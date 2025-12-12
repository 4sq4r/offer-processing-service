package kz.offerprocessservice.service.rabbit.producer;

import kz.offerprocessservice.model.dto.rabbit.RabbitMessage;
import kz.offerprocessservice.model.dto.rabbit.ResultMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.stream.Stream;

import static kz.offerprocessservice.configuration.RabbitConfiguration.VALIDATION_EXCHANGE;
import static kz.offerprocessservice.configuration.RabbitConfiguration.VALIDATION_RESULT_EXCHANGE;
import static kz.offerprocessservice.configuration.RabbitConfiguration.VALIDATION_RESULT_ROUTING_KEY;
import static kz.offerprocessservice.configuration.RabbitConfiguration.VALIDATION_ROUTING_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static util.Data.PRICE_LIST_ID;

@ExtendWith(MockitoExtension.class)
class PriceListValidationRabbitProducerTest {

    @Mock
    private RabbitTemplate template;

    @InjectMocks
    private PriceListValidationRabbitProducer underTest;

    @Test
    void sendToValidation_sendsMessage() {
        //when
        ArgumentCaptor<RabbitMessage> messageArgumentCaptor = ArgumentCaptor.forClass(RabbitMessage.class);
        //then
        underTest.sendToValidation(PRICE_LIST_ID);
        //when
        verify(template).convertAndSend(
                eq(VALIDATION_EXCHANGE),
                eq(VALIDATION_ROUTING_KEY),
                messageArgumentCaptor.capture()
        );
        RabbitMessage sendMessage = messageArgumentCaptor.getValue();
        assertThat(sendMessage).isNotNull();
        assertThat(sendMessage.getPriceListId()).isEqualTo(PRICE_LIST_ID);
    }

    @ParameterizedTest
    @MethodSource("argumentsFor_sendToValidationResult_sendsMessage")
    void sendToValidationResult_sendsMessage(boolean success) {
        //when
        ArgumentCaptor<ResultMessage> messageArgumentCaptor = ArgumentCaptor.forClass(
                ResultMessage.class);
        //then
        underTest.sendValidationResult(PRICE_LIST_ID, success);
        //when
        verify(template).convertAndSend(
                eq(VALIDATION_RESULT_EXCHANGE),
                eq(VALIDATION_RESULT_ROUTING_KEY),
                messageArgumentCaptor.capture()
        );
        ResultMessage sendMessage = messageArgumentCaptor.getValue();
        assertThat(sendMessage).isNotNull();
        assertThat(sendMessage.getPriceListId()).isEqualTo(PRICE_LIST_ID);
        assertThat(sendMessage.isSuccess()).isEqualTo(success);
    }

    private static Stream<Boolean> argumentsFor_sendToValidationResult_sendsMessage() {
        return Stream.of(
                true,
                false
        );
    }
}