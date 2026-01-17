package com.mylist.backend.controller;

import com.mylist.backend.model.*;
import com.mylist.backend.service.ItemService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow All Access for Deploy
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // --- Watch ---
    @GetMapping("/watch")
    public List<WatchItem> getWatchItems() {
        return itemService.getAllWatchItems();
    }

    @PostMapping("/watch")
    public WatchItem createWatchItem(@RequestBody WatchItem item) {
        return itemService.createWatchItem(item);
    }

    @PutMapping("/watch/{id}")
    public WatchItem updateWatchItem(@PathVariable Long id, @RequestBody WatchItem item) {
        return itemService.updateWatchItem(id, item);
    }

    // --- Upload ---
    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        java.io.File directory = new java.io.File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir + fileName);
        java.nio.file.Files.write(filePath, file.getBytes());

        return "/uploads/" + fileName;
    }

    @DeleteMapping("/watch/{id}")
    public void deleteWatchItem(@PathVariable Long id) {
        itemService.deleteWatchItem(id);
    }

    // --- Place ---
    @GetMapping("/places")
    public List<PlaceItem> getPlaceItems() {
        return itemService.getAllPlaceItems();
    }

    @PostMapping("/places")
    public PlaceItem createPlaceItem(@RequestBody PlaceItem item) {
        return itemService.createPlaceItem(item);
    }

    @PutMapping("/places/{id}")
    public PlaceItem updatePlaceItem(@PathVariable Long id, @RequestBody PlaceItem item) {
        return itemService.updatePlaceItem(id, item);
    }

    @DeleteMapping("/places/{id}")
    public void deletePlaceItem(@PathVariable Long id) {
        itemService.deletePlaceItem(id);
    }

    // --- Book ---
    @GetMapping("/books")
    public List<BookItem> getBookItems() {
        return itemService.getAllBookItems();
    }

    @PostMapping("/books")
    public BookItem createBookItem(@RequestBody BookItem item) {
        return itemService.createBookItem(item);
    }

    @PutMapping("/books/{id}")
    public BookItem updateBookItem(@PathVariable Long id, @RequestBody BookItem item) {
        return itemService.updateBookItem(id, item);
    }

    @DeleteMapping("/books/{id}")
    public void deleteBookItem(@PathVariable Long id) {
        itemService.deleteBookItem(id);
    }

    // --- Dashboard ---
    @GetMapping("/dashboard/stats")
    public Map<String, Object> getStats() {
        return itemService.getDashboardStats();
    }

    @GetMapping("/dashboard/top-picks")
    public Map<String, BaseItem> getTopPicks() {
        return itemService.getTopPicks();
    }

    @GetMapping("/dashboard/recent-reviews")
    public List<BaseItem> getRecentReviews() {
        return itemService.getRecentCompletedItems();
    }
}
