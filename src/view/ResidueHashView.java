package view;

import model.ResidueHashModel.HashEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedList;

/**
 * Vista mejorada para el árbol de residuo simple.
 * Incluye una tabla para mostrar el hash y un panel para visualizar el árbol.
 */
public class ResidueHashView extends JFrame {

    private final JButton btnInsert;
    private final JButton btnSearch;
    private final JButton btnDelete;
    private final JButton btnClear;
    private final JButton btnBack;
    private final JTable hashTable;
    private final DefaultTableModel tableModel;
    private final JTextField txtKey;
    private final JTextField txtValue;
    private final JTextField txtSearchKey;
    private final JTextField txtDeleteKey;
    private final JLabel lblResult;
    private final JLabel lblStatistics;
    private final TreePanel treeVisualizationPanel;

    // Interfaz para el visualizador del árbol
    public interface HashVisualizer {
        void paintHashVisualization(Graphics2D g2d, int width, int height);
    }

    // Referencia al visualizador de hash
    private HashVisualizer hashVisualizer;

    // Panel personalizado para dibujar el árbol con tamaño adecuado
    private class TreePanel extends JPanel {
        public TreePanel() {
            setBackground(Color.WHITE);
            // Usamos un tamaño preferido grande para asegurar que quepa el árbol completo
            setPreferredSize(new Dimension(700, 500));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (hashVisualizer != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                hashVisualizer.paintHashVisualization(g2d, getWidth(), getHeight());
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.drawString("No hay visualización disponible", getWidth()/2 - 80, getHeight()/2);
            }
        }
    }

