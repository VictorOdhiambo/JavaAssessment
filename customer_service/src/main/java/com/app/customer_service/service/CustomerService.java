package com.app.customer_service.service;

import com.app.customer_service.dto.CustomerDto;
import com.app.customer_service.event.AccountCreationEvent;
import com.app.customer_service.mapper.CustomerMapper;
import com.app.customer_service.messaging.AccountCreationEventPublisher;
import com.app.customer_service.repository.CustomerRepository;
import com.app.customer_service.service.contract.ICustomerService;
import com.app.customer_service.shared.CustomerStatus;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final AccountCreationEventPublisher accountCreationEventPublisher;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Logger log = LoggerFactory
            .getLogger(CustomerService.class);

    public CustomerService(CustomerMapper customerMapper, CustomerRepository customerRepository, EmailService emailService, AccountCreationEventPublisher accountCreationEventPublisher) {
        this.customerMapper = customerMapper;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
        this.accountCreationEventPublisher = accountCreationEventPublisher;
    }

    @Transactional
    @Override
    public Mono<Object> registerCustomer(CustomerDto customerDto) {
        log.info("Starting customer registration for email: {}", customerDto.getEmail());

        customerDto.setVerificationCode(generateVerificationCode());

        return customerRepository.findByEmail(customerDto.getEmail())
                .flatMap(existingCustomer -> {
                    // Customer already exists
                    log.warn("Customer already exists with email: {}", customerDto.getEmail());
                    return Mono.error(new IllegalArgumentException("Customer already exists"));
                })
                .switchIfEmpty(
                        customerRepository.save(customerMapper.toEntity(customerDto))
                                .map(customerMapper::toDto)
                                .doOnSuccess(saved -> {
                                    log.info("Customer registered successfully: {}", saved.getEmail());
                                    emailService.sendEmailVerification(saved.getEmail(), saved.getVerificationCode());
                                })
                );
    }

    public Mono<CustomerDto> verifyCustomer(CustomerDto request) {
        log.info("Starting customer verification for email: {}", request.getEmail());

        return customerRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Customer not found with email: {}", request.getEmail());
                    return Mono.error(new IllegalArgumentException("Customer not found"));
                }))
                .flatMap(customerEntity -> {
                    // Validate verification code exists
                    if (customerEntity.getVerificationCode() == null) {
                        log.error("No verification code found for customer: {}", request.getEmail());
                        return Mono.error(new IllegalArgumentException(
                                "No verification code found. Please register again."
                        ));
                    }

                    // Validate verification code matches
                    if (!customerEntity.getVerificationCode().equals(request.getVerificationCode())) {
                        log.error("Invalid verification code for customer: {}. Expected: {}, Got: {}",
                                request.getEmail(), customerEntity.getVerificationCode(), request.getVerificationCode());
                        return Mono.error(new IllegalArgumentException("Invalid verification code"));
                    }

                    // Update status and clear verification code
                    customerEntity.setStatus(CustomerStatus.ACTIVE.getValue());
                    customerEntity.setVerificationCode(null);

                    // Save the verified customer
                    return customerRepository.save(customerEntity)
                    .doOnSuccess(customer -> {
                        log.info("Customer verified successfully: {}", customer.getId());
                        // Publish event to Account Service via RabbitMQ
                        AccountCreationEvent event = new AccountCreationEvent();
                        event.setCustomerId(customer.getId());
                        accountCreationEventPublisher.publishAccountCreationEvent(event);
                    }).thenReturn(customerMapper.toDto(customerEntity));
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
        return passwordEncoder.encode(password);
    }

}
