package com.app.loan_service.service;

import com.app.loan_service.config.LoanProperties;
import com.app.loan_service.dto.LoanApplicationRequest;
import com.app.loan_service.entity.Loan;
import com.app.loan_service.messaging.LoanEventPublisher;
import com.app.loan_service.repository.LoanRepository;
import com.app.loan_service.event.LoanApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanProperties loanProperties;
    private final LoanEventPublisher loanEventPublisher;

    private static final Logger log = LoggerFactory
            .getLogger(LoanService.class);

    public LoanService(LoanRepository loanRepository, LoanProperties loanProperties, LoanEventPublisher loanEventPublisher) {
        this.loanRepository = loanRepository;
        this.loanProperties = loanProperties;
        this.loanEventPublisher = loanEventPublisher;
    }

    /**
     * Apply for a loan asynchronously.
     */
    public Mono<Loan> applyForLoan(LoanApplicationRequest request) {
        log.info("Starting loan application for accountId={}, amount={}",
                request.getAccountId(), request.getLoanAmount());

        // Validate loan amount limits
        if (request.getLoanAmount().doubleValue() < loanProperties.getMinAmount() ||
                request.getLoanAmount().doubleValue() > loanProperties.getMaxAmount()) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Loan amount must be between %.0f and %.0f",
                            loanProperties.getMinAmount(), loanProperties.getMaxAmount())
            ));
        }

        Loan loan = new Loan();
        loan.setAccountId(request.getAccountId());
        loan.setAmount(request.getLoanAmount().doubleValue());
        loan.setTenure(request.getTenureMonths());
        loan.setStatus("APPROVED");

        return loanRepository.save(loan)
                .doOnSuccess(savedLoan -> {
                    log.info("Loan approved and saved: {}", savedLoan.getId());
                    // Publish LoanApprovedEvent to Account Service via RabbitMQ
                    LoanApprovedEvent event = new LoanApprovedEvent();
                    event.setLoanId(savedLoan.getId());
                    event.setAccountId(savedLoan.getAccountId());
                    event.setAmount(savedLoan.getAmount());
                    loanEventPublisher.publishLoanApprovedEvent(event);
                });
    }
}
