package com.readingdiary;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Verwaltet die Persistenz und Dateioperationen für Bücher.
 * Verwendet Serialisierung zum Speichern und Laden von Buchdaten.
 */
public class BookDataManager {
    private static final String DATA_DIRECTORY = "data";
    private static final String DATA_FILE = "books.dat";
    private static final Path DATA_PATH = Paths.get(DATA_DIRECTORY, DATA_FILE);
    
    /**
     * Stellt sicher, dass das Datenverzeichnis existiert.
     * Erstellt es, falls es nicht existiert.
     */
    private static void ensureDataDirectoryExists() {
        Path directory = Paths.get(DATA_DIRECTORY);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
                System.out.println("Datenverzeichnis erstellt: " + directory.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Fehler beim Erstellen des Datenverzeichnisses: " + e.getMessage());
            }
        }
    }
    
    /**
     * Speichert die Liste der Bücher in eine Datei.
     *
     * @param books Die zu speichernde Bücherliste
     * @return true wenn erfolgreich gespeichert, sonst false
     */
    public static boolean saveBooks(List<Book> books) {
        ensureDataDirectoryExists();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(DATA_PATH.toFile())))) {
            oos.writeObject(new ArrayList<>(books));
            return true;
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Bücher: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lädt die Liste der Bücher aus einer Datei.
     *
     * @return Die geladene Bücherliste oder eine leere Liste, wenn keine Datei vorhanden ist
     */
    @SuppressWarnings("unchecked")
    public static List<Book> loadBooks() {
        ensureDataDirectoryExists();
        
        if (!Files.exists(DATA_PATH)) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(DATA_PATH.toFile())))) {
            return (List<Book>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Fehler beim Laden der Bücher: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Findet ein Buch anhand seiner ID.
     *
     * @param books Die Bücherliste
     * @param id    Die zu suchende ID
     * @return Ein Optional mit dem gefundenen Buch oder ein leeres Optional
     */
    public static Optional<Book> findBookById(List<Book> books, UUID id) {
        return books.stream()
                .filter(book -> book.id().equals(id))
                .findFirst();
    }
    
    /**
     * Aktualisiert ein Buch in der Bücherliste.
     *
     * @param books    Die Bücherliste
     * @param updatedBook Das aktualisierte Buch
     * @return Die aktualisierte Bücherliste
     */
    public static List<Book> updateBook(List<Book> books, Book updatedBook) {
        return books.stream()
                .map(book -> book.id().equals(updatedBook.id()) ? updatedBook : book)
                .collect(Collectors.toList());
    }
    
    /**
     * Löscht ein Buch aus der Bücherliste.
     *
     * @param books  Die Bücherliste
     * @param bookId Die ID des zu löschenden Buchs
     * @return Die aktualisierte Bücherliste
     */
    public static List<Book> deleteBook(List<Book> books, UUID bookId) {
        return books.stream()
                .filter(book -> !book.id().equals(bookId))
                .collect(Collectors.toList());
    }
}