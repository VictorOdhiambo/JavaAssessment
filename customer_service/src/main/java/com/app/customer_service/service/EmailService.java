package com.app.customer_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendEmailVerification(String email, String code) {
        // simulate async send
        Mono.delay(Duration.ofSeconds(1))
                .doOnNext(i -> log.info("[MOCK EMAIL] To={}, code={}", email, code))
                .then();
    }
}
