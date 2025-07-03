package view.menu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class WelcomeView extends JFrame {
    private JButton btnSearchAlgorithms;
    private JButton btnGraphs;
    private JButton btnExit;

    public WelcomeView() {
        // Window configuration
        setTitle("Ciencias de la Computación 2");
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
        titlePanel.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel lblTitle = new JLabel("Ciencias de la Computación 2");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Seleccione el módulo que desea explorar");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        // Dorado claro para el subtítulo
        lblSubtitle.setForeground(new Color(242, 202, 153));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDescription = new JLabel("Sistema Educativo Interactivo");
        lblDescription.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblDescription.setForeground(new Color(200, 200, 200));
        lblDescription.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(lblSubtitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(lblDescription);

        return titlePanel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(250, 248, 245));
        mainPanel.setBorder(new EmptyBorder(30, 50, 20, 50)); // Mismo padding que MainView

        // Colores diferenciados para cada módulo
        Color algorithmsColor = new Color(115, 73, 22);  // Color original para algoritmos
        Color graphsColor = new Color(34, 120, 89);      // Verde esmeralda para grafos

        // Create buttons
        btnSearchAlgorithms = createStyledButton("Algoritmos de Búsqueda", algorithmsColor);
        btnGraphs = createStyledButton("Grafos", algorithmsColor);
        btnExit = createStyledButton("Salir", new Color(0, 1, 13));

        // Main buttons panel - EXACTAMENTE como MainView pero con 2 botones
        JPanel gridPanel = new JPanel(new GridLayout(1, 2, 20, 20)); // 1 fila, 2 columnas, espaciado de 20
        gridPanel.setBackground(new Color(250, 248, 245));
        gridPanel.add(btnSearchAlgorithms);
        gridPanel.add(btnGraphs);

        // Exit button panel (centered) - IGUAL que MainView
        JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        exitPanel.setBackground(new Color(250, 248, 245));
        exitPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Botón de salir con tamaño más pequeño - IGUAL que MainView
        btnExit.setPreferredSize(new Dimension(120, 40));
        btnExit.setMinimumSize(new Dimension(120, 40));
        exitPanel.add(btnExit);

        // Combine all panels - IGUAL que MainView
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

        JLabel lblInfo = new JLabel("© 2025 - Ciencias de la Computación 2 - Grupo 1");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(new Color(39, 89, 80));

        bottomPanel.add(lblInfo);
        return bottomPanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(backgroundColor);

        // Texto blanco para todos los botones
        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tamaño optimizado para los botones principales (más grandes para bienvenida)
        if (!text.equals("Salir")) {
            button.setPreferredSize(new Dimension(320, 80));
            button.setMinimumSize(new Dimension(320, 80));
            button.setMaximumSize(new Dimension(320, 80));
        }

        // Efecto hover más sutil y profesional
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = backgroundColor;
            Color hoverColor = brightenColor(backgroundColor, 25);
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

    // Action listener methods
    public void addSearchAlgorithmsListener(ActionListener listener) {
        btnSearchAlgorithms.addActionListener(listener);
    }

    public void addGraphsListener(ActionListener listener) {
        btnGraphs.addActionListener(listener);
    }

    public void showView() {
        setVisible(true);
    }
}