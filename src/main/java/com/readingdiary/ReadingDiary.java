package com.readingdiary;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * A console application for tracking and rating books that have been read.
 * Users can add books with details like title, author, page count, notes, and rating,
 * and view their reading list.
 */
public class ReadingDiary {

    private static ArrayList<Book> books = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Main method that runs the reading diary application.
     * Presents a menu to the user and handles their choices.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Willkommen zu Ihrem Lesebuch!");
        
        while (true) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    addBook();
                    break;
                case "2":
                    showBooks();
                    break;
                case "3":
                    searchBooks();
                    break;
                case "4":
                    editBook();
                    break;
                case "5":
                    deleteBook();
                    break;
                case "6":
                    System.out.println("Auf Wiedersehen! Programm wird beendet.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Ungültige Eingabe. Bitte wählen Sie eine Option zwischen 1-6.");
            }
        }
    }

    /**
     * Prints the main menu options.
     */
    private static void printMenu() {
        System.out.println("\n=== Lesebuch - Hauptmenü ===");
        System.out.println("1) Buch hinzufügen");
        System.out.println("2) Bücher anzeigen");
        System.out.println("3) Bücher suchen");
        System.out.println("4) Buch bearbeiten");
        System.out.println("5) Buch löschen");
        System.out.println("6) Beenden");
        System.out.print("Bitte wählen Sie eine Option (1-6): ");
    }

    /**
     * Adds a new book to the reading list.
     * Prompts the user for book details and validates the input.
     */
    public static void addBook() {
        System.out.println("\n=== Neues Buch hinzufügen ===");
        
        System.out.print("Titel: ");
        String title = getNotEmptyInput("Titel darf nicht leer sein. Bitte erneut eingeben: ");

        System.out.print("Autor: ");
        String author = getNotEmptyInput("Autor darf nicht leer sein. Bitte erneut eingeben: ");

        int pages = getIntegerInput("Seitenzahl: ", "Bitte geben Sie eine gültige Zahl ein: ", 1, Integer.MAX_VALUE);

        System.out.print("Notizen: ");
        String notes = scanner.nextLine();

        int rating = getIntegerInput("Bewertung (0-5): ", "Bitte geben Sie eine Zahl zwischen 0 und 5 ein: ", 0, 5);

        Book newBook = new Book(title, author, pages, notes, rating);
        books.add(newBook);

        System.out.println("Buch \"" + title + "\" wurde erfolgreich hinzugefügt.");
    }

    /**
     * Displays all books in the reading list.
     */
    public static void showBooks() {
        System.out.println("\n=== Alle Bücher ===");
        if (books.isEmpty()) {
            System.out.println("Noch keine Bücher eingetragen.");
        } else {
            System.out.println("Anzahl der Bücher: " + books.size());
            for (int i = 0; i < books.size(); i++) {
                System.out.println("\nBuch #" + (i + 1) + books.get(i));
            }
        }
    }

    /**
     * Searches for books by title or author.
     */
    public static void searchBooks() {
        if (books.isEmpty()) {
            System.out.println("\nNoch keine Bücher eingetragen, um zu suchen.");
            return;
        }

        System.out.println("\n=== Bücher suchen ===");
        System.out.println("1) Nach Titel suchen");
        System.out.println("2) Nach Autor suchen");
        System.out.print("Wählen Sie eine Option (1-2): ");
        
        String option = scanner.nextLine().trim();
        
        switch (option) {
            case "1":
                searchByTitle();
                break;
            case "2":
                searchByAuthor();
                break;
            default:
                System.out.println("Ungültige Option. Zurück zum Hauptmenü.");
        }
    }

    /**
     * Searches for books by title.
     */
    private static void searchByTitle() {
        System.out.print("Geben Sie den Titel oder einen Teil davon ein: ");
        String searchTerm = scanner.nextLine().trim().toLowerCase();
        
        ArrayList<Book> results = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(searchTerm)) {
                results.add(book);
            }
        }
        
        displaySearchResults(results, "Titel");
    }

    /**
     * Searches for books by author.
     */
    private static void searchByAuthor() {
        System.out.print("Geben Sie den Autor oder einen Teil davon ein: ");
        String searchTerm = scanner.nextLine().trim().toLowerCase();
        
        ArrayList<Book> results = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().toLowerCase().contains(searchTerm)) {
                results.add(book);
            }
        }
        
        displaySearchResults(results, "Autor");
    }

    /**
     * Displays the search results.
     *
     * @param results    The list of books matching the search criteria
     * @param searchType The type of search performed (title or author)
     */
    private static void displaySearchResults(ArrayList<Book> results, String searchType) {
        if (results.isEmpty()) {
            System.out.println("Keine Bücher mit diesem " + searchType + " gefunden.");
        } else {
            System.out.println("\n=== Suchergebnisse: " + results.size() + " Buch(er) gefunden ===");
            for (int i = 0; i < results.size(); i++) {
                System.out.println("\nErgebnis #" + (i + 1) + results.get(i));
            }
        }
    }

    /**
     * Edits an existing book in the reading list.
     */
    public static void editBook() {
        if (books.isEmpty()) {
            System.out.println("\nNoch keine Bücher eingetragen, um zu bearbeiten.");
            return;
        }

        System.out.println("\n=== Buch bearbeiten ===");
        showBooks();
        
        int bookIndex = getIntegerInput(
            "\nWelches Buch möchten Sie bearbeiten? (1-" + books.size() + "): ",
            "Bitte geben Sie eine gültige Nummer zwischen 1 und " + books.size() + " ein: ",
            1, books.size()) - 1;
        
        Book selectedBook = books.get(bookIndex);
        
        System.out.println("\nAktuelles Buch: " + selectedBook);
        System.out.println("\nBitte geben Sie die neuen Informationen ein (oder leer lassen, um unverändert zu bleiben):");
        
        // Update title
        System.out.print("Neuer Titel [" + selectedBook.getTitle() + "]: ");
        String newTitle = scanner.nextLine().trim();
        if (!newTitle.isEmpty()) {
            selectedBook.setTitle(newTitle);
        }
        
        // Update author
        System.out.print("Neuer Autor [" + selectedBook.getAuthor() + "]: ");
        String newAuthor = scanner.nextLine().trim();
        if (!newAuthor.isEmpty()) {
            selectedBook.setAuthor(newAuthor);
        }
        
        // Update pages
        System.out.print("Neue Seitenzahl [" + selectedBook.getPages() + "]: ");
        String newPagesStr = scanner.nextLine().trim();
        if (!newPagesStr.isEmpty()) {
            try {
                int newPages = Integer.parseInt(newPagesStr);
                if (newPages > 0) {
                    selectedBook.setPages(newPages);
                } else {
                    System.out.println("Ungültige Seitenzahl. Wert bleibt unverändert.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Eingabe. Seitenzahl bleibt unverändert.");
            }
        }
        
        // Update notes
        System.out.print("Neue Notizen [" + selectedBook.getNotes() + "]: ");
        String newNotes = scanner.nextLine().trim();
        if (!newNotes.equals("")) {  // Allow empty notes
            selectedBook.setNotes(newNotes);
        }
        
        // Update rating
        String ratingPrompt = "Neue Bewertung (0-5) [" + selectedBook.getRating() + "]: ";
        String ratingErrorMsg = "Bitte geben Sie eine Zahl zwischen 0 und 5 ein: ";
        System.out.print(ratingPrompt);
        String newRatingStr = scanner.nextLine().trim();
        if (!newRatingStr.isEmpty()) {
            try {
                int newRating = Integer.parseInt(newRatingStr);
                if (newRating >= 0 && newRating <= 5) {
                    selectedBook.setRating(newRating);
                } else {
                    System.out.println("Bewertung muss zwischen 0 und 5 liegen. Wert bleibt unverändert.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Eingabe. Bewertung bleibt unverändert.");
            }
        }
        
        System.out.println("\nBuch wurde erfolgreich aktualisiert.");
        System.out.println(selectedBook);
    }

    /**
     * Deletes a book from the reading list.
     */
    public static void deleteBook() {
        if (books.isEmpty()) {
            System.out.println("\nNoch keine Bücher eingetragen, um zu löschen.");
            return;
        }

        System.out.println("\n=== Buch löschen ===");
        showBooks();
        
        int bookIndex = getIntegerInput(
            "\nWelches Buch möchten Sie löschen? (1-" + books.size() + "): ",
            "Bitte geben Sie eine gültige Nummer zwischen 1 und " + books.size() + " ein: ",
            1, books.size()) - 1;
        
        Book bookToDelete = books.get(bookIndex);
        System.out.println("\nSind Sie sicher, dass Sie dieses Buch löschen möchten?");
        System.out.println(bookToDelete);
        System.out.print("\nBestätigen Sie mit 'ja' oder 'j': ");
        
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("ja") || confirmation.equals("j")) {
            books.remove(bookIndex);
            System.out.println("Buch wurde erfolgreich gelöscht.");
        } else {
            System.out.println("Löschvorgang abgebrochen.");
        }
    }

    /**
     * Gets a valid non-empty string input from the user.
     *
     * @param errorMessage The error message to display for invalid input
     * @return A non-empty string
     */
    private static String getNotEmptyInput(String errorMessage) {
        String input = scanner.nextLine().trim();
        while (input.isEmpty()) {
            System.out.print(errorMessage);
            input = scanner.nextLine().trim();
        }
        return input;
    }

    /**
     * Gets a valid integer input from the user within the specified range.
     *
     * @param prompt       The prompt to display to the user
     * @param errorMessage The error message for invalid input
     * @param min          The minimum acceptable value
     * @param max          The maximum acceptable value
     * @return A valid integer within the specified range
     */
    private static int getIntegerInput(String prompt, String errorMessage, int min, int max) {
        int value;
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    break;
                } else {
                    System.out.print(errorMessage);
                }
            } catch (NumberFormatException e) {
                System.out.print(errorMessage);
            }
        }
        return value;
    }
}
