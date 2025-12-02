package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListState;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.statemachine.action.ActionNames;
import kz.offerprocessservice.service.statemachine.action.PriceListAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ActionNames.VALIDATION_ERROR)
@RequiredArgsConstructor
public class ValidationErrorAction implements PriceListAction {

    private final PriceListService priceListService;

    @Override
    public void doExecute(String priceListId, StateContext<PriceListState, PriceListEvent> context) {
        try {
            PriceListEntity ple = priceListService.findEntityById(priceListId);
            ple.setStatus(PriceListState.VALIDATION_FAILED);
            priceListService.updateOne(ple);
        } catch (CustomException e) {
            log.error("Error processing file upload event: {}", e.getMessage());
        }
    }
}
