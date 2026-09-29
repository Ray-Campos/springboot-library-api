package com.dev.library_api.repository;

import com.dev.library_api.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserIdAndReturnDateIsNull(Long userId);
    List<Loan> findByReturnDateIsNullAndDueDateBefore(LocalDate date);
    List<Loan> findByUserId(Long userId);
    List<Loan> findByBookCopy_Book_Id(Long bookId);
    List<Loan> findByReturnDateIsNull();
    List<Loan> findByBookCopy_Book_IdAndReturnDateIsNull(Long bookId);
}