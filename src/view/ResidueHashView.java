package view;

import model.ResidueHashModel.HashEntry;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedList;

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
    private final JPanel hashVisualizationPanel;

    // Interfaz para el visualizador de hash
    public interface HashVisualizer {
        void paintHashVisualization(Graphics2D g2d, int width, int height);
    }

    // Referencia al visualizador de hash
    private HashVisualizer hashVisualizer;

    public ResidueHashView() {
        // Basic window configuration
        setTitle("Dispersión por Residuos (División)");
        setSize(800, 950);
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

        JLabel lblTitle = new JLabel("Algoritmo de Dispersión por Residuos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Función Hash: h(k) = k mod m");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitle.setForeground(new Color(240, 248, 255));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(lblSubtitle);

        add(titlePanel, BorderLayout.NORTH);

        // Center panel with hash table visualization and operations
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Hash table visualization panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(240, 248, 255));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                "Tabla Hash",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(70, 130, 180)));

        // Create table model with three columns: index, keys, values
        tableModel = new DefaultTableModel(new Object[]{"Índice", "Claves", "Valores", "Hash Original"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        hashTable = new JTable(tableModel);
        hashTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hashTable.setRowHeight(30);
        hashTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        hashTable.getTableHeader().setBackground(new Color(41, 128, 185));
        hashTable.getTableHeader().setForeground(Color.WHITE);

        // Custom renderer for highlighting collisions
        hashTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Verificamos si hay un marcador de colisión en la columna "Claves"
                String keysCell = (String) table.getValueAt(row, 1);
                if (column == 1 || column == 2) {
                    if (keysCell != null && keysCell.contains(",")) {
                        // Si hay colisiones, pintamos la celda de un color amarillo claro
                        c.setBackground(new Color(255, 252, 204));
                    } else if (!isSelected) {
                        c.setBackground(Color.WHITE);
                    }
                } else if (!isSelected) {
                    c.setBackground(Color.WHITE);
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(hashTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 1));

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Hash function visualization panel
        hashVisualizationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // El controlador configurará un método para pintar aquí
                if (hashVisualizer != null) {
                    hashVisualizer.paintHashVisualization(g2d, getWidth(), getHeight());
                }
            }
        };
        hashVisualizationPanel.setBackground(Color.WHITE);
        hashVisualizationPanel.setPreferredSize(new Dimension(0, 200));
        hashVisualizationPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                "Visualización de la Función Hash",
                0,
                0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(70, 130, 180)));

        // Statistics label
        lblStatistics = new JLabel("Estadísticas: Capacidad: 0 | Elementos: 0 | Factor de carga: 0.0");
        lblStatistics.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatistics.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatistics.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(Color.WHITE);
        statsPanel.add(lblStatistics, BorderLayout.CENTER);

        // Add table and visualization to center panel
        JPanel visualizationPanel = new JPanel(new BorderLayout());
        visualizationPanel.add(hashVisualizationPanel, BorderLayout.CENTER);
        visualizationPanel.add(statsPanel, BorderLayout.SOUTH);

        tablePanel.add(visualizationPanel, BorderLayout.SOUTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        // Control panel (at the bottom)
        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.setBackground(new Color(240, 248, 255));
        controlPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Panel contenedor que organiza verticalmente los subpaneles
        JPanel verticalControlPanel = new JPanel();
        verticalControlPanel.setLayout(new BoxLayout(verticalControlPanel, BoxLayout.Y_AXIS));
        verticalControlPanel.setBackground(new Color(240, 248, 255));

        // Panel para insertar valores
        JPanel insertPanel = createControlPanel();
        JLabel lblKey = new JLabel("Clave (entero):");
        lblKey.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtKey = new JTextField(6);
        txtKey.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblValue = new JLabel("Valor:");
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtValue = new JTextField(10);
        txtValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnInsert = createStyledButton("Insertar", new Color(46, 204, 113));

        insertPanel.add(lblKey);
        insertPanel.add(txtKey);
        insertPanel.add(Box.createHorizontalStrut(10));
        insertPanel.add(lblValue);
        insertPanel.add(txtValue);
        insertPanel.add(Box.createHorizontalStrut(10));
        insertPanel.add(btnInsert);

        verticalControlPanel.add(insertPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para buscar valor
        JPanel searchPanel = createControlPanel();
        JLabel lblSearch = new JLabel("Buscar clave:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtSearchKey = new JTextField(10);
        txtSearchKey.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnSearch = createStyledButton("Buscar", new Color(41, 128, 185));

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearchKey);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(btnSearch);

        verticalControlPanel.add(searchPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para eliminar valores
        JPanel deletePanel = createControlPanel();
        JLabel lblDelete = new JLabel("Eliminar clave:");
        lblDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtDeleteKey = new JTextField(10);
        txtDeleteKey.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnDelete = createStyledButton("Eliminar", new Color(231, 76, 60));

        deletePanel.add(lblDelete);
        deletePanel.add(txtDeleteKey);
        deletePanel.add(Box.createHorizontalStrut(10));
        deletePanel.add(btnDelete);

        verticalControlPanel.add(deletePanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para botones adicionales
        JPanel additionalButtonsPanel = createControlPanel();
        btnClear = createStyledButton("Limpiar Tabla", new Color(155, 89, 182));
        additionalButtonsPanel.add(btnClear);

        verticalControlPanel.add(additionalButtonsPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Result label
        lblResult = new JLabel("");
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResult.setHorizontalAlignment(SwingConstants.CENTER);
        lblResult.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resultPanel.setBackground(new Color(240, 248, 255));
        resultPanel.add(lblResult);

        verticalControlPanel.add(resultPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Button panel for back button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 248, 255));

        btnBack = createStyledButton("Volver", new Color(231, 76, 60));
        buttonPanel.add(btnBack);

        verticalControlPanel.add(buttonPanel);

        controlPanel.add(verticalControlPanel, BorderLayout.CENTER);
        centerPanel.add(controlPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with information
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(220, 220, 220));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("© 2025 - Algoritmos de Dispersión v1.0");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(100, 100, 100));

        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBackground(new Color(240, 248, 255));
        return panel;
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
        button.setPreferredSize(new Dimension(150, 35));  // Estandarizar tamaño

        return button;
    }

    // Methods to assign external actions to buttons
    public void addInsertListener(ActionListener listener) {
        btnInsert.addActionListener(listener);
    }

    public void addSearchListener(ActionListener listener) {
        btnSearch.addActionListener(listener);
    }

    public void addDeleteListener(ActionListener listener) {
        btnDelete.addActionListener(listener);
    }

    public void addClearListener(ActionListener listener) {
        btnClear.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    // Methods to get user inputs
    public String getKey() {
        return txtKey.getText().trim();
    }

    public String getValue() {
        return txtValue.getText().trim();
    }

    public String getSearchKey() {
        return txtSearchKey.getText().trim();
    }

    public String getDeleteKey() {
        return txtDeleteKey.getText().trim();
    }

    // Method to display operation result
    public void setResultMessage(String message, boolean isSuccess) {
        lblResult.setText(message);
        lblResult.setForeground(isSuccess ? new Color(46, 125, 50) : new Color(198, 40, 40));
    }

    // Method to update statistics
    public void updateStatistics(int capacity, int size, double loadFactor) {
        String stats = String.format("Estadísticas: Capacidad: %d | Elementos: %d | Factor de carga: %.2f",
                capacity, size, loadFactor);
        lblStatistics.setText(stats);
    }

    // Method to populate the hash table
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

    // Method to highlight a specific row in the table
    public void highlightRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < hashTable.getRowCount()) {
            hashTable.setRowSelectionInterval(rowIndex, rowIndex);
            hashTable.scrollRectToVisible(hashTable.getCellRect(rowIndex, 0, true));
        }
    }

    // Method to get row count of the table
    public int getTableRowCount() {
        return hashTable.getRowCount();
    }

    // Method to get value at specific cell in the table
    public Object getTableValueAt(int row, int column) {
        return hashTable.getValueAt(row, column);
    }

    // Method to set hash visualizer
    public void setHashVisualizer(HashVisualizer visualizer) {
        this.hashVisualizer = visualizer;
    }

    // Method to get the hash visualization panel for drawing
    public JPanel getHashVisualizationPanel() {
        return hashVisualizationPanel;
    }

    // Method to clear input fields
    public void clearInputFields() {
        txtKey.setText("");
        txtValue.setText("");
        txtSearchKey.setText("");
        txtDeleteKey.setText("");
        lblResult.setText("");
    }

    // Method to show the window
    public void showWindow() {
        setVisible(true);
    }
}