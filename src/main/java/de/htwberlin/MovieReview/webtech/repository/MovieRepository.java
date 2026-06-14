package de.htwberlin.MovieReview.webtech.repository;

import de.htwberlin.MovieReview.webtech.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}