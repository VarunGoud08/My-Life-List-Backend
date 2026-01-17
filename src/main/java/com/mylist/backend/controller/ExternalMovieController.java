package com.mylist.backend.controller;

import com.mylist.backend.service.ExternalMovieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/external")
@CrossOrigin(origins = "http://localhost:5173")
public class ExternalMovieController {

    private final ExternalMovieService externalMovieService;

    public ExternalMovieController(ExternalMovieService externalMovieService) {
        this.externalMovieService = externalMovieService;
    }

    @GetMapping("/search")
    public List<Map<String, Object>> search(@RequestParam String query, @RequestParam(defaultValue = "1") int page) {
        return externalMovieService.searchMovies(query, page);
    }

    @GetMapping("/details")
    public Map<String, Object> getDetails(@RequestParam String id) {
        return externalMovieService.getMovieDetails(id);
    }
}
