package com.app.customer_service.service;

import com.app.customer_service.dto.CustomerDto;
import com.app.customer_service.entity.Customer;
import com.app.customer_service.mapper.CustomerMapper;
import com.app.customer_service.repository.CustomerRepository;
import com.app.customer_service.service.contract.ICustomerService;
import com.app.customer_service.shared.CustomerStatus;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Random;

@Service
@Slf4j
public class CustomerService implements ICustomerService {

    private static final int CODE_LENGTH = 6;
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private final AccountService accountService;

    private static final Logger log = LoggerFactory
            .getLogger(CustomerService.class);

    public CustomerService(CustomerMapper customerMapper, CustomerRepository customerRepository, EmailService emailService, AccountService accountService) {
        this.customerMapper = customerMapper;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
        this.accountService = accountService;
    }

    @Transactional
    @Override
    public Mono<Object> registerCustomer(CustomerDto customerDto) {
        log.info("Starting customer registration for email: {}", customerDto.email());

        return customerRepository.findCustomerByEmail(customerDto.email())
                .flatMap(existingCustomer -> {
                    // Customer already exists
                    log.warn("Customer already exists with email: {}", customerDto.email());
                    return Mono.error(new IllegalArgumentException("Customer already exists"));
                })
                .switchIfEmpty(
                        customerRepository.save(customerMapper.toEntity(customerDto))
                                .map(customerMapper::toDto)
                                .doOnSuccess(saved -> {
                                    log.info("Customer registered successfully: {}", saved.email());
                                    emailService.sendEmailVerification(saved);
                                })
                );
    }

    public Mono<CustomerDto> verifyCustomer(CustomerDto request) {
        log.info("Starting customer verification for email: {}", request.email());

        return customerRepository.findCustomerByEmail(request.email())
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Customer not found with email: {}", request.email());
                    return Mono.error(new IllegalArgumentException("Customer not found"));
                }))
                .map(customerMapper::toDto)
                .flatMap(customer -> {
                    // Validate verification code exists
                    if (customer.verificationCode() == null) {
                        log.error("No verification code found for customer: {}", request.email());
                        return Mono.error(new IllegalArgumentException(
                                "No verification code found. Please register again."
                        ));
                    }

                    // Validate verification code matches
                    if (!customer.verificationCode().equals(request.verificationCode())) {
                        log.error("Invalid verification code for customer: {}. Expected: {}, Got: {}",
                                request.email(), customer.verificationCode(), request.verificationCode());
                        return Mono.error(new IllegalArgumentException("Invalid verification code"));
                    }

                    Customer customerEntity = customerMapper.toEntity(customer);

                    // Update customer status to ACTIVE
                    customerEntity(CustomerStatus.ACTIVE);
                    customer.setVerificationCode(null);

                    return customerRepository.save(customerMapper.toEntity(customer))
                            .doOnNext(updated -> log.info("Customer verified successfully: {}", updated.getEmail()))
                            .flatMap(updatedCustomer ->
                                    accountService.createAccount(updatedCustomer)
                                            .doOnNext(account -> log.info("Account created automatically for customer: {} with account number: {}",
                                                    updatedCustomer.getEmail(), account.getAccountNumber()))
                                            .map(account -> CustomerVerificationResponse.builder()
                                                    .customerId(updatedCustomer.getId())
                                                    .email(updatedCustomer.getEmail())
                                                    .status(updatedCustomer.getStatus().toString())
                                                    .message("Email verified successfully. Account created.")
                                                    .accountId(account.getId())
                                                    .accountNumber(account.getAccountNumber())
                                                    .build())
                            );
                });
    }


    /**
     * Generates a random numeric verification code.
     *
     * @return Verification code as a String
     */
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }

        String generatedCode = code.toString();
        log.debug("Generated verification code: {}", generatedCode);
        return generatedCode;
    }

    /**
     * Hashes the password for storage.
     *
     * @param password Plain text password
     * @return Hashed password (currently returns plain text)
     */
    private String hashPassword(String password) {
        return password;
    }

}
