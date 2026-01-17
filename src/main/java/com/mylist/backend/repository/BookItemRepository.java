package com.mylist.backend.repository;

import com.mylist.backend.model.BookItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookItemRepository extends JpaRepository<BookItem, Long> {
    List<BookItem> findByIsCompletedFalse();

    List<BookItem> findByIsCompletedTrueOrderByUpdatedDateDesc();

    long countByIsCompleted(boolean isCompleted);
}
