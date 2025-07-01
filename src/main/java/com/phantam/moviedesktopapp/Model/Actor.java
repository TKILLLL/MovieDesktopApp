package com.phantam.moviedesktopapp.Model;

public class Actor {
    private final String name;
    private final String role;         // "Actor", "Director", "Producer", ...
    private final String profilePath;  // null nếu không có ảnh

    public Actor(String name, String role, String profilePath) {
        this.name = name;
        this.role = role;
        this.profilePath = profilePath;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getImageUrl() {
        return profilePath != null ? "https://image.tmdb.org/t/p/w200" + profilePath : null;
    }
}


