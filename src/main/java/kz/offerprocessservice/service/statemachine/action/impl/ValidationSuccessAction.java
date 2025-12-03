package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListState;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.rabbit.producer.PriceListProcessingProducer;
import kz.offerprocessservice.service.statemachine.action.ActionNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ActionNames.VALIDATION_SUCCESS)
@RequiredArgsConstructor
public class ValidationSuccessAction extends PriceListAction {

    private final PriceListProcessingProducer priceListProcessingProducer;
    private final PriceListService priceListService;

    @Override
    public void doExecute(String priceListId, StateContext<PriceListState, PriceListEvent> context) {
        PriceListEntity ple = priceListService.findEntityById(priceListId);
        ple.setStatus(PriceListState.VALIDATED);
        priceListService.updateOne(ple);
        priceListProcessingProducer.sendToProcessing(priceListId);
    }
}
