package kz.offerprocessservice.service.statemachine;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListStatus;
import kz.offerprocessservice.service.PriceListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceListStateMachineService {

    private final StateMachineFactory<PriceListStatus, PriceListEvent> stateMachineFactory;
    private final PriceListService priceListService;

    public void sendEvent(String priceListId, PriceListEvent event) throws CustomException {
        PriceListStatus state = priceListService.getCurrentState(priceListId);
        StateMachine<PriceListStatus, PriceListEvent> stateMachine = stateMachineFactory.getStateMachine(priceListId);
        stateMachine.stopReactively().subscribe();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(access -> access.resetStateMachineReactively(
                        new DefaultStateMachineContext<>(
                                state,
                                null,
                                null,
                                null,
                                null,
                                priceListId
                        )
                ).block());
        stateMachine.startReactively().subscribe();
        stateMachine.sendEvent(
                Mono.just(
                        MessageBuilder
                                .withPayload(event)
                                .setHeader(PRICE_LIST_ID_HEADER, priceListId)
                                .build()
                )
        ).subscribe();
    }
}
