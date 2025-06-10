package view.indexes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;

public class ImprovedMultilevelIndexView extends JFrame {

    private JButton btnSearch;
    private JButton btnConfigure;
    private JButton btnDeleteRecord;
    private JButton btnInsertRecord;
    private JButton btnBack;
    private JButton btnShowLevelDetails;

    private JTable dataTable;
    private JTable primaryIndexTable;
    private JTable secondaryIndexTable;

    private DefaultTableModel dataTableModel;
    private DefaultTableModel primaryIndexTableModel;
    private DefaultTableModel secondaryIndexTableModel;

    private JTextField txtSearchValue;
    private JTextField txtRecordCount;
    private JTextField txtBlockSizeBytes;
    private JTextField txtDataRecordSizeBytes;
    private JTextField txtIndexRecordSizeBytes;
    private JTextField txtInsertId;
    private JTextField txtInsertName;
    private JTextField txtInsertAge;
    private JTextField txtDeleteId;

    private JCheckBox chkVisualizeProcess;
    private JLabel lblResult;
    private JLabel lblSearchProgress;
    private JLabel lblConfigurationInfo;
    private JLabel lblStatistics;
    private JLabel lblLevelInfo;

    // Highlighting variables
    private int highlightedDataRecord = -1;
    private int highlightedPrimaryIndex = -1;
    private int highlightedSecondaryIndex = -1;
    private int foundRecordIndex = -1;

    public ImprovedMultilevelIndexView() {
        setTitle("Índices Multinivel Dinámicos");
        setSize(1500, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        getContentPane().setBackground(new Color(240, 248, 255));

        createTopPanel();
        createCenterPanel();
        createBottomPanel();
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(70, 130, 180));
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(70, 130, 180));

        JLabel lblTitle = new JLabel("Índices Multinivel Dinámicos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Generación automática de niveles según volumen de datos");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSubtitle.setForeground(new Color(240, 248, 255));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        titlePanel.add(lblSubtitle);

        // Information labels
        lblConfigurationInfo = new JLabel("Configuración: Cargando");
        lblConfigurationInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblConfigurationInfo.setForeground(new Color(240, 248, 255));
        lblConfigurationInfo.setHorizontalAlignment(SwingConstants.CENTER);

        lblLevelInfo = new JLabel("Estructura de niveles: Calculando");
        lblLevelInfo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblLevelInfo.setForeground(new Color(173, 216, 230));
        lblLevelInfo.setHorizontalAlignment(SwingConstants.CENTER);

        lblStatistics = new JLabel("Estadísticas: Cargando");
        lblStatistics.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatistics.setForeground(new Color(240, 248, 255));
        lblStatistics.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBackground(new Color(70, 130, 180));
        infoPanel.add(lblConfigurationInfo);
        infoPanel.add(lblLevelInfo);
        infoPanel.add(lblStatistics);

        topPanel.add(titlePanel, BorderLayout.CENTER);
        topPanel.add(infoPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
    }

