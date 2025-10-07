package com.app.customer_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {
    private String queue;
    private String exchange;
    private String routingKey;

    public String getQueue() {
        return queue;
    }

    public String getExchange() {
        return exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}
