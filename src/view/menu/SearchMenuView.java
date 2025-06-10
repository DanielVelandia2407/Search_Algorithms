package view.menu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class SearchMenuView extends JFrame {

    private JButton btnPartialSearch;
    private JButton btnTotalSearch;
    private JButton btnBack;

    public SearchMenuView() {
        // Basic window configuration
        setTitle("Tipos de Búsqueda");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // Set soft background color for the entire window
        getContentPane().setBackground(new Color(240, 248, 255));

        // Top panel with title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel Blue
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Tipos de Búsqueda");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Seleccione el tipo de búsqueda a realizar");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitle.setForeground(new Color(240, 248, 255));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(lblSubtitle);

        add(titlePanel, BorderLayout.NORTH);

        // Center panel with buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBackground(new Color(240, 248, 255));

        // Custom styled buttons
        btnPartialSearch = createStyledButton("Búsqueda Parcial", new Color(41, 128, 185));
        btnTotalSearch = createStyledButton("Búsqueda Total", new Color(46, 134, 193));
        btnBack = createStyledButton("Volver", new Color(231, 76, 60));

        // Add empty panel to maintain grid layout balance
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(new Color(240, 248, 255));

        buttonPanel.add(btnPartialSearch);
        buttonPanel.add(btnTotalSearch);
        buttonPanel.add(emptyPanel);
        buttonPanel.add(btnBack);

        centerPanel.add(buttonPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with information
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(220, 220, 220));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("© 2025 - Search Types v1.0 Grupo 1");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(100, 100, 100));

        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);

        // Back button action (empty for now)
        btnBack.addActionListener(e -> {
            // Add back functionality here
        });
    }

    // Method to create a styled button
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50));

        // Optional hover effect would require additional listeners
        return button;
    }

    // Methods to assign external actions to buttons
    public void addPartialSearchListener(ActionListener listener) {
        btnPartialSearch.addActionListener(listener);
    }

    public void addTotalSearchListener(ActionListener listener) {
        btnTotalSearch.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    // Method to show the window
    public void showWindow() {
        setVisible(true);
    }
}