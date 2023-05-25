module PixelSpriter {
    requires javafx.controls;
    requires okhttp3;
    requires java.desktop;
    requires javafx.swing;
    requires com.google.gson;
    requires java.sql;

    opens com.abdelkader.backend.modals to com.google.gson;

    exports com.abdelkader.app;
    exports com.abdelkader.app.scenes;
}