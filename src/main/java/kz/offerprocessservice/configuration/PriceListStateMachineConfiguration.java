package kz.offerprocessservice.configuration;

import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListState;
import kz.offerprocessservice.service.statemachine.action.ActionNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Map;

@Slf4j
@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class PriceListStateMachineConfiguration extends EnumStateMachineConfigurerAdapter<PriceListState, PriceListEvent> {

    public static final String PRICE_LIST_ID_HEADER = "priceListId";

    private final Map<String, Action<PriceListState, PriceListEvent>> actions;

    @Override
    public void configure(StateMachineConfigurationConfigurer<PriceListState, PriceListEvent> config) throws Exception {
        config.withConfiguration()
                .listener(loggingListener())
                .autoStartup(false);
    }

    @Override
    public void configure(StateMachineStateConfigurer<PriceListState, PriceListEvent> states) throws Exception {
        states.withStates()
                .initial(PriceListState.UPLOADED)
                .end(PriceListState.PROCESSED)
                .end(PriceListState.VALIDATION_FAILED)
                .end(PriceListState.PROCESSING_FAILED)
                .states(EnumSet.allOf(PriceListState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PriceListState, PriceListEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PriceListState.UPLOADED)
                .target(PriceListState.VALIDATION)
                .event(PriceListEvent.START_VALIDATION)
                .action(getAction(ActionNames.START_VALIDATION))
                .and()
                .withExternal()
                .source(PriceListState.VALIDATION)
                .target(PriceListState.VALIDATED)
                .event(PriceListEvent.VALIDATION_SUCCESS)
                .action(getAction(ActionNames.VALIDATION_SUCCESS))
                .and()
                .withExternal()
                .source(PriceListState.VALIDATION)
                .target(PriceListState.VALIDATION_FAILED)
                .event(PriceListEvent.VALIDATION_ERROR)
                .and()
                .withExternal()
                .source(PriceListState.VALIDATED)
                .target(PriceListState.PROCESSING)
                .event(PriceListEvent.START_PROCESSING)
                .and()
                .withExternal()
                .source(PriceListState.PROCESSING)
                .target(PriceListState.PROCESSED)
                .event(PriceListEvent.PROCESSING_SUCCESS)
                .and()
                .withExternal()
                .source(PriceListState.PROCESSING)
                .target(PriceListState.PARTIALLY_PROCESSED)
                .event(PriceListEvent.PROCESSING_SUCCESS)
                .and()
                .withExternal()
                .source(PriceListState.PROCESSING)
                .target(PriceListState.PROCESSING_FAILED)
                .event(PriceListEvent.PROCESSING_ERROR)
        ;
    }

    @Bean
    public StateMachineListener<PriceListState, PriceListEvent> loggingListener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PriceListState, PriceListEvent> from, State<PriceListState, PriceListEvent> to) {
                log.info("State changed from {} to {}", from == null ? "NONE" : from.getId(), to.getId());
            }

            @Override
            public void eventNotAccepted(Message<PriceListEvent> event) {
                log.warn("Event not accepted: {}", event);
            }
        };
    }

    private Action<PriceListState, PriceListEvent> getAction(String actionName) {
        var action = actions.get(actionName);
        if (action == null) {
            log.error("Action not found: {}", actionName);
        }
        return action;
    }
}
