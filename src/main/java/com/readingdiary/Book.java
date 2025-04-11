package com.readingdiary;

/**
 * Represents a book entry in the reading diary.
 * Contains information about the book including title, author, page count,
 * notes, and a user-provided rating.
 */
public class Book {
    private String title;
    private String author;
    private int pages;
    private String notes;
    private int rating;

    /**
     * Constructs a new Book with the provided details.
     *
     * @param title  The title of the book
     * @param author The author of the book
     * @param pages  The number of pages in the book
     * @param notes  Additional notes about the book
     * @param rating A rating from 0 to 5 stars
     */
    public Book(String title, String author, int pages, String notes, int rating) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.notes = notes;
        // Ensure rating is within valid range (0-5)
        this.rating = Math.min(5, Math.max(0, rating));
    }

    /**
     * Returns the title of the book.
     *
     * @return The book title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title The new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the author of the book.
     *
     * @return The book author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the book.
     *
     * @param author The new author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the number of pages in the book.
     *
     * @return The page count
     */
    public int getPages() {
        return pages;
    }

    /**
     * Sets the number of pages in the book.
     *
     * @param pages The new page count
     */
    public void setPages(int pages) {
        this.pages = pages;
    }

    /**
     * Returns the notes about the book.
     *
     * @return The notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes about the book.
     *
     * @param notes The new notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns the rating of the book.
     *
     * @return The rating (0-5)
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the rating of the book.
     * Ensures the rating is within the valid range (0-5).
     *
     * @param rating The new rating
     */
    public void setRating(int rating) {
        this.rating = Math.min(5, Math.max(0, rating));
    }

    /**
     * Returns a string representation of the rating as stars.
     *
     * @return A string of stars representing the rating
     */
    private String getRatingStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("★");
        }
        for (int i = rating; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }

    /**
     * Returns a string representation of the book.
     *
     * @return A formatted string with book details
     */
    @Override
    public String toString() {
        return "\n---------------------------" +
               "\nTitel: " + title +
               "\nAutor: " + author +
               "\nSeitenzahl: " + pages +
               "\nBewertung: " + getRatingStars() + " (" + rating + "/5)" +
               "\nNotizen: " + notes +
               "\n---------------------------";
    }
}
