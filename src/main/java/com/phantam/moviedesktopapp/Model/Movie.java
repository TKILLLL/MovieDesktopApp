package com.phantam.moviedesktopapp.Model;

public class Movie {
    private final int id;
    private final String title;
    private final String overview;
    private final String posterPath;
    private final String releaseDate;
    private final int runTime;
    private final double voteAverage;
    private final String genres;

    public Movie(int id, String title, String overview, String posterPath, String releaseDate, double voteAverage, String genres, int runTime) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.genres = genres;
        this.runTime = runTime;
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

    public String getPosterPath() {
        return posterPath;
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
}

