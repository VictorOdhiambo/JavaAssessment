package com.app.loan_service.controller;

import com.app.loan_service.dto.LoanApplicationRequest;
import com.app.loan_service.service.LoanService;
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
@RequestMapping("/loans")
public class LoanController {
    @Autowired
    private Validator validator;
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }


    @PostMapping("/apply")
    public Mono<ResponseEntity<Object>> apply(@RequestBody Mono<LoanApplicationRequest> request) {
        return request.doOnNext(this::validate)
                .flatMap(loanService::applyForLoan)
                .map(ResponseEntity::ok);
    }

    private <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