    /**
     * Constructor que inicializa la interfaz gráfica.
     */
    public ResidueHashView() {
        // Configuración básica de la ventana
        setTitle("Dispersión por Residuos (División)");
        setSize(800, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel principal con layout de borde
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBackground(new Color(240, 248, 255));
        setContentPane(mainPanel);

        // Panel superior con título
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(70, 130, 180)); // Azul acero
        titlePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Algoritmo de Dispersión por Residuos");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSubtitle = new JLabel("Función Hash: h(k) = k mod m");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitle.setForeground(Color.WHITE);
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        titlePanel.add(lblTitle, BorderLayout.CENTER);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Panel central que contendrá la tabla y la visualización
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Panel para la tabla hash
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                "Tabla Hash",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12),
                new Color(70, 130, 180)));

        // Modelo de tabla con 4 columnas
        tableModel = new DefaultTableModel(new Object[]{"Índice", "Claves", "Valores", "Hash Original"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable
            }
        };

        hashTable = new JTable(tableModel);
        hashTable.setFont(new Font("Arial", Font.PLAIN, 12));
        hashTable.setRowHeight(25);
        hashTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        hashTable.getTableHeader().setBackground(new Color(41, 128, 185));
        hashTable.getTableHeader().setForeground(Color.WHITE);

        // Renderer personalizado para resaltar colisiones
        hashTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Verificar si hay colisión (clave con coma)
                String keysCell = (String) table.getValueAt(row, 1);
                if (column == 1 || column == 2) {
                    if (keysCell != null && keysCell.contains(",")) {
                        c.setBackground(new Color(255, 252, 204)); // Amarillo claro
                    } else if (!isSelected) {
                        c.setBackground(Color.WHITE);
                    }
                } else if (!isSelected) {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(hashTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        tablePanel.setPreferredSize(new Dimension(0, 200)); // Altura fija para la tabla

        // Añadir panel de tabla al panel central (ocupará la parte superior)
        centerPanel.add(tablePanel, BorderLayout.NORTH);

        // Panel para la visualización del árbol con scroll
        JPanel visualizationPanel = new JPanel(new BorderLayout());
        visualizationPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                "Visualización de la Función Hash",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12),
                new Color(70, 130, 180)));

        // Crear panel para dibujar el árbol
        treeVisualizationPanel = new TreePanel();

        // Añadir el panel a un JScrollPane para permitir scroll
        JScrollPane scrollPane = new JScrollPane(treeVisualizationPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(null); // Quitar borde del scroll

        visualizationPanel.add(scrollPane, BorderLayout.CENTER);

        // Etiqueta para estadísticas
        lblStatistics = new JLabel("Estadísticas: Capacidad: 0 | Elementos: 0 | Factor de carga: 0.0");
        lblStatistics.setFont(new Font("Arial", Font.BOLD, 12));
        lblStatistics.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatistics.setBorder(new EmptyBorder(5, 0, 5, 0));
        visualizationPanel.add(lblStatistics, BorderLayout.SOUTH);

        centerPanel.add(visualizationPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Panel inferior con controles
        JPanel controlPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        controlPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        controlPanel.setBackground(new Color(240, 248, 255));

        // Fila 1: Inserción
        JPanel insertPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        insertPanel.setBackground(new Color(240, 248, 255));

        JLabel lblKey = new JLabel("Clave (entero):");
        lblKey.setFont(new Font("Arial", Font.PLAIN, 12));

        txtKey = new JTextField(6);
        txtKey.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel lblValue = new JLabel("Valor:");
        lblValue.setFont(new Font("Arial", Font.PLAIN, 12));

        txtValue = new JTextField(10);
        txtValue.setFont(new Font("Arial", Font.PLAIN, 12));

        btnInsert = new JButton("Insertar");
        btnInsert.setBackground(new Color(46, 204, 113));
        btnInsert.setForeground(Color.WHITE);
        btnInsert.setFocusPainted(false);

        insertPanel.add(lblKey);
        insertPanel.add(txtKey);
        insertPanel.add(lblValue);
        insertPanel.add(txtValue);
        insertPanel.add(btnInsert);

        // Fila 2: Búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 248, 255));

        JLabel lblSearch = new JLabel("Buscar clave:");
        lblSearch.setFont(new Font("Arial", Font.PLAIN, 12));

        txtSearchKey = new JTextField(8);
        txtSearchKey.setFont(new Font("Arial", Font.PLAIN, 12));

        btnSearch = new JButton("Buscar");
        btnSearch.setBackground(new Color(41, 128, 185));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearchKey);
        searchPanel.add(btnSearch);

        // Fila 3: Eliminación
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deletePanel.setBackground(new Color(240, 248, 255));

        JLabel lblDelete = new JLabel("Eliminar clave:");
        lblDelete.setFont(new Font("Arial", Font.PLAIN, 12));

        txtDeleteKey = new JTextField(8);
        txtDeleteKey.setFont(new Font("Arial", Font.PLAIN, 12));

        btnDelete = new JButton("Eliminar");
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);

        deletePanel.add(lblDelete);
        deletePanel.add(txtDeleteKey);
        deletePanel.add(btnDelete);

        // Fila 4: Botones adicionales y resultado
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(240, 248, 255));

        btnClear = new JButton("Limpiar Tabla");
        btnClear.setBackground(new Color(155, 89, 182));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);

        btnBack = new JButton("Volver");
        btnBack.setBackground(new Color(231, 76, 60));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);

        lblResult = new JLabel("");
        lblResult.setFont(new Font("Arial", Font.BOLD, 12));

        bottomPanel.add(btnClear);
        bottomPanel.add(Box.createHorizontalStrut(10));
        bottomPanel.add(btnBack);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(lblResult);

        // Añadir las filas al panel de control
        controlPanel.add(insertPanel);
        controlPanel.add(searchPanel);
        controlPanel.add(deletePanel);
        controlPanel.add(bottomPanel);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Panel de pie de página
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(220, 220, 220));
        JLabel lblFooter = new JLabel("© 2025 - Algoritmos de Dispersión v1.0");
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 10));
        lblFooter.setForeground(new Color(100, 100, 100));
        footerPanel.add(lblFooter);

        mainPanel.add(footerPanel, BorderLayout.PAGE_END);
    }

    /**
     * Asigna un listener al botón de insertar.
     *
     * @param listener ActionListener para manejar el evento
     */
    public void addInsertListener(ActionListener listener) {
        btnInsert.addActionListener(listener);
    }

    /**
     * Asigna un listener al botón de buscar.
     *
     * @param listener ActionListener para manejar el evento
     */
    public void addSearchListener(ActionListener listener) {
        btnSearch.addActionListener(listener);
    }

    /**
     * Asigna un listener al botón de eliminar.
     *
     * @param listener ActionListener para manejar el evento
     */
    public void addDeleteListener(ActionListener listener) {
        btnDelete.addActionListener(listener);
    }

    /**
     * Asigna un listener al botón de limpiar.
     *
     * @param listener ActionListener para manejar el evento
     */
    public void addClearListener(ActionListener listener) {
        btnClear.addActionListener(listener);
    }

    /**
     * Asigna un listener al botón de volver.
     *
     * @param listener ActionListener para manejar el evento
     */
    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    /**
     * Obtiene el valor de la clave a insertar.
     *
     * @return Texto ingresado en el campo de clave
     */
    public String getKey() {
        return txtKey.getText().trim();
    }

    /**
     * Obtiene el valor a asociar con la clave.
     *
     * @return Texto ingresado en el campo de valor
     */
    public String getValue() {
        return txtValue.getText().trim();
    }

    /**
     * Obtiene la clave a buscar.
     *
     * @return Texto ingresado en el campo de búsqueda
     */
    public String getSearchKey() {
        return txtSearchKey.getText().trim();
    }

    /**
     * Obtiene la clave a eliminar.
     *
     * @return Texto ingresado en el campo de eliminación
     */
    public String getDeleteKey() {
        return txtDeleteKey.getText().trim();
    }

    /**
     * Muestra un mensaje de resultado de una operación.
     *
     * @param message Mensaje a mostrar
     * @param isSuccess true si la operación fue exitosa, false en caso contrario
     */
    public void setResultMessage(String message, boolean isSuccess) {
        lblResult.setText(message);
        lblResult.setForeground(isSuccess ? new Color(46, 125, 50) : new Color(198, 40, 40));
    }

    /**
     * Actualiza las estadísticas mostradas.
     *
     * @param capacity Capacidad de la tabla
     * @param size Cantidad de elementos
     * @param loadFactor Factor de carga
     */
    public void updateStatistics(int capacity, int size, double loadFactor) {
        String stats = String.format("Estadísticas: Capacidad: %d | Elementos: %d | Factor de carga: %.2f",
                capacity, size, loadFactor);
        lblStatistics.setText(stats);
    }

    /**
     * Actualiza la tabla hash con los datos actuales.
     *
     * @param table Tabla hash con las entradas
     */
    public void updateHashTable(LinkedList<HashEntry>[] table) {
        tableModel.setRowCount(0);

        for (int i = 0; i < table.length; i++) {
            LinkedList<HashEntry> bucket = table[i];

            if (bucket.isEmpty()) {
                // Mostrar índices vacíos también
                tableModel.addRow(new Object[]{i, "", "", ""});
            } else {
                // Concatenar múltiples claves/valores para colisiones
                StringBuilder keys = new StringBuilder();
                StringBuilder values = new StringBuilder();
                StringBuilder hashes = new StringBuilder();

                for (HashEntry entry : bucket) {
                    if (keys.length() > 0) {
                        keys.append(", ");
                        values.append(", ");
                        hashes.append(", ");
                    }
                    keys.append(entry.getKey());
                    values.append(entry.getValue().toString());
                    hashes.append(entry.getOriginalHashCode());
                }

                tableModel.addRow(new Object[]{i, keys.toString(), values.toString(), hashes.toString()});
            }
        }
    }

    /**
     * Resalta una fila en la tabla.
     *
     * @param rowIndex Índice de la fila a resaltar
     */
    public void highlightRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < hashTable.getRowCount()) {
            hashTable.setRowSelectionInterval(rowIndex, rowIndex);
            hashTable.scrollRectToVisible(hashTable.getCellRect(rowIndex, 0, true));
        }
    }

    /**
     * Obtiene la cantidad de filas en la tabla.
     *
     * @return Número de filas
     */
    public int getTableRowCount() {
        return hashTable.getRowCount();
    }

    /**
     * Obtiene el valor en una celda específica de la tabla.
     *
     * @param row Fila
     * @param column Columna
     * @return Valor en la celda
     */
    public Object getTableValueAt(int row, int column) {
        return hashTable.getValueAt(row, column);
    }

    /**
     * Establece el visualizador de hash.
     *
     * @param visualizer Implementación de HashVisualizer
     */
    public void setHashVisualizer(HashVisualizer visualizer) {
        this.hashVisualizer = visualizer;
    }

    /**
     * Obtiene el panel de visualización del hash.
     *
     * @return Panel donde se dibuja la visualización
     */
    public JPanel getHashVisualizationPanel() {
        return treeVisualizationPanel;
    }

    /**
     * Limpia los campos de entrada.
     */
    public void clearInputFields() {
        txtKey.setText("");
        txtValue.setText("");
        txtSearchKey.setText("");
        txtDeleteKey.setText("");
        lblResult.setText("");
    }

    /**
     * Muestra la ventana.
     */
    public void showWindow() {
        setVisible(true);
    }

    /**
     * Repinta el panel de visualización del árbol.
     */
    public void repaintTreePanel() {
        if (treeVisualizationPanel != null) {
            treeVisualizationPanel.repaint();
        }
    }
}