package com.phantam.moviedesktopapp.Model;

public class Session {
    private static String loggedInUsername;
    private static int userId;

    public static void login(String username, int id) {
        loggedInUsername = username;
        userId = id;
    }

    /**
     * Gets the username of the logged-in user.
     *
     * @return the username, or null if no user is logged in
     */
    public static String getUsername() {
        return loggedInUsername;
    }

    /**
     * Gets the user ID of the logged-in user.
     *
     * @return the user ID, or -1 if no user is logged in
     */
    public static int getUserId() {
        return userId;
    }

    /**
     * Checks if a user is logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    public static void logout() {
        loggedInUsername = null;
        userId = -1;
    }
}

