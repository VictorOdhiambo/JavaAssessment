package com.app.customer_service.controller;

import com.app.customer_service.dto.CustomerDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@Repository("/customers")
public class CustomerController {

    @Autowired
    private Validator validator;

    public Mono<ResponseEntity<CustomerDto>> register(@RequestBody Mono<?> customerDto){
        return customerDto.doOnNext(this::validate)
                .flatMap()
    }

    private <T> void validate(T dto){
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
