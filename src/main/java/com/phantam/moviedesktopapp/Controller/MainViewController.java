package com.phantam.moviedesktopapp.Controller;

import com.phantam.moviedesktopapp.Model.Actor;
import com.phantam.moviedesktopapp.Model.Movie;
import com.phantam.moviedesktopapp.Service.TMDbService;
import com.phantam.moviedesktopapp.Util.MovieCard;
import com.phantam.moviedesktopapp.Util.SwitchStage;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.List;

public class MainViewController {

    private final SwitchStage switchStage = new SwitchStage();
    private final TrailerMovieDisplay trailerMovieDisplay = new TrailerMovieDisplay();

    /**
     * Movie List Content
     */
    @FXML private FlowPane listMovie;
    @FXML private ScrollPane listMovieContainer;
    @FXML private Hyperlink hlPage, hlPagePrev, hlPageNext;
    @FXML private Button btnPrev, btnNext;

    @FXML private Button upComingBtn, popularBtn, topRateBtn;

    private int currentPage = 1;
    private MovieCategory currentCategory = MovieCategory.POPULAR;

    /**
     * Movie Detail Content
     */
    @FXML private Pane movieDetailContainer;

    @FXML private ImageView posterImageView;
    @FXML private Label lblTitle;
    @FXML private Label lblMetaInfo;
    @FXML private Label lblOverview;
    @FXML private HBox hboxStarContaier;
    @FXML private FontIcon backIcon, exitIcon, miniIcon;
    @FXML private VBox producerListVbox, directorListVbox, genreListVbox;
    @FXML private HBox actorContainerList;
    @FXML private ScrollPane actorContainer;
    @FXML private Button btnTrailer;

    public Pane getMovieDetailContainer() {
        return movieDetailContainer;
    }

