package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListStatus;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.statemachine.action.ActionNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = ActionNames.VALIDATION_ERROR)
public class ValidationErrorAction extends PriceListAction {

    public ValidationErrorAction(PriceListService priceListService) {
        super(priceListService);
    }

    @Override
    public void doExecute(String priceListId, StateContext<PriceListStatus, PriceListEvent> context) {
        updatePriceListStatus(priceListId, PriceListStatus.VALIDATION_FAILED);
    }
}
