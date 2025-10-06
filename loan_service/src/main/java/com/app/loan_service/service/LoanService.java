package com.app.loan_service.service;

import com.app.customer_service.entity.Account;
import com.app.customer_service.repository.AccountRepository;
import com.app.loan_service.config.CoreBankingProperties;
import com.app.loan_service.config.LoanProperties;
import com.app.loan_service.dto.LoanApplicationRequest;
import com.app.loan_service.entity.Loan;
import com.app.loan_service.repository.LoanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final AccountRepository accountRepository;
    private final LoanRepository loanRepository;
    private final WebClient webClient; // mock Core Banking Service client
    private final CoreBankingProperties coreBankingProperties;
    private final LoanProperties loanProperties;

    private static final Logger log = LoggerFactory
            .getLogger(LoanService.class);

    public LoanService(AccountRepository accountRepository, LoanRepository loanRepository, WebClient webClient, CoreBankingProperties coreBankingProperties, LoanProperties loanProperties) {
        this.accountRepository = accountRepository;
        this.loanRepository = loanRepository;
        this.webClient = webClient;
        this.coreBankingProperties = coreBankingProperties;
        this.loanProperties = loanProperties;
    }

    /**
     * Submits a loan application after performing eligibility checks.
     */
    public Mono<Loan> applyForLoan(LoanApplicationRequest request) {
        log.info("Starting loan application for accountId={}, amount={}",
                request.getAccountId(), request.getLoanAmount().doubleValue());

        return accountRepository.findByAccountId(request.getAccountId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Account not found")))
                .flatMap(account -> validateEligibility(account, request))
                .flatMap(valid -> sendToCoreBanking(request))
                .flatMap(schedule -> {
                    Loan loan = new Loan();
                    loan.setAccountId(request.getAccountId());
                    loan.setAmount(request.getLoanAmount().doubleValue());
                    loan.setTenure(request.getTenureMonths());
                    loan.setStatus("APPROVED");

                    return loanRepository.save(loan);
                });
    }

    /**
     * Validate account eligibility before loan submission.
     */
    private Mono<Account> validateEligibility(Account account, LoanApplicationRequest request) {
        if (account.getBalance().doubleValue() < loanProperties.getMinFundLimit()) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Account not funded with minimum KES " + loanProperties.getMinFundLimit()));
        }

        if (request.getLoanAmount().doubleValue() < loanProperties.getMinAmount() ||
                request.getLoanAmount().doubleValue() > loanProperties.getMaxAmount()) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Loan amount must be between KES %.0f and %.0f",
                            loanProperties.getMinAmount(), loanProperties.getMaxAmount())));
        }

        return Mono.just(account);
    }

    /**
     * Simulate Core Banking Service call for loan schedule.
     */
    private Mono<String> sendToCoreBanking(LoanApplicationRequest request) {
        log.info("Sending loan request to core banking system...");
        String endpoint = coreBankingProperties.getBaseUrl() + "/loans/schedule";
        return webClient.post()
                .uri(endpoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(resp -> log.info("Received loan schedule from core banking: {}", resp))
                .onErrorResume(e -> {
                    log.error("Failed to contact core banking service: {}", e.getMessage());
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.SERVICE_UNAVAILABLE, "Core banking service unavailable"));
                });
    }
}

