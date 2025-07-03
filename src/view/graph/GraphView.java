package view.graph;

import model.graph.GraphModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class GraphView extends JFrame {

    private JButton btnCreateGraph;
    private JButton btnAnalyzeGraph;
    private JButton btnShowMatrices;
    private JButton btnClearGraph;
    private JButton btnBack;
    private JTable adjacencyTable;
    private JTable incidenceTable;
    private DefaultTableModel adjacencyModel;
    private DefaultTableModel incidenceModel;
    private JTextArea txtVertices;
    private JTextArea txtEdges;
    private JTextArea txtAnalysisResult;
    private GraphVisualPanel visualPanel;
    private JScrollPane analysisScroll;

    // Paleta de colores personalizada
    private static final Color DARK_NAVY = new Color(0, 1, 13);
    private static final Color WARM_BROWN = new Color(115, 73, 22);
    private static final Color LIGHT_BROWN = new Color(166, 133, 93);
    private static final Color CREAM = new Color(242, 202, 153);
    private static final Color VERY_DARK = new Color(13, 13, 13);
    private static final Color SOFT_WHITE = new Color(248, 248, 248);

    public GraphView() {
        setTitle("Análisis de Grafos - Visualizador");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        getContentPane().setBackground(CREAM);

        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        // Panel de título
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(DARK_NAVY);
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Sistema de Análisis y Visualización de Grafos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(SOFT_WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Ingrese vértices y aristas en formato texto");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitle.setForeground(CREAM);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(lblSubtitle);

        add(titlePanel, BorderLayout.NORTH);

        // Inicializar botones
        btnCreateGraph = createStyledButton("Crear Grafo", WARM_BROWN);
        btnAnalyzeGraph = createStyledButton("Analizar Grafo", DARK_NAVY);
        btnShowMatrices = createStyledButton("Mostrar Matrices", WARM_BROWN);
        btnClearGraph = createStyledButton("Limpiar", new Color(180, 67, 67));
        btnBack = createStyledButton("Salir", VERY_DARK);

        // Inicializar áreas de texto
        txtVertices = createStyledTextArea(3, 30);
        txtEdges = createStyledTextArea(3, 30);
        txtAnalysisResult = createStyledTextArea(8, 20);

        analysisScroll = new JScrollPane(txtAnalysisResult);
        styleScrollPane(analysisScroll);

        // Inicializar panel de visualización
        visualPanel = new GraphVisualPanel();

        // Inicializar tablas
        setupTables();
    }

    private void setupTables() {
        // Tabla de matriz de adyacencia
        adjacencyModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        adjacencyTable = new JTable(adjacencyModel);
        styleTable(adjacencyTable);

        // Tabla de matriz de incidencia
        incidenceModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        incidenceTable = new JTable(incidenceModel);
        styleTable(incidenceTable);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(WARM_BROWN);
        table.getTableHeader().setForeground(SOFT_WHITE);
        table.setBackground(SOFT_WHITE);
        table.setForeground(VERY_DARK);
        table.setGridColor(LIGHT_BROWN);
    }

    private void setupLayout() {
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(CREAM);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel izquierdo - Controles de entrada
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(CREAM);
        leftPanel.setPreferredSize(new Dimension(350, 0));

        // Panel de entrada de vértices
        JPanel verticesPanel = createSectionPanel("Vértices");
        JLabel lblVerticesHelp = new JLabel("<html><i>Ejemplo: A,B,C,D o 1,2,3,4</i></html>");
        lblVerticesHelp.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblVerticesHelp.setForeground(VERY_DARK);

        JScrollPane verticesScroll = new JScrollPane(txtVertices);
        styleScrollPane(verticesScroll);
        verticesScroll.setPreferredSize(new Dimension(320, 80));

        verticesPanel.add(lblVerticesHelp);
        verticesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        verticesPanel.add(verticesScroll);

        leftPanel.add(verticesPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel de entrada de aristas
        JPanel edgesPanel = createSectionPanel("Aristas");
        JLabel lblEdgesHelp = new JLabel("<html><i>Ejemplo: A-B,A-C,B-D (no dirigido)<br>o A->B,A->C,B->D (dirigido)</i></html>");
        lblEdgesHelp.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblEdgesHelp.setForeground(VERY_DARK);

        JScrollPane edgesScroll = new JScrollPane(txtEdges);
        styleScrollPane(edgesScroll);
        edgesScroll.setPreferredSize(new Dimension(320, 80));

        edgesPanel.add(lblEdgesHelp);
        edgesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        edgesPanel.add(edgesScroll);

        leftPanel.add(edgesPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel de botones
        JPanel buttonsPanel = createSectionPanel("Acciones");
        JPanel buttonRow1 = createControlPanel();
        buttonRow1.add(btnCreateGraph);
        buttonRow1.add(Box.createHorizontalStrut(10));
        buttonRow1.add(btnAnalyzeGraph);

        JPanel buttonRow2 = createControlPanel();
        buttonRow2.add(btnShowMatrices);
        buttonRow2.add(Box.createHorizontalStrut(10));
        buttonRow2.add(btnClearGraph);

        buttonsPanel.add(buttonRow1);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonsPanel.add(buttonRow2);

        leftPanel.add(buttonsPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel de resultados del análisis
        JPanel resultPanel = createSectionPanel("Resultado del Análisis");
        analysisScroll.setPreferredSize(new Dimension(320, 150));
        resultPanel.add(analysisScroll);
        leftPanel.add(resultPanel);

        // Botón de salir
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        JPanel backPanel = createControlPanel();
        backPanel.add(btnBack);
        leftPanel.add(backPanel);

        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Panel derecho - Visualización y matrices
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(CREAM);

        // Panel con pestañas para diferentes vistas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(CREAM);
        tabbedPane.setForeground(VERY_DARK);

        // Pestaña de visualización gráfica
        JPanel visualTab = new JPanel(new BorderLayout());
        visualTab.setBackground(CREAM);

        JPanel visualContainer = new JPanel(new BorderLayout());
        visualContainer.setBackground(SOFT_WHITE);
        visualContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(WARM_BROWN, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        visualContainer.add(visualPanel, BorderLayout.CENTER);

        visualTab.add(visualContainer, BorderLayout.CENTER);
        tabbedPane.addTab("Visualización Gráfica", visualTab);

        // Pestaña de matriz de adyacencia
        JPanel adjTab = new JPanel(new BorderLayout());
        adjTab.setBackground(CREAM);
        JScrollPane adjScroll = new JScrollPane(adjacencyTable);
        styleScrollPane(adjScroll);
        adjTab.add(adjScroll, BorderLayout.CENTER);
        tabbedPane.addTab("Matriz de Adyacencia", adjTab);

        // Pestaña de matriz de incidencia
        JPanel incTab = new JPanel(new BorderLayout());
        incTab.setBackground(CREAM);
        JScrollPane incScroll = new JScrollPane(incidenceTable);
        styleScrollPane(incScroll);
        incTab.add(incScroll, BorderLayout.CENTER);
        tabbedPane.addTab("Matriz de Incidencia", incTab);

        rightPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Panel inferior con información
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(LIGHT_BROWN);
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("© 2025 - Graph Analysis & Visualization System v2.0");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(SOFT_WHITE);

        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CREAM);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(WARM_BROWN, 2),
                title,
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                VERY_DARK
        ));
        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBackground(CREAM);
        return panel;
    }

    private JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea textArea = new JTextArea(rows, cols);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textArea.setBackground(SOFT_WHITE);
        textArea.setForeground(VERY_DARK);
        textArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(WARM_BROWN, 2));
        scrollPane.getViewport().setBackground(SOFT_WHITE);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(SOFT_WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 35));

        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = backgroundColor;

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    // Listeners para los botones
    public void addCreateGraphListener(ActionListener listener) {
        btnCreateGraph.addActionListener(listener);
    }

    public void addAnalyzeGraphListener(ActionListener listener) {
        btnAnalyzeGraph.addActionListener(listener);
    }

    public void addShowMatricesListener(ActionListener listener) {
        btnShowMatrices.addActionListener(listener);
    }

    public void addClearGraphListener(ActionListener listener) {
        btnClearGraph.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    // Getters para los valores de entrada
    public String getVerticesInput() {
        return txtVertices.getText().trim();
    }

    public String getEdgesInput() {
        return txtEdges.getText().trim();
    }

    // Métodos de visualización
    public void showGraphType(String type) {
        txtAnalysisResult.setText("=== ANÁLISIS DEL GRAFO ===\n");
        txtAnalysisResult.append("Tipo de grafo: " + type + "\n");
    }

    public void showAdjacencyMatrix(int[][] matrix, GraphModel graph) {
        // Limpiar datos anteriores
        adjacencyModel.setRowCount(0);
        adjacencyModel.setColumnCount(0);

        // Configurar encabezados de columnas
        adjacencyModel.addColumn("");
        for (int i = 0; i < graph.getNumVertices(); i++) {
            String vertexName = graph.getVertexName(i);
            adjacencyModel.addColumn(vertexName != null ? vertexName : "v" + i);
        }

        // Agregar filas
        for (int i = 0; i < graph.getNumVertices(); i++) {
            Object[] row = new Object[graph.getNumVertices() + 1];
            String vertexName = graph.getVertexName(i);
            row[0] = vertexName != null ? vertexName : "v" + i;
            for (int j = 0; j < graph.getNumVertices(); j++) {
                row[j + 1] = matrix[i][j];
            }
            adjacencyModel.addRow(row);
        }
    }

    public void showIncidenceMatrix(int[][] matrix, GraphModel graph) {
        // Limpiar datos anteriores
        incidenceModel.setRowCount(0);
        incidenceModel.setColumnCount(0);

        int numEdges = graph.getEdges().size();

        // Configurar encabezados de columnas
        incidenceModel.addColumn("");
        for (int i = 0; i < numEdges; i++) {
            incidenceModel.addColumn("e" + i);
        }

        // Agregar filas
        for (int i = 0; i < graph.getNumVertices(); i++) {
            Object[] row = new Object[numEdges + 1];
            String vertexName = graph.getVertexName(i);
            row[0] = vertexName != null ? vertexName : "v" + i;
            for (int j = 0; j < numEdges; j++) {
                row[j + 1] = matrix[i][j];
            }
            incidenceModel.addRow(row);
        }
    }

    public void showVisualRepresentation(GraphModel graph) {
        // Actualizar el panel de visualización gráfica
        visualPanel.setGraph(graph);

        // También mostrar información textual
        List<GraphModel.Edge> edges = graph.getEdges();

        StringBuilder sb = new StringBuilder();
        sb.append("=== INFORMACIÓN DEL GRAFO ===\n\n");
        sb.append("Vértices: ").append(graph.getNumVertices()).append("\n");
        sb.append("Aristas: ").append(edges.size()).append("\n\n");

        sb.append("Lista de vértices:\n");
        for (int i = 0; i < graph.getNumVertices(); i++) {
            String vertexName = graph.getVertexName(i);
            sb.append("- ").append(vertexName != null ? vertexName : "v" + i).append("\n");
        }

        sb.append("\nConexiones por vértice:\n");
        for (int i = 0; i < graph.getNumVertices(); i++) {
            List<Integer> adjacent = graph.getAdjacencyList().get(i);
            String vertexName = graph.getVertexName(i);
            String currentVertex = vertexName != null ? vertexName : "v" + i;

            if (!adjacent.isEmpty()) {
                sb.append(currentVertex).append(" conecta con: ");
                for (int j = 0; j < adjacent.size(); j++) {
                    String adjVertexName = graph.getVertexName(adjacent.get(j));
                    sb.append(adjVertexName != null ? adjVertexName : "v" + adjacent.get(j));
                    if (j < adjacent.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("\n");
            } else {
                sb.append(currentVertex).append(" no tiene conexiones salientes\n");
            }
        }

        sb.append("\nLista de aristas:\n");
        for (int i = 0; i < edges.size(); i++) {
            sb.append("e").append(i).append(": ").append(edges.get(i)).append("\n");
        }

        // No necesitamos mostrar esto en un área de texto adicional ya que está en la visualización
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String error) {
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String success) {
        JOptionPane.showMessageDialog(this, success, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearAll() {
        txtVertices.setText("");
        txtEdges.setText("");
        txtAnalysisResult.setText("");
        adjacencyModel.setRowCount(0);
        adjacencyModel.setColumnCount(0);
        incidenceModel.setRowCount(0);
        incidenceModel.setColumnCount(0);
        visualPanel.setGraph(null);
    }

    public void showWindow() {
        setVisible(true);
    }
}