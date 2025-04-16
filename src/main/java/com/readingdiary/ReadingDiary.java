package com.readingdiary;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.time.format.DateTimeFormatter;

public class ReadingDiary {

    private static List<Book> books = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    private static final String BOLD = "\u001B[1m";

    public static void main(String[] args) {
        books = BookDataManager.loadBooks();
        displayBanner();

        while (true) {
            printMenu();
            switch (scanner.nextLine().trim()) {
                case "1" -> addBook();
                case "2" -> showBooks();
                case "3" -> searchBooks();
                case "4" -> editBook();
                case "5" -> deleteBook();
                case "6" -> changeReadingStatus();
                case "7" -> showStatistics();
                case "8" -> saveAndExit();
                default -> System.out.println(RED + "Ungültige Auswahl." + RESET);
            }
        }
    }

    private static void displayBanner() {
        System.out.println(CYAN + BOLD + "=== MODERNES LESETAGEBUCH ===" + RESET);
        System.out.println(GREEN + "Verfolge und bewerte deine Bücher!" + RESET);
        System.out.println(CYAN + "===============================" + RESET);
    }

    private static void printMenu() {
        System.out.println("\n" + BLUE + BOLD + "Menü" + RESET);
        String[] options = {"Buch hinzufügen", "Bücher anzeigen", "Bücher suchen", "Buch bearbeiten", "Buch löschen", "Status ändern", "Statistiken anzeigen", "Speichern & Beenden"};
        for (int i = 0; i < options.length; i++) {
            System.out.println(GREEN + (i + 1) + ") " + WHITE + options[i] + RESET);
        }
        System.out.print(YELLOW + "Option wählen: " + RESET);
    }

    private static void addBook() {
        System.out.print(YELLOW + "Titel: " + RESET);
        String title = scanner.nextLine();
        System.out.print(YELLOW + "Autor: " + RESET);
        String author = scanner.nextLine();
        System.out.print(YELLOW + "Seitenzahl: " + RESET);
        int pages = Integer.parseInt(scanner.nextLine());
        System.out.print(YELLOW + "Notizen: " + RESET);
        String notes = scanner.nextLine();
        System.out.print(YELLOW + "Bewertung (0-5): " + RESET);
        int rating = Integer.parseInt(scanner.nextLine());
        Book.ReadingStatus status = selectReadingStatus();

        books.add(new Book(title, author, pages, notes, rating, status));
        BookDataManager.saveBooks(books);
        System.out.println(GREEN + "Buch hinzugefügt." + RESET);
    }

    private static void showBooks() {
        if (books.isEmpty()) {
            System.out.println(YELLOW + "Keine Bücher vorhanden." + RESET);
            return;
        }
        IntStream.range(0, books.size()).forEach(i -> System.out.println(PURPLE + (i + 1) + ". " + RESET + books.get(i)));
    }

    private static void searchBooks() {
        System.out.print(YELLOW + "Suchbegriff: " + RESET);
        String term = scanner.nextLine().toLowerCase();
        List<Book> results = books.stream().filter(b -> b.getTitle().toLowerCase().contains(term)).collect(Collectors.toList());
        results.forEach(System.out::println);
    }

    private static void editBook() {
        showBooks();
        System.out.print(YELLOW + "Buchnummer zum Bearbeiten: " + RESET);
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= books.size()) return;
        Book book = books.get(index);

        System.out.print(YELLOW + "Neuer Titel (" + book.getTitle() + "): " + RESET);
        String title = scanner.nextLine();
        if (!title.isEmpty()) {
            book = new Book(title, book.getAuthor(), book.getPages(), book.getNotes(), book.getRating(), book.getStatus());
        }

        books.set(index, book);
        BookDataManager.saveBooks(books);
    }

    private static void deleteBook() {
        showBooks();
        System.out.print(YELLOW + "Buchnummer zum Löschen: " + RESET);
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index >= 0 && index < books.size()) {
            books.remove(index);
            BookDataManager.saveBooks(books);
        }
    }

    private static void changeReadingStatus() {
        showBooks();
        System.out.print(YELLOW + "Buchnummer zum Status ändern: " + RESET);
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= books.size()) return;
        Book book = books.get(index);
        books.set(index, new Book(book.getTitle(), book.getAuthor(), book.getPages(), book.getNotes(), book.getRating(), selectReadingStatus()));
        BookDataManager.saveBooks(books);
    }

    private static void showStatistics() {
        System.out.println(GREEN + "Gesamt Bücher: " + books.size() + RESET);
        System.out.println(GREEN + "Gelesen: " + books.stream().filter(b -> b.getStatus() == Book.ReadingStatus.FINISHED).count() + RESET);
    }

    private static Book.ReadingStatus selectReadingStatus() {
        Book.ReadingStatus[] statuses = Book.ReadingStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.println(GREEN + (i + 1) + ") " + WHITE + statuses[i].getDisplayName() + RESET);
        }
        return statuses[Integer.parseInt(scanner.nextLine()) - 1];
    }

    private static void saveAndExit() {
        BookDataManager.saveBooks(books);
        System.exit(0);
    }
}