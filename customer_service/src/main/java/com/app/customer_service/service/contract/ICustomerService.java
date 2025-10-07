package com.app.customer_service.service.contract;

import com.app.customer_service.dto.CustomerDto;
import reactor.core.publisher.Mono;

public interface ICustomerService {
    Mono<Object> registerCustomer(CustomerDto customerDto);
    Mono<CustomerDto> verifyCustomer(CustomerDto customerDto);
}
