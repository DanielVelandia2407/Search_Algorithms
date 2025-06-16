package view.menu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class TreeView extends JFrame {

    private JButton btnDigitalTree;
    private JButton btnWastedTree;
    private JButton btnMultipleWastedTree;
    private JButton btnHuffmanTree;
    private JButton btnBack;

    // Paleta de colores personalizada
    private static final Color DARK_NAVY = new Color(0, 1, 13);      // #0001DD
    private static final Color WARM_BROWN = new Color(115, 73, 22);   // #734916
    private static final Color LIGHT_BROWN = new Color(166, 133, 93); // #A6855D
    private static final Color CREAM = new Color(242, 202, 153);      // #F2CA99
    private static final Color VERY_DARK = new Color(13, 13, 13);     // #0D0D0D
    private static final Color SOFT_WHITE = new Color(248, 248, 248); // Blanco suave para contraste
    private static final Color FOOTER_COLOR = new Color(245, 240, 235); // Color footer consistente

    public TreeView() {
        // Basic window configuration
        setTitle("Arboles de Búsqueda");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Background color consistente
        getContentPane().setBackground(new Color(250, 248, 245));

        // Top panel with title and subtitle
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(DARK_NAVY);
        titlePanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblTitle = new JLabel("Arboles de Búsqueda");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(SOFT_WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Seleccione una opción para continuar");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(CREAM);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        titlePanel.add(lblSubtitle);

        return titlePanel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(250, 248, 245));
        mainPanel.setBorder(new EmptyBorder(30, 50, 20, 50));

        // Create buttons with level 2 color (LIGHT_BROWN)
        btnDigitalTree = createStyledButton("A. Digital", LIGHT_BROWN);
        btnWastedTree = createStyledButton("A. por Residuos", LIGHT_BROWN);
        btnMultipleWastedTree = createStyledButton("A. por Residuos Multiples", LIGHT_BROWN);
        btnHuffmanTree = createStyledButton("A. Huffman", LIGHT_BROWN);
        btnBack = createStyledButton("Volver", DARK_NAVY);

        // Panel para los 4 botones principales en grid 2x2
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setBackground(new Color(250, 248, 245));
        gridPanel.add(btnDigitalTree);
        gridPanel.add(btnWastedTree);
        gridPanel.add(btnMultipleWastedTree);
        gridPanel.add(btnHuffmanTree);

        // Panel contenedor para centrar los botones principales
        JPanel buttonsMainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsMainPanel.setBackground(new Color(250, 248, 245));
        buttonsMainPanel.add(gridPanel);

        // Back button panel (centered)
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        backPanel.setBackground(new Color(250, 248, 245));
        backPanel.setBorder(new EmptyBorder(40, 0, 20, 0));

        // Botón de volver con tamaño más pequeño
        btnBack.setPreferredSize(new Dimension(120, 40));
        btnBack.setMinimumSize(new Dimension(120, 40));
        backPanel.add(btnBack);

        // Combine all panels
        JPanel buttonsContainer = new JPanel(new BorderLayout(0, 0));
        buttonsContainer.setBackground(new Color(250, 248, 245));
        buttonsContainer.add(buttonsMainPanel, BorderLayout.CENTER);
        buttonsContainer.add(backPanel, BorderLayout.SOUTH);

        mainPanel.add(buttonsContainer, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(FOOTER_COLOR);
        bottomPanel.setBorder(new EmptyBorder(12, 10, 12, 10));

        JLabel lblInfo = new JLabel("© 2025 - Search Algorithms v1.0 Daniel Velandia 20191020140");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(new Color(39, 89, 80));

        bottomPanel.add(lblInfo);
        return bottomPanel;
    }

    // Method to create a styled button
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(SOFT_WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tamaño optimizado para los botones principales
        button.setPreferredSize(new Dimension(260, 65));
        button.setMinimumSize(new Dimension(260, 65));
        button.setMaximumSize(new Dimension(260, 65));

        // Efecto hover consistente
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = backgroundColor;
            Color hoverColor = brightenColor(backgroundColor, 20);
            Color pressedColor = darkenColor(backgroundColor, 15);

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
                button.setBackground(pressedColor);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
        });

        return button;
    }

    // Métodos auxiliares para manipular colores
    private Color brightenColor(Color color, int amount) {
        int r = Math.min(255, color.getRed() + amount);
        int g = Math.min(255, color.getGreen() + amount);
        int b = Math.min(255, color.getBlue() + amount);
        return new Color(r, g, b);
    }

    private Color darkenColor(Color color, int amount) {
        int r = Math.max(0, color.getRed() - amount);
        int g = Math.max(0, color.getGreen() - amount);
        int b = Math.max(0, color.getBlue() - amount);
        return new Color(r, g, b);
    }

    // Methods to assign external actions to buttons
    public void addDigitalTreeListener(ActionListener listener) {
        btnDigitalTree.addActionListener(listener);
    }

    public void addWastedTreeListener(ActionListener listener) {
        btnWastedTree.addActionListener(listener);
    }

    public void addMultipleWastedTreeListener(ActionListener listener) {
        btnMultipleWastedTree.addActionListener(listener);
    }

    public void addHuffmanTreeListener(ActionListener listener) {
        btnHuffmanTree.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    // Method to show the window
    public void showWindow() {
        setVisible(true);
    }
}