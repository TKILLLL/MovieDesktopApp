package com.phantam.moviedesktopapp.Controller;

import com.phantam.moviedesktopapp.Model.Actor;
import com.phantam.moviedesktopapp.Model.Movie;
import com.phantam.moviedesktopapp.Service.TMDbService;
import com.phantam.moviedesktopapp.Util.MovieCard;
import com.phantam.moviedesktopapp.Util.SwitchStage;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;
import java.util.Map;

public class MainViewController {

    private final SwitchStage switchStage = new SwitchStage();

    @FXML
    private FlowPane listMovie;
    @FXML
    private ScrollPane listMovieContainer;
    @FXML
    private Hyperlink hlPage, hlPagePrev, hlPageNext;
    @FXML
    private Button btnPrev, btnNext;
    @FXML
    private Button upComingBtn, popularBtn, topRateBtn;
    @FXML
    private Button actionBtn, dramaBtn, honorBtn, adventureBtn, documentaryBtn;
    @FXML
    private ChoiceBox<String> genreChoiceBox;

    @FXML
    private Pane movieDetailContainer;
    @FXML
    private ImageView posterImageView;
    @FXML
    private Label lblTitle, lblMetaInfo, lblOverview;
    @FXML
    private HBox hboxStarContaier, actorContainerList;
    @FXML
    private FontIcon backIcon, exitIcon, miniIcon;
    @FXML
    private VBox producerListVbox, directorListVbox, genreListVbox;
    @FXML
    private ScrollPane actorContainer;
    @FXML
    private Button btnTrailer;

    private int currentPage = 1;
    private MovieCategory currentCategory = MovieCategory.POPULAR;

    public Pane getMovieDetailContainer() {
        return movieDetailContainer;
    }

    /**
     * Enum representing the different movie categories.
     * Used to filter and load movies based on user selection.
     */
    public enum MovieCategory {POPULAR, UPCOMING, TOP_RATED}

    /**
     * Initializes the main view controller.
     * Sets up the UI components, loads initial movie categories and genres,
     * and configures event handlers for pagination and genre selection.
     */
    @FXML
    public void initialize() {
        movieDetailContainer.setVisible(false);

        animationButton(btnNext);
        animationButton(btnPrev);
        animationHyperlink(hlPage);
        animationHyperlink(hlPagePrev);
        animationHyperlink(hlPageNext);

        listMovieContainer.setFitToWidth(true);
        listMovieContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        listMovieContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        listMovie.setPrefWrapLength(967);
        listMovie.setHgap(20);
        listMovie.setVgap(20);
        listMovie.setAlignment(Pos.CENTER);
        listMovie.setStyle("-fx-background-color: #222731;");

        loadPage(currentCategory, currentPage);

        btnNext.setOnAction(e -> loadPage(currentCategory, ++currentPage));
        btnPrev.setOnAction(e -> {
            if (currentPage > 1) loadPage(currentCategory, --currentPage);
        });

        hlPage.setOnAction(e -> loadPage(currentCategory, currentPage));
        hlPagePrev.setOnAction(e -> {
            if (currentPage > 1) loadPage(currentCategory, currentPage - 1);
        });
        hlPageNext.setOnAction(e -> loadPage(currentCategory, currentPage + 1));

        exitIcon.setOnMouseClicked(e -> System.exit(0));

        categoryLoad();
        genreLoad();
    }

