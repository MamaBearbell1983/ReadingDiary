package com.readingdiary;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import com.readingdiary.Book.ReadingStatus;

/**
 * Eine moderne Konsolenanwendung zum Verfolgen und Bewerten gelesener Bücher.
 * Benutzer können Bücher mit Details wie Titel, Autor, Seitenzahl, Notizen und Bewertung hinzufügen,
 * ihre Leseliste einsehen, bearbeiten, durchsuchen und löschen.
 * Die Daten werden zwischen Programmläufen in einer Datei gespeichert.
 */
public class ReadingDiary {

    private static List<Book> books = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    // ANSI-Farbcodes für die Konsolenausgabe
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    private static final String BOLD = "\u001B[1m";

    /**
     * Hauptmethode, die die Lesebuch-Anwendung ausführt.
     * Präsentiert ein Menü für den Benutzer und verarbeitet seine Auswahl.
     * Lädt die Bücherdaten beim Start und speichert sie beim Beenden.
     *
     * @param args Kommandozeilenargumente (nicht verwendet)
     */
    public static void main(String[] args) {
        displayWelcomeBanner();
        
        // Lade bestehende Bücher aus der Datei beim Start
        books = BookDataManager.loadBooks();
        System.out.println(CYAN + "✓ " + books.size() + " Bücher geladen" + RESET);
        
        while (true) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> addBook();
                case "2" -> showBooks();
                case "3" -> searchBooks();
                case "4" -> editBook();
                case "5" -> deleteBook();
                case "6" -> {
                    saveAndExit();
                    return;
                }
                case "7" -> changeReadingStatus();
                case "8" -> showStatistics();
                default -> System.out.println(RED + "Ungültige Eingabe. Bitte wählen Sie eine Option zwischen 1-8." + RESET);
            }
        }
    }

    /**
     * Zeigt ein schönes Willkommensbanner an.
     */
    private static void displayWelcomeBanner() {
        System.out.println(CYAN + "╔══════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║  " + BOLD + YELLOW + "📚  MODERNES LESETAGEBUCH  📚" + RESET + CYAN + "                   ║" + RESET);
        System.out.println(CYAN + "║                                                      ║" + RESET);
        System.out.println(CYAN + "║  " + GREEN + "Verfolgen Sie Ihre Lektüre und teilen Sie Ihre       " + RESET + CYAN + "║" + RESET);
        System.out.println(CYAN + "║  " + GREEN + "Gedanken zu jedem gelesenen Buch!                    " + RESET + CYAN + "║" + RESET);
        System.out.println(CYAN + "╚══════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
    }

    /**
     * Druckt die Hauptmenüoptionen mit Farbcodierung.
     */
    private static void printMenu() {
        System.out.println("\n" + BLUE + BOLD + "=== Lesebuch - Hauptmenü ===" + RESET);
        System.out.println(GREEN + "1) " + WHITE + "Buch hinzufügen" + RESET);
        System.out.println(GREEN + "2) " + WHITE + "Bücher anzeigen" + RESET);
        System.out.println(GREEN + "3) " + WHITE + "Bücher suchen" + RESET);
        System.out.println(GREEN + "4) " + WHITE + "Buch bearbeiten" + RESET);
        System.out.println(GREEN + "5) " + WHITE + "Buch löschen" + RESET);
        System.out.println(GREEN + "6) " + WHITE + "Speichern und beenden" + RESET);
        System.out.println(GREEN + "7) " + WHITE + "Lesestatus ändern" + RESET);
        System.out.println(GREEN + "8) " + WHITE + "Statistiken anzeigen" + RESET);
        System.out.print(YELLOW + "Bitte wählen Sie eine Option (1-8): " + RESET);
    }

    /**
     * Fügt ein neues Buch zur Leseliste hinzu.
     * Fordert vom Benutzer Buchdetails an und validiert die Eingabe.
     */
    public static void addBook() {
        System.out.println("\n" + BLUE + BOLD + "=== Neues Buch hinzufügen ===" + RESET);
        
        System.out.print(YELLOW + "Titel: " + RESET);
        String title = getNotEmptyInput(RED + "Titel darf nicht leer sein. Bitte erneut eingeben: " + RESET);

        System.out.print(YELLOW + "Autor: " + RESET);
        String author = getNotEmptyInput(RED + "Autor darf nicht leer sein. Bitte erneut eingeben: " + RESET);

        int pages = getIntegerInput(
            YELLOW + "Seitenzahl: " + RESET, 
            RED + "Bitte geben Sie eine gültige Zahl ein: " + RESET, 
            1, Integer.MAX_VALUE
        );

        System.out.print(YELLOW + "Notizen: " + RESET);
        String notes = scanner.nextLine();

        int rating = getIntegerInput(
            YELLOW + "Bewertung (0-5): " + RESET, 
            RED + "Bitte geben Sie eine Zahl zwischen 0 und 5 ein: " + RESET, 
            0, 5
        );
        
        System.out.println("\n" + YELLOW + "Lesestatus auswählen:" + RESET);
        ReadingStatus status = selectReadingStatus();

        Book newBook = new Book(title, author, pages, notes, rating, status);
        books.add(newBook);
        
        // Speichere die aktualisierte Liste
        BookDataManager.saveBooks(books);

        System.out.println(GREEN + "✓ Buch \"" + title + "\" wurde erfolgreich hinzugefügt." + RESET);
    }

    /**
     * Zeigt alle Bücher in der Leseliste an.
     */
    public static void showBooks() {
        System.out.println("\n" + BLUE + BOLD + "=== Alle Bücher ===" + RESET);
        if (books.isEmpty()) {
            System.out.println(YELLOW + "Noch keine Bücher eingetragen." + RESET);
        } else {
            System.out.println(GREEN + "Anzahl der Bücher: " + books.size() + RESET);
            
            // Verwende Streams und IntStream für modernes Java
            IntStream.range(0, books.size())
                .forEach(i -> System.out.println("\n" + PURPLE + "Buch #" + (i + 1) + RESET + books.get(i)));
        }
    }

    /**
     * Sucht nach Büchern nach Titel oder Autor.
     */
    public static void searchBooks() {
        if (books.isEmpty()) {
            System.out.println("\n" + YELLOW + "Noch keine Bücher eingetragen, um zu suchen." + RESET);
            return;
        }

        System.out.println("\n" + BLUE + BOLD + "=== Bücher suchen ===" + RESET);
        System.out.println(GREEN + "1) " + WHITE + "Nach Titel suchen" + RESET);
        System.out.println(GREEN + "2) " + WHITE + "Nach Autor suchen" + RESET);
        System.out.println(GREEN + "3) " + WHITE + "Nach Lesestatus suchen" + RESET);
        System.out.print(YELLOW + "Wählen Sie eine Option (1-3): " + RESET);
        
        String option = scanner.nextLine().trim();
        
        switch (option) {
            case "1" -> searchByTitle();
            case "2" -> searchByAuthor();
            case "3" -> searchByReadingStatus();
            default -> System.out.println(RED + "Ungültige Option. Zurück zum Hauptmenü." + RESET);
        }
    }

    /**
     * Sucht nach Büchern nach Titel.
     */
    private static void searchByTitle() {
        System.out.print(YELLOW + "Geben Sie den Titel oder einen Teil davon ein: " + RESET);
        String searchTerm = scanner.nextLine().trim().toLowerCase();
        
        // Verwende Stream-API für die Filterung
        List<Book> results = books.stream()
            .filter(book -> book.title().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
        
        displaySearchResults(results, "Titel");
    }

    /**
     * Sucht nach Büchern nach Autor.
     */
    private static void searchByAuthor() {
        System.out.print(YELLOW + "Geben Sie den Autor oder einen Teil davon ein: " + RESET);
        String searchTerm = scanner.nextLine().trim().toLowerCase();
        
        // Verwende Stream-API für die Filterung
        List<Book> results = books.stream()
            .filter(book -> book.author().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
        
        displaySearchResults(results, "Autor");
    }
    
    /**
     * Sucht nach Büchern nach Lesestatus.
     */
    private static void searchByReadingStatus() {
        System.out.println(YELLOW + "Wählen Sie den Lesestatus zum Suchen:" + RESET);
        ReadingStatus status = selectReadingStatus();
        
        // Verwende Stream-API für die Filterung
        List<Book> results = books.stream()
            .filter(book -> book.status() == status)
            .collect(Collectors.toList());
        
        displaySearchResults(results, "Lesestatus");
    }

    /**
     * Zeigt die Suchergebnisse an.
     *
     * @param results    Die Liste der Bücher, die den Suchkriterien entsprechen
     * @param searchType Die Art der durchgeführten Suche (Titel, Autor oder Status)
     */
    private static void displaySearchResults(List<Book> results, String searchType) {
        if (results.isEmpty()) {
            System.out.println(YELLOW + "Keine Bücher mit diesem " + searchType + " gefunden." + RESET);
        } else {
            System.out.println("\n" + GREEN + "=== Suchergebnisse: " + results.size() + " Buch(er) gefunden ===" + RESET);
            
            // Verwende Stream-API für die Ausgabe
            IntStream.range(0, results.size())
                .forEach(i -> System.out.println("\n" + PURPLE + "Ergebnis #" + (i + 1) + RESET + results.get(i)));
        }
    }

    /**
     * Bearbeitet ein vorhandenes Buch in der Leseliste.
     */
    public static void editBook() {
        if (books.isEmpty()) {
            System.out.println("\n" + YELLOW + "Noch keine Bücher eingetragen, um zu bearbeiten." + RESET);
            return;
        }

        System.out.println("\n" + BLUE + BOLD + "=== Buch bearbeiten ===" + RESET);
        showBooks();
        
        int bookIndex = getIntegerInput(
            "\n" + YELLOW + "Welches Buch möchten Sie bearbeiten? (1-" + books.size() + "): " + RESET,
            RED + "Bitte geben Sie eine gültige Nummer zwischen 1 und " + books.size() + " ein: " + RESET,
            1, books.size()) - 1;
        
        Book selectedBook = books.get(bookIndex);
        
        System.out.println("\n" + GREEN + "Aktuelles Buch: " + RESET + selectedBook);
        System.out.println("\n" + YELLOW + "Bitte geben Sie die neuen Informationen ein (oder leer lassen, um unverändert zu bleiben):" + RESET);
        
        // Immutable Objekt durch Builder-Pattern bearbeiten
        Book updatedBook = selectedBook;
        
        // Titel aktualisieren
        System.out.print(YELLOW + "Neuer Titel [" + selectedBook.title() + "]: " + RESET);
        String newTitle = scanner.nextLine().trim();
        if (!newTitle.isEmpty()) {
            updatedBook = updatedBook.withTitle(newTitle);
        }
        
        // Autor aktualisieren
        System.out.print(YELLOW + "Neuer Autor [" + selectedBook.author() + "]: " + RESET);
        String newAuthor = scanner.nextLine().trim();
        if (!newAuthor.isEmpty()) {
            updatedBook = updatedBook.withAuthor(newAuthor);
        }
        
        // Seitenzahl aktualisieren
        System.out.print(YELLOW + "Neue Seitenzahl [" + selectedBook.pages() + "]: " + RESET);
        String newPagesStr = scanner.nextLine().trim();
        if (!newPagesStr.isEmpty()) {
            try {
                int newPages = Integer.parseInt(newPagesStr);
                if (newPages > 0) {
                    updatedBook = updatedBook.withPages(newPages);
                } else {
                    System.out.println(RED + "Ungültige Seitenzahl. Wert bleibt unverändert." + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Ungültige Eingabe. Seitenzahl bleibt unverändert." + RESET);
            }
        }
        
        // Notizen aktualisieren
        System.out.print(YELLOW + "Neue Notizen [" + selectedBook.notes() + "]: " + RESET);
        String newNotes = scanner.nextLine().trim();
        if (!newNotes.equals("")) {  // Erlaube leere Notizen
            updatedBook = updatedBook.withNotes(newNotes);
        }
        
        // Bewertung aktualisieren
        System.out.print(YELLOW + "Neue Bewertung (0-5) [" + selectedBook.rating() + "]: " + RESET);
        String newRatingStr = scanner.nextLine().trim();
        if (!newRatingStr.isEmpty()) {
            try {
                int newRating = Integer.parseInt(newRatingStr);
                if (newRating >= 0 && newRating <= 5) {
                    updatedBook = updatedBook.withRating(newRating);
                } else {
                    System.out.println(RED + "Bewertung muss zwischen 0 und 5 liegen. Wert bleibt unverändert." + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Ungültige Eingabe. Bewertung bleibt unverändert." + RESET);
            }
        }
        
        // Lesestatus aktualisieren
        System.out.println(YELLOW + "Neuer Lesestatus [" + selectedBook.status().getDisplayName() + "]:" + RESET);
        System.out.println(YELLOW + "(Drücken Sie Enter, um den Status beizubehalten)" + RESET);
        String statusInput = scanner.nextLine().trim();
        if (!statusInput.isEmpty()) {
            ReadingStatus newStatus = selectReadingStatus();
            updatedBook = updatedBook.withStatus(newStatus);
        }
        
        // Ersetze das alte Buch durch das aktualisierte
        books.set(bookIndex, updatedBook);
        
        // Speichere die aktualisierte Liste
        BookDataManager.saveBooks(books);

        System.out.println(GREEN + "✓ Buch wurde erfolgreich aktualisiert." + RESET);
        System.out.println(updatedBook);
    }

    /**
     * Löscht ein Buch aus der Leseliste.
     */
    public static void deleteBook() {
        if (books.isEmpty()) {
            System.out.println("\n" + YELLOW + "Noch keine Bücher eingetragen, um zu löschen." + RESET);
            return;
        }

        System.out.println("\n" + BLUE + BOLD + "=== Buch löschen ===" + RESET);
        showBooks();
        
        int bookIndex = getIntegerInput(
            "\n" + YELLOW + "Welches Buch möchten Sie löschen? (1-" + books.size() + "): " + RESET,
            RED + "Bitte geben Sie eine gültige Nummer zwischen 1 und " + books.size() + " ein: " + RESET,
            1, books.size()) - 1;
        
        Book bookToDelete = books.get(bookIndex);
        System.out.println("\n" + RED + "Sind Sie sicher, dass Sie dieses Buch löschen möchten?" + RESET);
        System.out.println(bookToDelete);
        System.out.print("\n" + YELLOW + "Bestätigen Sie mit 'ja' oder 'j': " + RESET);
        
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("ja") || confirmation.equals("j")) {
            books.remove(bookIndex);
            
            // Speichere die aktualisierte Liste
            BookDataManager.saveBooks(books);
            
            System.out.println(GREEN + "✓ Buch wurde erfolgreich gelöscht." + RESET);
        } else {
            System.out.println(YELLOW + "Löschvorgang abgebrochen." + RESET);
        }
    }
    
    /**
     * Ändert den Lesestatus eines Buches.
     */
    private static void changeReadingStatus() {
        if (books.isEmpty()) {
            System.out.println("\n" + YELLOW + "Noch keine Bücher eingetragen, um den Status zu ändern." + RESET);
            return;
        }

        System.out.println("\n" + BLUE + BOLD + "=== Lesestatus ändern ===" + RESET);
        showBooks();
        
        int bookIndex = getIntegerInput(
            "\n" + YELLOW + "Bei welchem Buch möchten Sie den Lesestatus ändern? (1-" + books.size() + "): " + RESET,
            RED + "Bitte geben Sie eine gültige Nummer zwischen 1 und " + books.size() + " ein: " + RESET,
            1, books.size()) - 1;
        
        Book selectedBook = books.get(bookIndex);
        System.out.println("\n" + GREEN + "Aktuelles Buch: " + RESET + selectedBook);
        
        System.out.println("\n" + YELLOW + "Neuen Lesestatus wählen:" + RESET);
        ReadingStatus newStatus = selectReadingStatus();
        
        // Aktualisiere das Buch mit dem neuen Status
        Book updatedBook = selectedBook.withStatus(newStatus);
        books.set(bookIndex, updatedBook);
        
        // Speichere die aktualisierte Liste
        BookDataManager.saveBooks(books);
        
        System.out.println(GREEN + "✓ Lesestatus wurde erfolgreich aktualisiert auf: " + 
                          newStatus.getDisplayName() + RESET);
    }
    
    /**
     * Zeigt Statistiken über die Bücher in der Leseliste an.
     */
    private static void showStatistics() {
        System.out.println("\n" + BLUE + BOLD + "=== Lesetagebuch-Statistiken ===" + RESET);
        
        if (books.isEmpty()) {
            System.out.println(YELLOW + "Noch keine Bücher eingetragen, um Statistiken anzuzeigen." + RESET);
            return;
        }
        
        long totalBooks = books.size();
        long booksRead = books.stream().filter(book -> book.status() == ReadingStatus.FINISHED).count();
        long booksInProgress = books.stream().filter(book -> book.status() == ReadingStatus.IN_PROGRESS).count();
        long booksUnread = books.stream().filter(book -> book.status() == ReadingStatus.UNREAD).count();
        long booksAbandoned = books.stream().filter(book -> book.status() == ReadingStatus.ABANDONED).count();
        
        double averageRating = books.stream()
            .filter(book -> book.status() == ReadingStatus.FINISHED)
            .mapToInt(Book::rating)
            .average()
            .orElse(0.0);
        
        long totalPages = books.stream()
            .filter(book -> book.status() == ReadingStatus.FINISHED)
            .mapToInt(Book::pages)
            .sum();
            
        // Finde das am besten bewertete Buch
        Optional<Book> highestRatedBook = books.stream()
            .filter(book -> book.status() == ReadingStatus.FINISHED)
            .max((b1, b2) -> Integer.compare(b1.rating(), b2.rating()));
            
        System.out.println(CYAN + "╔══════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║ " + GREEN + "Gesamt Bücher:           " + RESET + totalBooks);
        System.out.println(CYAN + "║ " + GREEN + "Gelesene Bücher:         " + RESET + booksRead);
        System.out.println(CYAN + "║ " + GREEN + "Bücher in Bearbeitung:   " + RESET + booksInProgress);
        System.out.println(CYAN + "║ " + GREEN + "Ungelesene Bücher:       " + RESET + booksUnread);
        System.out.println(CYAN + "║ " + GREEN + "Abgebrochene Bücher:     " + RESET + booksAbandoned);
        System.out.println(CYAN + "║ " + GREEN + "Durchschnittsbewertung:  " + RESET + String.format("%.1f", averageRating) + " von 5");
        System.out.println(CYAN + "║ " + GREEN + "Insgesamt gelesene Seiten: " + RESET + totalPages);
        
        if (highestRatedBook.isPresent()) {
            Book bestBook = highestRatedBook.get();
            System.out.println(CYAN + "║ " + GREEN + "Am besten bewertet:     " + RESET + bestBook.title() + 
                             " (" + bestBook.rating() + "/5)");
        }
        
        System.out.println(CYAN + "╚══════════════════════════════════════════════════════╝" + RESET);
    }
    
    /**
     * Präsentiert eine Auswahl an Lesestatus-Optionen und lässt den Benutzer eine auswählen.
     *
     * @return Der ausgewählte ReadingStatus
     */
    private static ReadingStatus selectReadingStatus() {
        ReadingStatus[] statuses = ReadingStatus.values();
        
        for (int i = 0; i < statuses.length; i++) {
            System.out.println(GREEN + (i + 1) + ") " + WHITE + statuses[i].getDisplayName() + RESET);
        }
        
        int statusIndex = getIntegerInput(
            YELLOW + "Wählen Sie einen Status (1-" + statuses.length + "): " + RESET,
            RED + "Bitte geben Sie eine gültige Nummer zwischen 1 und " + statuses.length + " ein: " + RESET,
            1, statuses.length);
            
        return statuses[statusIndex - 1];
    }
    
    /**
     * Speichert alle Bücher und beendet das Programm.
     */
    private static void saveAndExit() {
        boolean saved = BookDataManager.saveBooks(books);
        if (saved) {
            System.out.println(GREEN + "✓ Alle Bücher wurden erfolgreich gespeichert." + RESET);
        } else {
            System.out.println(RED + "! Fehler beim Speichern der Bücher." + RESET);
        }
        System.out.println(YELLOW + "Auf Wiedersehen! Programm wird beendet." + RESET);
        scanner.close();
    }

    /**
     * Holt eine gültige nicht-leere Zeichenkette vom Benutzer.
     *
     * @param errorMessage Die Fehlermeldung für ungültige Eingaben
     * @return Eine nicht-leere Zeichenkette
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
     * Holt eine gültige Ganzzahleingabe vom Benutzer innerhalb des angegebenen Bereichs.
     *
     * @param prompt       Die Aufforderung für den Benutzer
     * @param errorMessage Die Fehlermeldung für ungültige Eingaben
     * @param min          Der kleinste akzeptable Wert
     * @param max          Der größte akzeptable Wert
     * @return Eine gültige Ganzzahl innerhalb des angegebenen Bereichs
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