    private void createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Create tabbed pane for the tables
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tabla de Índice de Nivel Superior (antes "Secundario")
        JPanel upperLevelPanel = createTablePanel("Índice de Nivel Superior");
        secondaryIndexTableModel = new DefaultTableModel(
                new Object[]{"Nivel-Bloque", "Clave Min", "Clave Max", "Puntero", "Registros", "Utilización", "Apunta a"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        secondaryIndexTable = createStyledTable(secondaryIndexTableModel);
        JScrollPane upperLevelScrollPane = new JScrollPane(secondaryIndexTable);
        upperLevelScrollPane.setPreferredSize(new Dimension(0, 400));
        upperLevelPanel.add(upperLevelScrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Nivel Superior", upperLevelPanel);

        // Table of Primary Index (before "Primario")
        JPanel primaryPanel = createTablePanel("Índice Primario (Nivel 1)");
        primaryIndexTableModel = new DefaultTableModel(
                new Object[]{"Nivel-Bloque", "Clave Min", "Clave Max", "Puntero", "Registros", "Utilización", "Apunta a"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        primaryIndexTable = createStyledTable(primaryIndexTableModel);
        JScrollPane primaryScrollPane = new JScrollPane(primaryIndexTable);
        primaryScrollPane.setPreferredSize(new Dimension(0, 400));
        primaryPanel.add(primaryScrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Índice Primario", primaryPanel);

        // Table of Data Records (before "Datos")
        JPanel dataPanel = createTablePanel("Datos (Nivel 0)");
        dataTableModel = new DefaultTableModel(
                new Object[]{"Bloque", "Pos. Bloque", "ID", "Nombre", "Edad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dataTable = createStyledTable(dataTableModel);
        JScrollPane dataScrollPane = new JScrollPane(dataTable);
        dataScrollPane.setPreferredSize(new Dimension(0, 400));
        dataPanel.add(dataScrollPane, BorderLayout.CENTER);
        tabbedPane.addTab("Datos", dataPanel);

        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        // Panel of controls on the right side
        JPanel controlPanel = createControlPanel();
        centerPanel.add(controlPanel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createTablePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(new Color(70, 130, 180));
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(22);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);

        table.setAutoCreateRowSorter(false);
        table.setFillsViewportHeight(true);

        // Custom cell renderer para highlighting
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                // Data table highlighting
                if (table == dataTable) {
                    if (row == foundRecordIndex && foundRecordIndex != -1) {
                        c.setBackground(new Color(150, 255, 150));
                        c.setForeground(Color.BLACK);
                    } else if (row == highlightedDataRecord && highlightedDataRecord != -1) {
                        c.setBackground(new Color(255, 255, 150));
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                // Primary index highlighting
                else if (table == primaryIndexTable) {
                    if (row == highlightedPrimaryIndex && highlightedPrimaryIndex != -1) {
                        c.setBackground(new Color(173, 216, 230));
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                // Secondary/Upper level index highlighting
                else if (table == secondaryIndexTable) {
                    if (row == highlightedSecondaryIndex && highlightedSecondaryIndex != -1) {
                        c.setBackground(new Color(255, 182, 193));
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }

                return c;
            }
        });

        return table;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(new Color(240, 248, 255));
        controlPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        controlPanel.setPreferredSize(new Dimension(400, 0));

        // Section of advanced configuration
        JPanel configSection = createSectionPanel("Configuración Técnica Avanzada");

        JPanel recordCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recordCountPanel.setBackground(new Color(240, 248, 255));
        JLabel lblRecordCount = new JLabel("Núm. registros:");
        lblRecordCount.setFont(new Font("Segoe UI", Font.BOLD, 11));
        txtRecordCount = new JTextField(8);
        txtRecordCount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtRecordCount.setText("15");
        txtRecordCount.setToolTipText("Niveles calculados automáticamente según volumen");
        recordCountPanel.add(lblRecordCount);
        recordCountPanel.add(txtRecordCount);

        JPanel blockSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        blockSizePanel.setBackground(new Color(240, 248, 255));
        JLabel lblBlockSize = new JLabel("Tamaño bloque (bytes):");
        lblBlockSize.setFont(new Font("Segoe UI", Font.BOLD, 11));
        txtBlockSizeBytes = new JTextField(8);
        txtBlockSizeBytes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtBlockSizeBytes.setText("1024");
        txtBlockSizeBytes.setToolTipText("Afecta registros por bloque y niveles necesarios");
        blockSizePanel.add(lblBlockSize);
        blockSizePanel.add(txtBlockSizeBytes);

        JPanel dataRecordSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dataRecordSizePanel.setBackground(new Color(240, 248, 255));
        JLabel lblDataRecordSize = new JLabel("Tamaño reg. datos (bytes):");
        lblDataRecordSize.setFont(new Font("Segoe UI", Font.BOLD, 11));
        txtDataRecordSizeBytes = new JTextField(8);
        txtDataRecordSizeBytes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtDataRecordSizeBytes.setText("64");
        txtDataRecordSizeBytes.setToolTipText("Determina registros de datos por bloque");
        dataRecordSizePanel.add(lblDataRecordSize);
        dataRecordSizePanel.add(txtDataRecordSizeBytes);

        JPanel indexRecordSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        indexRecordSizePanel.setBackground(new Color(240, 248, 255));
        JLabel lblIndexRecordSize = new JLabel("Tamaño reg. índice (bytes):");
        lblIndexRecordSize.setFont(new Font("Segoe UI", Font.BOLD, 11));
        txtIndexRecordSizeBytes = new JTextField(8);
        txtIndexRecordSizeBytes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        txtIndexRecordSizeBytes.setText("12");
        txtIndexRecordSizeBytes.setToolTipText("Determina registros de índice por bloque");
        indexRecordSizePanel.add(lblIndexRecordSize);
        indexRecordSizePanel.add(txtIndexRecordSizeBytes);

        btnConfigure = createStyledButton("Aplicar Configuración", new Color(46, 204, 113));
        btnShowLevelDetails = createStyledButton("Ver Detalles Niveles", new Color(142, 68, 173));

        configSection.add(recordCountPanel);
        configSection.add(blockSizePanel);
        configSection.add(dataRecordSizePanel);
        configSection.add(indexRecordSizePanel);
        configSection.add(btnConfigure);
        configSection.add(Box.createRigidArea(new Dimension(0, 5)));
        configSection.add(btnShowLevelDetails);

        controlPanel.add(configSection);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Section of insertion of records
        JPanel insertSection = createSectionPanel("Insertar Registro");

        JPanel insertIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        insertIdPanel.setBackground(new Color(240, 248, 255));
        JLabel lblInsertId = new JLabel("ID:");
        lblInsertId.setFont(new Font("Segoe UI", Font.BOLD, 11));
        txtInsertId = new JTextField(8);
        txtInsertId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        insertIdPanel.add(lblInsertId);
        insertIdPanel.add(txtInsertId);

        JPanel insertNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        insertNamePanel.setBackground(new Color(240, 248, 255));
        JLabel lblInsertName = new JLabel("Nombre:");
        lblInsertName.setFont(new Font("Segoe UI", Font.BOLD, 11));
        txtInsertName = new JTextField(10);
        txtInsertName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        insertNamePanel.add(lblInsertName);
        insertNamePanel.add(txtInsertName);

        JPanel insertAgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        insertAgePanel.setBackground(new Color(240, 248, 255));
        JLabel lblInsertAge = new JLabel("Edad:");
        lblInsertAge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        txtInsertAge = new JTextField(8);
        txtInsertAge.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        insertAgePanel.add(lblInsertAge);
        insertAgePanel.add(txtInsertAge);

        btnInsertRecord = createStyledButton("Insertar", new Color(41, 128, 185));

        insertSection.add(insertIdPanel);
        insertSection.add(insertNamePanel);
        insertSection.add(insertAgePanel);
        insertSection.add(btnInsertRecord);

        controlPanel.add(insertSection);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Section of search
        JPanel searchSection = createSectionPanel("Búsqueda Multinivel");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 248, 255));
        JLabel lblSearch = new JLabel("ID a buscar:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 11));
        txtSearchValue = new JTextField(8);
        txtSearchValue.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        searchPanel.add(lblSearch);
        searchPanel.add(txtSearchValue);

        JPanel visualizationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        visualizationPanel.setBackground(new Color(240, 248, 255));
        chkVisualizeProcess = new JCheckBox("Visualizar proceso multinivel");
        chkVisualizeProcess.setFont(new Font("Segoe UI", Font.BOLD, 11));
        chkVisualizeProcess.setBackground(new Color(240, 248, 255));
        chkVisualizeProcess.setSelected(true);
        chkVisualizeProcess.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkVisualizeProcess.setToolTipText("Visualización paso a paso por todos los niveles");
        visualizationPanel.add(chkVisualizeProcess);

        btnSearch = createStyledButton("Buscar", new Color(41, 128, 185));

        // Label for search progress
        lblSearchProgress = new JLabel("");
        lblSearchProgress.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblSearchProgress.setForeground(new Color(70, 130, 180));
        lblSearchProgress.setHorizontalAlignment(SwingConstants.LEFT);

        searchSection.add(searchPanel);
        searchSection.add(visualizationPanel);
        searchSection.add(btnSearch);
        searchSection.add(lblSearchProgress);

        controlPanel.add(searchSection);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Section of deletion of records
        JPanel deleteSection = createSectionPanel("Eliminar Registro");

        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deletePanel.setBackground(new Color(240, 248, 255));
        JLabel lblDelete = new JLabel("ID a eliminar:");
        lblDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
        txtDeleteId = new JTextField(8);
        txtDeleteId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        deletePanel.add(lblDelete);
        deletePanel.add(txtDeleteId);

        btnDeleteRecord = createStyledButton("Eliminar", new Color(231, 76, 60));

        deleteSection.add(deletePanel);
        deleteSection.add(btnDeleteRecord);

        controlPanel.add(deleteSection);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Label for results
        lblResult = new JLabel("");
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblResult.setHorizontalAlignment(SwingConstants.LEFT);
        lblResult.setBorder(new EmptyBorder(10, 0, 10, 0));

        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resultPanel.setBackground(new Color(240, 248, 255));
        resultPanel.add(lblResult);

        controlPanel.add(resultPanel);
        controlPanel.add(Box.createVerticalGlue());

        // Button panel for actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 248, 255));
        btnBack = createStyledButton("Volver", new Color(231, 76, 60));
        buttonPanel.add(btnBack);

        controlPanel.add(buttonPanel);

        return controlPanel;
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 1),
                title,
                0, 0,
                new Font("Segoe UI", Font.BOLD, 12),
                new Color(41, 128, 185)
        ));
        return panel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 28));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(220, 220, 220));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("© 2025 - Simulación Avanzada de Índices Multinivel Dinámicos - Grupo 1");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(100, 100, 100));

        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Methods to add action listeners for buttons
    public void addConfigureListener(ActionListener listener) {
        btnConfigure.addActionListener(listener);
    }

    public void addInsertRecordListener(ActionListener listener) {
        btnInsertRecord.addActionListener(listener);
    }

    public void addSearchListener(ActionListener listener) {
        btnSearch.addActionListener(listener);
    }

    public void addDeleteRecordListener(ActionListener listener) {
        btnDeleteRecord.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    public void addShowLevelDetailsListener(ActionListener listener) {
        btnShowLevelDetails.addActionListener(listener);
    }

    // Getters for input values
    public String getSearchValue() {
        return txtSearchValue.getText().trim();
    }

    public String getRecordCount() {
        return txtRecordCount.getText().trim();
    }

    public String getBlockSizeBytes() {
        return txtBlockSizeBytes.getText().trim();
    }

    public String getDataRecordSizeBytes() {
        return txtDataRecordSizeBytes.getText().trim();
    }

    public String getIndexRecordSizeBytes() {
        return txtIndexRecordSizeBytes.getText().trim();
    }

    public String getInsertId() {
        return txtInsertId.getText().trim();
    }

    public String getInsertName() {
        return txtInsertName.getText().trim();
    }

    public String getInsertAge() {
        return txtInsertAge.getText().trim();
    }

    public String getDeleteId() {
        return txtDeleteId.getText().trim();
    }

    // Methods to set messages and information
    public void setResultMessage(String message, boolean isSuccess) {
        lblResult.setText("<html><div style='width: 350px;'>" + message + "</div></html>");
        lblResult.setForeground(isSuccess ? new Color(46, 125, 50) : new Color(198, 40, 40));
    }

    public void setSearchProgress(String message) {
        lblSearchProgress.setText("<html><div style='width: 350px;'>" + message + "</div></html>");
    }

    public void setConfigurationInfo(String configInfo) {
        lblConfigurationInfo.setText("<html><div style='width: 1200px; text-align: center;'>" + configInfo + "</div></html>");
    }

    public void setLevelInfo(String levelInfo) {
        lblLevelInfo.setText("<html><div style='width: 1200px; text-align: center;'>" + levelInfo + "</div></html>");
    }

    public void setStatistics(String stats) {
        lblStatistics.setText("<html><div style='width: 1200px; text-align: center;'>" + stats + "</div></html>");
    }

    // Methods to set data for tables
    public void setDataTableData(Object[][] data) {
        if (data.length > 10000) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    SwingUtilities.invokeLater(() -> {
                        dataTableModel.setRowCount(0);
                        for (Object[] row : data) {
                            dataTableModel.addRow(row);
                        }
                    });
                    return null;
                }
            };
            worker.execute();
        } else {
            dataTableModel.setRowCount(0);
            for (Object[] row : data) {
                dataTableModel.addRow(row);
            }
        }
    }

