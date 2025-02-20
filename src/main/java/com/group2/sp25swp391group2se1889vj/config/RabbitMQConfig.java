package com.group2.sp25swp391group2se1889vj.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String DEBT_QUEUE = "debtQueue";
    public static final String DEBT_EXCHANGE = "debtExchange";
    public static final String DEBT_ROUTING_KEY = "debtRoutingKey";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Queue debtQueue() {
        return new Queue(DEBT_QUEUE, false);
    }

    @Bean
    public DirectExchange debtExchange() {
        return new DirectExchange(DEBT_EXCHANGE);
    }

    @Bean
    public Binding debtBinding() {
        return BindingBuilder.bind(debtQueue()).to(debtExchange()).with(DEBT_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

}
