package com.phantam.moviedesktopapp.Service;

import com.phantam.moviedesktopapp.Controller.MainViewController;
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
    private static final String API_KEY = "353b77db8adc458f0629009286d18b41";
    private static final String BASE_URL = "https://api.themoviedb.org/3";

    /**
     * Lấy danh sách phim phổ biến từ TMDb theo trang.
     *
     * @param page Số trang (1, 2, 3...)
     * @return Danh sách Movie
     */
    private static List<Movie> fetchPopularMovies(int page) {
        List<Movie> movies = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(10); // tối đa 10 luồng
        List<Future<Movie>> futureMovies = new ArrayList<>();

        try {
            URL url = new URL(BASE_URL + "/movie/popular?api_key=" + API_KEY + "&language=vi-VN&page=" + page);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray results = json.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);
                int movieId = obj.getInt("id");

                futureMovies.add(executor.submit(() -> {
                    JSONObject detail = fetchMovieDetail(movieId);
                    if (detail == null) return null;

                    JSONArray genreArray = detail.getJSONArray("genres");
                    List<String> genreNames = new ArrayList<>();
                    for (int j = 0; j < genreArray.length(); j++) {
                        genreNames.add(genreArray.getJSONObject(j).getString("name"));
                    }

                    int runtime = detail.optInt("runtime", 0); // nếu không có thì = 0

                    return new Movie(
                            movieId,
                            detail.getString("title"),
                            detail.getString("overview"),
                            detail.getString("poster_path"),
                            detail.getString("release_date"),
                            detail.getDouble("vote_average"),
                            String.join(", ", genreNames),
                            runtime
                    );
                }));
            }

            // Đợi các movie xử lý xong
            for (Future<Movie> f : futureMovies) {
                try {
                    movies.add(f.get()); // chờ từng movie hoàn thành
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown(); // đóng pool
        }

        return movies;
    }

    /**
     * Lấy danh sách phim sắp chiếu từ TMDb theo trang.
     *
     * @param page Số trang (1, 2, 3...)
     * @return Danh sách Movie
     */
    private static List<Movie> fetchUpComingMovies(int page) {
        List<Movie> movies = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(10); // tối đa 10 luồng
        List<Future<Movie>> futureMovies = new ArrayList<>();

        try {
            URL url = new URL(BASE_URL + "/movie/upcoming?api_key=" + API_KEY + "&language=vi-VN&page=" + page);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray results = json.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);
                int movieId = obj.getInt("id");

                futureMovies.add(executor.submit(() -> {
                    JSONObject detail = fetchMovieDetail(movieId);
                    if (detail == null) return null;

                    JSONArray genreArray = detail.getJSONArray("genres");
                    List<String> genreNames = new ArrayList<>();
                    for (int j = 0; j < genreArray.length(); j++) {
                        genreNames.add(genreArray.getJSONObject(j).getString("name"));
                    }

                    int runtime = detail.optInt("runtime", 0); // nếu không có thì = 0

                    return new Movie(
                            movieId,
                            detail.getString("title"),
                            detail.getString("overview"),
                            detail.getString("poster_path"),
                            detail.getString("release_date"),
                            detail.getDouble("vote_average"),
                            String.join(", ", genreNames),
                            runtime
                    );
                }));
            }

            // Đợi các movie xử lý xong
            for (Future<Movie> f : futureMovies) {
                try {
                    movies.add(f.get()); // chờ từng movie hoàn thành
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown(); // đóng pool
        }

        return movies;
    }

    /**
     * Lấy danh sách phim sắp chiếu từ TMDb theo trang.
     *
     * @param page Số trang (1, 2, 3...)
     * @return Danh sách Movie
     */
    private static List<Movie> fetchTopRatedMovies(int page) {
        List<Movie> movies = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(10); // tối đa 10 luồng
        List<Future<Movie>> futureMovies = new ArrayList<>();

        try {
            URL url = new URL(BASE_URL + "/movie/top_rated?api_key=" + API_KEY + "&language=vi-VN&page=" + page);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray results = json.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);
                int movieId = obj.getInt("id");

                futureMovies.add(executor.submit(() -> {
                    JSONObject detail = fetchMovieDetail(movieId);
                    if (detail == null) return null;

                    JSONArray genreArray = detail.getJSONArray("genres");
                    List<String> genreNames = new ArrayList<>();
                    for (int j = 0; j < genreArray.length(); j++) {
                        genreNames.add(genreArray.getJSONObject(j).getString("name"));
                    }

                    int runtime = detail.optInt("runtime", 0); // nếu không có thì = 0

                    return new Movie(
                            movieId,
                            detail.getString("title"),
                            detail.getString("overview"),
                            detail.getString("poster_path"),
                            detail.getString("release_date"),
                            detail.getDouble("vote_average"),
                            String.join(", ", genreNames),
                            runtime
                    );
                }));
            }

            // Đợi các movie xử lý xong
            for (Future<Movie> f : futureMovies) {
                try {
                    movies.add(f.get()); // chờ từng movie hoàn thành
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown(); // đóng pool
        }

        return movies;
    }

    public static List<Movie> fetchMoviesByCategory(MainViewController.MovieCategory category, int page) {
        switch (category) {
            case POPULAR:
                return fetchPopularMovies(page);
            case UPCOMING:
                return fetchUpComingMovies(page);
            case TOP_RATED:
                return fetchTopRatedMovies(page);
            default:
                return fetchPopularMovies(page);
        }
    }

    public static List<Movie> fetchMoviesByGenre(int genreId, int page) {
        List<Movie> movies = new ArrayList<>();

        try {
            URL url = new URL(BASE_URL + "/discover/movie?api_key=" + API_KEY +
                    "&language=vi-VN&with_genres=" + genreId + "&page=" + page);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray resultArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject obj = resultArray.getJSONObject(i);
                int movieId = obj.getInt("id");

                // Lấy chi tiết phim (hoặc nếu muốn nhanh, dùng luôn obj)
                JSONObject detail = TMDbService.fetchMovieDetail(movieId);
                if (detail == null) continue;

                JSONArray genreArray = detail.optJSONArray("genres");
                List<String> genreNames = new ArrayList<>();
                if (genreArray != null) {
                    for (int j = 0; j < genreArray.length(); j++) {
                        genreNames.add(genreArray.getJSONObject(j).getString("name"));
                    }
                }

                int runtime = detail.optInt("runtime", 0);

                movies.add(new Movie(
                        movieId,
                        detail.getString("title"),
                        detail.getString("overview"),
                        detail.getString("poster_path"),
                        detail.optString("release_date", ""),
                        detail.optDouble("vote_average", 0),
                        String.join(", ", genreNames),
                        runtime
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movies;
    }

    /**
     * Lấy danh sách thể loại phim (genre) từ TMDb và trả về dưới dạng Map<id, name>.
     */
    public static Map<Integer, String> fetchGenreMap() {
        Map<Integer, String> genreMap = new HashMap<>();

        try {
            String apiUrl = BASE_URL + "/genre/movie/list?api_key=" + API_KEY;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray genresArray = json.getJSONArray("genres");

            for (int i = 0; i < genresArray.length(); i++) {
                JSONObject genreObj = genresArray.getJSONObject(i);
                int id = genreObj.getInt("id");
                String name = genreObj.getString("name");
                genreMap.put(id, name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return genreMap;
    }

    /**
     * Lấy danh sách phim theo thể loại từ một danh mục (category) cụ thể.
     *
     * @param page     Số trang (1, 2, 3...)
     * @param genreId  ID của thể loại
     * @return Danh sách Movie
     */
    public static List<Movie> fetchGenreFormCategoryMovies(int page, int genreId) {
        List<Movie> movies = new ArrayList<>();
        Map<Integer, String> genreMap = fetchGenreMap(); // Map id → name

        try {
            String apiUrl = BASE_URL + "/discover/movie" +
                    "?api_key=" + API_KEY +
                    "&language=vi-VN" +
                    "&sort_by=popularity.desc" +
                    "&with_genres=" + genreId +
                    "&page=" + page;

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray results = json.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.getJSONObject(i);

                int movieId = obj.getInt("id");
                // Lấy chi tiết phim (hoặc nếu muốn nhanh, dùng luôn obj)
                JSONObject detail = TMDbService.fetchMovieDetail(movieId);
                if (detail == null) continue;
                String title = obj.optString("title", "Không rõ tiêu đề");
                String overview = obj.optString("overview", "");
                String posterPath = obj.optString("poster_path", "");
                String releaseDate = obj.optString("release_date", "");
                double voteAverage = obj.optDouble("vote_average", 0.0);

                // Genre name mapping
                JSONArray genreIds = obj.getJSONArray("genre_ids");
                List<String> genreNames = new ArrayList<>();
                for (int j = 0; j < genreIds.length(); j++) {
                    int id = genreIds.getInt(j);
                    if (genreMap.containsKey(id)) {
                        genreNames.add(genreMap.get(id));
                    }
                }

                int runtime = detail.optInt("runtime", 0);

                Movie movie = new Movie(
                        movieId,
                        title,
                        overview,
                        posterPath,
                        releaseDate,
                        voteAverage,
                        String.join(", ", genreNames),
                        runtime
                );

                movies.add(movie);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movies;
    }


    /**
     * Tìm kiếm phim theo từ khóa.
     *
     * @param query Từ khóa tìm kiếm
     * @param page  Số trang (1, 2, 3...)
     * @return Danh sách Movie
     */
    public static List<Movie> fetchMoviesBySearch(String query, int page) {
        List<Movie> movies = new ArrayList<>();

        try {
            URL url = new URL(BASE_URL + "/search/movie?api_key=" + API_KEY +
                    "&language=vi-VN&query=" + query + "&page=" + page);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray resultArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject obj = resultArray.getJSONObject(i);
                int movieId = obj.getInt("id");

                // Lấy chi tiết phim (hoặc nếu muốn nhanh, dùng luôn obj)
                JSONObject detail = TMDbService.fetchMovieDetail(movieId);
                if (detail == null) continue;

                JSONArray genreArray = detail.optJSONArray("genres");
                List<String> genreNames = new ArrayList<>();
                if (genreArray != null) {
                    for (int j = 0; j < genreArray.length(); j++) {
                        genreNames.add(genreArray.getJSONObject(j).getString("name"));
                    }
                }

                int runtime = detail.optInt("runtime", 0);

                movies.add(new Movie(
                        movieId,
                        detail.getString("title"),
                        detail.getString("overview"),
                        detail.getString("poster_path"),
                        detail.optString("release_date", ""),
                        detail.optDouble("vote_average", 0),
                        String.join(", ", genreNames),
                        runtime
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return movies;
    }


    /**
     * Lấy thông tin diễn viên và đoàn làm phim của một bộ phim.
     *
     * @param movieId ID của bộ phim
     * @return Danh sách Person (diễn viên, đạo diễn, nhà sản xuất...)
     */
    public static List<Actor> fetchMovieCredits(int movieId) {
        List<Actor> credits = new ArrayList<>();

        try {
            URL url = new URL(BASE_URL + "/movie/" + movieId + "/credits?api_key=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();

            JSONObject json = new JSONObject(response.toString());

            // Diễn viên
            JSONArray cast = json.getJSONArray("cast");
            for (int i = 0; i < Math.min(cast.length(), 10); i++) {
                JSONObject actor = cast.getJSONObject(i);
                String name = actor.getString("name");
                credits.add(new Actor(name, "Actor", actor.optString("profile_path", null)));
            }

            // Đạo diễn và những người khác
            JSONArray crew = json.getJSONArray("crew");
            for (int i = 0; i < crew.length(); i++) {
                JSONObject member = crew.getJSONObject(i);
                String job = member.getString("job");
                if (job.equals("Director") || job.equals("Producer") || job.equals("Writer")) {
                    String name = member.getString("name");
                    credits.add(new Actor(name, job, member.optString("profile_path", null)));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return credits;
    }

    /**
     * Lấy chi tiết của một bộ phim.
     *
     * @param movieId ID của bộ phim
     * @return JSONObject chứa thông tin chi tiết của bộ phim
     */
    public static JSONObject fetchMovieDetail(int movieId) {
        try {
            URL url = new URL(BASE_URL + "/movie/" + movieId + "?api_key=" + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();

            return new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String fetchYouTubeTrailer(int movieId) {
        try {
            String urlString = BASE_URL + "/movie/" + movieId + "/videos?api_key=" + API_KEY;
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) jsonBuilder.append(line);
            reader.close();

            JSONObject json = new JSONObject(jsonBuilder.toString());
            JSONArray results = json.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject video = results.getJSONObject(i);
                if ("Trailer".equalsIgnoreCase(video.getString("type")) &&
                        "YouTube".equalsIgnoreCase(video.getString("site"))) {

                    String key = video.getString("key");
                    return "https://www.youtube.com/embed/" + key + "?autoplay=1&modestbranding=1&rel=0";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
