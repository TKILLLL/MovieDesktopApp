package com.phantam.moviedesktopapp.Model;

public class Genre {
    private int id;
    private String name;

    // Constructor, getter, setter
    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

