package view.menu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class IndicesMenuView extends JFrame {

    private JButton btnPrimaryIndex;
    private JButton btnSecondaryIndex;
    private JButton btnMultilevelPrimary;
    private JButton btnMultilevelSecondary;
    private JButton btnBack;

    public IndicesMenuView() {
        // Basic window configuration
        setTitle("Algoritmos de Búsqueda - Índices");
        setSize(500, 400);
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

        JLabel lblTitle = new JLabel("Algoritmos de Búsqueda - Índices");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Seleccione una opción para continuar");
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
        buttonPanel.setLayout(new GridLayout(3, 2, 20, 20)); // 3 filas, 2 columnas para 5 botones
        buttonPanel.setBackground(new Color(240, 248, 255));

        // Custom styled buttons con colores consistentes
        btnPrimaryIndex = createStyledButton("Índice Principal", new Color(41, 128, 185));
        btnSecondaryIndex = createStyledButton("Índice Secundario", new Color(46, 134, 193));
        btnMultilevelPrimary = createStyledButton("Multinivel Principal", new Color(52, 152, 219));
        btnMultilevelSecondary = createStyledButton("Multinivel Dinámico", new Color(155, 89, 182));
        btnBack = createStyledButton("Volver", new Color(231, 76, 60));

        buttonPanel.add(btnPrimaryIndex);
        buttonPanel.add(btnSecondaryIndex);
        buttonPanel.add(btnMultilevelPrimary);
        buttonPanel.add(btnMultilevelSecondary);
        buttonPanel.add(btnBack);
        buttonPanel.add(new JLabel());

        centerPanel.add(buttonPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with information
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(220, 220, 220));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("© 2025 - Sistema de Índices v2.0 Grupo 1");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(100, 100, 100));

        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);

        // Back button action placeholder
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

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = backgroundColor;
            Color hoverColor = backgroundColor.brighter();

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    // Methods to assign external actions to buttons
    public void addPrimaryIndexListener(ActionListener listener) {
        btnPrimaryIndex.addActionListener(listener);
    }

    public void addSecondaryIndexListener(ActionListener listener) {
        btnSecondaryIndex.addActionListener(listener);
    }

    public void addMultilevelPrimaryListener(ActionListener listener) {
        btnMultilevelPrimary.addActionListener(listener);
    }

    public void addMultilevelSecondaryListener(ActionListener listener) {
        btnMultilevelSecondary.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        for (ActionListener al : btnBack.getActionListeners()) {
            btnBack.removeActionListener(al);
        }
        btnBack.addActionListener(listener);
    }

    // Method to show the window
    public void showWindow() {
        setVisible(true);
    }
}