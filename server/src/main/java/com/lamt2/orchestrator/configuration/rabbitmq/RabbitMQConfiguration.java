package com.lamt2.orchestrator.configuration.rabbitmq;

import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class RabbitMQConfiguration {
    @Value("${spring.rabbitmq.queue_name}")
    public static String QUEUE_NAME;
    @Value("${spring.rabbitmq.exchange_name}")
    public static String EXCHANGE_NAME;
    @Value("${spring.rabbitmq.routing_key}")
    public static String ROUTING_KEY;

    @Bean
    public Queue queue() {
        return new Queue(RabbitMQConfiguration.QUEUE_NAME, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(RabbitMQConfiguration.EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(RabbitMQConfiguration.ROUTING_KEY);
    }
}
