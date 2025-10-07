package com.app.account_service.messaging;

import com.app.account_service.event.LoanApprovedEvent;
import com.app.account_service.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class LoanEventConsumer {

    private final AccountRepository accountRepository;

    private static final Logger log = LoggerFactory
            .getLogger(LoanEventConsumer.class);

    public LoanEventConsumer(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @RabbitListener(queues = "${rabbitmq.loan.queue}")
    public void handleLoanApprovedEvent(LoanApprovedEvent event) {
        log.info("Received LoanApprovedEvent: {}", event);

        accountRepository.findByCustomerId(event.getAccountId())
                .flatMap(account -> {
                    account.setBalance(account.getBalance().add(BigDecimal.valueOf(event.getAmount())));
                    return accountRepository.save(account);
                })
                .doOnSuccess(account -> log.info("Account balance updated for accountId={}", account.getAccountNumber()))
                .doOnError(error -> log.error("Failed to update account: {}", error.getMessage()))
                .subscribe();
    }

}

