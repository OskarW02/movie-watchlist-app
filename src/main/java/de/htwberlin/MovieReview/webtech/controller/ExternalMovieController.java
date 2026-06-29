package de.htwberlin.MovieReview.webtech.controller;

import de.htwberlin.MovieReview.webtech.dto.MovieDetails;
import de.htwberlin.MovieReview.webtech.dto.MovieSuggestion;
import de.htwberlin.MovieReview.webtech.service.OmdbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/external-movies")
public class ExternalMovieController {

    private final OmdbService omdbService;

    public ExternalMovieController(OmdbService omdbService) {
        this.omdbService = omdbService;
    }

    @GetMapping("/search")
    public List<MovieSuggestion> searchMovies(@RequestParam String title) {
        return omdbService.searchMovies(title);
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<MovieDetails> getMovieDetails(@PathVariable String externalId) {
        MovieDetails details = omdbService.getMovieDetails(externalId);

        if (details == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(details);
    }
}