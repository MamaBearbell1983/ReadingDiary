package com.readingdiary.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:readingdiary.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "author TEXT NOT NULL," +
                "genre TEXT," +
                "status TEXT," +
                "rating INTEGER," +
                "notes TEXT)";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("📁 Datenbank initialisiert!");
        } catch (SQLException e) {
            System.err.println("❌ Fehler bei der DB-Initialisierung: " + e.getMessage());
        }
    }
}
