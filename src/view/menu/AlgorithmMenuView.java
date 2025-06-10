package view.menu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class AlgorithmMenuView extends JFrame {

    private JButton btnSequentialSearch;
    private JButton btnBinarySearch;
    private JButton btnHashSearch;
    private JButton btnTreeSearch;
    private JButton btnBack;

    public AlgorithmMenuView() {
        // Basic window configuration
        setTitle("Algoritmos de Búsqueda Interna");
        setSize(550, 400);
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

        JLabel lblTitle = new JLabel("Búsqueda Interna");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Seleccione un algoritmo de búsqueda");
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
        btnSequentialSearch = createStyledButton("Búsqueda Secuencial", new Color(41, 128, 185));
        btnBinarySearch = createStyledButton("Búsqueda Binaria", new Color(46, 134, 193));
        btnHashSearch = createStyledButton("Funciones Hash", new Color(52, 152, 219));
        btnTreeSearch = createStyledButton("Árboles de Búsqueda", new Color(155, 89, 182));

        buttonPanel.add(btnSequentialSearch);
        buttonPanel.add(btnBinarySearch);
        buttonPanel.add(btnHashSearch);
        buttonPanel.add(btnTreeSearch);

        centerPanel.add(buttonPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with back button and information
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(new Color(240, 248, 255));

        // Back button panel
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backPanel.setBackground(new Color(240, 248, 255));
        backPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        btnBack = createStyledButton("Volver", new Color(231, 76, 60));
        btnBack.setPreferredSize(new Dimension(120, 40));
        backPanel.add(btnBack);

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(new Color(220, 220, 220));
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("© 2025 - Search Algorithms v1.0 Grupo 1");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(100, 100, 100));
        infoPanel.add(lblInfo);

        bottomContainer.add(backPanel, BorderLayout.NORTH);
        bottomContainer.add(infoPanel, BorderLayout.SOUTH);

        add(bottomContainer, BorderLayout.SOUTH);

        // Back button action
        btnBack.addActionListener(e -> {
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

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = backgroundColor;
            Color hoverColor = backgroundColor.darker();

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
                button.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
                button.repaint();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor.darker());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
        });

        return button;
    }

    // Methods to assign external actions to buttons
    public void addSequentialSearchListener(ActionListener listener) {
        btnSequentialSearch.addActionListener(listener);
    }

    public void addBinarySearchListener(ActionListener listener) {
        btnBinarySearch.addActionListener(listener);
    }

    public void addHashSearchListener(ActionListener listener) {
        btnHashSearch.addActionListener(listener);
    }

    public void addTreeSearchListener(ActionListener listener) {
        btnTreeSearch.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    // Method to show the window
    public void showWindow() {
        setVisible(true);
    }
}