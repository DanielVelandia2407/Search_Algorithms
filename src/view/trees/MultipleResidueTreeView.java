package view.trees;

import model.trees.MultipleResidueTreeModel.TreeNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class MultipleResidueTreeView extends JFrame {
    // Paneles principales
    private JPanel mainPanel;
    private JPanel operationsPanel;
    private JPanel treeVisualizationPanel;
    private JPanel tablePanel;
    private JPanel statusPanel;

    // Componentes para inserción
    private JTextField keyField;
    private JTextField valueField;
    private JButton insertButton;

    // Componentes para búsqueda
    private JTextField searchKeyField;
    private JButton searchButton;

    // Componentes para eliminación
    private JTextField deleteKeyField;
    private JButton deleteButton;

    // Otros botones
    private JButton clearButton;
    private JButton backButton;

    // Tabla para mostrar los nodos
    private JTable nodeTable;
    private DefaultTableModel tableModel;

    // Etiqueta para mostrar mensajes
    private JLabel messageLabel;

    // Interfaz para la visualización del árbol
    public interface TreeVisualizer {
        void paintTreeVisualization(Graphics2D g2d, int width, int height);
    }

    private TreeVisualizer treeVisualizer;

    public MultipleResidueTreeView() {
        setTitle("Árbol por Residuos Múltiples");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        operationsPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        operationsPanel.setBorder(new TitledBorder("Operaciones"));

        treeVisualizationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (treeVisualizer != null) {
                    treeVisualizer.paintTreeVisualization((Graphics2D) g, getWidth(), getHeight());
                }
            }
        };
        treeVisualizationPanel.setBorder(new TitledBorder("Visualización del Árbol"));
        treeVisualizationPanel.setPreferredSize(new Dimension(500, 400));

        tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new TitledBorder("Nodos del Árbol"));

        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Componentes para inserción
        JPanel insertPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        insertPanel.setBorder(new TitledBorder("Insertar"));
        keyField = new JTextField(5);
        valueField = new JTextField(15);
        insertButton = new JButton("Insertar");
        insertPanel.add(new JLabel("Clave:"));
        insertPanel.add(keyField);
        insertPanel.add(new JLabel("Valor:"));
        insertPanel.add(valueField);
        insertPanel.add(insertButton);

        // Componentes para búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(new TitledBorder("Buscar"));
        searchKeyField = new JTextField(5);
        searchButton = new JButton("Buscar");
        searchPanel.add(new JLabel("Clave:"));
        searchPanel.add(searchKeyField);
        searchPanel.add(searchButton);

        // Componentes para eliminación
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deletePanel.setBorder(new TitledBorder("Eliminar"));
        deleteKeyField = new JTextField(5);
        deleteButton = new JButton("Eliminar");
        deletePanel.add(new JLabel("Clave:"));
        deletePanel.add(deleteKeyField);
        deletePanel.add(deleteButton);

        // Panel para otros botones
        JPanel otherButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clearButton = new JButton("Limpiar Árbol");
        backButton = new JButton("Volver");
        otherButtonsPanel.add(clearButton);
        otherButtonsPanel.add(backButton);

        // Configurar tabla
        String[] columnNames = {"Clave", "Valor", "Nivel", "Residuo1", "Residuo2", "..."};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        nodeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(nodeTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Configurar etiqueta de mensajes
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusPanel.add(messageLabel, BorderLayout.WEST);

        // Añadir componentes a los paneles
        operationsPanel.add(insertPanel);
        operationsPanel.add(searchPanel);
        operationsPanel.add(deletePanel);
        operationsPanel.add(otherButtonsPanel);
    }

    private void layoutComponents() {
        // Panel izquierdo para operaciones y tabla
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.add(operationsPanel, BorderLayout.NORTH);
        leftPanel.add(tablePanel, BorderLayout.CENTER);

        // Añadir todos los componentes al panel principal
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(treeVisualizationPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        // Añadir el panel principal al frame
        setContentPane(mainPanel);
    }

    // Métodos para añadir listeners a los botones
    public void addInsertListener(ActionListener listener) {
        insertButton.addActionListener(listener);
    }

    public void addSearchListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }

    public void addDeleteListener(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }

    public void addClearListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    // Métodos para obtener valores de los campos
    public String getKey() {
        return keyField.getText().trim();
    }

    public String getValue() {
        return valueField.getText().trim();
    }

    public String getSearchKey() {
        return searchKeyField.getText().trim();
    }

    public String getDeleteKey() {
        return deleteKeyField.getText().trim();
    }

    // Métodos para limpiar campos y mostrar mensajes
    public void clearInputFields() {
        keyField.setText("");
        valueField.setText("");
        searchKeyField.setText("");
        deleteKeyField.setText("");
        keyField.requestFocus();
    }

    public void setResultMessage(String message, boolean success) {
        messageLabel.setText(message);
        messageLabel.setForeground(success ? new Color(0, 128, 0) : Color.RED);
    }

    // Métodos para actualizar la tabla y las estadísticas
    public void updateTreeTable(List<TreeNode> nodes, int[] divisors) {
        // Limpiar tabla
        tableModel.setRowCount(0);

        // Actualizar columnas dinámicamente según la cantidad de divisores
        String[] columnNames = new String[divisors.length + 3];
        columnNames[0] = "Clave";
        columnNames[1] = "Valor";
        columnNames[2] = "Nivel";

        for (int i = 0; i < divisors.length; i++) {
            columnNames[i + 3] = "Res mod " + divisors[i];
        }

        tableModel.setColumnIdentifiers(columnNames);

        // Añadir filas a la tabla
        for (TreeNode node : nodes) {
            Object[] rowData = new Object[divisors.length + 3];
            rowData[0] = node.key;
            rowData[1] = node.value;
            rowData[2] = node.level;

            // Calcular y añadir residuos
            for (int i = 0; i < divisors.length; i++) {
                rowData[i + 3] = node.key % divisors[i];
            }

            tableModel.addRow(rowData);
        }
    }

    public void updateStatistics(int nodeCount, int height) {
        // Aquí se podría añadir más estadísticas en el futuro
        String stats = "Nodos: " + nodeCount + " | Altura: " + height;
        JLabel statsLabel = new JLabel(stats);
        statusPanel.add(statsLabel, BorderLayout.EAST);
    }

    public void highlightRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < nodeTable.getRowCount()) {
            nodeTable.setRowSelectionInterval(rowIndex, rowIndex);
            nodeTable.scrollRectToVisible(nodeTable.getCellRect(rowIndex, 0, true));
        }
    }

    // Métodos para acceder a los componentes de la interfaz
    public JPanel getTreeVisualizationPanel() {
        return treeVisualizationPanel;
    }

    public int getTableRowCount() {
        return tableModel.getRowCount();
    }

    public Object getTableValueAt(int row, int column) {
        return tableModel.getValueAt(row, column);
    }

    public void setTreeVisualizer(TreeVisualizer visualizer) {
        this.treeVisualizer = visualizer;
    }

    public void showWindow() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
        });
    }
}