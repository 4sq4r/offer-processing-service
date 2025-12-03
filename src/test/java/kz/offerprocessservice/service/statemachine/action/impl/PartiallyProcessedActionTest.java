package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.service.statemachine.action.AbstractPriceListActionTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;
import static kz.offerprocessservice.model.PriceListStatus.PARTIALLY_PROCESSED;
import static kz.offerprocessservice.model.PriceListStatus.PROCESSING;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    void execute_callsDoExecute() throws CustomException {
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
}