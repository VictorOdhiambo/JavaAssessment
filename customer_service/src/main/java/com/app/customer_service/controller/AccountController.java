package com.app.customer_service.controller;

import com.app.customer_service.dto.FundAccountRequest;
import com.app.customer_service.service.AccountService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private Validator validator;
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/fund")
    public Mono<ResponseEntity<Object>> fund(@RequestBody Mono<FundAccountRequest> request){
        return request.doOnNext(this::validate)
                .flatMap(accountService::fundAccount)
                .map(ResponseEntity::ok);
    }

    private <T> void validate(T dto){
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
