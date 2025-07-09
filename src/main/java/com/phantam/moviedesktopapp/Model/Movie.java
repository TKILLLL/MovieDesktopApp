package com.phantam.moviedesktopapp.Model;

import java.util.List;

public class Movie {
    private final int id;
    private final String title;
    private final String overview;
    private final String posterPath;
    private final String releaseDate;
    private final int runTime;
    private final double voteAverage;
    private final String genres;
    private final List<Integer> genreIds;

    public Movie(int id, String title, String overview, String posterPath, String releaseDate, double voteAverage, String genres, int runTime, List<Integer> genreIds) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.genres = genres;
        this.runTime = runTime;
        this.genreIds = genreIds;
    }

    public String getImageUrl() {
        return "https://image.tmdb.org/t/p/w200" + posterPath;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getRunTime() {
        return runTime;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getGenres() {
        return genres;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }
}

