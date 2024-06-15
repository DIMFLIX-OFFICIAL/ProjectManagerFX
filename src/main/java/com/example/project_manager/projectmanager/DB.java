package com.example.project_manager.projectmanager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DB {
    private Connection connection;

    public DB() {
        String dbPath = "data/database.db";
        connect(dbPath);
        createTables();
    }

    private void connect(String dbPath) {
        try {
            String url = "jdbc:sqlite:" + dbPath;
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createTables() {
        String sql = """
        CREATE TABLE IF NOT EXISTS projects (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            path TEXT NOT NULL,
            color TEXT NOT NULL,
            description TEXT NOT NULL
        );
        """;

        try (Statement db = connection.createStatement()) {
            db.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ProjectData addProject(String name, String path, String color, String description) {
        String sql = "INSERT INTO projects(name, path, color, description) VALUES(?,?,?,?) RETURNING id;";
        int generatedId = -1;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, path);
            pstmt.setString(3, color);
            pstmt.setString(4, description);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    generatedId = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return new ProjectData(
                generatedId,
                name,
                path,
                color,
                description
        );
    }

    public void deleteProject(Integer id) {
        String sql = "DELETE FROM projects WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<ProjectData> getAllProjects() {
        List<ProjectData> projects = new ArrayList<>();

        try (Statement stmt  = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, path, color, description FROM projects")) {
            while (rs.next()) {
                ProjectData proj = new ProjectData(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("path"),
                        rs.getString("color"),
                        rs.getString("description")
                );
                projects.add(proj);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return projects;
    }

    public boolean checkProjectExistsByPath(String path) {
        String sql = "SELECT COUNT(id) AS count FROM projects WHERE path = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, path);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}