package com.app.account_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {

    private RabbitConfig loan;
    private RabbitConfig account;

    @Data
    public static class RabbitConfig {
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

        public void setRoutingKey(String routingKey) {
            this.routingKey = routingKey;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public void setQueue(String queue) {
            this.queue = queue;
        }
    }

    public RabbitConfig getLoan() {
        return loan;
    }

    public RabbitConfig getAccount() {
        return account;
    }

    public void setLoan(RabbitConfig loan) {
        this.loan = loan;
    }

    public void setAccount(RabbitConfig account) {
        this.account = account;
    }
}

