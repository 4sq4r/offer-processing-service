package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.PriceListState;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.service.statemachine.action.AbstractPriceListActionTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;
import static kz.offerprocessservice.model.PriceListState.VALIDATION_FAILED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Data.PRICE_LIST_ID;

@ExtendWith(MockitoExtension.class)
class ValidationErrorActionTest extends AbstractPriceListActionTest<ValidationErrorAction> {

    @Override
    protected ValidationErrorAction createAction() {
        return new ValidationErrorAction(priceListService);
    }

    @Test
    void execute_callsDoExecute() throws CustomException {
        //given
        PriceListEntity priceListEntity = new PriceListEntity();
        priceListEntity.setStatus(PriceListState.VALIDATION);
        when(priceListService.findEntityById(PRICE_LIST_ID)).thenReturn(priceListEntity);
        when(context.getMessageHeader(PRICE_LIST_ID_HEADER)).thenReturn(PRICE_LIST_ID);
        //when
        action.execute(context);
        //then
        assertThat(priceListEntity.getStatus()).isEqualTo(VALIDATION_FAILED);
        verify(priceListService).updateOne(priceListEntity);
    }
}