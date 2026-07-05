package de.htwberlin.MovieReview.webtech.controller;

import de.htwberlin.MovieReview.webtech.dto.MovieDetails;
import de.htwberlin.MovieReview.webtech.dto.MovieSuggestion;
import de.htwberlin.MovieReview.webtech.model.Movie;
import de.htwberlin.MovieReview.webtech.repository.MovieRepository;
import de.htwberlin.MovieReview.webtech.service.OmdbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.mockito.ArgumentCaptor;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private OmdbService omdbService;

    private MovieController movieController;
    private ExternalMovieController externalMovieController;

    @BeforeEach
    void setUp() {
        movieController = new MovieController(movieRepository);
        externalMovieController = new ExternalMovieController(omdbService);
    }

    @Test
    void testSetupWorks() {
        assertNotNull(movieController);
        assertNotNull(externalMovieController);
    }

    @Test
    void getMoviesReturnsAllMovies() {
        Movie movie1 = createMovie(1L, "Interstellar");
        Movie movie2 = createMovie(2L, "Inception");

        when(movieRepository.findAll()).thenReturn(List.of(movie1, movie2));

        List<Movie> result = movieController.getMovies();

        assertEquals(2, result.size());
        assertEquals("Interstellar", result.get(0).getTitle());
        assertEquals("Inception", result.get(1).getTitle());

        verify(movieRepository).findAll();
    }

    @Test
    void addMovieSavesMovie() {
        Movie movieToSave = createMovie(null, "Interstellar");

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setId(1L);
            return movie;
        });

        Movie savedMovie = movieController.addMovie(movieToSave);

        assertNotNull(savedMovie);
        assertEquals(1L, savedMovie.getId());
        assertEquals("Interstellar", savedMovie.getTitle());

        verify(movieRepository).save(movieToSave);
    }

    @Test
    void addMovieSetsWatchedFalseWhenWatchedIsMissing() {
        Movie movieToSave = createMovie(null, "Interstellar");
        movieToSave.setWatched(null);

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Movie savedMovie = movieController.addMovie(movieToSave);

        assertFalse(savedMovie.getWatched());

        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository).save(captor.capture());

        assertFalse(captor.getValue().getWatched());
    }

    @Test
    void addMovieSetsEmptyCommentWhenCommentIsMissing() {
        Movie movieToSave = createMovie(null, "Interstellar");
        movieToSave.setComment(null);

        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Movie savedMovie = movieController.addMovie(movieToSave);

        assertEquals("", savedMovie.getComment());

        ArgumentCaptor<Movie> captor = ArgumentCaptor.forClass(Movie.class);
        verify(movieRepository).save(captor.capture());

        assertEquals("", captor.getValue().getComment());
    }

    @Test
    void updateMovieUpdatesExistingMovie() {
        Movie existingMovie = createMovie(1L, "Old Title");

        Movie updatedMovie = createMovie(1L, "Interstellar");
        updatedMovie.setRating(8.5);
        updatedMovie.setWatched(true);
        updatedMovie.setComment("Sehr guter Film");
        updatedMovie.setCriticRating(8.7);
        updatedMovie.setExternalId("tt0816692");
        updatedMovie.setPosterUrl("https://example.com/poster.jpg");

        when(movieRepository.findById(1L)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Movie> response = movieController.updateMovie(1L, updatedMovie);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        Movie result = response.getBody();

        assertEquals("Interstellar", result.getTitle());
        assertEquals(2014, result.getReleaseYear());
        assertEquals(8.5, result.getRating());
        assertEquals(8.7, result.getCriticRating());
        assertEquals("tt0816692", result.getExternalId());
        assertEquals("https://example.com/poster.jpg", result.getPosterUrl());
        assertEquals(true, result.getWatched());
        assertEquals("Sehr guter Film", result.getComment());

        verify(movieRepository).findById(1L);
        verify(movieRepository).save(existingMovie);
    }

    @Test
    void updateMovieReturns404ForUnknownMovie() {
        Movie updatedMovie = createMovie(999L, "Interstellar");

        when(movieRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Movie> response = movieController.updateMovie(999L, updatedMovie);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(movieRepository).findById(999L);
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void deleteMovieDeletesMovieById() {
        movieController.deleteMovie(1L);

        verify(movieRepository).deleteById(1L);
    }

    @Test
    void searchExternalMoviesReturnsSuggestions() {
        MovieSuggestion suggestion = new MovieSuggestion(
                "Interstellar",
                2014,
                "tt0816692"
        );

        when(omdbService.searchMovies("interstellar")).thenReturn(List.of(suggestion));

        List<MovieSuggestion> result = externalMovieController.searchMovies("interstellar");

        assertEquals(1, result.size());
        assertEquals("Interstellar", result.get(0).getTitle());
        assertEquals(2014, result.get(0).getReleaseYear());
        assertEquals("tt0816692", result.get(0).getExternalId());

        verify(omdbService).searchMovies("interstellar");
    }

    @Test
    void getExternalMovieDetailsReturnsDetails() {
        MovieDetails details = new MovieDetails(
                "Interstellar",
                2014,
                8.7,
                "tt0816692",
                "https://example.com/poster.jpg"
        );

        when(omdbService.getMovieDetails("tt0816692")).thenReturn(details);

        ResponseEntity<MovieDetails> response = externalMovieController.getMovieDetails("tt0816692");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        MovieDetails result = response.getBody();

        assertEquals("Interstellar", result.getTitle());
        assertEquals(2014, result.getReleaseYear());
        assertEquals(8.7, result.getCriticRating());
        assertEquals("tt0816692", result.getExternalId());
        assertEquals("https://example.com/poster.jpg", result.getPosterUrl());

        verify(omdbService).getMovieDetails("tt0816692");
    }

    @Test
    void getExternalMovieDetailsReturns404WhenMovieIsNotFound() {
        when(omdbService.getMovieDetails("unknown-id")).thenReturn(null);

        ResponseEntity<MovieDetails> response = externalMovieController.getMovieDetails("unknown-id");

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(omdbService).getMovieDetails("unknown-id");
    }

    private Movie createMovie(Long id, String title) {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle(title);
        movie.setReleaseYear(2014);
        movie.setRating(null);
        movie.setCriticRating(8.7);
        movie.setExternalId("tt0816692");
        movie.setPosterUrl("https://example.com/poster.jpg");
        movie.setWatched(false);
        movie.setComment("");
        return movie;
    }

}