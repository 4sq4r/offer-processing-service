package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListStatus;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.statemachine.action.ActionNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ActionNames.PROCESSING_FAILED)
public class ProcessingFailedAction extends PriceListAction {

    public ProcessingFailedAction(PriceListService priceListService) {
        super(priceListService);
    }

    @Override
    protected void doExecute(String priceListId, StateContext<PriceListStatus, PriceListEvent> context) {
        updatePriceListStatus(priceListId, PriceListStatus.PROCESSING_FAILED);
    }
}
