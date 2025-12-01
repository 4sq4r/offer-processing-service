package kz.offerprocessservice.service.rabbit.listener;

import kz.offerprocessservice.service.statemachine.PriceListStateMachineService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.openMocks;

public abstract class AbstractRabbitListenerTest<TListener, TMessage> {

    @Mock
    protected PriceListStateMachineService stateMachineService;

    protected TListener listener;

    @BeforeEach
    void setUp() {
        openMocks(this);
        listener = createListener();
    }

    protected abstract TListener createListener();

    protected abstract void handle(TMessage message) throws Exception;
}
