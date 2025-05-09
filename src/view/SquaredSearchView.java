package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class SquaredSearchView extends JFrame {

    private final JButton btnSearch;
    private final JButton btnGenerateArray;
    private final JButton btnDeleteValue;
    private final JButton btnInsertValue;
    private final JButton btnBack;
    private final JTable dataTable;
    private final DefaultTableModel tableModel;
    private final JTextField txtValueToSearch;
    private final JTextField txtArraySize;
    private final JTextField txtInsertValue;
    private final JTextField txtValueToDelete;
    private final JLabel lblResult;

    public SquaredSearchView() {
        // Basic window configuration
        setTitle("Función Cuadrado");
        setSize(600, 950);
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

        JLabel lblTitle = new JLabel("Algoritmo de Función Cuadrado");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Visualización y prueba del algoritmo");
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

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(240, 248, 255));

        // Create table model with two columns: position and value
        tableModel = new DefaultTableModel(new Object[]{"Posición", "Valor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        dataTable = new JTable(tableModel);
        dataTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dataTable.setRowHeight(25);
        dataTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        dataTable.getTableHeader().setBackground(new Color(41, 128, 185));
        dataTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 1));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        // Control panel (at the bottom) - Reorganizado con mejor espaciado
        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.setBackground(new Color(240, 248, 255));
        controlPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Panel contenedor que organiza verticalmente los subpaneles
        JPanel verticalControlPanel = new JPanel();
        verticalControlPanel.setLayout(new BoxLayout(verticalControlPanel, BoxLayout.Y_AXIS));
        verticalControlPanel.setBackground(new Color(240, 248, 255));

        // Panel para generar arreglo
        JPanel generatePanel = createControlPanel();
        JLabel lblArraySize = new JLabel("Tamaño del arreglo:");
        lblArraySize.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtArraySize = new JTextField(10);
        txtArraySize.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnGenerateArray = createStyledButton("Generar Arreglo", new Color(46, 204, 113));

        generatePanel.add(lblArraySize);
        generatePanel.add(txtArraySize);
        generatePanel.add(Box.createHorizontalStrut(10));
        generatePanel.add(btnGenerateArray);

        verticalControlPanel.add(generatePanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para insertar valores
        JPanel insertPanel = createControlPanel();
        JLabel lblInsert = new JLabel("Inserte un valor:");
        lblInsert.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtInsertValue = new JTextField(10);
        txtInsertValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnInsertValue = createStyledButton("Insertar", new Color(41, 128, 185));

        insertPanel.add(lblInsert);
        insertPanel.add(txtInsertValue);
        insertPanel.add(Box.createHorizontalStrut(10));
        insertPanel.add(btnInsertValue);

        verticalControlPanel.add(insertPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para buscar valor
        JPanel searchPanel = createControlPanel();
        JLabel lblSearch = new JLabel("Valor a buscar:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtValueToSearch = new JTextField(10);
        txtValueToSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnSearch = createStyledButton("Buscar", new Color(41, 128, 185));

        searchPanel.add(lblSearch);
        searchPanel.add(txtValueToSearch);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(btnSearch);

        verticalControlPanel.add(searchPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para eliminar valores
        JPanel deletePanel = createControlPanel();
        JLabel lblDelete = new JLabel("Eliminar un valor:");
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

        JLabel lblInfo = new JLabel("© 2025 - Search Algorithms v1.0");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(100, 100, 100));

        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
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

    // Method to display search result
    public void setResultMessage(String message, boolean isSuccess) {
        lblResult.setText(message);
        lblResult.setForeground(isSuccess ? new Color(46, 125, 50) : new Color(198, 40, 40));
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

    // Method to show the window
    public void showWindow() {
        setVisible(true);
    }
}

