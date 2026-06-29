package de.htwberlin.MovieReview.webtech.dto;

public class MovieSuggestion {

    private String title;
    private Integer releaseYear;
    private String externalId;

    public MovieSuggestion() {
    }

    public MovieSuggestion(String title, Integer releaseYear, String externalId) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.externalId = externalId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
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

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}