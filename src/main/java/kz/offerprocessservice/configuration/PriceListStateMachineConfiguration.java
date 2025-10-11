package kz.offerprocessservice.configuration;

import kz.offerprocessservice.statemachine.PriceListEvent;
import kz.offerprocessservice.statemachine.PriceListState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class PriceListStateMachineConfiguration extends StateMachineConfigurerAdapter<PriceListState, PriceListEvent> {

    private static final String PRICE_LIST_ID_HEADER = "priceListId";

    @Override
    public void configure(StateMachineStateConfigurer<PriceListState, PriceListEvent> states) throws Exception {
        states.withStates()
                .initial(PriceListState.UPLOADED)
                .state(PriceListState.VALIDATION)
                .state(PriceListState.VALIDATED)
                .state(PriceListState.PROCESSING)
                .end(PriceListState.PROCESSED)
                .state(PriceListState.PARTIALLY_PROCESSED)
                .end(PriceListState.VALIDATION_FAILED)
                .end(PriceListState.PROCESSING_FAILED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PriceListState, PriceListEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(PriceListState.UPLOADED)
                .target(PriceListState.VALIDATION)
                .event(PriceListEvent.START_VALIDATION)
                .and()
                .withExternal()
                .source(PriceListState.VALIDATION)
                .target(PriceListState.VALIDATED)
                .event(PriceListEvent.VALIDATION_SUCCESS)
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
                .event(PriceListEvent.PROCESSING_ERROR);
    }

    @Bean
    public Action<PriceListState, PriceListEvent> validateAction() {
        return context -> log.info(
                "Running validation for price list: {}",
                context.getMessageHeader(PRICE_LIST_ID_HEADER)
        );
    }

    @Bean
    public Action<PriceListState, PriceListEvent> processAction() {
        return context -> log.info(
                "Processing price list: {}",
                context.getMessageHeader(PRICE_LIST_ID_HEADER)
        );
    }
}
