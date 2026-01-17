package com.mylist.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExternalMovieService {

    @Value("${omdb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "https://www.omdbapi.com/";

    // Search for movies (list)
    public List<Map<String, Object>> searchMovies(String query, int page) {
        if (apiKey == null || apiKey.contains("YOUR_OMDB_API_KEY_HERE")) {
            System.err.println("OMDB API Key is missing or invalid.");
            return Collections.emptyList();
        }

        String url = String.format("%s?apikey=%s&s=%s&page=%d", BASE_URL, apiKey.trim(), query, page);
        System.out.println("Searching OMDb for: " + query); // Debug Log

        try {
            Map result = restTemplate.getForObject(url, Map.class);
            if (result != null && result.containsKey("Search")) {
                List<Map<String, Object>> searchResults = (List<Map<String, Object>>) result.get("Search");
                System.out.println("Found " + searchResults.size() + " results.");
                return searchResults.stream()
                        .map(m -> {
                            Map<String, Object> map = new java.util.HashMap<>();
                            map.put("imdbID", m.get("imdbID"));
                            map.put("title", m.get("Title"));
                            map.put("year", m.get("Year"));
                            String poster = (String) m.get("Poster");
                            map.put("posterUrl", (poster != null && !poster.equals("N/A")) ? poster : null);
                            return map;
                        })
                        .collect(Collectors.toList());
            } else {
                System.out.println("No results found or 'Search' key missing. Response: " + result);
                if (result != null && result.containsKey("Error")) {
                    System.err.println("OMDb Error: " + result.get("Error"));
                }
            }
        } catch (Exception e) {
            System.err.println("Exception while searching OMDb: " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    // Get full details including genres
    public Map<String, Object> getMovieDetails(String imdbId) {
        if (apiKey == null || apiKey.contains("YOUR_OMDB_API_KEY_HERE")) {
            return Collections.emptyMap();
        }

        String url = String.format("%s?apikey=%s&i=%s", BASE_URL, apiKey.trim(), imdbId);

        try {
            Map m = restTemplate.getForObject(url, Map.class);
            if (m != null) {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("title", m.get("Title"));
                map.put("description", m.get("Plot"));
                map.put("year", m.get("Year"));
                String poster = (String) m.get("Poster");
                map.put("posterUrl", (poster != null && !poster.equals("N/A")) ? poster : null);

                // Parse Genres: "Action, Sci-Fi" -> ["Action", "Sci-Fi"]
                String genreStr = (String) m.get("Genre");
                if (genreStr != null && !genreStr.equals("N/A")) {
                    List<String> genres = java.util.Arrays.stream(genreStr.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                    map.put("genres", genres);
                } else {
                    map.put("genres", Collections.emptyList());
                }

                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
}
