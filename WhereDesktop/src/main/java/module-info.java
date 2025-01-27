module com.example.wheredesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;
    requires java.desktop;
    requires okhttp3;
    requires org.json;

    // Export your package to javafx.fxml for FXML access
    opens com.example.wheredesktop to javafx.fxml;

    // Export to allow other modules access to the package if needed
    exports com.example.wheredesktop;
    exports com.example.wheredesktop.Panels;
    opens com.example.wheredesktop.Panels to javafx.fxml;
    exports com.example.wheredesktop.Objects;
    opens com.example.wheredesktop.Objects to javafx.fxml;
}
