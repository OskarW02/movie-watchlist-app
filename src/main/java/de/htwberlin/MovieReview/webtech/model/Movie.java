package de.htwberlin.MovieReview.webtech.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer releaseYear;

    @Column(name = "user_rating")
    private Double rating;

    private String posterUrl;

    // Externes Rating, z. B. IMDb-Rating aus OMDb/TMDB
    private Double criticRating;

    // Externe Film-ID, z. B. IMDb-ID wie "tt0816692"
    private String externalId;

    private Boolean watched = false;

    @Column(length = 1000)
    private String comment;

    public Movie() {
    }

    public Movie(String title, Integer releaseYear, Double rating, Double criticRating, String externalId, Boolean watched, String comment) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.criticRating = criticRating;
        this.externalId = externalId;
        this.watched = watched;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public Double getRating() {
        return rating;
    }

    public Double getCriticRating() {
        return criticRating;
    }

    public String getExternalId() {
        return externalId;
    }

    public Boolean getWatched() {
        return watched;
    }

    public String getComment() {
        return comment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setCriticRating(Double criticRating) {
        this.criticRating = criticRating;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setWatched(Boolean watched) {
        this.watched = watched;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}