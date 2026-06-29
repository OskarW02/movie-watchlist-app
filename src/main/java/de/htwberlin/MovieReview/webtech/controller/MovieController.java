package de.htwberlin.MovieReview.webtech.controller;

import de.htwberlin.MovieReview.webtech.model.Movie;
import de.htwberlin.MovieReview.webtech.repository.MovieRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        methods = {
                RequestMethod.GET,
                RequestMethod.POST,
                RequestMethod.PUT,
                RequestMethod.DELETE,
                RequestMethod.OPTIONS
        }
)
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
        if (movie.getWatched() == null) {
            movie.setWatched(false);
        }

        if (movie.getComment() == null) {
            movie.setComment("");
        }

        return movieRepository.save(movie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie updatedMovie) {
        return movieRepository.findById(id)
                .map(movie -> {
                    movie.setTitle(updatedMovie.getTitle());
                    movie.setRating(updatedMovie.getRating());
                    movie.setReleaseYear(updatedMovie.getReleaseYear());
                    movie.setCriticRating(updatedMovie.getCriticRating());
                    movie.setExternalId(updatedMovie.getExternalId());
                    movie.setWatched(updatedMovie.getWatched());
                    movie.setComment(updatedMovie.getComment());

                    Movie savedMovie = movieRepository.save(movie);
                    return ResponseEntity.ok(savedMovie);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieRepository.deleteById(id);
    }

    @GetMapping("/ping")
    public String ping() {
        return "UPDATE_VERSION";
    }
}