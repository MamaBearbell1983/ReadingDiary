package com.readingdiary;

import java.util.UUID;

public class Book {
    public enum ReadingStatus {
        UNREAD, IN_PROGRESS, FINISHED, ABANDONED;

        public String getDisplayName() {
            return switch (this) {
                case UNREAD -> "Ungelesen";
                case IN_PROGRESS -> "Am Lesen";
                case FINISHED -> "Gelesen";
                case ABANDONED -> "Abgebrochen";
            };
        }
    }

    private final UUID id;
    private String title;
    private String author;
    private int pages;
    private String notes;
    private int rating;
    private ReadingStatus status;

    public Book(String title, String author, int pages, String notes, int rating, ReadingStatus status) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.notes = notes;
        this.rating = rating;
        this.status = status;
    }

    public UUID id() { return id; }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getPages() { return pages; }
    public String getNotes() { return notes; }
    public int getRating() { return rating; }
    public ReadingStatus getStatus() { return status; }

    public Book withTitle(String title) {
        return new Book(title, this.author, this.pages, this.notes, this.rating, this.status);
    }

    public Book withAuthor(String author) {
        return new Book(this.title, author, this.pages, this.notes, this.rating, this.status);
    }

    public Book withPages(int pages) {
        return new Book(this.title, this.author, pages, this.notes, this.rating, this.status);
    }

    public Book withNotes(String notes) {
        return new Book(this.title, this.author, this.pages, notes, this.rating, this.status);
    }

    public Book withRating(int rating) {
        return new Book(this.title, this.author, this.pages, this.notes, rating, this.status);
    }

    public Book withStatus(ReadingStatus status) {
        return new Book(this.title, this.author, this.pages, this.notes, this.rating, status);
    }

    @Override
    public String toString() {
        return "Titel: " + title + ", Autor: " + author + ", Seiten: " + pages +
                ", Status: " + status.getDisplayName() + ", Bewertung: " + rating + "/5, Notizen: " + notes;
    }
}