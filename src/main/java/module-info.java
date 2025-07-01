module com.phantam.moviedesktopapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires jakarta.mail;
    requires json;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires javafx.web;


    exports com.phantam.moviedesktopapp.Controller;
    opens com.phantam.moviedesktopapp.Controller to javafx.fxml;
    exports com.phantam.moviedesktopapp.App;
    opens com.phantam.moviedesktopapp.App to javafx.fxml;
    exports com.phantam.moviedesktopapp.Util;
    opens com.phantam.moviedesktopapp.Util to javafx.fxml;
}