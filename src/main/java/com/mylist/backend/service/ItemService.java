package com.mylist.backend.service;

import com.mylist.backend.model.*;
import com.mylist.backend.repository.BookItemRepository;
import com.mylist.backend.repository.PlaceItemRepository;
import com.mylist.backend.repository.WatchItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final WatchItemRepository watchItemRepository;
    private final PlaceItemRepository placeItemRepository;
    private final BookItemRepository bookItemRepository;

    // Comparator for Eagerness: High > Medium > Low
    private final Comparator<BaseItem> eagernessComparator = Comparator.comparing(BaseItem::getEagernessLevel)
            .reversed();

    public ItemService(WatchItemRepository watchItemRepository,
            PlaceItemRepository placeItemRepository,
            BookItemRepository bookItemRepository) {
        this.watchItemRepository = watchItemRepository;
        this.placeItemRepository = placeItemRepository;
        this.bookItemRepository = bookItemRepository;
    }

    // --- Watch Items ---
    public List<WatchItem> getAllWatchItems() {
        return watchItemRepository.findAll().stream()
                .sorted(eagernessComparator)
                .collect(Collectors.toList());
    }

    public WatchItem createWatchItem(WatchItem item) {
        return watchItemRepository.save(item);
    }

    public WatchItem updateWatchItem(Long id, WatchItem updated) {
        return watchItemRepository.findById(id).map(item -> {
            updateBaseFields(item, updated);
            item.setType(updated.getType());
            item.setGenres(updated.getGenres());
            item.setCategory(updated.getCategory());
            return watchItemRepository.save(item);
        }).orElseThrow(() -> new RuntimeException("WatchItem not found"));
    }

    public void deleteWatchItem(Long id) {
        watchItemRepository.deleteById(id);
    }

    // --- Place Items ---
    public List<PlaceItem> getAllPlaceItems() {
        return placeItemRepository.findAll().stream()
                .sorted(eagernessComparator)
                .collect(Collectors.toList());
    }

    public PlaceItem createPlaceItem(PlaceItem item) {
        return placeItemRepository.save(item);
    }

    public PlaceItem updatePlaceItem(Long id, PlaceItem updated) {
        return placeItemRepository.findById(id).map(item -> {
            updateBaseFields(item, updated);
            item.setCategory(updated.getCategory());
            return placeItemRepository.save(item);
        }).orElseThrow(() -> new RuntimeException("PlaceItem not found"));
    }

    public void deletePlaceItem(Long id) {
        placeItemRepository.deleteById(id);
    }

    // --- Book Items ---
    public List<BookItem> getAllBookItems() {
        return bookItemRepository.findAll().stream()
                .sorted(eagernessComparator)
                .collect(Collectors.toList());
    }

    public BookItem createBookItem(BookItem item) {
        return bookItemRepository.save(item);
    }

    public BookItem updateBookItem(Long id, BookItem updated) {
        return bookItemRepository.findById(id).map(item -> {
            updateBaseFields(item, updated);
            item.setCategory(updated.getCategory());
            return bookItemRepository.save(item);
        }).orElseThrow(() -> new RuntimeException("BookItem not found"));
    }

    public void deleteBookItem(Long id) {
        bookItemRepository.deleteById(id);
    }

    // --- Dashboard logic ---
    public List<BaseItem> getRecentCompletedItems() {
        List<BaseItem> allCompleted = new ArrayList<>();
        allCompleted.addAll(watchItemRepository.findByIsCompletedTrueOrderByUpdatedDateDesc());
        allCompleted.addAll(placeItemRepository.findByIsCompletedTrueOrderByUpdatedDateDesc());
        allCompleted.addAll(bookItemRepository.findByIsCompletedTrueOrderByUpdatedDateDesc());

        return allCompleted.stream()
                .sorted(Comparator.comparing(BaseItem::getUpdatedDate).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    public java.util.Map<String, Object> getDashboardStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        // Watch
        java.util.Map<String, Long> watchStats = new java.util.HashMap<>();
        watchStats.put("active", watchItemRepository.countByIsCompleted(false));
        watchStats.put("completed", watchItemRepository.countByIsCompleted(true));
        stats.put("watch", watchStats);

        // Places
        java.util.Map<String, Long> placeStats = new java.util.HashMap<>();
        placeStats.put("active", placeItemRepository.countByIsCompleted(false));
        placeStats.put("completed", placeItemRepository.countByIsCompleted(true));
        stats.put("places", placeStats);

        // Books
        java.util.Map<String, Long> bookStats = new java.util.HashMap<>();
        bookStats.put("active", bookItemRepository.countByIsCompleted(false));
        bookStats.put("completed", bookItemRepository.countByIsCompleted(true));
        stats.put("books", bookStats);

        return stats;
    }

    public java.util.Map<String, BaseItem> getTopPicks() {
        java.util.Map<String, BaseItem> picks = new java.util.HashMap<>();

        getAllWatchItems().stream().filter(i -> !i.isCompleted()).findFirst().ifPresent(i -> picks.put("watch", i));
        getAllPlaceItems().stream().filter(i -> !i.isCompleted()).findFirst().ifPresent(i -> picks.put("place", i));
        getAllBookItems().stream().filter(i -> !i.isCompleted()).findFirst().ifPresent(i -> picks.put("book", i));

        return picks;
    }

    private void updateBaseFields(BaseItem target, BaseItem source) {
        target.setTitle(source.getTitle());
        target.setDescription(source.getDescription());
        target.setEagernessLevel(source.getEagernessLevel());
        target.setCompleted(source.isCompleted());
        target.setRating(source.getRating());
        target.setReviewComment(source.getReviewComment());
    }

}
