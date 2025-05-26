package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;

public class ExternalSequentialSearchView extends JFrame {

    private JButton btnSearch;
    private JButton btnGenerateBlocks;
    private JButton btnDeleteValue;
    private JButton btnInsertValue;
    private JButton btnBack;
    private JTable blocksTable;
    private DefaultTableModel tableModel;
    private JTextField txtValueToSearch;
    private JTextField txtBlockCount;
    private JTextField txtBlockSize;
    private JTextField txtInsertValue;
    private JTextField txtValueToDelete;
    private JTextField txtDigitLimit;
    private JCheckBox chkVisualizeProcess;
    private JLabel lblResult;
    private JLabel lblBlockAccessCount;
    private JLabel lblCurrentBlock;
    private int currentSearchBlockIndex = -1;
    private int currentSearchRecordIndex = -1;
    private int foundBlockIndex = -1;
    private int foundRecordIndex = -1;
    private int blockAccessCount = 0;

    public ExternalSequentialSearchView() {
        // Basic window configuration
        setTitle("Búsqueda Secuencial Externa");
        setSize(900, 1100);
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

        JLabel lblTitle = new JLabel("Algoritmo de Búsqueda Secuencial Externa");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Simulación con manejo de bloques de disco");
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

        // Status panel for block access information
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 248, 255));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Estado de la Búsqueda"));

        lblBlockAccessCount = new JLabel("Accesos a bloques: 0");
        lblBlockAccessCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBlockAccessCount.setForeground(new Color(41, 128, 185));

        lblCurrentBlock = new JLabel("Bloque actual: Ninguno");
        lblCurrentBlock.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCurrentBlock.setForeground(new Color(231, 76, 60));

        statusPanel.add(lblBlockAccessCount);
        statusPanel.add(Box.createHorizontalStrut(30));
        statusPanel.add(lblCurrentBlock);

        centerPanel.add(statusPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(240, 248, 255));

        // Create table model with columns for blocks
        String[] columnNames = {"Bloque", "Reg 1", "Reg 2", "Reg 3", "Reg 4", "Reg 5"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        blocksTable = new JTable(tableModel);
        blocksTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        blocksTable.setRowHeight(30);
        blocksTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        blocksTable.getTableHeader().setBackground(new Color(41, 128, 185));
        blocksTable.getTableHeader().setForeground(Color.WHITE);

        // Custom cell renderer for highlighting blocks and records
        blocksTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                // Highlight current search block
                if (row == currentSearchBlockIndex && currentSearchBlockIndex != -1) {
                    if (column == 0) {
                        c.setBackground(new Color(255, 193, 7)); // Amarillo para bloque siendo accedido
                        c.setForeground(Color.BLACK);
                    } else if (column == currentSearchRecordIndex + 1 && currentSearchRecordIndex != -1) {
                        c.setBackground(new Color(255, 255, 150)); // Amarillo claro para registro actual
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(new Color(255, 235, 205)); // Amarillo muy claro para el resto del bloque
                        c.setForeground(Color.BLACK);
                    }
                }
                // Highlight found item
                else if (row == foundBlockIndex && foundBlockIndex != -1) {
                    if (column == 0) {
                        c.setBackground(new Color(40, 167, 69)); // Verde para bloque encontrado
                        c.setForeground(Color.WHITE);
                    } else if (column == foundRecordIndex + 1 && foundRecordIndex != -1) {
                        c.setBackground(new Color(150, 255, 150)); // Verde claro para registro encontrado
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(new Color(200, 255, 200)); // Verde muy claro para el resto del bloque
                        c.setForeground(Color.BLACK);
                    }
                }
                else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);

                    // Color de fondo para bloques alternos
                    if (row % 2 == 1) {
                        c.setBackground(new Color(248, 249, 250));
                    }
                }

                // Centrar el texto
                setHorizontalAlignment(SwingConstants.CENTER);

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(blocksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 1));
        scrollPane.setPreferredSize(new Dimension(800, 300));

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

        // Panel para configurar bloques
        JPanel blockConfigPanel = createControlPanel();
        JLabel lblBlockCount = new JLabel("Número de bloques:");
        lblBlockCount.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtBlockCount = new JTextField(5);
        txtBlockCount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBlockCount.setText("5");

        JLabel lblBlockSize = new JLabel("Registros por bloque:");
        lblBlockSize.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtBlockSize = new JTextField(5);
        txtBlockSize.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBlockSize.setText("5");

        btnGenerateBlocks = createStyledButton("Generar Bloques", new Color(46, 204, 113));

        blockConfigPanel.add(lblBlockCount);
        blockConfigPanel.add(txtBlockCount);
        blockConfigPanel.add(Box.createHorizontalStrut(10));
        blockConfigPanel.add(lblBlockSize);
        blockConfigPanel.add(txtBlockSize);
        blockConfigPanel.add(Box.createHorizontalStrut(10));
        blockConfigPanel.add(btnGenerateBlocks);

        verticalControlPanel.add(blockConfigPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para insertar valores
        JPanel insertPanel = createControlPanel();
        JLabel lblInsert = new JLabel("Insertar clave:");
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
        JLabel lblSearch = new JLabel("Clave a buscar:");
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

        JLabel lblInfo = new JLabel("© 2025 - External Search Algorithms v1.0");
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
    public void addGenerateBlocksListener(ActionListener listener) {
        btnGenerateBlocks.addActionListener(listener);
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

    public String getBlockCount() {
        return txtBlockCount.getText().trim();
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

    public void setCurrentBlock(String blockInfo) {
        lblCurrentBlock.setText("Bloque actual: " + blockInfo);
    }

    public void setTableData(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    // Highlighting methods for external search visualization
    public void highlightBlockAccess(int blockIndex, int recordIndex) {
        currentSearchBlockIndex = blockIndex;
        currentSearchRecordIndex = recordIndex;
        foundBlockIndex = -1;
        foundRecordIndex = -1;
        blocksTable.repaint();

        if (blockIndex >= 0 && blockIndex < blocksTable.getRowCount()) {
            blocksTable.scrollRectToVisible(blocksTable.getCellRect(blockIndex, 0, true));
        }
    }

    public void highlightFoundItem(int blockIndex, int recordIndex) {
        currentSearchBlockIndex = -1;
        currentSearchRecordIndex = -1;
        foundBlockIndex = blockIndex;
        foundRecordIndex = recordIndex;
        blocksTable.repaint();

        if (blockIndex >= 0 && blockIndex < blocksTable.getRowCount()) {
            blocksTable.scrollRectToVisible(blocksTable.getCellRect(blockIndex, 0, true));
        }
    }

    public void clearHighlights() {
        currentSearchBlockIndex = -1;
        currentSearchRecordIndex = -1;
        foundBlockIndex = -1;
        foundRecordIndex = -1;
        blocksTable.repaint();
    }

    public void showWindow() {
        setVisible(true);
    }
}