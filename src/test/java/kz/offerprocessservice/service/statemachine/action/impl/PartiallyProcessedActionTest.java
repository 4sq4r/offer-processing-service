package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.service.statemachine.action.AbstractPriceListActionTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;
import static kz.offerprocessservice.model.PriceListState.PARTIALLY_PROCESSED;
import static kz.offerprocessservice.model.PriceListState.PROCESSING;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Data.PRICE_LIST_ID;

@ExtendWith(MockitoExtension.class)
class PartiallyProcessedActionTest extends AbstractPriceListActionTest<PartiallyProcessedAction> {

    @Override
    protected PartiallyProcessedAction createAction() {
        return new PartiallyProcessedAction(priceListService);
    }

    @Test
    void execute_shouldCallDoExecuteAndUpdateStatus() throws CustomException {
        // given
        PriceListEntity entity = new PriceListEntity();
        entity.setStatus(PROCESSING);
        when(priceListService.findEntityById(PRICE_LIST_ID)).thenReturn(entity);
        when(context.getMessageHeader(PRICE_LIST_ID_HEADER)).thenReturn(PRICE_LIST_ID);
        // when
        action.execute(context);
        // then
        assertThat(entity.getStatus()).isEqualTo(PARTIALLY_PROCESSED);
        verify(priceListService).updateOne(entity);
    }

    @Test
    void doExecute_shouldHandleCustomExceptionGracefully() throws CustomException {
        //when
        when(context.getSource()).thenReturn(sourceState);
        when(context.getTarget()).thenReturn(targetState);
        when(sourceState.getId()).thenReturn(PROCESSING);
        when(targetState.getId()).thenReturn(PARTIALLY_PROCESSED);
        doThrow(CustomException.builder()
                        .message("fail")
                        .build()).when(priceListService).findEntityById(PRICE_LIST_ID);
        //when
        assertDoesNotThrow(() -> action.doExecute(PRICE_LIST_ID, context));
        //then
        verify(priceListService, never()).updateOne(any());
    }
}