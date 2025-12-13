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
                .end(PriceListStatus.VALIDATION_FAILED)
                .end(PriceListStatus.PROCESSED)
                .end(PriceListStatus.PARTIALLY_PROCESSED)
                .end(PriceListStatus.PROCESSING_FAILED)
                .states(EnumSet.allOf(PriceListStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PriceListStatus, PriceListEvent> transitions)
            throws Exception {
        configure(transitions,
                PriceListStatus.UPLOADED,
                PriceListStatus.VALIDATION,
                PriceListEvent.START_VALIDATION,
                ActionNames.START_VALIDATION
        );
        configure(transitions,
                PriceListStatus.VALIDATION,
                PriceListStatus.VALIDATED,
                PriceListEvent.VALIDATION_SUCCESS,
                ActionNames.VALIDATION_SUCCESS
        );
        configure(transitions,
                PriceListStatus.VALIDATION,
                PriceListStatus.VALIDATION_FAILED,
                PriceListEvent.VALIDATION_ERROR,
                ActionNames.VALIDATION_ERROR
        );
        configure(transitions,
                PriceListStatus.VALIDATED,
                PriceListStatus.PROCESSING,
                PriceListEvent.START_PROCESSING,
                ActionNames.START_PROCESSING
        );
        configure(transitions,
                PriceListStatus.PROCESSING,
                PriceListStatus.PROCESSED,
                PriceListEvent.PROCESSING_SUCCESS,
                ActionNames.PROCESSED
        );
        configure(transitions,
                PriceListStatus.PROCESSING,
                PriceListStatus.PARTIALLY_PROCESSED,
                PriceListEvent.PROCESSING_PARTIALLY_SUCCESS,
                ActionNames.PARTIALLY_PROCESSED
        );
        configure(transitions,
                PriceListStatus.PROCESSING,
                PriceListStatus.PROCESSING_FAILED,
                PriceListEvent.PROCESSING_ERROR,
                ActionNames.PROCESSING_FAILED
        );
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

    private void configure(
            StateMachineTransitionConfigurer<PriceListStatus, PriceListEvent> transitions,
            PriceListStatus source,
            PriceListStatus target,
            PriceListEvent event,
            String action
    ) throws Exception {
        transitions.withExternal()
                .source(source)
                .target(target)
                .event(event)
                .action(getAction(action))
                .and();
    }
}