    public void setPrimaryIndexTableData(Object[][] data) {
        primaryIndexTableModel.setRowCount(0);
        for (Object[] row : data) {
            primaryIndexTableModel.addRow(row);
        }
    }

    public void setSecondaryIndexTableData(Object[][] data) {
        secondaryIndexTableModel.setRowCount(0);
        for (Object[] row : data) {
            secondaryIndexTableModel.addRow(row);
        }
    }

    // Methods to highlight records in tables
    public void highlightDataRecord(int rowIndex) {
        this.highlightedDataRecord = rowIndex;
        this.foundRecordIndex = -1;
        dataTable.repaint();

        if (rowIndex >= 0 && rowIndex < dataTable.getRowCount()) {
            dataTable.scrollRectToVisible(dataTable.getCellRect(rowIndex, 0, true));
        }
    }

    public void highlightPrimaryIndex(int rowIndex) {
        this.highlightedPrimaryIndex = rowIndex;
        primaryIndexTable.repaint();

        if (rowIndex >= 0 && rowIndex < primaryIndexTable.getRowCount()) {
            primaryIndexTable.scrollRectToVisible(primaryIndexTable.getCellRect(rowIndex, 0, true));
        }
    }

    public void highlightSecondaryIndex(int rowIndex) {
        this.highlightedSecondaryIndex = rowIndex;
        secondaryIndexTable.repaint();

        if (rowIndex >= 0 && rowIndex < secondaryIndexTable.getRowCount()) {
            secondaryIndexTable.scrollRectToVisible(secondaryIndexTable.getCellRect(rowIndex, 0, true));
        }
    }

