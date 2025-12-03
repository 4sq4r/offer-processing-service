package kz.offerprocessservice.configuration;

import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListStatus;
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
public class PriceListStateMachineConfiguration extends EnumStateMachineConfigurerAdapter<PriceListStatus, PriceListEvent> {

    public static final String PRICE_LIST_ID_HEADER = "priceListId";

    private final Map<String, Action<PriceListStatus, PriceListEvent>> actions;

    @Override
    public void configure(StateMachineConfigurationConfigurer<PriceListStatus, PriceListEvent> config)
            throws Exception {
        config.withConfiguration()
                .listener(loggingListener())
                .autoStartup(false);
    }

    @Override
    public void configure(StateMachineStateConfigurer<PriceListStatus, PriceListEvent> states) throws Exception {
        states.withStates()
                .initial(PriceListStatus.UPLOADED)
                .end(PriceListStatus.PROCESSED)
                .end(PriceListStatus.VALIDATION_FAILED)
                .end(PriceListStatus.PROCESSING_FAILED)
                .states(EnumSet.allOf(PriceListStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PriceListStatus, PriceListEvent> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(PriceListStatus.UPLOADED)
                .target(PriceListStatus.VALIDATION)
                .event(PriceListEvent.START_VALIDATION)
                .action(getAction(ActionNames.START_VALIDATION))
                .and()
                .withExternal()
                .source(PriceListStatus.VALIDATION)
                .target(PriceListStatus.VALIDATED)
                .event(PriceListEvent.VALIDATION_SUCCESS)
                .action(getAction(ActionNames.VALIDATION_SUCCESS))
                .and()
                .withExternal()
                .source(PriceListStatus.VALIDATION)
                .target(PriceListStatus.VALIDATION_FAILED)
                .event(PriceListEvent.VALIDATION_ERROR)
                .action(getAction(ActionNames.VALIDATION_ERROR))
                .and()
                .withExternal()
                .source(PriceListStatus.VALIDATED)
                .target(PriceListStatus.PROCESSING)
                .event(PriceListEvent.START_PROCESSING)
                .action(getAction(ActionNames.START_PROCESSING))
                .and()
                .withExternal()
                .source(PriceListStatus.PROCESSING)
                .target(PriceListStatus.PROCESSED)
                .event(PriceListEvent.PROCESSING_SUCCESS)
                .action(getAction(ActionNames.PROCESSED))
                .and()
                .withExternal()
                .source(PriceListStatus.PROCESSING)
                .target(PriceListStatus.PARTIALLY_PROCESSED)
                .event(PriceListEvent.PROCESSING_PARTIALLY_SUCCESS)
                .action(getAction(ActionNames.PARTIALLY_PROCESSED))
                .and()
                .withExternal()
                .source(PriceListStatus.PROCESSING)
                .target(PriceListStatus.PROCESSING_FAILED)
                .event(PriceListEvent.PROCESSING_ERROR)
                .action(getAction(ActionNames.PROCESSING_FAILED))
        ;
    }

    @Bean
    public StateMachineListener<PriceListStatus, PriceListEvent> loggingListener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(
                    State<PriceListStatus, PriceListEvent> from,
                    State<PriceListStatus, PriceListEvent> to
            ) {
                log.info("State changed from {} to {}", from == null ? "NONE" : from.getId(), to.getId());
            }

            @Override
            public void eventNotAccepted(Message<PriceListEvent> event) {
                log.warn("Event not accepted: {}", event);
            }
        };
    }

    private Action<PriceListStatus, PriceListEvent> getAction(String actionName) {
        var action = actions.get(actionName);
        if (action == null) {
            log.error("Action not found: {}", actionName);
        }
        return action;
    }
}
