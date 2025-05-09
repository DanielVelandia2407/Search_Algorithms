package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;


public class MainView extends JFrame {
    private JButton btnInternalSearch;
    private JButton btnExternalSearch;
    private JButton btnTreeSearch;
    private JButton btnExit;

    public MainView() {
        // Window configuration
        setTitle("Search Algorithms");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // Background color
        getContentPane().setBackground(new Color(240, 248, 255));

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Algoritmos de Búsqueda");
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

        // Panel de botones con márgenes mejorados
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Styled buttons
        btnInternalSearch = createStyledButton("Búsqueda Interna", new Color(41, 128, 185));
        btnExternalSearch = createStyledButton("Búsqueda Externa", new Color(46, 134, 193));
        btnTreeSearch = createStyledButton("Árboles de Búsqueda", new Color(52, 152, 219));
        btnExit = createStyledButton("Salir", new Color(231, 76, 60));

        // Añadir botones con márgenes
        buttonPanel.add(btnInternalSearch);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Espacio vertical
        buttonPanel.add(btnExternalSearch);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Espacio vertical
        buttonPanel.add(btnTreeSearch);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Espacio vertical
        buttonPanel.add(btnExit);

        centerPanel.add(buttonPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(220, 220, 220));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("© 2025 - Search Algorithms v1.0 Daniel Velandia 20191020140");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(100, 100, 100));

        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);

        // Exit button action
        btnExit.addActionListener(e -> System.exit(0));
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(250, 50)); // Ancho aumentado para mejor apariencia
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Permite que se expanda horizontalmente
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Centra los botones
        return button;
    }

    public void addInternalSearchListener(ActionListener listener) {
        btnInternalSearch.addActionListener(listener);
    }

    public void addExternalSearchListener(ActionListener listener) {
        btnExternalSearch.addActionListener(listener);
    }

    public void addTreeSearchListener(ActionListener listener) {
        btnTreeSearch.addActionListener(listener);
    }

    public void showView() {
        setVisible(true);
    }
}