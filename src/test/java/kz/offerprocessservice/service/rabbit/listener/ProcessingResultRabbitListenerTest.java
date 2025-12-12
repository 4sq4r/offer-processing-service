package kz.offerprocessservice.service.rabbit.listener;

import kz.offerprocessservice.file.processing.ProcessingResultStatus;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.dto.rabbit.ProcessingResultMessage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import util.Data;

import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ProcessingResultRabbitListenerTest extends AbstractRabbitListenerTest<ProcessingResultRabbitListener, ProcessingResultMessage> {

    @Override
    protected ProcessingResultRabbitListener createListener() {
        return new ProcessingResultRabbitListener(stateMachineService);
    }

    @Override
    protected void handle(ProcessingResultMessage processingResultMessage) {
        listener.handle(processingResultMessage);
    }

    @ParameterizedTest
    @MethodSource("arguments")
    void handle_throwsCustomException(ProcessingResultStatus status, PriceListEvent event) {
        //given
        ProcessingResultMessage message = new ProcessingResultMessage(Data.PRICE_LIST_ID, status);
        //when
        listener.handle(message);
        //then
        verify(stateMachineService, times(1)).sendEvent(Data.PRICE_LIST_ID, event);
    }

    private static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of(ProcessingResultStatus.PARTIALLY, PriceListEvent.PROCESSING_PARTIALLY_SUCCESS),
                Arguments.of(ProcessingResultStatus.SUCCESS, PriceListEvent.PROCESSING_SUCCESS),
                Arguments.of(ProcessingResultStatus.FAIL, PriceListEvent.PROCESSING_ERROR)
        );
    }
}