    /**
     * Khởi tạo controller, thiết lập các thành phần giao diện và sự kiện.
     */
    @FXML
    public void initialize() {
        movieDetailContainer.setVisible(false);

        // Hover animation
        animationButton(btnNext);
        animationButton(btnPrev);
        animationHyperlink(hlPage);
        animationHyperlink(hlPagePrev);
        animationHyperlink(hlPageNext);

        // Cấu hình scroll
        listMovieContainer.setFitToWidth(true);
        listMovieContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        listMovieContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        listMovie.setPrefWrapLength(967);
        listMovie.setHgap(20);
        listMovie.setVgap(20);
        listMovie.setAlignment(Pos.CENTER);
        listMovie.setStyle("-fx-background-color: #222731;");

        // Load trang đầu tiên
        loadPage(currentCategory, currentPage);

        // Nút điều hướng
        btnNext.setOnAction(e -> {
            currentPage++;
            loadPage(currentCategory, currentPage);
        });

        btnPrev.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                loadPage(currentCategory, currentPage);
            }
        });

        // Thiết lập sự kiện cho các liên kết và nút
        hlPage.setOnAction(e -> loadPage(currentCategory, currentPage));
        hlPagePrev.setOnAction(e -> {
            if (currentPage > 1) loadPage(currentCategory, currentPage - 1);
        });
        hlPageNext.setOnAction(e -> loadPage(currentCategory, currentPage + 1));

        exitIcon.setOnMouseClicked(e -> {
            System.exit(0);
        });
        miniIcon.setOnMouseClicked(e -> {
            if (movieDetailContainer.getScene() != null) {
                movieDetailContainer.getScene().getWindow();
            }
        });

        // Thiết lập sự kiện cho các nút thể loại phim
        upComingBtn.setOnAction(e -> {
            if (movieDetailContainer.isVisible()) { animateHideMovieDetail(); }
            currentCategory = MovieCategory.UPCOMING;
            currentPage = 1;
            loadPage(currentCategory, currentPage);
        });
        popularBtn.setOnAction(e -> {
            if (movieDetailContainer.isVisible()) { animateHideMovieDetail(); }
            currentCategory = MovieCategory.POPULAR;
            currentPage = 1;
            loadPage(currentCategory, currentPage);
        });
        topRateBtn.setOnAction(e -> {
            if (movieDetailContainer.isVisible()) { animateHideMovieDetail(); }
            currentCategory = MovieCategory.TOP_RATED;
            currentPage = 1;
            loadPage(currentCategory, currentPage);
        });
    }

    public enum MovieCategory {
        POPULAR, UPCOMING, TOP_RATED
    }


    /**
     * Tải danh sách phim cho trang hiện tại.
     * @param page Số trang cần tải.
     * @param category Thể loại phim (POPULAR, UPCOMING, ...).
     */
    private void loadPage(MovieCategory category, int page) {
        listMovie.getChildren().clear();
        hlPage.setText(String.valueOf(page));
        hlPagePrev.setText(String.valueOf(page - 1));
        hlPageNext.setText(String.valueOf(page + 1));

        List<Movie> movies = TMDbService.fetchMoviesByCategory(category, page);
        for (Movie movie : movies) {
            listMovie.getChildren().add(new MovieCard(movie, this));
        }
    }

    /**
     * Hiệu ứng hover cho nút và liên kết.
     * @param button Nút cần áp dụng hiệu ứng.
     */
    private void animationButton(Button button) {
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #3a3f4b; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #222731; -fx-text-fill: white;"));
    }

    /**
     * Hiệu ứng hover cho liên kết.
     * @param hyperlink Liên kết cần áp dụng hiệu ứng.
     */
    private void animationHyperlink(Hyperlink hyperlink) {
        hyperlink.setOnMouseEntered(e -> hyperlink.setStyle("-fx-text-fill: #00bcd4; -fx-underline: true;"));
        hyperlink.setOnMouseExited(e -> hyperlink.setStyle("-fx-text-fill: white; -fx-underline: false;"));
    }

    /**
     * Thiết lập thông tin chi tiết của bộ phim.
     * @param movie Đối tượng Movie chứa thông tin phim.
     */
    public void setMovie(Movie movie) {
        // Hiển thị phần chi tiết phim
        animateShowMovieDetail();

        // Cấu hình scroll
        actorContainer.setFitToHeight(true);
        actorContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        actorContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        actorContainer.setPannable(true);
        actorContainer.setStyle("-fx-background-color: transparent;");
        actorContainer.getContent().setStyle("-fx-background-color: transparent;");

        // Tạo biểu tượng quay lại
        backIcon.setOnMouseClicked(event -> animateHideMovieDetail());
        backIcon.setCursor(javafx.scene.Cursor.HAND);
        backIcon.setAccessibleText("Quay lại danh sách phim");
        HBox backContainer = new HBox(backIcon);
        backContainer.setLayoutX(19);
        backContainer.setLayoutY(13);
        movieDetailContainer.getChildren().add(backContainer);

        // Gán dữ liệu
        posterImageView.setImage(new Image(movie.getImageUrl(), true));
        lblTitle.setText(movie.getTitle());

        hboxStarContaier.getChildren().clear();
        hboxStarContaier.getChildren().add(generateStars(movie.getVoteAverage() / 2.0));

        String year = movie.getReleaseDate().split("-")[0];
        int runtime = movie.getRunTime();
        lblMetaInfo.setText((runtime / 60) + " h " + (runtime % 60) + " min, " + year);

        lblOverview.setText(movie.getOverview());

        // Thiết lập các đạo diễn, nhà sản xuất
        List<Actor> credits = TMDbService.fetchMovieCredits(movie.getId());
        for (Actor actor : credits) {
            if ("Director".equalsIgnoreCase(actor.getRole()) && directorListVbox.getChildren().size() <= 2) {
                addPersontoList(directorListVbox, actor.getName());
            } else if ("Producer".equalsIgnoreCase(actor.getRole()) && producerListVbox.getChildren().size() <= 5) {
                addPersontoList(producerListVbox, actor.getName());
            }
        }

        // Thiết lập thể loại
        genreListVbox.getChildren().clear();
        String[] genres = movie.getGenres().split(",\\s*");
        for (String genre : genres) {
            addPersontoList(genreListVbox, genre);
        }

        // Thiết lập diễn viên
        actorContainerList.getChildren().clear();
        List<Actor> actors = TMDbService.fetchMovieCredits(movie.getId());
        for (Actor actor : actors) {
            if ("Actor".equalsIgnoreCase(actor.getRole()) && actorContainerList.getChildren().size() < 15) {
                ImageView imageView = new ImageView(new Image(actor.getImageUrl(), true));
                actorCard(actor.getName(), imageView);
            }
        }

        btnTrailer.setOnAction(event -> {
            TrailerMovieDisplay trailerMovieDisplay = switchStage.switchToStage(
                    "/com/phantam/moviedesktopapp/fxml/trailer-display-view.fxml", "", true
            );
            if (trailerMovieDisplay != null) {
                trailerMovieDisplay.setMovieId(movie.getId());
            }
        });
    }


    /**
     * Tạo một HBox chứa các biểu tượng sao dựa trên số điểm đánh giá.
     * @param stars Số điểm đánh giá (từ 0 đến 10).
     * @return HBox chứa các biểu tượng sao.
     */
    private HBox generateStars(double stars) {
        HBox starBox = new HBox(3);
        int full = (int) stars;
        boolean hasHalf = (stars - full) >= 0.5;

        for (int i = 0; i < full; i++) {
            FontIcon icon = new FontIcon("fas-star");
            icon.setStyle("-fx-font-size: 19px; -fx-icon-color: white;");
            starBox.getChildren().add(icon);
        }
        if (hasHalf) {
            FontIcon icon = new FontIcon("fas-star-half-alt");
            icon.setStyle("-fx-font-size: 19px; -fx-icon-color: white;");
            starBox.getChildren().add(icon);
        }
        for (int i = full + (hasHalf ? 1 : 0); i < 5; i++) {
            FontIcon icon = new FontIcon("far-star");
            icon.setStyle("-fx-font-size: 19px; -fx-icon-color: white;");
            starBox.getChildren().add(icon);
        }
        return starBox;
    }

    /**
     * Hiệu ứng hiển thị chi tiết phim.
     * Bắt đầu từ bên phải và trượt vào.
     */
    private void animateShowMovieDetail() {
        movieDetailContainer.setVisible(true);
        movieDetailContainer.setTranslateX(movieDetailContainer.getWidth()); // Bắt đầu bên phải
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), movieDetailContainer);
        slideIn.setFromX(movieDetailContainer.getWidth());
        slideIn.setToX(0);
        slideIn.play();
    }

    /**
     * Hiệu ứng ẩn chi tiết phim.
     * Trượt ra bên phải.
     */
    private void animateHideMovieDetail() {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), movieDetailContainer);
        slideOut.setFromX(0);
        slideOut.setToX(movieDetailContainer.getWidth());
        slideOut.setOnFinished(e -> movieDetailContainer.setVisible(false));
        slideOut.play();
    }

    private void addPersontoList(VBox list, String name) {
        Label label = new Label(name);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        list.getChildren().add(label);
    }

    private void actorCard(String name, ImageView image) {
        // Tạo label tên diễn viên
        Label label = new Label(name);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 15px;");

        // Tạo ảnh với clip bo góc
        ImageView actorImage = new ImageView(image.getImage());
        actorImage.setFitWidth(150);
        actorImage.setFitHeight(150);

        Rectangle clip = new Rectangle(150, 150);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        actorImage.setClip(clip);

        // Tạo VBox chứa ảnh và tên
        VBox actorBox = new VBox(actorImage, label);
        actorBox.setSpacing(10);
        actorBox.setAlignment(Pos.CENTER);
        actorBox.setPrefWidth(180);
        actorBox.setStyle(
                "-fx-background-color: #2c2f3a;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;"
        );
        // Tạo hiệu ứng bóng (DropShadow)
        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setOffsetX(4); // bóng phải
        shadow.setOffsetY(4); // bóng dưới
        shadow.setColor(Color.rgb(0, 0, 0, 0.6)); // bóng màu đen, mờ
        actorBox.setEffect(shadow);

        // Thêm vào danh sách
        actorContainerList.getChildren().add(actorBox);
    }

}
