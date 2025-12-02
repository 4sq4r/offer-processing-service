package kz.offerprocessservice.service.statemachine.action;

import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;

public interface PriceListAction extends Action<PriceListState, PriceListEvent> {

    @Override
    default void execute(StateContext<PriceListState, PriceListEvent> context){
        doExecute(context.getMessageHeader(PRICE_LIST_ID_HEADER).toString(), context);
    }

    void doExecute(String priceListId, StateContext<PriceListState, PriceListEvent> context);
}
