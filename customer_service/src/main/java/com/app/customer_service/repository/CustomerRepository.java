package com.app.customer_service.repository;

import com.app.customer_service.entity.Customer;
import com.app.customer_service.shared.CustomerStatus;
import reactor.core.publisher.Mono;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repository interface for Customer entity.
 * Provides reactive database operations for customer management.
 */
@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {

    /**
     * Find a customer by email address.
     *
     * @param email the email address to search for
     * @return Mono of Customer if found, empty Mono otherwise
     */
    Mono<Customer> findByEmail(String email);

    /**
     * Check if a customer exists with the given email.
     *
     * @param email the email address to check
     * @return Mono<Boolean> true if exists, false otherwise
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * Find all customers by status.
     *
     * @param status the customer status
     * @return Flux of customers with the given status
     */
    Flux<Customer> findByStatus(CustomerStatus status);

    /**
     * Find customers with pending verification.
     *
     * @return Flux of customers with PENDING_VERIFICATION status
     */
    @Query("SELECT * FROM customers WHERE status = 'PENDING_VERIFICATION'")
    Flux<Customer> findPendingVerifications();

    /**
     * Find customers with active status.
     *
     * @return Flux of active customers
     */
    @Query("SELECT * FROM customers WHERE status = 'ACTIVE'")
    Flux<Customer> findActiveCustomers();

    /**
     * Count customers by status.
     *
     * @param status the customer status
     * @return Mono<Long> count of customers
     */
    Mono<Long> countByStatus(CustomerStatus status);

    /**
     * Delete customer by email.
     *
     * @param email the email address
     * @return Mono<Void>
     */
    Mono<Void> deleteByEmail(String email);
}
