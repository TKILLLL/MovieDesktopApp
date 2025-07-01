package com.phantam.moviedesktopapp.Controller;

import com.phantam.moviedesktopapp.Service.TMDbService;
import com.phantam.moviedesktopapp.Util.SwitchStage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;

public class TrailerMovieDisplay {

    private final SwitchStage switchStage = new SwitchStage();
    private int movieId;

    @FXML
    private WebView webViewTrailer;

    /**
     * Thiết lập ID phim để tải trailer.
     *
     * @param id ID của phim cần tải trailer.
     */
    public void setMovieId(int id) {
        this.movieId = id;
        Platform.runLater(this::loadTrailer);
    }

    /**
     * Tải trailer của phim từ TMDb và hiển thị trong WebView.
     * Sử dụng YouTube để nhúng video trailer.
     */
    private void loadTrailer() {
        String embedUrl = TMDbService.fetchYouTubeTrailer(movieId);
        if (embedUrl != null) {
            String html = "<html><body style='margin:0;padding:0;'>" +
                    "<iframe width='900' height='550' src='" + embedUrl + "' " +
                    "frameborder='0' allow='autoplay; encrypted-media' allowfullscreen></iframe>" +
                    "</body></html>";
            webViewTrailer.getEngine().loadContent(html);
        } else {
            webViewTrailer.getEngine().loadContent("<p style='color:red;'>Không tìm thấy trailer.</p>", "text/html");
        }
    }

    /**
     * Xử lý sự kiện khi người dùng nhấp vào biểu tượng thoát.
     *
     * @param event Sự kiện chuột khi người dùng nhấp vào biểu tượng thoát.
     */
    @FXML
    protected void onExitIconClicked(MouseEvent event) {
        webViewTrailer.getEngine().loadContent("");
        switchStage.closeCurrentStage(event);
    }
}

