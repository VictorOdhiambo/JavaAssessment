package com.app.customer_service.controller;

import com.app.customer_service.dto.CustomerDto;
import com.app.customer_service.service.CustomerService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@Repository("/customers")
public class CustomerController {

    @Autowired
    private Validator validator;
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<Object>> register(@RequestBody Mono<CustomerDto> customerDto) {
        return customerDto
                .doOnNext(this::validate)
                .flatMap(customerService::registerCustomer)
                .map(saved -> ResponseEntity.status(HttpStatus.ACCEPTED).body(saved))
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(null))
                );
    }

    @PostMapping("/verify")
    public Mono<ResponseEntity<CustomerDto>> verify(@RequestBody Mono<CustomerDto> customerDto) {
        return customerDto
                .doOnNext(this::validate)
                .flatMap(customerService::verifyCustomer)
                .map(ResponseEntity::ok)
                .onErrorResume(e ->
                                Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(null))
                        );
    }
    private <T> void validate(T dto){
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
