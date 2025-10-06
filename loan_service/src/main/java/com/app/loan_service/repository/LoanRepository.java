package com.app.loan_service.repository;

import com.app.loan_service.entity.Loan;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends R2dbcRepository<Loan, Long> {
}
