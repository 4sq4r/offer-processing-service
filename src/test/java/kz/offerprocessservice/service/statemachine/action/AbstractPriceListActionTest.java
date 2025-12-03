package kz.offerprocessservice.service.statemachine.action;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.exception.PriceListActionException;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListState;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.statemachine.action.impl.PriceListAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.state.State;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static util.Data.PRICE_LIST_ID;

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

    @Test
    void execute_wrapsCustomExceptionInPriceListActionException() {
        // given
        when(context.getMessageHeader(PRICE_LIST_ID_HEADER)).thenReturn(PRICE_LIST_ID);
        doThrow(new CustomException(HttpStatus.BAD_REQUEST, "fail"))
                .when(priceListService).findEntityById(PRICE_LIST_ID);

        when(context.getSource()).thenReturn(sourceState);
        when(context.getTarget()).thenReturn(targetState);
        when(sourceState.getId()).thenReturn(PriceListState.UPLOADED);
        when(targetState.getId()).thenReturn(PriceListState.PROCESSING);

        // when
        PriceListActionException exception = assertThrows(
                PriceListActionException.class,
                () -> action.execute(context)
        );

        // then
        assertThat(exception.getCause()).isInstanceOf(CustomException.class);
        assertThat(exception.getPriceListId()).isEqualTo(PRICE_LIST_ID);
    }
}
