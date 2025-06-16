package view.external_search;

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
    private JButton btnLoadFromFile;
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

    // Paleta de colores personalizada
    private static final Color DARK_NAVY = new Color(0, 1, 13);      // #0001DD
    private static final Color WARM_BROWN = new Color(115, 73, 22);   // #734916
    private static final Color LIGHT_BROWN = new Color(166, 133, 93); // #A6855D
    private static final Color CREAM = new Color(242, 202, 153);      // #F2CA99
    private static final Color VERY_DARK = new Color(13, 13, 13);     // #0D0D0D
    private static final Color SOFT_WHITE = new Color(248, 248, 248); // Blanco suave para contraste

    public ExternalSequentialSearchView() {
        // Basic window configuration
        setTitle("Búsqueda Secuencial Externa");
        setSize(900, 1100);
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

        JLabel lblTitle = new JLabel("Algoritmo de Búsqueda Secuencial Externa");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(SOFT_WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Simulación con manejo de bloques de disco");
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

        // Status panel for block access information
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(CREAM);
        statusPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(WARM_BROWN, 2),
                "Estado de la Búsqueda",
                0, 0, new Font("Segoe UI", Font.BOLD, 14), VERY_DARK
        ));

        lblBlockAccessCount = new JLabel("Accesos a bloques: 0");
        lblBlockAccessCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBlockAccessCount.setForeground(DARK_NAVY);

        lblCurrentBlock = new JLabel("Bloque actual: Ninguno");
        lblCurrentBlock.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCurrentBlock.setForeground(WARM_BROWN);

        statusPanel.add(lblBlockAccessCount);
        statusPanel.add(Box.createHorizontalStrut(30));
        statusPanel.add(lblCurrentBlock);

        centerPanel.add(statusPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CREAM);

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
        blocksTable.getTableHeader().setBackground(WARM_BROWN);
        blocksTable.getTableHeader().setForeground(SOFT_WHITE);

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
                        c.setBackground(new Color(255, 218, 185)); // Cream más intenso para bloque siendo accedido
                        c.setForeground(VERY_DARK);
                    } else if (column == currentSearchRecordIndex + 1 && currentSearchRecordIndex != -1) {
                        c.setBackground(new Color(255, 235, 205)); // Cream claro para registro actual
                        c.setForeground(VERY_DARK);
                    } else {
                        c.setBackground(LIGHT_BROWN); // Light brown para el resto del bloque
                        c.setForeground(SOFT_WHITE);
                    }
                }
                // Highlight found item
                else if (row == foundBlockIndex && foundBlockIndex != -1) {
                    if (column == 0) {
                        c.setBackground(new Color(144, 238, 144)); // Verde suave para bloque encontrado
                        c.setForeground(VERY_DARK);
                    } else if (column == foundRecordIndex + 1 && foundRecordIndex != -1) {
                        c.setBackground(new Color(152, 251, 152)); // Verde claro para registro encontrado
                        c.setForeground(VERY_DARK);
                    } else {
                        c.setBackground(new Color(200, 255, 200)); // Verde muy claro para el resto del bloque
                        c.setForeground(VERY_DARK);
                    }
                }
                else {
                    c.setBackground(SOFT_WHITE);
                    c.setForeground(VERY_DARK);

                    // Color de fondo para bloques alternos
                    if (row % 2 == 1) {
                        c.setBackground(new Color(245, 245, 245));
                    }
                }

                // Centrar el texto
                setHorizontalAlignment(SwingConstants.CENTER);

                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(blocksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(WARM_BROWN, 2));
        scrollPane.setPreferredSize(new Dimension(800, 300));

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.setBackground(CREAM);
        controlPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel verticalControlPanel = new JPanel();
        verticalControlPanel.setLayout(new BoxLayout(verticalControlPanel, BoxLayout.Y_AXIS));
        verticalControlPanel.setBackground(CREAM);

        // Panel para límite de dígitos
        JPanel digitLimitPanel = createControlPanel();
        JLabel lblDigitLimit = new JLabel("Límite de dígitos:");
        lblDigitLimit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDigitLimit.setForeground(VERY_DARK);

        txtDigitLimit = createStyledTextField();
        txtDigitLimit.setText("2");

        digitLimitPanel.add(lblDigitLimit);
        digitLimitPanel.add(txtDigitLimit);

        verticalControlPanel.add(digitLimitPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para configurar bloques
        JPanel blockConfigPanel = createControlPanel();
        JLabel lblBlockCount = new JLabel("Número de bloques:");
        lblBlockCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBlockCount.setForeground(VERY_DARK);

        txtBlockCount = createStyledTextField();
        txtBlockCount.setText("5");

        JLabel lblBlockSize = new JLabel("Registros por bloque:");
        lblBlockSize.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBlockSize.setForeground(VERY_DARK);

        txtBlockSize = createStyledTextField();
        txtBlockSize.setText("5");

        btnGenerateBlocks = createStyledButton("Generar Bloques", WARM_BROWN);

        blockConfigPanel.add(lblBlockCount);
        blockConfigPanel.add(txtBlockCount);
        blockConfigPanel.add(Box.createHorizontalStrut(10));
        blockConfigPanel.add(lblBlockSize);
        blockConfigPanel.add(txtBlockSize);
        blockConfigPanel.add(Box.createHorizontalStrut(10));
        blockConfigPanel.add(btnGenerateBlocks);

        verticalControlPanel.add(blockConfigPanel);
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

        // Panel para insertar valores
        JPanel insertPanel = createControlPanel();
        JLabel lblInsert = new JLabel("Insertar clave:");
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

        // Panel para buscar valor
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

        // Panel para el checkbox de visualización
        JPanel visualizationPanel = createControlPanel();
        chkVisualizeProcess = new JCheckBox("Visualizar proceso de búsqueda");
        chkVisualizeProcess.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chkVisualizeProcess.setBackground(CREAM);
        chkVisualizeProcess.setForeground(VERY_DARK);
        chkVisualizeProcess.setSelected(true);
        chkVisualizeProcess.setCursor(new Cursor(Cursor.HAND_CURSOR));

        visualizationPanel.add(chkVisualizeProcess);

        verticalControlPanel.add(visualizationPanel);
        verticalControlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para eliminar valores
        JPanel deletePanel = createControlPanel();
        JLabel lblDelete = new JLabel("Eliminar clave:");
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

        JLabel lblInfo = new JLabel("© 2025 - External Search Algorithms v1.0");
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

    public void addLoadFromFileListener(ActionListener listener) {
        btnLoadFromFile.addActionListener(listener);
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
        lblResult.setForeground(isSuccess ? new Color(76, 175, 80) : new Color(183, 28, 28));
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