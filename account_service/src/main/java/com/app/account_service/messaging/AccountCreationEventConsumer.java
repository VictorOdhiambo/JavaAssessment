package com.app.account_service.messaging;

import com.app.account_service.entity.Account;
import com.app.account_service.event.AccountCreationEvent;
import com.app.account_service.repository.AccountRepository;
import com.app.account_service.shared.AccountStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class AccountCreationEventConsumer {

    @Value("${banking.account.currency}")
    private String accountCurrency;

    private final AccountRepository accountRepository;

    private static final Logger log = LoggerFactory
            .getLogger(AccountCreationEventConsumer.class);

    public AccountCreationEventConsumer(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Create a new account for a given customer id
     * @param event the customer for whom the account is created
     */
    @RabbitListener(queues = "${rabbitmq.account.queue}")
    public void handleAccountCreationEvent(AccountCreationEvent event) {
        log.info("Received AccountCreationEvent: {}", event);

        if (event == null || event.getCustomerId() == null) {
            log.error(String.valueOf(new IllegalArgumentException("Customer must not be null and must have an ID")));
        }

        // Create a new account entity
        Account account = new Account();
        account.setCustomerId(event.getCustomerId());
        account.setAccountNumber(generateRandomAccountNumber());
        account.setBalance(BigDecimal.ZERO); // default initial balance
        account.setCurrency(accountCurrency);          // default currency
        account.setStatus(AccountStatus.ACTIVE.getValue());
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account)
                .doOnSuccess(acc -> log.info("Created new account: {}", acc.getId()))
                .doOnError(err -> log.error("Failed to create account for customer {}: {}",
                        event.getCustomerId(), err.getMessage()))
                .subscribe();
    }

    private Long generateRandomAccountNumber(){
        SecureRandom secureRandom = new SecureRandom();
        return 1_000_000_000L + secureRandom.nextLong(9_000_000_000L);
    }
}
