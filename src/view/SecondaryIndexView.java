package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vista para el simulador de índice secundario
 * Interfaz gráfica que permite configurar parámetros y visualizar resultados
 */
public class SecondaryIndexView extends JFrame {

    // Componentes de entrada
    private JTextField txtRegistros;
    private JTextField txtBloque;
    private JTextField txtDato;
    private JTextField txtIndice;
    private JButton btnCalcular;
    private JButton btnLimpiar;
    private JButton btnVolver;

    // Componentes de salida
    private JTextArea areaResultados;
    private JPanel panelEstructura;

    public SecondaryIndexView() {
        initializeComponents();
        setupLayout();
        setupStyles();
    }

    /**
     * Inicializa todos los componentes de la vista
     */
    private void initializeComponents() {
        setTitle("Simulador de Índice Secundario");
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Campos de entrada con valores por defecto
        txtRegistros = new JTextField("1000", 10);
        txtBloque = new JTextField("4096", 10);
        txtDato = new JTextField("100", 10);
        txtIndice = new JTextField("12", 10);

        // Botones
        btnCalcular = new JButton("Calcular Índice");
        btnLimpiar = new JButton("Limpiar");
        btnVolver = new JButton("Volver al Menú");

        // Área de resultados
        areaResultados = new JTextArea(10, 50);
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Consolas", Font.PLAIN, 12));

        // Panel para estructura visual
        panelEstructura = new JPanel();
        panelEstructura.setLayout(new GridLayout(2, 1, 10, 10));
    }

