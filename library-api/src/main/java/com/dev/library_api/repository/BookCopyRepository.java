package com.dev.library_api.repository;

import com.dev.library_api.model.BookCopy;
import com.dev.library_api.model.CopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findByBookIdAndStatus(Long bookId, CopyStatus status);
}