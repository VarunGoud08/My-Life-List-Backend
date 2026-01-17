package com.mylist.backend.repository;

import com.mylist.backend.model.WatchItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchItemRepository extends JpaRepository<WatchItem, Long> {
    List<WatchItem> findByIsCompletedFalse();

    List<WatchItem> findByIsCompletedTrueOrderByUpdatedDateDesc();

    long countByIsCompleted(boolean isCompleted);
}
