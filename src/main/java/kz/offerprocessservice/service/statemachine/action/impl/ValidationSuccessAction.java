package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListStatus;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.rabbit.producer.PriceListProcessingProducer;
import kz.offerprocessservice.service.statemachine.action.ActionNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ActionNames.VALIDATION_SUCCESS)
public class ValidationSuccessAction extends PriceListAction {

    private final PriceListProcessingProducer priceListProcessingProducer;

    public ValidationSuccessAction(
            PriceListService priceListService,
            PriceListProcessingProducer priceListProcessingProducer
    ) {
        super(priceListService);
        this.priceListProcessingProducer = priceListProcessingProducer;
    }

    @Override
    public void doExecute(String priceListId, StateContext<PriceListStatus, PriceListEvent> context) {
        updatePriceListStatus(priceListId, PriceListStatus.VALIDATED);
        priceListProcessingProducer.sendToProcessing(priceListId);
    }
}
