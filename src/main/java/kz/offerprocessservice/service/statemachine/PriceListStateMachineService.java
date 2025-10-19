package kz.offerprocessservice.service.statemachine;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListState;
import kz.offerprocessservice.service.PriceListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceListStateMachineService {

    private final StateMachineFactory<PriceListState, PriceListEvent> stateMachineFactory;
    private final PriceListService priceListService;

    public void sendEvent(String priceListId, PriceListEvent event) throws CustomException {
        PriceListState state = priceListService.getCurrentState(priceListId);
        StateMachine<PriceListState, PriceListEvent> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.stopReactively().subscribe();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(access -> {
                    access.resetStateMachineReactively(
                            new DefaultStateMachineContext<>(state, null, null, null, null, priceListId.toString())
                    ).block();
                });
        stateMachine.startReactively().subscribe();
        stateMachine.sendEvent(Mono.just(MessageBuilder
                .withPayload(event)
                .setHeader(PRICE_LIST_ID_HEADER, priceListId)
                .build()
        )).subscribe();
    }
}