    public void highlightFoundRecord(int rowIndex) {
        this.highlightedDataRecord = -1;
        this.highlightedPrimaryIndex = -1;
        this.highlightedSecondaryIndex = -1;
        this.foundRecordIndex = rowIndex;

        dataTable.repaint();
        primaryIndexTable.repaint();
        secondaryIndexTable.repaint();

        if (rowIndex >= 0 && rowIndex < dataTable.getRowCount()) {
            dataTable.scrollRectToVisible(dataTable.getCellRect(rowIndex, 0, true));
        }
    }

    public void clearHighlights() {
        this.highlightedDataRecord = -1;
        this.highlightedPrimaryIndex = -1;
        this.highlightedSecondaryIndex = -1;
        this.foundRecordIndex = -1;

        dataTable.repaint();
        primaryIndexTable.repaint();
        secondaryIndexTable.repaint();
    }

    public boolean isVisualizationEnabled() {
        return chkVisualizeProcess.isSelected();
    }

    public void setInputValues(String recordCount, String blockSize, String dataRecordSize, String indexRecordSize) {
        txtRecordCount.setText(recordCount);
        txtBlockSizeBytes.setText(blockSize);
        txtDataRecordSizeBytes.setText(dataRecordSize);
        txtIndexRecordSizeBytes.setText(indexRecordSize);
    }

    public void showWindow() {
        setVisible(true);
    }

    /**
     * Method to show a dialog with the details of the multilevel structure.
     */
    public void showLevelDetailsDialog(String levelDetails) {
        JDialog dialog = new JDialog(this, "Detalles de Estructura Multinivel", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea(levelDetails);
        textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBackground(new Color(248, 248, 248));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = createStyledButton("Cerrar", new Color(70, 130, 180));
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}