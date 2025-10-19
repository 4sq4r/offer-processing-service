package kz.offerprocessservice.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitConfiguration {

    public static final String VALIDATION_EXCHANGE = "price-list.validation.exchange";
    public static final String VALIDATION_QUEUE = "price-list.validation.queue";
    public static final String VALIDATION_ROUTING_KEY = "validation.request";

    public static final String VALIDATION_RESULT_EXCHANGE = "price-list.validation-result.exchange";
    public static final String VALIDATION_RESULT_QUEUE = "price-list.validation-result.queue";
    public static final String VALIDATION_RESULT_ROUTING_KEY = "validation.result";

    public static final String PROCESSING_EXCHANGE = "price-list.processing.exchange";
    public static final String PROCESSING_QUEUE = "price-list.processing.queue";
    public static final String PROCESSING_ROUTING_KEY = "processing.request";

    public static final String PROCESSING_RESULT_EXCHANGE = "price-list.processing-result.exchange";
    public static final String PROCESSING_RESULT_QUEUE = "price-list.processing-result.queue";
    public static final String PROCESSING_RESULT_ROUTING_KEY = "processing.result";

    @Bean
    public Declarables priceListBindings() {
        return new Declarables(
                new TopicExchange(VALIDATION_EXCHANGE, true, false),
//                new Queue(VALIDATION_QUEUE, true),
                new Queue(VALIDATION_QUEUE, false, false, true),
                new Binding(VALIDATION_QUEUE, Binding.DestinationType.QUEUE, VALIDATION_EXCHANGE, VALIDATION_ROUTING_KEY, null),

                new TopicExchange(VALIDATION_RESULT_EXCHANGE, true, false),
//                new Queue(VALIDATION_RESULT_QUEUE, true),
                new Queue(VALIDATION_RESULT_QUEUE, false, false, true),
                new Binding(VALIDATION_RESULT_QUEUE, Binding.DestinationType.QUEUE, VALIDATION_RESULT_EXCHANGE, VALIDATION_RESULT_ROUTING_KEY, null),

                new TopicExchange(PROCESSING_EXCHANGE, true, false),
//                new Queue(PROCESSING_QUEUE, true),
                new Queue(PROCESSING_QUEUE, false, false, true),
                new Binding(PROCESSING_QUEUE, Binding.DestinationType.QUEUE, PROCESSING_EXCHANGE, PROCESSING_ROUTING_KEY, null),

                new TopicExchange(PROCESSING_RESULT_EXCHANGE, true, false),
//                new Queue(PROCESSING_RESULT_QUEUE, true),
                new Queue(PROCESSING_RESULT_QUEUE, false, false, true),
                new Binding(PROCESSING_RESULT_QUEUE, Binding.DestinationType.QUEUE, PROCESSING_RESULT_EXCHANGE, PROCESSING_RESULT_ROUTING_KEY, null)
        );
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        template.setMandatory(true);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("Message not delivered: {}", cause);
            }
        });

        return template;
    }
}
