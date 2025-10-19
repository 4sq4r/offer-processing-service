package kz.offerprocessservice.service.statemachine.action;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.util.UUID;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;

public interface PriceListAction extends Action<PriceListState, PriceListEvent> {

    @Override
    default void execute(StateContext<PriceListState, PriceListEvent> context) {
        UUID priceListId = (UUID) context.getMessageHeader(PRICE_LIST_ID_HEADER);
        try {
            doExecute(priceListId, context);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    void doExecute(UUID priceListId, StateContext<PriceListState, PriceListEvent> context) throws CustomException;
}
