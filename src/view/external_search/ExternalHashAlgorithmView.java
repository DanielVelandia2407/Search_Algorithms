package view.external_search;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class ExternalHashAlgorithmView extends JFrame {

    private JButton btnModSearch;
    private JButton btnSquaredSearch;
    private JButton btnTruncatedSearch;
    private JButton btnFoldingSearch;
    private JButton btnBack;

    public ExternalHashAlgorithmView() {
        // Basic window configuration
        setTitle("Algoritmos de Búsqueda Hash Externa");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // Set soft background color for the entire window
        getContentPane().setBackground(new Color(240, 248, 255)); // Alice Blue

        // Top panel with title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel Blue
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Algoritmos de Búsqueda Hash Externa");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Funciones hash con manejo de bloques de disco");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitle.setForeground(new Color(240, 248, 255));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(lblSubtitle);

        add(titlePanel, BorderLayout.NORTH);

        // Center panel with buttons - Improved layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Panel para los 4 botones principales en grid 2x2
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        buttonPanel.setBackground(new Color(240, 248, 255));

        // Custom styled buttons with external hash theme
        btnModSearch = createStyledButton("Función Módulo", new Color(142, 68, 173)); // Purple
        btnSquaredSearch = createStyledButton("Función Cuadrado", new Color(155, 89, 182)); // Light purple
        btnTruncatedSearch = createStyledButton("Función Truncamiento", new Color(155, 89, 182));
        btnFoldingSearch = createStyledButton("Función Plegamiento", new Color(155, 89, 182));

        buttonPanel.add(btnModSearch);
        buttonPanel.add(btnSquaredSearch);
        buttonPanel.add(btnTruncatedSearch);
        buttonPanel.add(btnFoldingSearch);

        centerPanel.add(buttonPanel, BorderLayout.CENTER);

        // Panel separado para el botón de volver
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backButtonPanel.setBackground(new Color(240, 248, 255));
        backButtonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnBack = createStyledButton("Volver", new Color(231, 76, 60));
        btnBack.setPreferredSize(new Dimension(150, 40));

        backButtonPanel.add(btnBack);
        centerPanel.add(backButtonPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with information
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(220, 220, 220));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("© 2025 - External Hash Algorithms v1.0");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(100, 100, 100));

        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);
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

        // Añadir un poco de margen interior a los botones
        button.setMargin(new Insets(10, 15, 10, 15));

        return button;
    }

    // Methods to assign external actions to buttons
    public void addModSearchListener(ActionListener listener) {
        btnModSearch.addActionListener(listener);
    }

    public void addSquaredSearchListener(ActionListener listener) {
        btnSquaredSearch.addActionListener(listener);
    }

    public void addTruncatedSearchListener(ActionListener listener) {
        btnTruncatedSearch.addActionListener(listener);
    }

    public void addFoldingSearchListener(ActionListener listener) {
        btnFoldingSearch.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    // Method to show the window
    public void showWindow() {
        setVisible(true);
    }
}