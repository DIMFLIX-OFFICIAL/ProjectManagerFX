package com.example.project_manager.projectmanager;

public class ProjectData {
    Integer id;
    String projectName;
    String projectPath;
    String projectColor;
    String projectDescription;

    public ProjectData(Integer id, String  name, String path, String color, String description) {
        this.id = id;
        this.projectName = name;
        this.projectPath = path;
        this.projectColor = color;
        this.projectDescription = description;
    }
}
