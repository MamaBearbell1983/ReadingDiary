package com.readingdiary;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReadingDiaryGUI extends JFrame {

    private final DefaultListModel<String> bookListModel;
    private final JTextArea outputArea;
    private final List<Book> books;

    public ReadingDiaryGUI() {
        setTitle("Mein Reading Diary");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        books = BookDataManager.loadBooks();
        bookListModel = new DefaultListModel<>();

        JLabel header = new JLabel(" Mein Reading Diary", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addButton(buttonPanel, "➕ Hinzufügen", this::addBook);
        addButton(buttonPanel, "📖 Anzeigen", this::showBooks);
        addButton(buttonPanel, "🔍 Suchen", this::searchBooks);
        addButton(buttonPanel, "✏️ Bearbeiten", this::editBook);
        addButton(buttonPanel, "❌ Löschen", this::deleteBook);
        addButton(buttonPanel, "📊 Statistiken", this::showStatistics);
        addButton(buttonPanel, "🟢 Status ändern", this::changeStatus);
        addButton(buttonPanel, "💾 Speichern & 🛑 Beenden", this::saveAndExit);

        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void addButton(JPanel panel, String title, Runnable action) {
        JButton button = new JButton(title);
        button.addActionListener(e -> action.run());
        panel.add(button);
    }

    private void addBook() {
        String title = JOptionPane.showInputDialog(this, "Titel:");
        String author = JOptionPane.showInputDialog(this, "Autor:");
        String pagesStr = JOptionPane.showInputDialog(this, "Seitenanzahl:");
        String notes = JOptionPane.showInputDialog(this, "Notizen:");
        String ratingStr = JOptionPane.showInputDialog(this, "Bewertung (0–5):");

        if (title == null || author == null || pagesStr == null || ratingStr == null) return;

        try {
            int pages = Integer.parseInt(pagesStr);
            int rating = Integer.parseInt(ratingStr);

            Book.ReadingStatus status = (Book.ReadingStatus) JOptionPane.showInputDialog(
                    this, "Lesestatus wählen:", "Status",
                    JOptionPane.QUESTION_MESSAGE, null,
                    Book.ReadingStatus.values(), Book.ReadingStatus.UNREAD
            );

            Book book = new Book(title, author, pages, notes, rating, status);
            books.add(book);
            BookDataManager.saveBooks(books);
            outputArea.setText("✅ Buch hinzugefügt:\n" + book);
        } catch (NumberFormatException e) {
            outputArea.setText("❌ Ungültige Eingabe bei Seitenzahl oder Bewertung.");
        }
    }

    private void showBooks() {
        if (books.isEmpty()) {
            outputArea.setText("📂 Keine Bücher vorhanden.");
        } else {
            StringBuilder sb = new StringBuilder("📚 Bücherliste:\n");
            int i = 1;
            for (Book book : books) {
                sb.append(i++).append(") ").append(book).append("\n\n");
            }
            outputArea.setText(sb.toString());
        }
    }

    private void searchBooks() {
        String query = JOptionPane.showInputDialog(this, "Suchbegriff (Titel oder Autor):");
        if (query == null || query.isBlank()) return;

        List<Book> results = books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        b.getAuthor().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            outputArea.setText("🔍 Keine Treffer gefunden.");
        } else {
            StringBuilder sb = new StringBuilder("🔍 Suchergebnisse:\n");
            int i = 1;
            for (Book book : results) {
                sb.append(i++).append(") ").append(book).append("\n\n");
            }
            outputArea.setText(sb.toString());
        }
    }

    private void editBook() {
        String input = JOptionPane.showInputDialog(this, "Nummer des Buchs zum Bearbeiten:");
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < books.size()) {
                Book old = books.get(index);

                String newTitle = JOptionPane.showInputDialog(this, "Neuer Titel:", old.getTitle());
                String newAuthor = JOptionPane.showInputDialog(this, "Neuer Autor:", old.getAuthor());
                String newPagesStr = JOptionPane.showInputDialog(this, "Neue Seitenanzahl:", old.getPages());
                String newNotes = JOptionPane.showInputDialog(this, "Neue Notizen:", old.getNotes());
                String newRatingStr = JOptionPane.showInputDialog(this, "Neue Bewertung (0–5):", old.getRating());

                int newPages = Integer.parseInt(newPagesStr);
                int newRating = Integer.parseInt(newRatingStr);

                Book updated = new Book(
                        newTitle, newAuthor, newPages, newNotes, newRating, old.getStatus());

                books.set(index, updated);
                BookDataManager.saveBooks(books);
                outputArea.setText("✅ Buch aktualisiert:\n" + updated);
            } else {
                outputArea.setText("❌ Ungültige Nummer.");
            }
        } catch (Exception e) {
            outputArea.setText("❌ Fehler beim Bearbeiten.");
        }
    }

    private void deleteBook() {
        String input = JOptionPane.showInputDialog(this, "Nummer des Buchs zum Löschen:");
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < books.size()) {
                Book removed = books.remove(index);
                BookDataManager.saveBooks(books);
                outputArea.setText("🗑️ Buch gelöscht:\n" + removed);
            } else {
                outputArea.setText("❌ Ungültige Nummer.");
            }
        } catch (Exception e) {
            outputArea.setText("❌ Fehler beim Löschen.");
        }
    }

    private void changeStatus() {
        String input = JOptionPane.showInputDialog(this, "Nummer des Buchs:");
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < books.size()) {
                Book old = books.get(index);
                Book.ReadingStatus newStatus = (Book.ReadingStatus) JOptionPane.showInputDialog(
                        this, "Neuer Lesestatus:", "Status ändern",
                        JOptionPane.QUESTION_MESSAGE, null,
                        Book.ReadingStatus.values(), old.getStatus());

                Book updated = new Book(old.getTitle(), old.getAuthor(), old.getPages(),
                        old.getNotes(), old.getRating(), newStatus);

                books.set(index, updated);
                BookDataManager.saveBooks(books);
                outputArea.setText("✅ Status geändert:\n" + updated);
            } else {
                outputArea.setText("❌ Ungültige Nummer.");
            }
        } catch (Exception e) {
            outputArea.setText("❌ Fehler beim Status ändern.");
        }
    }

    private void showStatistics() {
        long gelesen = books.stream().filter(b -> b.getStatus() == Book.ReadingStatus.FINISHED).count();
        long ungelesen = books.stream().filter(b -> b.getStatus() == Book.ReadingStatus.UNREAD).count();
        long inProgress = books.stream().filter(b -> b.getStatus() == Book.ReadingStatus.IN_PROGRESS).count();
        long abgebrochen = books.stream().filter(b -> b.getStatus() == Book.ReadingStatus.ABANDONED).count();

        outputArea.setText("📊 Statistik:\n" +
                "Gesamt: " + books.size() + "\n" +
                "Gelesen: " + gelesen + "\n" +
                "Ungelesen: " + ungelesen + "\n" +
                "In Bearbeitung: " + inProgress + "\n" +
                "Abgebrochen: " + abgebrochen);
    }

    private void saveAndExit() {
        BookDataManager.saveBooks(books);
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ReadingDiaryGUI::new);
    }
}
