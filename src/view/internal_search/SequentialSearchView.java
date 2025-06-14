package view.internal_search;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;

public class SequentialSearchView extends JFrame {

    private JButton btnSearch;
    private JButton btnGenerateArray;
    private JButton btnDeleteValue;
    private JButton btnInsertValue;
    private JButton btnLoadFromFile;
    private JButton btnClearArray;
    private JButton btnBack;
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JTextField txtValueToSearch;
    private JTextField txtArraySize;
    private JTextField txtInsertValue;
    private JTextField txtValueToDelete;
    private JTextField txtDigitLimit;
    private JCheckBox chkVisualizeProcess;
    private JLabel lblResult;
    private int currentSearchIndex = -1;
    private int foundIndex = -1;

    // Paleta de colores personalizada
    private static final Color DARK_NAVY = new Color(0, 1, 13);      // #0001DD
    private static final Color WARM_BROWN = new Color(115, 73, 22);   // #734916
    private static final Color LIGHT_BROWN = new Color(166, 133, 93); // #A6855D
    private static final Color CREAM = new Color(242, 202, 153);      // #F2CA99
    private static final Color VERY_DARK = new Color(13, 13, 13);     // #0D0D0D
    private static final Color SOFT_WHITE = new Color(248, 248, 248); // Blanco suave para contraste

    public SequentialSearchView() {
        // Basic window configuration
        setTitle("Búsqueda Secuencial");
        setSize(600, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // Set background color using cream tone
        getContentPane().setBackground(CREAM);

        // Top panel with title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(DARK_NAVY);
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Algoritmo de Búsqueda Secuencial");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(SOFT_WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Visualización y prueba del algoritmo");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitle.setForeground(CREAM);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(lblSubtitle);

        add(titlePanel, BorderLayout.NORTH);

        // Center panel with table and search components
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(CREAM);
        centerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CREAM);