    /**
     * Configura el layout de la ventana
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Panel principal con padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel superior: título
        JPanel titlePanel = createTitlePanel();

        // Panel de entrada
        JPanel inputPanel = createInputPanel();

        // Panel de resultados
        JPanel resultsPanel = createResultsPanel();

        // Panel de estructura visual
        JPanel structurePanel = createStructurePanel();

        // Panel inferior: botones
        JPanel buttonPanel = createButtonPanel();

        // Ensamblar layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(resultsPanel, BorderLayout.CENTER);
        centerPanel.add(structurePanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Crea el panel del título
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(70, 130, 180));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Simulador de Índice Secundario");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Configure los parámetros y calcule la estructura del índice");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitleLabel.setForeground(new Color(240, 248, 255));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.setLayout(new BorderLayout());
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea el panel de entrada de parámetros
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "Parámetros de Configuración",
                0, 0, new Font("Segoe UI", Font.BOLD, 14)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 1: Número de registros
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Número de registros:"), gbc);
        gbc.gridx = 1;
        panel.add(txtRegistros, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(registros totales en el archivo)"), gbc);

        // Fila 2: Tamaño del bloque
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tamaño del bloque:"), gbc);
        gbc.gridx = 1;
        panel.add(txtBloque, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(bytes por bloque)"), gbc);

        // Fila 3: Longitud del dato
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Longitud del registro de datos:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDato, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(bytes por registro de datos)"), gbc);

        // Fila 4: Longitud del índice
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Longitud del registro de índice:"), gbc);
        gbc.gridx = 1;
        panel.add(txtIndice, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(bytes por entrada de índice)"), gbc);

        return panel;
    }

    /**
     * Crea el panel de resultados
     */
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(46, 134, 193), 2),
                "Resultados del Cálculo",
                0, 0, new Font("Segoe UI", Font.BOLD, 14)));

        areaResultados.setBackground(new Color(245, 245, 245));
        areaResultados.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(areaResultados);
        scrollPane.setPreferredSize(new Dimension(0, 200));

        panel.add(scrollPane);
        return panel;
    }

    /**
     * Crea el panel de estructura visual
     */
    private JPanel createStructurePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                "Visualización de la Estructura",
                0, 0, new Font("Segoe UI", Font.BOLD, 14)));

        panelEstructura.setBackground(Color.WHITE);
        panelEstructura.setPreferredSize(new Dimension(0, 200));

        panel.add(panelEstructura);
        return panel;
    }

    /**
     * Crea el panel de botones
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(240, 248, 255));

        panel.add(btnCalcular);
        panel.add(btnLimpiar);
        panel.add(btnVolver);

        return panel;
    }

    /**
     * Configura los estilos de los componentes
     */
    private void setupStyles() {
        // Estilo de la ventana
        getContentPane().setBackground(new Color(240, 248, 255));

        // Estilo de los botones
        styleButton(btnCalcular, new Color(41, 128, 185));
        styleButton(btnLimpiar, new Color(231, 76, 60));
        styleButton(btnVolver, new Color(155, 89, 182));

        // Estilo de los campos de texto
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 12);
        txtRegistros.setFont(fieldFont);
        txtBloque.setFont(fieldFont);
        txtDato.setFont(fieldFont);
        txtIndice.setFont(fieldFont);
    }

    /**
     * Aplica estilo a un botón
     */
    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 35));

        // Efecto hover
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
    }

    /**
     * Muestra los resultados del cálculo en el área de texto
     */
    public void mostrarResultados(String resultados) {
        areaResultados.setText(resultados);
    }

    /**
     * Visualiza la estructura del índice y archivo
     */
    public void mostrarEstructura(int bloques, int bfri) {
        panelEstructura.removeAll();

        // Panel superior: Bloques de Índice
        JPanel panelIndice = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelIndice.setBackground(new Color(255, 250, 240));
        panelIndice.setBorder(BorderFactory.createTitledBorder("Bloques de Índice"));

        int bloquesAMostrar = Math.min(bloques, 8); // Máximo 8 bloques para visualización
        for (int i = 1; i <= bloquesAMostrar; i++) {
            JPanel bloque = new JPanel(new GridLayout(Math.min(bfri, 4), 1, 1, 1));
            bloque.setPreferredSize(new Dimension(70, 80));
            bloque.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

            int registrosAMostrar = Math.min(bfri, 4);
            for (int j = 1; j <= registrosAMostrar; j++) {
                JLabel celda = new JLabel("I" + j, SwingConstants.CENTER);
                celda.setOpaque(true);
                celda.setBackground(new Color(135, 206, 250));
                celda.setForeground(Color.BLACK);
                celda.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                celda.setFont(new Font("Arial", Font.BOLD, 9));
                bloque.add(celda);
            }
            panelIndice.add(bloque);
        }

        if (bloques > bloquesAMostrar) {
            JLabel masLabel = new JLabel("... +" + (bloques - bloquesAMostrar) + " más");
            masLabel.setFont(new Font("Arial", Font.ITALIC, 10));
            panelIndice.add(masLabel);
        }

        // Panel inferior: Bloques de Datos (representativo)
        JPanel panelDatos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelDatos.setBackground(new Color(240, 255, 240));
        panelDatos.setBorder(BorderFactory.createTitledBorder("Bloques de Datos (muestra)"));

        for (int i = 1; i <= Math.min(6, bloquesAMostrar); i++) {
            JPanel bloque = new JPanel(new GridLayout(Math.min(4, bfri), 1, 1, 1));
            bloque.setPreferredSize(new Dimension(70, 80));
            bloque.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

            for (int j = 1; j <= Math.min(4, bfri); j++) {
                JLabel celda = new JLabel("D" + j, SwingConstants.CENTER);
                celda.setOpaque(true);
                celda.setBackground(new Color(152, 251, 152));
                celda.setForeground(Color.BLACK);
                celda.setBorder(BorderFactory.createLineBorder(Color.WHITE));
                celda.setFont(new Font("Arial", Font.BOLD, 9));
                bloque.add(celda);
            }
            panelDatos.add(bloque);
        }

        panelEstructura.add(panelIndice);
        panelEstructura.add(panelDatos);
        panelEstructura.revalidate();
        panelEstructura.repaint();
    }

    /**
     * Limpia todos los campos y resultados
     */
    public void limpiarCampos() {
        txtRegistros.setText("");
        txtBloque.setText("");
        txtDato.setText("");
        txtIndice.setText("");
        areaResultados.setText("");
        panelEstructura.removeAll();
        panelEstructura.revalidate();
        panelEstructura.repaint();
    }

    // Getters para los campos de entrada
    public String getRegistros() { return txtRegistros.getText().trim(); }
    public String getBloque() { return txtBloque.getText().trim(); }
    public String getDato() { return txtDato.getText().trim(); }
    public String getIndice() { return txtIndice.getText().trim(); }

    // Métodos para asignar listeners
    public void addCalcularListener(ActionListener listener) {
        btnCalcular.addActionListener(listener);
    }

    public void addLimpiarListener(ActionListener listener) {
        btnLimpiar.addActionListener(listener);
    }

    public void addVolverListener(ActionListener listener) {
        btnVolver.addActionListener(listener);
    }

    /**
     * Muestra la ventana
     */
    public void showWindow() {
        setVisible(true);
    }
}
