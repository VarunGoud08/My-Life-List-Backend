package com.mylist.backend.controller;

import com.mylist.backend.service.ExternalPlaceService;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external/places")
@CrossOrigin(origins = "http://localhost:5173")
public class ExternalPlaceController {

    private final ExternalPlaceService placeService;

    public ExternalPlaceController(ExternalPlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/geocode")
    public Map<String, Double> geocode(@RequestParam String query) {
        return placeService.getCoordinates(query);
    }

    @GetMapping("/search")
    public List<Map<String, Object>> search(
            @RequestParam(required = false) String location,
            @RequestParam String category) {

        Double lat = null;
        Double lon = null;

        if (location != null && !location.isEmpty()) {
            Map<String, Double> coords = placeService.getCoordinates(location);
            if (coords != null) {
                lat = coords.get("lat");
                lon = coords.get("lon");
            }
        }

        // If we didn't find coords and location was provided, maybe return empty
        // But for "Global" search we might want defaults? For now strict on location
        if (location != null && lat == null) {
            return Collections.emptyList();
        }

        return placeService.searchPlaces(category, location, lat, lon);
    }
}
