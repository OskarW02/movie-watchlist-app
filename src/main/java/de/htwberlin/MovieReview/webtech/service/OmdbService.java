package de.htwberlin.MovieReview.webtech.service;

import de.htwberlin.MovieReview.webtech.dto.MovieDetails;
import de.htwberlin.MovieReview.webtech.dto.MovieSuggestion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OmdbService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${omdb.api.key}")
    private String apiKey;

    public List<MovieSuggestion> searchMovies(String title) {
        String url = UriComponentsBuilder
                .fromUriString("https://www.omdbapi.com/")
                .queryParam("apikey", apiKey)
                .queryParam("s", title)
                .queryParam("type", "movie")
                .toUriString();

        Map response = restTemplate.getForObject(url, Map.class);

        List<MovieSuggestion> suggestions = new ArrayList<>();

        if (response == null || !"True".equals(response.get("Response"))) {
            return suggestions;
        }

        List<Map<String, Object>> searchResults = (List<Map<String, Object>>) response.get("Search");

        if (searchResults == null) {
            return suggestions;
        }

        for (Map<String, Object> result : searchResults) {
            String movieTitle = (String) result.get("Title");
            String yearText = (String) result.get("Year");
            String imdbId = (String) result.get("imdbID");

            Integer releaseYear = parseYear(yearText);

            suggestions.add(new MovieSuggestion(movieTitle, releaseYear, imdbId));
        }

        return suggestions;
    }

    public MovieDetails getMovieDetails(String externalId) {
        String url = UriComponentsBuilder
                .fromUriString("https://www.omdbapi.com/")
                .queryParam("apikey", apiKey)
                .queryParam("i", externalId)
                .queryParam("type", "movie")
                .toUriString();

        Map response = restTemplate.getForObject(url, Map.class);

        if (response == null || !"True".equals(response.get("Response"))) {
            return null;
        }

        String title = (String) response.get("Title");
        String yearText = (String) response.get("Year");
        String imdbRatingText = (String) response.get("imdbRating");
        String imdbId = (String) response.get("imdbID");

        Integer releaseYear = parseYear(yearText);
        Double criticRating = parseDouble(imdbRatingText);

        return new MovieDetails(title, releaseYear, criticRating, imdbId);
    }

    private Integer parseYear(String yearText) {
        if (yearText == null || yearText.length() < 4) {
            return null;
        }

        try {
            return Integer.parseInt(yearText.substring(0, 4));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.equalsIgnoreCase("N/A")) {
            return null;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}