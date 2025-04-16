package com.readingdiary;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ReadingDiaryGUI extends JFrame {

    private final DefaultListModel<String> bookListModel;
    private final java.util.List<Book> books = new ArrayList<>();

    public ReadingDiaryGUI() {
        setTitle("Mein Reading Diary");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Obere Leiste
        JLabel header = new JLabel("Mein Reading Diary", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(header, BorderLayout.NORTH);

        // Bücherliste
        bookListModel = new DefaultListModel<>();
        JList<String> bookList = new JList<>(bookListModel);
        bookList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(bookList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bücherliste"));
        add(scrollPane, BorderLayout.CENTER);

        // Buttons unten
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        String[] btnLabels = {"Hinzufügen", "Bearbeiten", "Löschen", "Suchen", "Status ändern", "Statistiken", "Speichern", "Beenden"};
        for (String label : btnLabels) {
            JButton button = new JButton(label);
            buttonPanel.add(button);
        }

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);

        // Beispielbuch hinzufügen
        addSampleBook();
    }

    private void addSampleBook() {
        Book exampleBook = new Book("Der Hobbit", "J.R.R. Tolkien", 310, "Tolles Abenteuer", 5, Book.ReadingStatus.FINISHED);
        books.add(exampleBook);
        updateBookList();
    }

    private void updateBookList() {
        bookListModel.clear();
        for (Book book : books) {
            bookListModel.addElement(book.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ReadingDiaryGUI::new);
    }
}