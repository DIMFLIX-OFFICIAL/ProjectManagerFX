module com.example.project_manager.projectmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.sql.rowset;
    requires java.desktop;

    opens com.example.project_manager.projectmanager to javafx.fxml;
    exports com.example.project_manager.projectmanager;
}