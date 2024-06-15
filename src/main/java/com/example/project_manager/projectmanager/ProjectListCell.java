package com.example.project_manager.projectmanager;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ProjectListCell extends ListCell<ProjectData> {
    private FXMLLoader mLLoader;
    private Pane projectPane;
    private Button runProjectButton;
    private Button removeProjectButton;
    private Label projectNameLabel;
    private Pane projectColor;
    private ChoiceBox<String> choiceIde;
    private final Map<String, String> ideCommands;

    public ProjectListCell() {
        ideCommands = new HashMap<>();
        ideCommands.put("Pycharm", "pycharm");
        ideCommands.put("VsCode", "code");
        ideCommands.put("Intellij", "idea");
        ideCommands.put("RustRover", "rustrover");
        ideCommands.put("SublimeText", "subl3");
    }

    @Override
    protected void updateItem(ProjectData item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setStyle("-fx-background-color: transparent;");
            setGraphic(null);
            setText(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("custom-list-cell.fxml"));
                mLLoader.setController(this);
                try {
                    projectPane = mLLoader.load();
                    runProjectButton = (Button) projectPane.lookup("#run_project");
                    removeProjectButton = (Button) projectPane.lookup("#remove_project");
                    projectNameLabel = (Label) projectPane.lookup("#project_name");
                    projectColor = (Pane) projectPane.lookup("#project_color");
                    choiceIde = (ChoiceBox<String>) projectPane.lookup("#cb");
                    choiceIde.setItems(FXCollections.observableArrayList(ideCommands.keySet()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            projectNameLabel.setText(item.projectName);
            projectColor.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 10;", item.projectColor));

            runProjectButton.setOnAction(_ -> {
                String selectedIde = choiceIde.getValue();
                String command = ideCommands.get(selectedIde);

                if (command == null) {
                    Notify.send("IDE не выбрана", "Пожалуйста, выберите нужную IDE");
                    return;
                }

                if (!(Files.exists(Paths.get(item.projectPath)) && Files.isDirectory(Paths.get(item.projectPath)))) {
                    Notify.send(
                            "Не корректный путь к проекту",
                            "Пожалуйста, удалите проект и создайте заново с корректным путем"
                    );
                    return;
                }

                if (isIDEAvailable(command)) {
                    launchIDE(command + " " + item.projectPath);
                } else {
                    Notify.send(
                            "IDE не найдена",
                            "Пожалуйста, установите " + selectedIde
                    );
                }
            });

            removeProjectButton.setOnAction(_ -> {
                DB db = new DB();
                db.deleteProject(item.id);
                db.closeConnection();
                getListView().getItems().remove(item);
            });

            Tooltip tooltip = new Tooltip(item.projectDescription);
            Tooltip.install(projectNameLabel, tooltip);

            setGraphic(projectPane);
        }
    }

    private boolean isIDEAvailable(String ideCommand) {
        String osName = System.getProperty("os.name").toLowerCase();
        String checkCommand;

        if (osName.contains("windows")) { checkCommand = "where " + ideCommand; }
        else { checkCommand = "which " + ideCommand; }

        try {
            Process process = Runtime.getRuntime().exec(checkCommand);
            if (process.waitFor() == 0) { return true; }
        } catch (IOException | InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    private void launchIDE(String command) {
        try { Runtime.getRuntime().exec(command); }
        catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка запуска", "Не удалось запустить IDE.");
        }
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}