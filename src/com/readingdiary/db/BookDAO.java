package com.readingdiary.db;

import com.readingdiary.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public static void insertBook(Book book) {
        String sql = "INSERT INTO books(title, author, genre, status, rating, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getGenre());
            pstmt.setString(4, book.getStatus());
            pstmt.setInt(5, book.getRating());
            pstmt.setString(6, book.getNotes());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Fehler beim Einfügen: " + e.getMessage());
        }
    }

    public static List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DatabaseManager.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getString("status"),
                        rs.getInt("rating"),
                        rs.getString("notes")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("❌ Fehler beim Laden: " + e.getMessage());
        }
        return books;
    }

    public static void deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Fehler beim Löschen: " + e.getMessage());
        }
    }

    public static void updateBook(Book book) {
        String sql = "UPDATE books SET title=?, author=?, genre=?, status=?, rating=?, notes=? WHERE id=?";
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getGenre());
            pstmt.setString(4, book.getStatus());
            pstmt.setInt(5, book.getRating());
            pstmt.setString(6, book.getNotes());
            pstmt.setInt(7, book.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Fehler beim Aktualisieren: " + e.getMessage());
        }
    }
}
