package com.example.project_manager.projectmanager;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class App extends Application {
    private final double xOffset = 0;
    private final double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main-app.fxml"));
        fxmlLoader.setController(new AppController());
        Scene scene = new Scene(fxmlLoader.load(), 700, 600, Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public static class AppController {
        private double xOffset = 0;
        private double yOffset = 0;

        private Stage stage;
        private Scene scene;


        //==> CSS
        private final String CSS = "data:text/css," + // language=CSS
            """
                    .text-area {
                        -fx-focus-color: #b4befe; /* Цвет границы при фокусе */
                        -fx-faint-focus-color: #b4befe; /* Цвет границы при фокусе в 'faint' режиме */
                    }
                    
                    .text-area .content {
                        -fx-text-fill: #ffffff; /* Цвет текста placeholder */
                    }
                    
                    .input_color .color-picker-label {
                        -fx-text-fill: #FFFFFF;
                    }
                    """;

        //==> Общие элементы
        ////////////////////////////////////////////////////
        @FXML
        private AnchorPane titleBar;
        @FXML
        private Button close_window;
        @FXML
        private Button minimize_window;
        @FXML
        private Button maximize_window;


        //==> Главная страница
        ////////////////////////////////////////////////////
        @FXML
        private ListView<ProjectData> projects_list;


        //==> Главная страница
        ////////////////////////////////////////////////////
        @FXML
        private TextField input_directory;
        @FXML
        private ColorPicker input_color;
        @FXML
        private TextArea input_description;


        //==> Инициализация главного окна
        ///////////////////////////////////////////
        @FXML
        public void initialize() {
            DB db = new DB();
            List<ProjectData> projects = db.getAllProjects();
            db.closeConnection();
            projects_list.setCellFactory(event -> new ProjectListCell());
            projects_list.getItems().addAll(projects);

            titleBar.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            titleBar.setOnMouseDragged(event -> {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
        }

        //==> Общее управление
        //////////////////////////////////////////
        private void switchWindows(ActionEvent event, String source) throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(source));
            loader.setController(this);
            Parent root = loader.load();
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root, 700, 600, Color.TRANSPARENT);
            scene.getStylesheets().add(CSS);
            stage.setScene(scene);
            stage.show();
        }

        @FXML
        private void switchToMain(ActionEvent event) throws IOException {
            switchWindows(event, "main-app.fxml");
        }
        @FXML
        private void switchToSettings(ActionEvent event) throws IOException {
            switchWindows(event, "settings.fxml");
        }

        @FXML
        private void handleCloseWindow() {
            Stage stage = (Stage) close_window.getScene().getWindow();
            stage.close();
        }
        @FXML
        private void handleMinimizeWindow() {
            Stage stage = (Stage) minimize_window.getScene().getWindow();
            stage.setIconified(true);
        }
        @FXML
        private void handleMaximizeWindow() {
            Stage stage = (Stage) maximize_window.getScene().getWindow();
            stage.setMaximized(!stage.isMaximized());
        }


        //==> Управление окном настроек
        //////////////////////////////////////////////////
        public static String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
        }
        @FXML
        public void add_project(ActionEvent event) throws Exception {
            File project_path = new File(input_directory.getText());
            if (!project_path.exists() || !project_path.isDirectory() ) {
                Notify.send("Директории не существует!", "Пожалуйста выберите корректный путь до проекта!");
                return;
            }

            DB db = new DB();
            boolean exists = db.checkProjectExistsByPath(project_path.toString());
            if (!(exists)) {
                ProjectData data = db.addProject(
                    project_path.getName(),
                    project_path.toString(),
                    toHexString(input_color.getValue()),
                    input_description.getText()
                );
                projects_list.getItems().add(data);
                this.switchToMain(event);
            } else {
                Notify.send("Проект существует!", "Такой проект уже есть.");
            }

            db.closeConnection();
        }

        @FXML
        public void choose_directory() {
            Stage stage = (Stage) maximize_window.getScene().getWindow();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Выберите проект");
            File selectedDirectory = directoryChooser.showDialog(stage);
            if (selectedDirectory != null) {
                input_directory.setText(selectedDirectory.getAbsolutePath());
            }
        }
    }

    public static void main(String[] args) {
        launch();
    }
}