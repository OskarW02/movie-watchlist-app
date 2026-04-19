package de.htwberlin.MovieReview.webtech.model;

public class Movie {

    private Long id;
    private String title;
    private int year;

    public Movie(Long id, String title, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }
}