package com.dev.library_api.controller;

import com.dev.library_api.model.Book;
import com.dev.library_api.model.BookCopy;
import com.dev.library_api.repository.BookCopyRepository;
import com.dev.library_api.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/copies")
public class BookCopyController {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BookCopyController(BookCopyRepository bookCopyRepository, BookRepository bookRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
    }

    @PostMapping
    public ResponseEntity<BookCopy> createBookCopy(@RequestBody Map<String, Long> payload) {
        Long bookId = payload.get("bookId");
        Optional<Book> bookOptional = bookRepository.findById(bookId);

        if (bookOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        BookCopy bookCopy = new BookCopy();
        bookCopy.setBook(bookOptional.get());

        BookCopy savedCopy = bookCopyRepository.save(bookCopy);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCopy);
    }

    @GetMapping
    public ResponseEntity<List<BookCopy>> getAllBookCopies() {
        return ResponseEntity.ok(bookCopyRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookCopy> getBookCopyById(@PathVariable Long id) {
        return bookCopyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookCopy> updateBookCopyStatus(@PathVariable Long id, @RequestBody BookCopy copyDetails) {
        return bookCopyRepository.findById(id)
                .map(copy -> {
                    copy.setStatus(copyDetails.getStatus());
                    return ResponseEntity.ok(bookCopyRepository.save(copy));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookCopy(@PathVariable Long id) {
        return bookCopyRepository.findById(id)
                .map(copy -> {
                    bookCopyRepository.delete(copy);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}