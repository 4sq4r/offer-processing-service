package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListState;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.statemachine.action.ActionNames;
import kz.offerprocessservice.service.statemachine.action.PriceListAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component(ActionNames.PARTIALLY_PROCESSED)
public class PartiallyProcessedAction implements PriceListAction {

    private final PriceListService priceListService;

    public PartiallyProcessedAction(PriceListService priceListService) {
        this.priceListService = priceListService;
    }

    @Override
    public void doExecute(String priceListId, StateContext<PriceListState, PriceListEvent> context) {
        try {
            PriceListEntity priceListEntity = priceListService.findEntityById(priceListId);
            priceListEntity.setStatus(PriceListState.PARTIALLY_PROCESSED);
            priceListService.updateOne(priceListEntity);
        } catch (CustomException e) {
            log.error(
                    "Error while change price list status from {} to {}",
                    context.getSource().getId(),
                    context.getTarget().getId()
            );
        }
    }
}
