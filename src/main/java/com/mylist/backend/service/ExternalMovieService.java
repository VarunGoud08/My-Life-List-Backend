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
    private final String BASE_URL = "http://www.omdbapi.com/";

    // Search for movies (list)
    public List<Map<String, Object>> searchMovies(String query, int page) {
        if (apiKey == null || apiKey.contains("YOUR_OMDB_API_KEY_HERE")) {
            return Collections.emptyList();
        }

        // Removed &type=movie to allow searching for Series and Episodes as well
        String url = String.format("%s?apikey=%s&s=%s&page=%d", BASE_URL, apiKey.trim(), query, page);

        try {
            Map result = restTemplate.getForObject(url, Map.class);
            if (result != null && result.containsKey("Search")) {
                List<Map<String, Object>> searchResults = (List<Map<String, Object>>) result.get("Search");

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
            }
        } catch (Exception e) {
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
