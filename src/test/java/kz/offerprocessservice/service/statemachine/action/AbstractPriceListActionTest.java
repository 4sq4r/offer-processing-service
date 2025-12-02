package kz.offerprocessservice.service.statemachine.action;

import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListState;
import kz.offerprocessservice.service.PriceListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.state.State;

@ExtendWith(MockitoExtension.class)
public abstract class AbstractPriceListActionTest<T extends PriceListAction> {

    @Mock
    protected PriceListService priceListService;

    @Mock
    protected StateContext<PriceListState, PriceListEvent> context;

    @Mock
    protected Message<PriceListEvent> message;


    @Mock
    protected State<PriceListState, PriceListEvent> sourceState;

    @Mock
    protected State<PriceListState, PriceListEvent> targetState;

    protected T action;

    @BeforeEach
    void setUp() {
        action = createAction();
    }

    protected abstract T createAction();
}
