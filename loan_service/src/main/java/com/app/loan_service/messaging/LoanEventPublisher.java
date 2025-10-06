package com.app.loan_service.messaging;

import com.app.loan_service.config.RabbitMQProperties;
import com.app.loan_service.event.LoanApprovedEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoanEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties properties;

    private static final Logger log = LoggerFactory
            .getLogger(LoanEventPublisher.class);

    public LoanEventPublisher(RabbitTemplate rabbitTemplate, RabbitMQProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    public void publishLoanApprovedEvent(LoanApprovedEvent event) {
        rabbitTemplate.convertAndSend(
                properties.getExchange(),
                properties.getRoutingKey(),
                event
        );
        log.info("Loan approved event published: {}", event);
    }
}

