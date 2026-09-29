package com.dev.library_api.service;

import com.dev.library_api.model.*;
import com.dev.library_api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookCopyRepository bookCopyRepository;

    public LoanService(LoanRepository loanRepository, UserRepository userRepository, BookCopyRepository bookCopyRepository) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    @Transactional
    public Loan borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isBlocked()) {
            throw new RuntimeException("User is blocked due to pending issues");
        }

        // Enforces the limit based on user bond type
        List<Loan> activeLoans = loanRepository.findByUserIdAndReturnDateIsNull(userId);
        if (activeLoans.size() >= getLimitForRole(user.getRole())) {
            throw new RuntimeException("Loan limit exceeded for this user type");
        }

        List<BookCopy> availableCopies = bookCopyRepository.findByBookIdAndStatus(bookId, CopyStatus.AVAILABLE);
        if (availableCopies.isEmpty()) {
            throw new RuntimeException("No copies available for this book at the moment");
        }

        BookCopy copyToBorrow = availableCopies.get(0);
        copyToBorrow.setStatus(CopyStatus.BORROWED);
        bookCopyRepository.save(copyToBorrow);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBookCopy(copyToBorrow);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(getDaysForRole(user.getRole())));

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId, CopyStatus returnCondition) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (loan.getReturnDate() != null) {
            throw new RuntimeException("This book has already been returned");
        }

        loan.setReturnDate(LocalDate.now());

        // Calculate fines and block user if overdue
        if (loan.getReturnDate().isAfter(loan.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), loan.getReturnDate());
            double fineRatePerDay = 2.0;
            loan.setFineAmount(daysLate * fineRatePerDay);

            User user = loan.getUser();
            user.setBlocked(true);
            userRepository.save(user);
        } else {
            loan.setFineAmount(0.0);
        }

        BookCopy copy = loan.getBookCopy();
        copy.setStatus(returnCondition);

        // If the book is lost or damaged, block the user for manual resolution
        if (returnCondition == CopyStatus.LOST || returnCondition == CopyStatus.DAMAGED) {
            User user = loan.getUser();
            user.setBlocked(true);
            userRepository.save(user);
        }

        bookCopyRepository.save(copy);
        return loanRepository.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> getAllActiveLoans() {
        return loanRepository.findByReturnDateIsNull();
    }

    public List<Loan> getLoansByUserId(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    public List<Loan> getActiveLoansByUserId(Long userId) {
        return loanRepository.findByUserIdAndReturnDateIsNull(userId);
    }

    public List<Loan> getLoansByBookId(Long bookId) {
        return loanRepository.findByBookCopy_Book_Id(bookId);
    }

    public List<Loan> getActiveLoansByBookId(Long bookId) {
        return loanRepository.findByBookCopy_Book_IdAndReturnDateIsNull(bookId);
    }

    private int getLimitForRole(UserRole role) {
        return switch (role) {
            case STUDENT -> 3;
            case PROFESSOR -> 5;
            case EXTERNAL -> 1;
        };
    }

    private int getDaysForRole(UserRole role) {
        return switch (role) {
            case STUDENT -> 7;
            case PROFESSOR -> 15;
            case EXTERNAL -> 3;
        };
    }
}