        // Create table model with two columns: position and value
        tableModel = new DefaultTableModel(new Object[]{"Posición", "Clave"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        dataTable = new JTable(tableModel);
        dataTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dataTable.setRowHeight(25);
        dataTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        dataTable.getTableHeader().setBackground(WARM_BROWN);
        dataTable.getTableHeader().setForeground(SOFT_WHITE);

        // Custom cell renderer for highlighting
        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (row == currentSearchIndex && currentSearchIndex != -1) {
                    // Cream más intenso para búsqueda actual
                    c.setBackground(new Color(255, 218, 185));
                    c.setForeground(VERY_DARK);
                } else if (row == foundIndex && foundIndex != -1) {
                    // Verde suave para elemento encontrado
                    c.setBackground(new Color(144, 238, 144));
                    c.setForeground(VERY_DARK);
                } else {
                    c.setBackground(SOFT_WHITE);
                    c.setForeground(VERY_DARK);
                }

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(WARM_BROWN, 2));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.setBackground(CREAM);
        controlPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel verticalControlPanel = new JPanel();
        verticalControlPanel.setLayout(new BoxLayout(verticalControlPanel, BoxLayout.Y_AXIS));
        verticalControlPanel.setBackground(CREAM);

        JPanel digitLimitPanel = createControlPanel();
        JLabel lblDigitLimit = new JLabel("Límite de dígitos:");
        lblDigitLimit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDigitLimit.setForeground(VERY_DARK);

        txtDigitLimit = createStyledTextField();
        txtDigitLimit.setText("2"); // Valor por defecto

        digitLimitPanel.add(lblDigitLimit);
        digitLimitPanel.add(txtDigitLimit);

        verticalControlPanel.add(digitLimitPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel generatePanel = createControlPanel();
        JLabel lblArraySize = new JLabel("Tamaño del arreglo:");
        lblArraySize.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblArraySize.setForeground(VERY_DARK);

        txtArraySize = createStyledTextField();

        btnGenerateArray = createStyledButton("Generar Arreglo", WARM_BROWN);

        generatePanel.add(lblArraySize);
        generatePanel.add(txtArraySize);
        generatePanel.add(Box.createHorizontalStrut(10));
        generatePanel.add(btnGenerateArray);

        verticalControlPanel.add(generatePanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel to load from file
        JPanel loadFilePanel = createControlPanel();
        JLabel lblLoadFile = new JLabel("Cargar datos desde archivo:");
        lblLoadFile.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLoadFile.setForeground(VERY_DARK);

        btnLoadFromFile = createStyledButton("Seleccionar Archivo", WARM_BROWN);

        loadFilePanel.add(lblLoadFile);
        loadFilePanel.add(Box.createHorizontalStrut(10));
        loadFilePanel.add(btnLoadFromFile);

        verticalControlPanel.add(loadFilePanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel to insert value
        JPanel insertPanel = createControlPanel();
        JLabel lblInsert = new JLabel("Insertar una clave:");
        lblInsert.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInsert.setForeground(VERY_DARK);

        txtInsertValue = createStyledTextField();

        btnInsertValue = createStyledButton("Insertar", LIGHT_BROWN);

        insertPanel.add(lblInsert);
        insertPanel.add(txtInsertValue);
        insertPanel.add(Box.createHorizontalStrut(10));
        insertPanel.add(btnInsertValue);

        verticalControlPanel.add(insertPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel to search value
        JPanel searchPanel = createControlPanel();
        JLabel lblSearch = new JLabel("Clave a buscar:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSearch.setForeground(VERY_DARK);

        txtValueToSearch = createStyledTextField();

        btnSearch = createStyledButton("Buscar", DARK_NAVY);

        searchPanel.add(lblSearch);
        searchPanel.add(txtValueToSearch);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(btnSearch);

        verticalControlPanel.add(searchPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel for visualization checkbox
        JPanel visualizationPanel = createControlPanel();
        chkVisualizeProcess = new JCheckBox("Visualizar proceso de búsqueda");
        chkVisualizeProcess.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkVisualizeProcess.setBackground(CREAM);
        chkVisualizeProcess.setForeground(VERY_DARK);
        chkVisualizeProcess.setSelected(true); // Por defecto activado
        chkVisualizeProcess.setCursor(new Cursor(Cursor.HAND_CURSOR));

        visualizationPanel.add(chkVisualizeProcess);

        verticalControlPanel.add(visualizationPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel to delete value
        JPanel deletePanel = createControlPanel();
        JLabel lblDelete = new JLabel("Eliminar una clave:");
        lblDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDelete.setForeground(VERY_DARK);

        txtValueToDelete = createStyledTextField();

        btnDeleteValue = createStyledButton("Eliminar", new Color(180, 67, 67)); // Rojo más suave

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
        resultPanel.setBackground(CREAM);
        resultPanel.add(lblResult);

        verticalControlPanel.add(resultPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Button panel for back button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(CREAM);

        btnBack = createStyledButton("Volver", VERY_DARK);
        buttonPanel.add(btnBack);

        verticalControlPanel.add(buttonPanel);

        controlPanel.add(verticalControlPanel, BorderLayout.CENTER);
        centerPanel.add(controlPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with information
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(LIGHT_BROWN);
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("© 2025 - Search Algorithms v1.0");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(SOFT_WHITE);

        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panel.setBackground(CREAM);
        return panel;
    }

    // Method to create a styled text field
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(10);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(SOFT_WHITE);
        textField.setForeground(VERY_DARK);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BROWN, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return textField;
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
        button.setPreferredSize(new Dimension(150, 35));

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

    // Methods to assign external actions to buttons
    public void addGenerateArrayListener(ActionListener listener) {
        btnGenerateArray.addActionListener(listener);
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

    public void addLoadFromFileListener(ActionListener listener) {
        btnLoadFromFile.addActionListener(listener);
    }

    public void addClearArrayListener(ActionListener listener) {
        btnClearArray.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    // Method to get search value
    public String getSearchValue() {
        return txtValueToSearch.getText().trim();
    }

    // Method to get array size value
    public String getArraySize() {
        return txtArraySize.getText().trim();
    }

    // Method to get insert value
    public String getInsertValue() {
        return txtInsertValue.getText().trim();
    }

    // Method to get delete value
    public String getDeleteValue() {
        return txtValueToDelete.getText().trim();
    }

    // Method to get digit limit
    public String getDigitLimit() {
        return txtDigitLimit.getText().trim();
    }

    // Method to display search result
    public void setResultMessage(String message, boolean isSuccess) {
        lblResult.setText(message);
        lblResult.setForeground(isSuccess ? new Color(76, 175, 80) : new Color(183, 28, 28));
    }

    // Method to populate the table with values
    public void setTableData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    // Method to highlight a specific row in the table
    public void highlightRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < dataTable.getRowCount()) {
            dataTable.setRowSelectionInterval(rowIndex, rowIndex);
            dataTable.scrollRectToVisible(dataTable.getCellRect(rowIndex, 0, true));
        }
    }

    // Method to highlight search progress
    public void highlightSearchProgress(int rowIndex) {
        currentSearchIndex = rowIndex;
        foundIndex = -1;
        dataTable.repaint();

        if (rowIndex >= 0 && rowIndex < dataTable.getRowCount()) {
            dataTable.scrollRectToVisible(dataTable.getCellRect(rowIndex, 0, true));
        }
    }

    // Method to highlight found item
    public void highlightFoundItem(int rowIndex) {
        currentSearchIndex = -1;
        foundIndex = rowIndex;
        dataTable.repaint();

        if (rowIndex >= 0 && rowIndex < dataTable.getRowCount()) {
            dataTable.scrollRectToVisible(dataTable.getCellRect(rowIndex, 0, true));
        }
    }

    // Method to clear highlights
    public void clearHighlights() {
        currentSearchIndex = -1;
        foundIndex = -1;
        dataTable.repaint();
    }

    // Method to check if visualization is enabled
    public boolean isVisualizationEnabled() {
        return chkVisualizeProcess.isSelected();
    }

    // Method to show the window
    public void showWindow() {
        setVisible(true);
    }
}