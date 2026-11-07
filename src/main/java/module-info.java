module org.example.raycaster {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens org.example.raycaster to javafx.fxml;
    exports org.example.raycaster;
    exports org.example.raycaster.MapEditor;
    exports org.example.raycaster.Entities;
    opens org.example.raycaster.Entities to javafx.fxml;
}