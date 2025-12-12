package kz.offerprocessservice.service.rabbit.producer;

import kz.offerprocessservice.file.processing.ProcessingResultStatus;
import kz.offerprocessservice.model.dto.rabbit.ProcessingResultMessage;
import kz.offerprocessservice.model.dto.rabbit.RabbitMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.stream.Stream;

import static kz.offerprocessservice.configuration.rabbit.RabbitConfiguration.PROCESSING_EXCHANGE;
import static kz.offerprocessservice.configuration.rabbit.RabbitConfiguration.PROCESSING_RESULT_EXCHANGE;
import static kz.offerprocessservice.configuration.rabbit.RabbitConfiguration.PROCESSING_RESULT_ROUTING_KEY;
import static kz.offerprocessservice.configuration.rabbit.RabbitConfiguration.PROCESSING_ROUTING_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static util.Data.PRICE_LIST_ID;

@ExtendWith(MockitoExtension.class)
class PriceListProcessingProducerTest {

    @Mock
    private RabbitTemplate template;

    @InjectMocks
    private PriceListProcessingProducer underTest;

    @Test
    void sendToProcessing_sendsMessage() {
        //when
        ArgumentCaptor<RabbitMessage> messageArgumentCaptor = ArgumentCaptor.forClass(RabbitMessage.class);
        //then
        underTest.sendToProcessing(PRICE_LIST_ID);
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

    @ParameterizedTest
    @MethodSource("argumentsFor_sendToProcessingResult_sendsMessage")
    void sendToProcessingResult_sendsMessage(ProcessingResultStatus status) {
        //given
        ArgumentCaptor<ProcessingResultMessage> messageArgumentCaptor = ArgumentCaptor.forClass(
                ProcessingResultMessage.class);
        //when
        underTest.sendProcessingResult(PRICE_LIST_ID, status);
        //then
        verify(template).convertAndSend(
                eq(PROCESSING_RESULT_EXCHANGE),
                eq(PROCESSING_RESULT_ROUTING_KEY),
                messageArgumentCaptor.capture()
        );
        ProcessingResultMessage sendMessage = messageArgumentCaptor.getValue();
        assertThat(sendMessage).isNotNull();
        assertThat(sendMessage.getPriceListId()).isEqualTo(PRICE_LIST_ID);
        assertThat(sendMessage.getStatus()).isEqualTo(status);
    }

    private static Stream<ProcessingResultStatus> argumentsFor_sendToProcessingResult_sendsMessage() {
        return Arrays.stream(ProcessingResultStatus.values());
    }

}