module org.openjfx.gradle.javafx.test {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    //requires jpro.webapi;

    exports com.aramatthew.MazeFX;
}