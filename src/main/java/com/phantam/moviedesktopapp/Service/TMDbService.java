package com.phantam.moviedesktopapp.Service;

import com.phantam.moviedesktopapp.Controller.MainViewController.MovieCategory;
import com.phantam.moviedesktopapp.Model.Actor;
import com.phantam.moviedesktopapp.Model.Movie;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TMDbService {
    // API key and base URL for The Movie Database (TMDb) API
    private static final String API_KEY = "353b77db8adc458f0629009286d18b41";
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    /*
     * Fetches movies based on the specified category and page number.
     * @param category The category of movies to fetch (e.g., popular, top_rated, upcoming).
     * @param page The page number to fetch.
     * @return A list of Movie objects containing the fetched movies.
     */
    public static List<Movie> fetchMoviesByCategory(MovieCategory category, int page) {
        String cat;
        switch (category) {
            case UPCOMING:
                cat = "upcoming";
                break;
            case TOP_RATED:
                cat = "top_rated";
                break;
            default:
                cat = "popular";
        }
        return fetchMovies(page, cat);
    }

    /*
     * Fetches movies by genre ID and page number.
     * @param genreId The ID of the genre to filter movies by.
     * @param page The page number to fetch.
     * @return A list of Movie objects containing the fetched movies.
     */
    public static List<Movie> fetchMoviesByGenre(int genreId, int page) {
        List<Movie> movies = new ArrayList<>();
        try {
            JSONArray results = getJsonArray(BASE_URL + "/discover/movie?api_key=" + API_KEY +
                    "&language=vi-VN&with_genres=" + genreId + "&page=" + page, "results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);
                int movieId = obj.getInt("id");
                JSONObject detail = fetchMovieDetail(movieId);
                if (detail == null) continue;

                JSONArray genreArray = detail.optJSONArray("genres");
                List<String> genreNames = new ArrayList<>();
                List<Integer> genreIds = new ArrayList<>();
                if (genreArray != null) {
                    for (int j = 0; j < genreArray.length(); j++) {
                        JSONObject genre = genreArray.getJSONObject(j);
                        genreNames.add(genre.getString("name"));
                        genreIds.add(genre.getInt("id"));
                    }
                }

                movies.add(new Movie(
                        movieId,
                        detail.getString("title"),
                        detail.getString("overview"),
                        detail.optString("poster_path", ""),
                        detail.optString("release_date", ""),
                        detail.optDouble("vote_average", 0),
                        String.join(", ", genreNames),
                        detail.optInt("runtime", 0),
                        genreIds
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }

    /*
     * Fetches movies based on the specified page and category.
     * @param page The page number to fetch.
     * @param category The category of movies to fetch (e.g., popular, top_rated, upcoming).
     * @return A list of Movie objects containing the fetched movies.
     */
    private static List<Movie> fetchMovies(int page, String category) {
        List<Movie> movies = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Movie>> futures = new ArrayList<>();

        try {
            String urlString = BASE_URL + "/movie/" + category + "?api_key=" + API_KEY + "&page=" + page;
            JSONArray results = getJsonArray(urlString, "results");

            for (int i = 0; i < results.length(); i++) {
                int movieId = results.getJSONObject(i).getInt("id");
                futures.add(executor.submit(() -> {
                    JSONObject detail = fetchMovieDetail(movieId);
                    if (detail == null) return null;

                    JSONArray genreArray = detail.optJSONArray("genres");
                    List<String> genreNames = new ArrayList<>();
                    List<Integer> genreIds = new ArrayList<>();
                    for (int j = 0; j < genreArray.length(); j++) {
                        JSONObject genre = genreArray.getJSONObject(j);
                        genreNames.add(genre.getString("name"));
                        genreIds.add(genre.getInt("id"));
                    }

                    return new Movie(
                            movieId,
                            detail.getString("title"),
                            detail.getString("overview"),
                            detail.optString("poster_path", ""),
                            detail.optString("release_date", ""),
                            detail.optDouble("vote_average", 0),
                            String.join(", ", genreNames),
                            detail.optInt("runtime", 0),
                            genreIds
                    );
                }));
            }

            for (Future<Movie> future : futures) {
                try {
                    Movie m = future.get();
                    if (m != null) movies.add(m);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        return movies;
    }

    /*
     * Fetches detailed information about a movie by its ID.
     * @param movieId The ID of the movie.
     * @return A JSONObject containing detailed information about the movie.
     */
    public static JSONObject fetchMovieDetail(int movieId) {
        try {
            String urlString = BASE_URL + "/movie/" + movieId + "?api_key=" + API_KEY;
            return new JSONObject(readFromUrl(urlString));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Fetches movie credits (actors and crew) for a given movie ID.
     * @param movieId The ID of the movie.
     * @return A list of Actor objects representing the cast and crew.
     */
    public static List<Actor> fetchMovieCredits(int movieId) {
        List<Actor> credits = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(readFromUrl(BASE_URL + "/movie/" + movieId + "/credits?api_key=" + API_KEY));
            JSONArray cast = json.getJSONArray("cast");
            for (int i = 0; i < Math.min(cast.length(), 10); i++) {
                JSONObject actor = cast.getJSONObject(i);
                credits.add(new Actor(actor.getString("name"), "Actor", actor.optString("profile_path", null)));
            }

            JSONArray crew = json.getJSONArray("crew");
            for (int i = 0; i < crew.length(); i++) {
                JSONObject member = crew.getJSONObject(i);
                String job = member.getString("job");
                if (job.equals("Director") || job.equals("Producer") || job.equals("Writer")) {
                    credits.add(new Actor(member.getString("name"), job, member.optString("profile_path", null)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return credits;
    }

    /*
     * Fetches a map of genre IDs to genre names.
     * @return A map where keys are genre IDs and values are genre names.
     */
    public static Map<Integer, String> fetchGenreMap() {
        Map<Integer, String> genreMap = new HashMap<>();
        try {
            JSONObject json = new JSONObject(readFromUrl(BASE_URL + "/genre/movie/list?api_key=" + API_KEY));
            JSONArray genres = json.getJSONArray("genres");
            for (int i = 0; i < genres.length(); i++) {
                JSONObject genre = genres.getJSONObject(i);
                genreMap.put(genre.getInt("id"), genre.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return genreMap;
    }

    /*
     * Fetches the YouTube trailer for a movie by its ID.
     * @param movieId The ID of the movie.
     * @return The YouTube embed URL for the trailer, or null if not found.
     */
    public static String fetchYouTubeTrailer(int movieId) {
        try {
            JSONObject json = new JSONObject(readFromUrl(BASE_URL + "/movie/" + movieId + "/videos?api_key=" + API_KEY));
            JSONArray results = json.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject video = results.getJSONObject(i);
                if ("Trailer".equalsIgnoreCase(video.getString("type")) &&
                        "YouTube".equalsIgnoreCase(video.getString("site"))) {
                    return "https://www.youtube.com/embed/" + video.getString("key") + "?autoplay=1&modestbranding=1&rel=0";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * Helper method to fetch a JSON array from a URL and extract a specific key.
     * @param url The URL to fetch the JSON from.
     * @param key The key to extract the JSON array from.
     * @return A JSONArray containing the results for the specified key.
     * @throws Exception If an error occurs while fetching or parsing the JSON.
     */
    private static JSONArray getJsonArray(String url, String key) throws Exception {
        return new JSONObject(readFromUrl(url)).getJSONArray(key);
    }

    /*
     * Helper method to read content from a URL.
     * @param urlString The URL to read from.
     * @return The content as a String.
     * @throws Exception If an error occurs while reading from the URL.
     */
    private static String readFromUrl(String urlString) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
}
