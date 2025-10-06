package com.app.customer_service.repository;

import com.app.customer_service.entity.Account;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AccountRepository extends R2dbcRepository<Account, UUID> {
    Mono<Account> findByCustomerId(Long customerId);
    Mono<Account> findByAccountId(Long accountId);
}
