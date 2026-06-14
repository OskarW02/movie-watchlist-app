package de.htwberlin.MovieReview.webtech.controller;

import de.htwberlin.MovieReview.webtech.model.Movie;
import de.htwberlin.MovieReview.webtech.repository.MovieRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/movies")
public class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }

    @PostMapping
    public Movie addMovie(@RequestBody Movie movie) {
        return movieRepository.save(movie);
    }
}