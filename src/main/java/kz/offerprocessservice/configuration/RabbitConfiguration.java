package kz.offerprocessservice.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    public static final String PRICE_LIST_EXCHANGE = "price-list.exchange";
    public static final String VALIDATION_QUEUE = "price-list.validation.queue";
    public static final String PROCESSING_QUEUE = "price-list.processing.queue";

    public static final String VALIDATION_ROUTING_KEY = "price-list.validation";
    public static final String PROCESSING_ROUTING_KEY = "price-list.processing";

    @Bean
    public TopicExchange priceListExchange() {
        return new TopicExchange(PRICE_LIST_EXCHANGE);
    }

    @Bean
    public Queue validationQueue() {
        return new Queue(VALIDATION_QUEUE, true);
    }

    @Bean
    public Queue processingQueue() {
        return new Queue(PROCESSING_QUEUE, true);
    }

    @Bean
    public Binding validationBinding() {
        return BindingBuilder
                .bind(validationQueue())
                .to(priceListExchange())
                .with(VALIDATION_ROUTING_KEY);
    }

    @Bean
    public Binding processingBinding() {
        return BindingBuilder
                .bind(processingQueue())
                .to(priceListExchange())
                .with(PROCESSING_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);

        return template;
    }
}
