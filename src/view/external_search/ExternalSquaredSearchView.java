package view.external_search;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;

public class ExternalSquaredSearchView extends JFrame {

    private JButton btnSearch;
    private JButton btnGenerateTable;
    private JButton btnDeleteValue;
    private JButton btnInsertValue;
    private JButton btnBack;
    private JTable hashTable;
    private DefaultTableModel tableModel;
    private JTextField txtValueToSearch;
    private JTextField txtTableSize;
    private JTextField txtBlockSize;
    private JTextField txtInsertValue;
    private JTextField txtValueToDelete;
    private JTextField txtDigitLimit;
    private JCheckBox chkVisualizeProcess;
    private JLabel lblResult;
    private JLabel lblBlockAccessCount;
    private JLabel lblCurrentOperation;
    private JLabel lblHashFunction;

    // Highlighting variables for external hash search
    private int currentSearchIndex = -1;
    private int foundIndex = -1;
    private int currentBlockAccess = -1;
    private int blockAccessCount = 0;

    public ExternalSquaredSearchView() {
        // Basic window configuration
        setTitle("Función Cuadrado Externa");
        setSize(1000, 1150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // Set soft background color for the entire window
        getContentPane().setBackground(new Color(240, 248, 255));

        // Top panel with title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(155, 89, 182)); // Light purple theme for external squared hash
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Algoritmo de Función Cuadrado Externa");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Tabla hash cuadrado medio con manejo de bloques de disco");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitle.setForeground(new Color(240, 248, 255));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(lblSubtitle);

        add(titlePanel, BorderLayout.NORTH);

        // Center panel with table and search components
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Status panel for hash and block information
        JPanel statusPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        statusPanel.setBackground(new Color(240, 248, 255));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Estado de la Tabla Hash Externa"));

        lblBlockAccessCount = new JLabel("Accesos a bloques: 0");
        lblBlockAccessCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBlockAccessCount.setForeground(new Color(155, 89, 182));

        lblCurrentOperation = new JLabel("Operación: Ninguna");
        lblCurrentOperation.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCurrentOperation.setForeground(new Color(231, 76, 60));

        lblHashFunction = new JLabel("Función hash: cuadrado medio % tableSize");
        lblHashFunction.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHashFunction.setForeground(new Color(46, 204, 113));

        JLabel lblExternalInfo = new JLabel("Algoritmo: Hash cuadrado externo con resolución de colisiones");
        lblExternalInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblExternalInfo.setForeground(new Color(41, 128, 185));

        statusPanel.add(lblBlockAccessCount);
        statusPanel.add(lblCurrentOperation);
        statusPanel.add(lblHashFunction);
        statusPanel.add(lblExternalInfo);

        centerPanel.add(statusPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(240, 248, 255));

        // Create table model with dynamic columns for hash table with blocks
        String[] columnNames = {"Posición", "Bloque", "Slot 1", "Slot 2", "Slot 3", "Slot 4", "Slot 5"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        hashTable = new JTable(tableModel);
        hashTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hashTable.setRowHeight(30);
        hashTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        hashTable.getTableHeader().setBackground(new Color(155, 89, 182));
        hashTable.getTableHeader().setForeground(Color.WHITE);

        // Set column widths
        hashTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Posición
        hashTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Bloque
        for (int i = 2; i < 7; i++) {
            hashTable.getColumnModel().getColumn(i).setPreferredWidth(80); // Slots
        }

        // Custom cell renderer for highlighting external hash operations
        hashTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                // Found item highlighting (highest priority)
                if (row == foundIndex && foundIndex != -1) {
                    c.setBackground(new Color(150, 255, 150)); // Light green for found
                    c.setForeground(Color.BLACK);
                }
                // Block access highlighting (during search)
                else if (row == currentBlockAccess && currentBlockAccess != -1) {
                    c.setBackground(new Color(255, 193, 7)); // Yellow for block being accessed
                    c.setForeground(Color.BLACK);
                }
                // Current search position highlighting
                else if (row == currentSearchIndex && currentSearchIndex != -1) {
                    c.setBackground(new Color(255, 255, 150)); // Light yellow for current search
                    c.setForeground(Color.BLACK);
                }
                else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);

                    // Alternate row colors for better visibility
                    if (row % 2 == 1) {
                        c.setBackground(new Color(248, 249, 250));
                    }
                }

