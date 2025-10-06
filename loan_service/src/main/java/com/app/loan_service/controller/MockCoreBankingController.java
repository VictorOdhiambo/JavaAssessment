package com.app.loan_service.controller;

import com.app.loan_service.dto.LoanApplicationRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/core/loans")
public class MockCoreBankingController {

    @PostMapping("/schedule")
    public Mono<String> getLoanSchedule(@RequestBody LoanApplicationRequest request) {
        String schedule = String.format(
                "Loan approved: %.2f KES for %d months @ 10%% interest",
                request.getLoanAmount(), request.getTenureMonths()
        );
        return Mono.just(schedule).delayElement(Duration.ofMillis(500));
    }
}
