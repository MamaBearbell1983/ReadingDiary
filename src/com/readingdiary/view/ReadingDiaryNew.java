package com.readingdiary.view;

import com.readingdiary.db.BookDAO;
import com.readingdiary.db.DatabaseManager;
import com.readingdiary.model.Book;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadingDiaryGUI {

    public static void main(String[] args) {
        DatabaseManager.initDatabase();

        JFrame frame = new JFrame(" Reading Diary – Dein stylisches Lesetagebuch");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 650);
        frame.setLocationRelativeTo(null);

        Color bgColor = new Color(245, 248, 255);
        Color buttonColor = new Color(230, 240, 255);
        Color hoverColor = new Color(210, 225, 255);
        Color borderColor = new Color(180, 200, 240);
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 20);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel(" Dein digitales Reading Diary");
        titleLabel.setFont(titleFont);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        panel.add(titleLabel);

        String[] labels = {
                "Buch hinzufügen", "Bücher anzeigen", "Buch suchen",
                "Buch bearbeiten", "Buch löschen", "Statistiken",
                "Bücher filtern", "Bücher exportieren", "Speichern & Beenden"
        };

        Map<String, JButton> buttons = new HashMap<>();

        for (String label : labels) {
            String iconPath = "src/resources/icons/" + getIconFile(label);
            ImageIcon icon = new ImageIcon(iconPath);
            JButton btn = new JButton(label, icon);
            btn.setFont(buttonFont);
            btn.setFocusPainted(false);
            btn.setBackground(buttonColor);
            btn.setForeground(Color.DARK_GRAY);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(400, 40));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            btn.setVisible(false);

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(hoverColor);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(buttonColor);
                }
            });

            panel.add(btn);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            buttons.put(label, btn);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        frame.setContentPane(scrollPane);
        frame.setVisible(true);

        new Thread(() -> {
            try {
                for (String label : labels) {
                    Thread.sleep(150);
                    SwingUtilities.invokeLater(() -> {
                        buttons.get(label).setVisible(true);
                        buttons.get(label).revalidate();
                        buttons.get(label).repaint();
                    });
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();

        buttons.get("Buch hinzufügen").addActionListener(e -> {
            JTextField title = new JTextField();
            JTextField author = new JTextField();
            JTextField genre = new JTextField();
            JTextField status = new JTextField();
            JTextField rating = new JTextField();
            JTextArea notes = new JTextArea(5, 20);

            JPanel inputPanel = new JPanel(new GridLayout(0, 1));
            inputPanel.add(new JLabel("Titel:")); inputPanel.add(title);
            inputPanel.add(new JLabel("Autor:")); inputPanel.add(author);
            inputPanel.add(new JLabel("Genre:")); inputPanel.add(genre);
            inputPanel.add(new JLabel("Status (gelesen/offen):")); inputPanel.add(status);
            inputPanel.add(new JLabel("Bewertung (0–10):")); inputPanel.add(rating);
            inputPanel.add(new JLabel("Notizen:")); inputPanel.add(new JScrollPane(notes));

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "➕ Buch hinzufügen", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Book book = new Book(0, title.getText(), author.getText(), genre.getText(), status.getText(), Integer.parseInt(rating.getText()), notes.getText());
                BookDAO.insertBook(book);
                JOptionPane.showMessageDialog(null, "✅ Buch gespeichert!");
            }
        });

        buttons.get("Bücher anzeigen").addActionListener(e -> {
            List<Book> books = BookDAO.getAllBooks();
            StringBuilder sb = new StringBuilder();
            for (Book b : books) {
                sb.append(b.toString()).append("\n\n");
            }
            if (books.isEmpty()) sb.append("📭 Keine Bücher vorhanden.");
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(null, new JScrollPane(textArea), "📖 Bücher anzeigen", JOptionPane.INFORMATION_MESSAGE);
        });

        buttons.get("Buch suchen").addActionListener(e -> {
            String keyword = JOptionPane.showInputDialog("🔍 Suchbegriff (Titel oder Autor):");
            List<Book> books = BookDAO.getAllBooks();
            StringBuilder sb = new StringBuilder();
            for (Book b : books) {
                if (b.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        b.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                    sb.append(b.toString()).append("\n\n");
                }
            }
            if (sb.length() == 0) sb.append("❌ Kein Treffer gefunden.");
            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            JOptionPane.showMessageDialog(null, new JScrollPane(area), "🔍 Suchergebnisse", JOptionPane.INFORMATION_MESSAGE);
        });

        buttons.get("Buch bearbeiten").addActionListener(e -> {
            List<Book> books = BookDAO.getAllBooks();
            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(null, "📭 Keine Bücher vorhanden."); return;
            }
            String[] bookList = books.stream().map(b -> b.getId() + ": " + b.getTitle()).toArray(String[]::new);
            String selected = (String) JOptionPane.showInputDialog(null, "Wähle ein Buch zum Bearbeiten:", "✏️ Buch bearbeiten", JOptionPane.PLAIN_MESSAGE, null, bookList, bookList[0]);
            if (selected == null) return;
            int id = Integer.parseInt(selected.split(":")[0].trim());
            Book book = books.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
            if (book == null) return;

            JTextField title = new JTextField(book.getTitle());
            JTextField author = new JTextField(book.getAuthor());
            JTextField genre = new JTextField(book.getGenre());
            JTextField status = new JTextField(book.getStatus());
            JTextField rating = new JTextField(String.valueOf(book.getRating()));
            JTextArea notes = new JTextArea(book.getNotes(), 5, 20);

            JPanel inputPanel = new JPanel(new GridLayout(0, 1));
            inputPanel.add(new JLabel("Titel:")); inputPanel.add(title);
            inputPanel.add(new JLabel("Autor:")); inputPanel.add(author);
            inputPanel.add(new JLabel("Genre:")); inputPanel.add(genre);
            inputPanel.add(new JLabel("Status:")); inputPanel.add(status);
            inputPanel.add(new JLabel("Bewertung:")); inputPanel.add(rating);
            inputPanel.add(new JLabel("Notizen:")); inputPanel.add(new JScrollPane(notes));

            int result = JOptionPane.showConfirmDialog(null, inputPanel, "✏️ Buch bearbeiten", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                book.setTitle(title.getText());
                book.setAuthor(author.getText());
                book.setGenre(genre.getText());
                book.setStatus(status.getText());
                book.setRating(Integer.parseInt(rating.getText()));
                book.setNotes(notes.getText());
                BookDAO.updateBook(book);
                JOptionPane.showMessageDialog(null, "✅ Buch aktualisiert!");
            }
        });

        buttons.get("Buch löschen").addActionListener(e -> {
            List<Book> books = BookDAO.getAllBooks();
            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(null, "📭 Keine Bücher vorhanden."); return;
            }
            String[] bookList = books.stream().map(b -> b.getId() + ": " + b.getTitle()).toArray(String[]::new);
            String selected = (String) JOptionPane.showInputDialog(null, "Wähle ein Buch zum Löschen:", "🗑️ Buch löschen", JOptionPane.PLAIN_MESSAGE, null, bookList, bookList[0]);
            if (selected == null) return;
            int id = Integer.parseInt(selected.split(":")[0].trim());

            BookDAO.deleteBook(id);
            JOptionPane.showMessageDialog(null, "✅ Buch gelöscht!");
        });

        buttons.get("Statistiken").addActionListener(e -> {
            List<Book> books = BookDAO.getAllBooks();
            int total = books.size();
            long gelesen = books.stream().filter(b -> b.getStatus().equalsIgnoreCase("gelesen")).count();
            long offen = books.stream().filter(b -> b.getStatus().equalsIgnoreCase("offen")).count();
            double avg = books.stream().mapToInt(Book::getRating).average().orElse(0.0);
            String msg = String.format("📚 Gesamt: %d\n✅ Gelesen: %d\n🕓 Offen: %d\n⭐ Schnitt: %.1f", total, gelesen, offen, avg);
            JOptionPane.showMessageDialog(null, msg, "📊 Statistiken", JOptionPane.INFORMATION_MESSAGE);
        });

        buttons.get("Bücher filtern").addActionListener(e -> {
            String[] options = {"Alle", "Gelesen", "Offen"};
            int choice = JOptionPane.showOptionDialog(null, "Was möchtest du anzeigen?", "🔍 Filter", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            List<Book> books = BookDAO.getAllBooks();
            StringBuilder sb = new StringBuilder();
            for (Book b : books) {
                boolean anzeigen = switch (choice) {
                    case 1 -> b.getStatus().equalsIgnoreCase("gelesen");
                    case 2 -> b.getStatus().equalsIgnoreCase("offen");
                    default -> true;
                };
                if (anzeigen) sb.append(b.toString()).append("\n\n");
            }
            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            JOptionPane.showMessageDialog(null, new JScrollPane(area), "🔍 Gefilterte Bücher", JOptionPane.INFORMATION_MESSAGE);
        });

        buttons.get("Bücher exportieren").addActionListener(e -> {
            List<Book> books = BookDAO.getAllBooks();
            StringBuilder sb = new StringBuilder();
            for (Book b : books) {
                sb.append(b.toString()).append("\n\n");
            }
            JFileChooser fc = new JFileChooser();
            int result = fc.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                try (FileWriter writer = new FileWriter(fc.getSelectedFile() + ".txt")) {
                    writer.write(sb.toString());
                    JOptionPane.showMessageDialog(null, "✅ Bücher exportiert!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "❌ Fehler beim Export: " + ex.getMessage());
                }
            }
        });

        buttons.get("Speichern & Beenden").addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "✅ Deine Daten wurden gespeichert. Bis bald!");
            System.exit(0);
        });
    }

    private static String getIconFile(String label) {
        if (label.contains("hinzufügen")) return "add.png";
        if (label.contains("anzeigen")) return "list.png";
        if (label.contains("suchen") && !label.contains("filtern")) return "search.png";
        if (label.contains("bearbeiten")) return "edit.png";
        if (label.contains("löschen")) return "delete.png";
        if (label.contains("Statistik")) return "stats.png";
        if (label.contains("filtern")) return "filter.png";
        if (label.contains("exportieren")) return "export.png";
        if (label.contains("Beenden")) return "save.png";
        return "default.png";
    }
}