    // Setup the genre filter for the movie list
    private void setupGenreFilter(List<Movie> allMovies) {
        Map<Integer, String> genreMap = TMDbService.fetchGenreMap();
        genreChoiceBox.getItems().setAll("Tất cả");
        genreChoiceBox.getItems().addAll(genreMap.values());
        genreChoiceBox.setValue("Tất cả");

        genreChoiceBox.setOnAction(e -> {
            String selected = genreChoiceBox.getValue();
            listMovie.getChildren().clear();

            if ("Tất cả".equals(selected)) {
                allMovies.forEach(m -> listMovie.getChildren().add(new MovieCard(m, this)));
            } else {
                genreMap.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(selected))
                        .findFirst()
                        .ifPresent(entry -> allMovies.stream()
                                .filter(m -> m.getGenreIds().contains(entry.getKey()))
                                .forEach(m -> listMovie.getChildren().add(new MovieCard(m, this))));
            }
        });
    }

    // Load the genre buttons and set up their actions
    private void genreLoad() {
        setupGenreButton(actionBtn, 28);
        setupGenreButton(dramaBtn, 18);
        setupGenreButton(honorBtn, 10751);
        setupGenreButton(adventureBtn, 12);
        setupGenreButton(documentaryBtn, 99);
    }

    /*
     * Setup the genre buttons to load movies based on the selected genre
     * @param btn The button to set up
     * @param genreId The ID of the genre to load movies for
     * @return void
     * This method sets the action for the button to load movies of the specified genre
     * when the button is clicked. It clears the current movie list,
     * sets the current category to POPULAR,
     * and loads the first page of movies for that genre.
     */
    private void setupGenreButton(Button btn, int genreId) {
        btn.setOnAction(e -> {
            if (movieDetailContainer.isVisible()) animateHideMovieDetail();
            currentCategory = MovieCategory.POPULAR;
            currentPage = 1;
            loadMoviesByGenre(genreId, currentPage);
        });
    }

    // Load the initial movie categories and set up their buttons
    private void categoryLoad() {
        setupCategoryButton(upComingBtn, MovieCategory.UPCOMING);
        setupCategoryButton(popularBtn, MovieCategory.POPULAR);
        setupCategoryButton(topRateBtn, MovieCategory.TOP_RATED);
    }

    // Setup the category buttons to load movies based on the selected category
    private void setupCategoryButton(Button btn, MovieCategory category) {
        btn.setOnAction(e -> {
            if (movieDetailContainer.isVisible()) animateHideMovieDetail();
            currentCategory = category;
            currentPage = 1;
            loadPage(currentCategory, currentPage);
        });
    }

    // Load movies by category with pagination
    private void loadPage(MovieCategory category, int page) {
        listMovie.getChildren().clear();
        hlPage.setText(String.valueOf(page));
        hlPagePrev.setText(String.valueOf(page - 1));
        hlPageNext.setText(String.valueOf(page + 1));

        List<Movie> movies = TMDbService.fetchMoviesByCategory(category, page);
        movies.forEach(m -> listMovie.getChildren().add(new MovieCard(m, this)));
        setupGenreFilter(movies);
    }

    // Load movies by genre with pagination
    private void loadMoviesByGenre(int genreId, int page) {
        listMovie.getChildren().clear();
        currentPage = page;
        List<Movie> movies = TMDbService.fetchMoviesByGenre(genreId, page);
        movies.forEach(m -> listMovie.getChildren().add(new MovieCard(m, this)));

        hlPage.setText(String.valueOf(page));
        hlPagePrev.setText(String.valueOf(page - 1));
        hlPageNext.setText(String.valueOf(page + 1));
    }

    // Animate the button to change color on hover
    private void animationButton(Button button) {
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #3a3f4b; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #222731; -fx-text-fill: white;"));
    }

    // Animate the hyperlink to change color and underline on hover
    private void animationHyperlink(Hyperlink link) {
        link.setOnMouseEntered(e -> link.setStyle("-fx-text-fill: #00bcd4; -fx-underline: true;"));
        link.setOnMouseExited(e -> link.setStyle("-fx-text-fill: white; -fx-underline: false;"));
    }

    // Set the movie details in the detail container
    public void setMovie(Movie movie) {
        animateShowMovieDetail();
        actorContainer.setFitToHeight(true);
        actorContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        actorContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        actorContainer.setPannable(true);

        backIcon.setOnMouseClicked(e -> animateHideMovieDetail());
        backIcon.setCursor(javafx.scene.Cursor.HAND);

        posterImageView.setImage(new Image(movie.getImageUrl(), true));
        lblTitle.setText(movie.getTitle());
        hboxStarContaier.getChildren().setAll(generateStars(movie.getVoteAverage() / 2.0));

        String year = movie.getReleaseDate().split("-")[0];
        int runtime = movie.getRunTime();
        lblMetaInfo.setText((runtime / 60) + " h " + (runtime % 60) + " min, " + year);
        lblOverview.setText(movie.getOverview());

        List<Actor> credits = TMDbService.fetchMovieCredits(movie.getId());
        directorListVbox.getChildren().clear();
        producerListVbox.getChildren().clear();
        for (Actor actor : credits) {
            if ("Director".equalsIgnoreCase(actor.getRole()) && directorListVbox.getChildren().size() <= 2)
                addPersontoList(directorListVbox, actor.getName());
            else if ("Producer".equalsIgnoreCase(actor.getRole()) && producerListVbox.getChildren().size() <= 5)
                addPersontoList(producerListVbox, actor.getName());
        }

        genreListVbox.getChildren().clear();
        for (String genre : movie.getGenres().split(",\\s*")) {
            addPersontoList(genreListVbox, genre);
        }

        actorContainerList.getChildren().clear();
        for (Actor actor : credits) {
            if ("Actor".equalsIgnoreCase(actor.getRole()) && actorContainerList.getChildren().size() < 15) {
                actorCard(actor.getName(), new ImageView(new Image(actor.getImageUrl(), true)));
            }
        }

        btnTrailer.setOnAction(e -> {
            TrailerMovieDisplay display = switchStage.switchToStage(
                    "/com/phantam/moviedesktopapp/fxml/trailer-display-view.fxml", "", true);
            if (display != null) display.setMovieId(movie.getId());
        });
    }

    // Generate star icons based on the rating
    private HBox generateStars(double stars) {
        HBox box = new HBox(3);
        int full = (int) stars;
        boolean half = (stars - full) >= 0.5;

        for (int i = 0; i < full; i++) box.getChildren().add(createStar("fas-star"));
        if (half) box.getChildren().add(createStar("fas-star-half-alt"));
        for (int i = full + (half ? 1 : 0); i < 5; i++) box.getChildren().add(createStar("far-star"));

        return box;
    }

    // Create a star icon with specific style
    private FontIcon createStar(String iconLiteral) {
        FontIcon icon = new FontIcon(iconLiteral);
        icon.setStyle("-fx-font-size: 19px; -fx-icon-color: white;");
        return icon;
    }

    // Animate the showing of the movie detail container
    private void animateShowMovieDetail() {
        movieDetailContainer.setVisible(true);
        movieDetailContainer.setTranslateX(movieDetailContainer.getWidth());
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), movieDetailContainer);
        slide.setFromX(movieDetailContainer.getWidth());
        slide.setToX(0);
        slide.play();
    }

    // Animate the hiding of the movie detail container
    public void animateHideMovieDetail() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), movieDetailContainer);
        slide.setFromX(0);
        slide.setToX(movieDetailContainer.getWidth());
        slide.setOnFinished(e -> movieDetailContainer.setVisible(false));
        slide.play();
    }

    // Add a person to the list of directors, producers, or genres
    private void addPersontoList(VBox list, String name) {
        Label label = new Label(name);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        list.getChildren().add(label);
    }

    // Create a card for each actor with their name and image
    private void actorCard(String name, ImageView image) {
        Label label = new Label(name);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");

        ImageView actorImage = image;
        actorImage.setFitWidth(150);
        actorImage.setFitHeight(150);
        Rectangle clip = new Rectangle(150, 150);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        actorImage.setClip(clip);

        VBox box = new VBox(actorImage, label);
        box.setSpacing(10);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(180);
        box.setStyle("-fx-background-color: #2c2f3a; -fx-background-radius: 15; -fx-border-radius: 15;");

        DropShadow shadow = new DropShadow(10, 4, 4, Color.rgb(0, 0, 0, 0.6));
        box.setEffect(shadow);

        actorContainerList.getChildren().add(box);
    }
}
