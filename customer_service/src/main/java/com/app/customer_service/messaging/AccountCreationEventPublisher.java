package com.app.customer_service.messaging;

import com.app.customer_service.config.RabbitMQProperties;
import com.app.customer_service.event.AccountCreationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class AccountCreationEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    private static final Logger log = LoggerFactory
            .getLogger(AccountCreationEventPublisher.class);

    public AccountCreationEventPublisher(RabbitTemplate rabbitTemplate, RabbitMQProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    public void publishAccountCreationEvent(AccountCreationEvent event) {
        rabbitTemplate.convertAndSend(
                properties.getExchange(),
                properties.getRoutingKey(),
                event
        );
        log.info("Account creation event published: {}", event);
    }
}
