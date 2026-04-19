package de.htwberlin.MovieReview.webtech.controller;

import de.htwberlin.MovieReview.webtech.model.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovieController {

    @GetMapping("/movies")
    public List<Movie> getMovies() {

        return List.of(
                new Movie(1L, "Inception", 2010),
                new Movie(2L, "Interstellar", 2014),
                new Movie(3L, "The Matrix", 1999)
        );
    }
}