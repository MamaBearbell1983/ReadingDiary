package com.readingdiary;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a book entry in the reading diary.
 * Modern implementation using Java record for immutability and data-focused approach.
 * Additional fields like UUID, reading date and reading status enhance the functionality.
 */
public record Book(
    UUID id,
    String title,
    String author,
    int pages,
    String notes,
    int rating,
    ReadingStatus status,
    LocalDate dateAdded,
    LocalDate dateFinished
) implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Enum representing the reading status of a book
     */
    public enum ReadingStatus {
        UNREAD("Ungelesen"),
        IN_PROGRESS("Wird gelesen"),
        FINISHED("Gelesen"),
        ABANDONED("Abgebrochen");

        private final String displayName;

        ReadingStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Constructs a new Book with the provided details.
     * Simplified constructor that uses the record constructor.
     *
     * @param title  The title of the book
     * @param author The author of the book
     * @param pages  The number of pages in the book
     * @param notes  Additional notes about the book
     * @param rating A rating from 0 to 5 stars
     * @param status The reading status of the book
     */
    public Book(String title, String author, int pages, String notes, int rating, ReadingStatus status) {
        this(
            UUID.randomUUID(),
            title,
            author,
            pages,
            notes,
            Math.min(5, Math.max(0, rating)),
            status,
            LocalDate.now(),
            status == ReadingStatus.FINISHED ? LocalDate.now() : null
        );
    }

    /**
     * Constructs a basic book with minimal information.
     * Status defaults to UNREAD.
     */
    public Book(String title, String author, int pages, String notes, int rating) {
        this(title, author, pages, notes, rating, ReadingStatus.UNREAD);
    }

    /**
     * Copy constructor with modification options
     * Creates a new book with an updated field
     */
    public Book withTitle(String newTitle) {
        return new Book(id, newTitle, author, pages, notes, rating, status, dateAdded, dateFinished);
    }

    public Book withAuthor(String newAuthor) {
        return new Book(id, title, newAuthor, pages, notes, rating, status, dateAdded, dateFinished);
    }

    public Book withPages(int newPages) {
        return new Book(id, title, author, newPages, notes, rating, status, dateAdded, dateFinished);
    }

    public Book withNotes(String newNotes) {
        return new Book(id, title, author, pages, newNotes, rating, status, dateAdded, dateFinished);
    }

    public Book withRating(int newRating) {
        int validRating = Math.min(5, Math.max(0, newRating));
        return new Book(id, title, author, pages, notes, validRating, status, dateAdded, dateFinished);
    }

    public Book withStatus(ReadingStatus newStatus) {
        LocalDate finishDate = newStatus == ReadingStatus.FINISHED ? 
                              (dateFinished == null ? LocalDate.now() : dateFinished) : 
                              dateFinished;
        return new Book(id, title, author, pages, notes, rating, newStatus, dateAdded, finishDate);
    }

    /**
     * Returns a string representation of the rating as colored stars.
     *
     * @return A string of stars representing the rating
     */
    public String getRatingStars() {
        final String GOLD_STAR = "\u001B[33m★\u001B[0m";
        final String EMPTY_STAR = "\u001B[37m☆\u001B[0m";
        
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append(GOLD_STAR);
        }
        for (int i = rating; i < 5; i++) {
            stars.append(EMPTY_STAR);
        }
        return stars.toString();
    }

    /**
     * Returns a formatted date string or a placeholder if the date is null
     */
    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "N/A";
    }

    /**
     * Returns a string representation of the book.
     * Uses ANSI colors for a more appealing console display.
     *
     * @return A formatted string with book details
     */
    @Override
    public String toString() {
        final String CYAN = "\u001B[36m";
        final String GREEN = "\u001B[32m";
        final String RESET = "\u001B[0m";
        
        return "\n" + CYAN + "╔═══════════════════════════════════════╗" + RESET +
               "\n" + CYAN + "║ " + GREEN + "Titel: " + RESET + title +
               "\n" + CYAN + "║ " + GREEN + "Autor: " + RESET + author +
               "\n" + CYAN + "║ " + GREEN + "Seitenzahl: " + RESET + pages +
               "\n" + CYAN + "║ " + GREEN + "Status: " + RESET + status.getDisplayName() +
               "\n" + CYAN + "║ " + GREEN + "Hinzugefügt am: " + RESET + formatDate(dateAdded) +
               (status == ReadingStatus.FINISHED ? "\n" + CYAN + "║ " + GREEN + "Fertig gelesen am: " + RESET + formatDate(dateFinished) : "") +
               "\n" + CYAN + "║ " + GREEN + "Bewertung: " + RESET + getRatingStars() + " (" + rating + "/5)" +
               "\n" + CYAN + "║ " + GREEN + "Notizen: " + RESET + notes +
               "\n" + CYAN + "╚═══════════════════════════════════════╝" + RESET;
    }
}
