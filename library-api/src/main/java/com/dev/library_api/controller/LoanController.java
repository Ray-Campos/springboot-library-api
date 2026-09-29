package com.dev.library_api.controller;

import com.dev.library_api.dto.BorrowRequest;
import com.dev.library_api.dto.ReturnRequest;
import com.dev.library_api.model.Loan;
import com.dev.library_api.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest request) {
        try {
            Loan loan = loanService.borrowBook(request.userId(), request.bookId());
            return ResponseEntity.status(HttpStatus.CREATED).body(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnBook(@RequestBody ReturnRequest request) {
        try {
            Loan loan = loanService.returnBook(request.loanId(), request.condition());
            return ResponseEntity.ok(loan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Loan>> getAllActiveLoans() {
        return ResponseEntity.ok(loanService.getAllActiveLoans());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Loan>> getLoansByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.getLoansByUserId(userId));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Loan>> getActiveLoansByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.getActiveLoansByUserId(userId));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Loan>> getLoansByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(loanService.getLoansByBookId(bookId));
    }

    @GetMapping("/book/{bookId}/active")
    public ResponseEntity<List<Loan>> getActiveLoansByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(loanService.getActiveLoansByBookId(bookId));
    }
}