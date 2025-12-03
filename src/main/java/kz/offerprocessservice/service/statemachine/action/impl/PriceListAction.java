package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.exception.PriceListActionException;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;

@Slf4j
public abstract class PriceListAction implements Action<PriceListState, PriceListEvent> {

    @Override
    public void execute(StateContext<PriceListState, PriceListEvent> context) {
        String priceListId = String.valueOf(context.getMessageHeader(PRICE_LIST_ID_HEADER));
        try {
            doExecute(priceListId, context);
        } catch (Exception e) {
            log.error(
                    "PriceListAction error. action={}, id={}, from{}, to={}",
                    this.getClass().getSimpleName(),
                    priceListId,
                    context.getSource() != null ? context.getSource().getId() : "null",
                    context.getTarget() != null ? context.getTarget().getId() : "null",
                    e
            );

            throw new PriceListActionException(priceListId, e);
        }
    }

    protected abstract void doExecute(String priceListId, StateContext<PriceListState, PriceListEvent> context)
            throws CustomException;
}
