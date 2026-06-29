package de.htwberlin.MovieReview.webtech.dto;

public class MovieDetails {

    private String title;
    private Integer releaseYear;
    private Double criticRating;
    private String externalId;
    private String posterUrl;

    public MovieDetails() {
    }

    public MovieDetails(String title, Integer releaseYear, Double criticRating, String externalId, String posterUrl) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.criticRating = criticRating;
        this.externalId = externalId;
        this.posterUrl = posterUrl;
    }

    public String getTitle() {
        return title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public Double getCriticRating() {
        return criticRating;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setCriticRating(Double criticRating) {
        this.criticRating = criticRating;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}