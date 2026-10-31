module org.example.raycaster {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens org.example.raycaster to javafx.fxml;
    exports org.example.raycaster;
}