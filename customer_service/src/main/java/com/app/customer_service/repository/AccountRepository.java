package com.app.customer_service.repository;

import com.app.customer_service.entity.Account;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface AccountRepository extends R2dbcRepository<Account, UUID> {
    Mono<Account> findByCustomerId(Long customerId);
    Mono<Account> findByAccountNumber(Long accountId);
}
