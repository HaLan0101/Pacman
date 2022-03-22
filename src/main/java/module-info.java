module com.example.pacman {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires jdk.scripting.nashorn;


    opens com.example.pacman to javafx.fxml;
    exports com.example.pacman;
}