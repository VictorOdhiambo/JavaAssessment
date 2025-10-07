package com.app.account_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class RabbitMQConfig {

    @Autowired
    private RabbitMQProperties rabbitMQProperties;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue loanQueue() {
        return new Queue(rabbitMQProperties.getLoan().getQueue(), true);
    }

    @Bean
    public DirectExchange loanExchange() {
        return new DirectExchange(rabbitMQProperties.getLoan().getExchange());
    }

    @Bean
    public Binding loanBinding(Queue loanQueue, DirectExchange loanExchange) {
        return BindingBuilder.bind(loanQueue)
                .to(loanExchange)
                .with(rabbitMQProperties.getLoan().getRoutingKey());
    }

    @Bean
    public RabbitTemplate loanRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(rabbitMQProperties.getLoan().getExchange());
        template.setRoutingKey(rabbitMQProperties.getLoan().getRoutingKey());
        return template;
    }

    @Bean
    public Queue accountQueue() {
        return new Queue(rabbitMQProperties.getAccount().getQueue(), true);
    }

    @Bean
    public DirectExchange accountExchange() {
        return new DirectExchange(rabbitMQProperties.getAccount().getExchange());
    }

    @Bean
    public Binding accountBinding(Queue accountQueue, DirectExchange accountExchange) {
        return BindingBuilder.bind(accountQueue)
                .to(accountExchange)
                .with(rabbitMQProperties.getAccount().getRoutingKey());
    }

    @Bean
    public RabbitTemplate accountRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(rabbitMQProperties.getAccount().getExchange());
        template.setRoutingKey(rabbitMQProperties.getAccount().getRoutingKey());
        return template;
    }
}

