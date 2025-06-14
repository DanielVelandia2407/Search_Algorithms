package view.menu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainView extends JFrame {
    private JButton btnInternalSearch;
    private JButton btnExternalSearch;
    private JButton btnIndices;
    private JButton btnExit;

    public MainView() {
        // Window configuration
        setTitle("Search Algorithms");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Background color - Fondo muy claro
        getContentPane().setBackground(new Color(250, 248, 245));

        // Title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        // Exit button action
        btnExit.addActionListener(e -> System.exit(0));
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        // Header con negro elegante
        titlePanel.setBackground(new Color(0, 1, 13));
        titlePanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblTitle = new JLabel("Algoritmos de Búsqueda");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Seleccione una opción para continuar");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        // Dorado claro para el subtítulo
        lblSubtitle.setForeground(new Color(242, 202, 153));
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

        // Color estándar para el menú principal (color del primer botón)
        Color mainMenuColor = new Color(115, 73, 22);

        // Create buttons with the same color for main menu level
        // REMOVIDO: btnDynamicSearch - Ahora será parte de búsqueda externa
        btnInternalSearch = createStyledButton("Búsqueda Interna", mainMenuColor);
        btnExternalSearch = createStyledButton("Búsqueda Externa", mainMenuColor);
        btnIndices = createStyledButton("Índices", mainMenuColor);
        btnExit = createStyledButton("Salir", new Color(0, 1, 13)); // Mantener el color original para el botón salir

        // Main buttons panel - ahora solo 3 botones principales en diseño 2x2
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        gridPanel.setBackground(new Color(250, 248, 245));
        gridPanel.add(btnInternalSearch);
        gridPanel.add(btnExternalSearch);
        gridPanel.add(btnIndices);

        // Panel vacío para balance visual
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(new Color(250, 248, 245));
        gridPanel.add(emptyPanel);

        // Exit button panel (centered)
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        exitPanel.setBackground(new Color(250, 248, 245));
        exitPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Botón de salir con tamaño más pequeño
        btnExit.setPreferredSize(new Dimension(120, 40));
        btnExit.setMinimumSize(new Dimension(120, 40));
        exitPanel.add(btnExit);

        // Combine all panels
        JPanel buttonsContainer = new JPanel(new BorderLayout(0, 0));
        buttonsContainer.setBackground(new Color(250, 248, 245));
        buttonsContainer.add(gridPanel, BorderLayout.CENTER);
        buttonsContainer.add(exitPanel, BorderLayout.SOUTH);

        mainPanel.add(buttonsContainer, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // Footer elegante con tono tierra muy suave
        bottomPanel.setBackground(new Color(245, 240, 235));
        bottomPanel.setBorder(new EmptyBorder(12, 10, 12, 10));

        JLabel lblInfo = new JLabel("© 2025 - Search Algorithms v1.0 Grupo 1");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(new Color(39, 89, 80));

        bottomPanel.add(lblInfo);
        return bottomPanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);

        // Texto blanco para todos los botones del menú principal
        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tamaño optimizado para los botones principales
        button.setPreferredSize(new Dimension(260, 65));
        button.setMinimumSize(new Dimension(260, 65));
        button.setMaximumSize(new Dimension(260, 65));

        // Efecto hover más sutil y profesional
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

    // Métodos auxiliares para manipular colores de forma más sutil
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

    // Action listener methods
    public void addInternalSearchListener(ActionListener listener) {
        btnInternalSearch.addActionListener(listener);
    }

    public void addExternalSearchListener(ActionListener listener) {
        btnExternalSearch.addActionListener(listener);
    }

    // REMOVIDO: addDynamicSearchListener - Ya no es necesario en el menú principal

    public void addIndicesListener(ActionListener listener) {
        btnIndices.addActionListener(listener);
    }

    public void showView() {
        setVisible(true);
    }
}