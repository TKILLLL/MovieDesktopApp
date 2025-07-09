package com.phantam.moviedesktopapp.Util;

import com.phantam.moviedesktopapp.Controller.MainViewController;
import com.phantam.moviedesktopapp.Model.Movie;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class MovieCard extends VBox {

    private final SwitchStage switchStage = new SwitchStage();
    private MainViewController mainViewController;


    /**
     * Tạo một MovieCard hiển thị thông tin của một bộ phim.
     *
     * @param movie Đối tượng Movie chứa thông tin phim.
     */
    public MovieCard(Movie movie, MainViewController controller) {

        // Spacer để căn chỉnh các thành phần
        Region spacer = new Region();

        // Ảnh phim
        ImageView imageView = new ImageView(new Image(movie.getImageUrl(), true));
        imageView.setFitWidth(200);
        imageView.setFitHeight(300);

        // Tên phim
        Label title = new Label(movie.getTitle());
        title.setWrapText(true);
        title.setMaxWidth(200);
        title.getStyleClass().add("movie-title");

        // Đánh giá & thời lượng
        Label rate = new Label("⭐ " + movie.getVoteAverage());
        Label runTime = new Label(movie.getRunTime() + "p");
        rate.getStyleClass().add("movie-rate");
        runTime.getStyleClass().add("movie-runtime");

        // Box chứa rate và runtime
        HBox overViewBox = new HBox();
        overViewBox.setAlignment(Pos.CENTER);
        overViewBox.setSpacing(10);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        overViewBox.getChildren().addAll(rate, spacer, runTime);
        VBox.setMargin(overViewBox, new Insets(5, 0, 0, 0)); // khoảng cách phía trên

        // Box chứa image và title
        VBox titleBox = new VBox(imageView, title);
        titleBox.setSpacing(5);

        // Thêm tất cả vào card
        this.getChildren().addAll(titleBox, overViewBox);
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        setVgrow(spacer, Priority.ALWAYS);
        this.getStyleClass().add("movie-card");

        this.setOnMouseClicked(event -> {
            controller.setMovie(movie); // Gọi hàm setMovie bên MainViewController
            controller.getMovieDetailContainer().setVisible(true); // Hiện phần chi tiết
        });

        DropShadow shadow = new DropShadow(20, Color.BLACK);
        this.setOnMouseEntered(e -> {
            this.setEffect(shadow);
            ScaleTransition st = new ScaleTransition(Duration.millis(200), this);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        this.setOnMouseExited(e -> {
            this.setEffect(null);
            ScaleTransition st = new ScaleTransition(Duration.millis(200), this);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

}