                // Center text alignment
                setHorizontalAlignment(SwingConstants.CENTER);

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(hashTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(155, 89, 182), 1));
        scrollPane.setPreferredSize(new Dimension(950, 350));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.setBackground(new Color(240, 248, 255));
        controlPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel verticalControlPanel = new JPanel();
        verticalControlPanel.setLayout(new BoxLayout(verticalControlPanel, BoxLayout.Y_AXIS));
        verticalControlPanel.setBackground(new Color(240, 248, 255));

        // Panel para límite de dígitos
        JPanel digitLimitPanel = createControlPanel();
        JLabel lblDigitLimit = new JLabel("Límite de dígitos:");
        lblDigitLimit.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtDigitLimit = new JTextField(5);
        txtDigitLimit.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDigitLimit.setText("2");

        digitLimitPanel.add(lblDigitLimit);
        digitLimitPanel.add(txtDigitLimit);

        verticalControlPanel.add(digitLimitPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para configurar tabla hash externa
        JPanel tableConfigPanel = createControlPanel();
        JLabel lblTableSize = new JLabel("Tamaño de tabla:");
        lblTableSize.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtTableSize = new JTextField(5);
        txtTableSize.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTableSize.setText("10");

        JLabel lblBlockSize = new JLabel("Slots por bloque:");
        lblBlockSize.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtBlockSize = new JTextField(5);
        txtBlockSize.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBlockSize.setText("5");

        btnGenerateTable = createStyledButton("Generar Tabla", new Color(46, 204, 113));

        tableConfigPanel.add(lblTableSize);
        tableConfigPanel.add(txtTableSize);
        tableConfigPanel.add(Box.createHorizontalStrut(10));
        tableConfigPanel.add(lblBlockSize);
        tableConfigPanel.add(txtBlockSize);
        tableConfigPanel.add(Box.createHorizontalStrut(10));
        tableConfigPanel.add(btnGenerateTable);

        verticalControlPanel.add(tableConfigPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para insertar valores
        JPanel insertPanel = createControlPanel();
        JLabel lblInsert = new JLabel("Insertar clave:");
        lblInsert.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtInsertValue = new JTextField(10);
        txtInsertValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnInsertValue = createStyledButton("Insertar", new Color(155, 89, 182));

        insertPanel.add(lblInsert);
        insertPanel.add(txtInsertValue);
        insertPanel.add(Box.createHorizontalStrut(10));
        insertPanel.add(btnInsertValue);

        verticalControlPanel.add(insertPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para buscar valor
        JPanel searchPanel = createControlPanel();
        JLabel lblSearch = new JLabel("Clave a buscar:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtValueToSearch = new JTextField(10);
        txtValueToSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnSearch = createStyledButton("Buscar", new Color(155, 89, 182));

        searchPanel.add(lblSearch);
        searchPanel.add(txtValueToSearch);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(btnSearch);

        verticalControlPanel.add(searchPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para el checkbox de visualización
        JPanel visualizationPanel = createControlPanel();
        chkVisualizeProcess = new JCheckBox("Visualizar proceso de búsqueda");
        chkVisualizeProcess.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkVisualizeProcess.setBackground(new Color(240, 248, 255));
        chkVisualizeProcess.setSelected(true);
        chkVisualizeProcess.setCursor(new Cursor(Cursor.HAND_CURSOR));

        visualizationPanel.add(chkVisualizeProcess);

        verticalControlPanel.add(visualizationPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para eliminar valores
        JPanel deletePanel = createControlPanel();
        JLabel lblDelete = new JLabel("Eliminar clave:");
        lblDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtValueToDelete = new JTextField(10);
        txtValueToDelete.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnDeleteValue = createStyledButton("Eliminar", new Color(231, 76, 60));

        deletePanel.add(lblDelete);
        deletePanel.add(txtValueToDelete);
        deletePanel.add(Box.createHorizontalStrut(10));
        deletePanel.add(btnDeleteValue);

        verticalControlPanel.add(deletePanel);
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

        JLabel lblInfo = new JLabel("© 2025 - External Hash Squared Algorithm v1.0");
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

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 35));
        return button;
    }

    // Listener methods
    public void addGenerateTableListener(ActionListener listener) {
        btnGenerateTable.addActionListener(listener);
    }

    public void addInsertValueListener(ActionListener listener) {
        btnInsertValue.addActionListener(listener);
    }

    public void addSearchListener(ActionListener listener) {
        btnSearch.addActionListener(listener);
    }

    public void addDeleteValueListener(ActionListener listener) {
        btnDeleteValue.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    // Getter methods
    public String getSearchValue() {
        return txtValueToSearch.getText().trim();
    }

    public String getTableSize() {
        return txtTableSize.getText().trim();
    }

    public String getBlockSize() {
        return txtBlockSize.getText().trim();
    }

    public String getInsertValue() {
        return txtInsertValue.getText().trim();
    }

    public String getDeleteValue() {
        return txtValueToDelete.getText().trim();
    }

    public String getDigitLimit() {
        return txtDigitLimit.getText().trim();
    }

    public boolean isVisualizationEnabled() {
        return chkVisualizeProcess.isSelected();
    }

    // Display methods
    public void setResultMessage(String message, boolean isSuccess) {
        lblResult.setText(message);
        lblResult.setForeground(isSuccess ? new Color(46, 125, 50) : new Color(198, 40, 40));
    }

    public void setBlockAccessCount(int count) {
        blockAccessCount = count;
        lblBlockAccessCount.setText("Accesos a bloques: " + count);
    }

    public void setCurrentOperation(String operation) {
        lblCurrentOperation.setText("Operación: " + operation);
    }

    public void setHashFunction(String function) {
        lblHashFunction.setText("Función hash: " + function);
    }

    public void setTableData(Object[][] data, String[] headers) {
        // Recreate table model with new headers
        tableModel = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        hashTable.setModel(tableModel);

        // Add data row by row
        for (Object[] row : data) {
            tableModel.addRow(row);
        }

        // Apply custom renderer after adding data
        hashTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (row == foundIndex && foundIndex != -1) {
                    c.setBackground(new Color(150, 255, 150));
                    c.setForeground(Color.BLACK);
                } else if (row == currentBlockAccess && currentBlockAccess != -1) {
                    c.setBackground(new Color(255, 193, 7));
                    c.setForeground(Color.BLACK);
                } else if (row == currentSearchIndex && currentSearchIndex != -1) {
                    c.setBackground(new Color(255, 255, 150));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);

                    if (row % 2 == 1) {
                        c.setBackground(new Color(248, 249, 250));
                    }
                }

                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        // Configure header styles
        hashTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        hashTable.getTableHeader().setBackground(new Color(155, 89, 182));
        hashTable.getTableHeader().setForeground(Color.WHITE);

        hashTable.revalidate();
        hashTable.repaint();
    }

    // Highlighting methods for external hash visualization
    public void highlightBlockAccess(int position) {
        currentBlockAccess = position;
        currentSearchIndex = -1;
        foundIndex = -1;
        hashTable.repaint();

        if (position >= 0 && position < hashTable.getRowCount()) {
            hashTable.scrollRectToVisible(hashTable.getCellRect(position, 0, true));
        }
    }

    public void highlightSearchProgress(int position) {
        currentSearchIndex = position;
        currentBlockAccess = -1;
        foundIndex = -1;
        hashTable.repaint();

        if (position >= 0 && position < hashTable.getRowCount()) {
            hashTable.scrollRectToVisible(hashTable.getCellRect(position, 0, true));
        }
    }

    public void highlightFoundItem(int position) {
        foundIndex = position;
        currentSearchIndex = -1;
        currentBlockAccess = -1;
        hashTable.repaint();

        if (position >= 0 && position < hashTable.getRowCount()) {
            hashTable.scrollRectToVisible(hashTable.getCellRect(position, 0, true));
        }
    }

    public void clearHighlights() {
        currentSearchIndex = -1;
        foundIndex = -1;
        currentBlockAccess = -1;
        hashTable.repaint();
    }

    public void showWindow() {
        setVisible(true);
    }
}
