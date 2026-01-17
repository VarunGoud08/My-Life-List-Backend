package com.mylist.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExternalPlaceService {

    @Value("${geoapify.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GEOCODE_URL = "https://api.geoapify.com/v1/geocode/search";
    private static final String PLACES_URL = "https://api.geoapify.com/v2/places";

    public Map<String, Double> getCoordinates(String query) {
        if (query == null || query.trim().isEmpty())
            return null;
        if ("YOUR_GEOAPIFY_API_KEY_HERE".equals(apiKey))
            return null;

        String url = UriComponentsBuilder.fromHttpUrl(GEOCODE_URL)
                .queryParam("text", query)
                .queryParam("apiKey", apiKey)
                .queryParam("limit", 1)
                .toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("features")) {
                List<Map<String, Object>> features = (List<Map<String, Object>>) response.get("features");
                if (!features.isEmpty()) {
                    Map<String, Object> props = (Map<String, Object>) features.get(0).get("properties");
                    double lat = ((Number) props.get("lat")).doubleValue();
                    double lon = ((Number) props.get("lon")).doubleValue();
                    return Map.of("lat", lat, "lon", lon);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> searchPlaces(String category, String query, Double lat, Double lon) {
        if ("YOUR_GEOAPIFY_API_KEY_HERE".equals(apiKey))
            return Collections.emptyList();

        // Map UI category to Geoapify categories
        String apiCategory;
        switch (category) {
            case "RESTAURANT":
                apiCategory = "catering.restaurant";
                break;
            case "CAFE":
                apiCategory = "catering.cafe";
                break;
            case "TRIP": // Tourist attractions
                apiCategory = "tourism";
                break;
            case "LOCAL_SPOT":
                apiCategory = "leisure";
                break;
            default:
                apiCategory = "catering";
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(PLACES_URL)
                .queryParam("categories", apiCategory)
                .queryParam("apiKey", apiKey)
                .queryParam("limit", 20);

        if (lat != null && lon != null) {
            builder.queryParam("filter", "circle:" + lon + "," + lat + ",5000"); // 5km radius
            builder.queryParam("bias", "proximity:" + lon + "," + lat);
        }

        String url = builder.toUriString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("features")) {
                List<Map<String, Object>> features = (List<Map<String, Object>>) response.get("features");
                return features.stream().map(f -> {
                    Map<String, Object> props = (Map<String, Object>) f.get("properties");
                    return Map.of(
                            "name", props.get("name") != null ? props.get("name") : props.get("formatted"),
                            "formatted", props.get("formatted"),
                            "lat", props.get("lat"),
                            "lon", props.get("lon"),
                            "category", category);
                }).filter(m -> m.get("name") != null) // Filter unnamed places
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
