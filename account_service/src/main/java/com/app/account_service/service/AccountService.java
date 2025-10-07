package com.app.account_service.service;


import com.app.account_service.dto.FundAccountRequest;
import com.app.account_service.dto.FundAccountResponse;
import com.app.account_service.entity.Account;
import com.app.account_service.event.AccountCreationEvent;
import com.app.account_service.repository.AccountRepository;
import com.app.account_service.shared.AccountStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service class for managing account operations.
 * Handles account funding and balance management.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private static final Logger log = LoggerFactory
            .getLogger(AccountService.class);

    @Value("${banking.account.currency}")
    private String accountCurrency;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Fund an account with the specified amount.
     *
     * @param request the funding request containing account ID and amount
     * @return Mono of FundAccountResponse with updated balance
     * @throws Exception if account not found or amount is invalid
     */
    @Transactional
    public Mono<FundAccountResponse> fundAccount(FundAccountRequest request) {
        log.info("Starting account funding: AccountID={}, Amount={}",
                request.getAccountId(), request.getAmount());

        return accountRepository.findByCustomerId(request.getAccountId())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Account funding failed: Account not found - ID={}",
                            request.getAccountId());
                    return Mono.error(new Exception("Account not found"));
                }))
                .flatMap(account -> validateAndFundAccount(account, request.getAmount()));
    }

    /**
     * Validate funding amount and update account balance.
     *
     * @param account the account to fund
     * @param amount  the amount to add
     * @return Mono of FundAccountResponse
     */
    private Mono<FundAccountResponse> validateAndFundAccount(Account account, BigDecimal amount) {
        // Validate amount is positive
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid funding amount: Amount={}, AccountID={}",
                    amount, account.getId());
            return Mono.error(new Exception("Amount must be greater than zero"));
        }

        // Validate account is active
        if (account.getStatus() != AccountStatus.ACTIVE.getValue()) {
            log.warn("Funding failed: Account is not active - AccountID={}, Status={}",
                    account.getId(), account.getStatus());
            return Mono.error(new Exception(
                    "Account is not active. Cannot fund inactive or frozen accounts."));
        }

        // Calculate new balance
        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance = currentBalance.add(amount);

        log.debug("Updating account balance: AccountID={}, CurrentBalance={}, Amount={}, NewBalance={}",
                account.getId(), currentBalance, amount, newBalance);

        // Update account
        account.setBalance(newBalance);
        account.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(account)
                .doOnSuccess(savedAccount ->
                        log.info("Account funded successfully: AccountID={}, PreviousBalance={}, FundedAmount={}, NewBalance={}",
                                savedAccount.getId(), currentBalance, amount, savedAccount.getBalance()))
                .doOnError(error ->
                        log.error("Error funding account: AccountID={}, Amount={}",
                                account.getId(), amount, error))
                .map(savedAccount -> {
                    FundAccountResponse response = new FundAccountResponse();
                    response.setAccountId(savedAccount.getId());
                    response.setAccountNumber(savedAccount.getAccountNumber());
                    response.setNewBalance(savedAccount.getBalance());
                    response.setCurrency(savedAccount.getCurrency());
                    response.setMessage(String.format("Account funded successfully with %s %s. New balance: %s %s",
                            accountCurrency, amount, accountCurrency, savedAccount.getBalance()));
                    return response;
                });
    }

    /**
     * Get account by ID.
     *
     * @param accountId the account ID
     * @return Mono of Account
     */
    public Mono<Account> getAccountById(Long accountId) {
        log.debug("Fetching account by ID: {}", accountId);

        return accountRepository.findByAccountNumber(accountId)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Account not found: ID={}", accountId);
                    return Mono.error(new Exception("Account not found"));
                }))
                .doOnSuccess(account ->
                        log.debug("Account retrieved: ID={}, AccountNumber={}, Balance={}",
                                account.getId(), account.getAccountNumber(), account.getBalance()));
    }

    /**
     * Get account by customer ID.
     *
     * @param customerId the customer ID
     * @return Mono of Account
     */
    public Mono<Account> getAccountByCustomerId(UUID customerId) {
        log.debug("Fetching account by customer ID: {}", customerId);

        return accountRepository.findByCustomerId(customerId)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Account not found for customer: CustomerID={}", customerId);
                    return Mono.error(new Exception("Account not found for customer"));
                }))
                .doOnSuccess(account ->
                        log.debug("Account retrieved for customer: CustomerID={}, AccountID={}, Balance={}",
                                customerId, account.getId(), account.getBalance()));
    }

    /**
     * Check if account has sufficient balance.
     *
     * @param accountId      the account number
     * @param requiredAmount the required amount
     * @return Mono<Boolean> true if balance is sufficient
     */
    public Mono<Boolean> hasSufficientBalance(Long accountId, BigDecimal requiredAmount) {
        log.debug("Checking sufficient balance: AccountID={}, RequiredAmount={}", accountId, requiredAmount);

        return accountRepository.findByAccountNumber(accountId)
                .map(account -> {
                    boolean sufficient = account.getBalance().compareTo(requiredAmount) >= 0;
                    log.debug("Balance check result: AccountID={}, Balance={}, Required={}, Sufficient={}",
                            accountId, account.getBalance(), requiredAmount, sufficient);
                    return sufficient;
                })
                .defaultIfEmpty(false); // if account not found, return false
    }

    /**
     * Deduct amount from account balance.
     * Used for loan disbursement or other deductions.
     *
     * @param accountId the account ID
     * @param amount    the amount to deduct
     * @return Mono of updated Account
     */
    @Transactional
    public Mono<Account> deductFromAccount(Long accountId, BigDecimal amount) {
        log.info("Deducting from account: AccountID={}, Amount={}", accountId, amount);

        return accountRepository.findByAccountNumber(accountId)
                .switchIfEmpty(Mono.error(new Exception("Account not found")))
                .flatMap(account -> {
                    if (account.getBalance().compareTo(amount) < 0) {
                        log.warn("Insufficient balance for deduction: AccountID={}, Balance={}, Requested={}",
                                accountId, account.getBalance(), amount);
                        return Mono.error(new Exception("Insufficient balance"));
                    }

                    BigDecimal newBalance = account.getBalance().subtract(amount);
                    account.setBalance(newBalance);
                    account.setUpdatedAt(LocalDateTime.now());

                    return accountRepository.save(account)
                            .doOnSuccess(saved ->
                                    log.info("Amount deducted successfully: AccountID={}, Amount={}, NewBalance={}",
                                            saved.getId(), amount, saved.getBalance()));
                });
    }
}
