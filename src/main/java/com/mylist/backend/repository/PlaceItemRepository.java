package com.mylist.backend.repository;

import com.mylist.backend.model.PlaceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceItemRepository extends JpaRepository<PlaceItem, Long> {
    List<PlaceItem> findByIsCompletedFalse();

    List<PlaceItem> findByIsCompletedTrueOrderByUpdatedDateDesc();

    long countByIsCompleted(boolean isCompleted);
}
