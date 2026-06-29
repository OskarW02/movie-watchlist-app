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
                .build()
                .toUriString();

        Map<?, ?> response = restTemplate.getForObject(url, Map.class);

        List<MovieSuggestion> suggestions = new ArrayList<>();

        if (response == null || !"True".equals(response.get("Response"))) {
            return suggestions;
        }

        Object searchObject = response.get("Search");

        if (!(searchObject instanceof List<?> searchResults)) {
            return suggestions;
        }

        for (Object item : searchResults) {
            if (!(item instanceof Map<?, ?> result)) {
                continue;
            }

            String movieTitle = getString(result, "Title");
            String yearText = getString(result, "Year");
            String imdbId = getString(result, "imdbID");

            suggestions.add(new MovieSuggestion(
                    movieTitle,
                    parseYear(yearText),
                    imdbId
            ));
        }

        return suggestions;
    }

    public MovieDetails getMovieDetails(String externalId) {
        String url = UriComponentsBuilder
                .fromUriString("https://www.omdbapi.com/")
                .queryParam("apikey", apiKey)
                .queryParam("i", externalId)
                .queryParam("type", "movie")
                .build()
                .toUriString();

        Map<?, ?> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !"True".equals(response.get("Response"))) {
            return null;
        }

        String title = getString(response, "Title");
        String yearText = getString(response, "Year");
        String imdbRatingText = getString(response, "imdbRating");
        String imdbId = getString(response, "imdbID");
        String posterUrl = getString(response, "Poster");

        return new MovieDetails(
                title,
                parseYear(yearText),
                parseDouble(imdbRatingText),
                imdbId,
                posterUrl
        );
    }

    private String getString(Map<?, ?> map, String key) {
        Object value = map.get(key);

        if (value == null) {
            return null;
        }

        String text = String.valueOf(value);

        if (text.equalsIgnoreCase("N/A") || text.equalsIgnoreCase("null")) {
            return null;
        }

        return text;
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
        if (value == null) {
            return null;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}