package view.indexes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ImprovedMultilevelIndexView extends JFrame {

    private JButton btnSearch;
    private JButton btnConfigure;
    private JButton btnDeleteRecord;
    private JButton btnInsertRecord;
    private JButton btnBack;

    private JTable dataTable;
    private List<JTable> indexTables; // Lista de tablas de índices para múltiples niveles
    private List<DefaultTableModel> indexTableModels;

    private DefaultTableModel dataTableModel;

    private JTextField txtSearchValue;
    private JTextField txtRecordCount;
    private JTextField txtBlockSize;
    private JTextField txtDataRecordSize;
    private JTextField txtIndexRecordSize;
    private JTextField txtIndexPerBlock;
    private JTextField txtDataPerBlock;
    private JTextField txtInsertId;
    private JTextField txtInsertName;
    private JTextField txtInsertAge;
    private JTextField txtDeleteId;

    private JCheckBox chkVisualizeProcess;
    private JLabel lblResult;
    private JLabel lblSearchProgress;

    // Variables para highlighting simplificado
    private int highlightedDataRecord = -1;
    private List<Integer> highlightedIndexRecords; // Para múltiples niveles de índices
    private int foundRecordIndex = -1;

    public ImprovedMultilevelIndexView() {
        setTitle("Índices Multinivel - Vista Simplificada");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        getContentPane().setBackground(new Color(245, 245, 245));

        // Inicializar listas
        indexTables = new ArrayList<>();
        indexTableModels = new ArrayList<>();
        highlightedIndexRecords = new ArrayList<>();

        createTopPanel();
        createCenterPanel();
        createBottomPanel();
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(70, 130, 180));
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Estructura de Índices Multinivel", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        topPanel.add(lblTitle, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
    }

    private void createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel principal con scroll para las estructuras
        JPanel structuresPanel = new JPanel();
        structuresPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));
        structuresPanel.setBackground(new Color(245, 245, 245));

        JScrollPane structuresScrollPane = new JScrollPane(structuresPanel);
        structuresScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        structuresScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        structuresScrollPane.setPreferredSize(new Dimension(900, 450));
        structuresScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        // Inicialmente mostrar solo mensaje de "Configurar primero"
        JLabel lblConfigFirst = new JLabel("<html><center>Presione 'Generar Estructura'<br>para crear las tablas de índices</center></html>");
        lblConfigFirst.setFont(new Font("Arial", Font.ITALIC, 14));
        lblConfigFirst.setForeground(Color.GRAY);
        lblConfigFirst.setHorizontalAlignment(SwingConstants.CENTER);
        structuresPanel.add(lblConfigFirst);

        centerPanel.add(structuresScrollPane, BorderLayout.CENTER);

        // Panel de controles en la parte derecha
        JPanel controlPanel = createControlPanel();
        centerPanel.add(controlPanel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void createDataPanel(JPanel parent) {
        // Este método ya no se necesita porque creamos los datos directamente en recreateIndexPanels
    }

    public void recreateIndexPanels(int numLevels) {
        JScrollPane scrollPane = (JScrollPane) ((JPanel) getContentPane().getComponent(1)).getComponent(0);
        JPanel structuresPanel = (JPanel) scrollPane.getViewport().getView();

        // Limpiar paneles existentes
        structuresPanel.removeAll();
        indexTables.clear();
        indexTableModels.clear();
        highlightedIndexRecords.clear();

        System.out.println("=== RECREANDO VISTA PARA " + numLevels + " NIVELES ===");

        // Crear paneles de índices (de mayor a menor nivel)
        for (int level = numLevels; level >= 1; level--) {
            String title = "Estructura Nivel " + level;
            String subtitle;

            if (level == numLevels && numLevels > 1) {
                subtitle = "Índice Raíz"; // El nivel más alto
            } else if (level == 1) {
                subtitle = "Índice Primario"; // El nivel más bajo (apunta a datos)
            } else {
                subtitle = "Índice Nivel " + level; // Niveles intermedios
            }

            JPanel indexPanel = createLevelPanel(title, subtitle);

            DefaultTableModel indexModel = new DefaultTableModel(
                    new Object[]{"Ind", "Apunta"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            JTable indexTable = createSimpleTable(indexModel, level - 1); // 0-based para highlighting
            indexTable.setPreferredSize(new Dimension(200, 300));
            JScrollPane indexScrollPane = new JScrollPane(indexTable);
            indexScrollPane.setPreferredSize(new Dimension(200, 300));
            indexPanel.add(indexScrollPane, BorderLayout.CENTER);

            indexTables.add(indexTable);
            indexTableModels.add(indexModel);
            highlightedIndexRecords.add(-1);

            structuresPanel.add(indexPanel);

            System.out.println("✓ Creada tabla para " + title + " (" + subtitle + ")");
        }

        // Crear panel de datos al final
        JPanel dataPanel = createLevelPanel("FICHERO", "Datos");
        dataTableModel = new DefaultTableModel(
                new Object[]{"PK", "Nombre", "Edad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dataTable = createSimpleTable(dataTableModel, -1); // -1 para datos
        dataTable.setPreferredSize(new Dimension(200, 300));
        JScrollPane dataScrollPane = new JScrollPane(dataTable);
        dataScrollPane.setPreferredSize(new Dimension(200, 300));
        dataPanel.add(dataScrollPane, BorderLayout.CENTER);

        structuresPanel.add(dataPanel);
        System.out.println("✓ Creada tabla de DATOS");

        System.out.println("=== TOTAL: " + (numLevels + 1) + " TABLAS CREADAS ===");
        System.out.println("  - " + numLevels + " tablas de índices");
        System.out.println("  - 1 tabla de datos");

        structuresPanel.revalidate();
        structuresPanel.repaint();

        // Forzar actualización del scroll
        SwingUtilities.invokeLater(() -> {
            scrollPane.getHorizontalScrollBar().setValue(0);
            scrollPane.revalidate();
            scrollPane.repaint();
        });
    }

    private JPanel createLevelPanel(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(220, 380)); // Tamaño fijo para consistencia

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(245, 245, 245));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Arial", Font.ITALIC, 11));
        lblSubtitle.setForeground(Color.GRAY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);

        panel.add(titlePanel, BorderLayout.NORTH);
        return panel;
    }

    private JTable createSimpleTable(DefaultTableModel model, int levelIndex) {
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(200, 220, 240));
        table.getTableHeader().setForeground(Color.BLACK);

        table.setGridColor(new Color(180, 180, 180));
        table.setShowGrid(true);

        // Renderer para highlighting
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                // Data table highlighting
                if (table == dataTable) {
                    if (row == foundRecordIndex && foundRecordIndex != -1) {
                        c.setBackground(new Color(144, 238, 144)); // Verde claro
                        c.setForeground(Color.BLACK);
                    } else if (row == highlightedDataRecord && highlightedDataRecord != -1) {
                        c.setBackground(new Color(255, 255, 150)); // Amarillo
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                // Index tables highlighting
                else {
                    // Encontrar qué tabla de índice es esta
                    int tableIndex = -1;
                    for (int i = 0; i < indexTables.size(); i++) {
                        if (table == indexTables.get(i)) {
                            tableIndex = i;
                            break;
                        }
                    }

                    if (tableIndex != -1 && tableIndex < highlightedIndexRecords.size() &&
                            row == highlightedIndexRecords.get(tableIndex) && highlightedIndexRecords.get(tableIndex) != -1) {
                        Color[] colors = {
                                new Color(173, 216, 230), // Azul claro
                                new Color(255, 182, 193), // Rosa claro
                                new Color(221, 160, 221), // Violeta claro
                                new Color(255, 218, 185), // Naranja claro
                                new Color(152, 251, 152)  // Verde menta
                        };
                        c.setBackground(colors[tableIndex % colors.length]);
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }

                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        return table;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(new Color(245, 245, 245));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        controlPanel.setPreferredSize(new Dimension(300, 0));

        // Configuración
        JPanel configSection = createSectionPanel("Configuración");

        JPanel recordCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recordCountPanel.setBackground(new Color(245, 245, 245));
        JLabel lblRecordCount = new JLabel("Número de registros:");
        txtRecordCount = new JTextField(8);
        txtRecordCount.setText("15");
        recordCountPanel.add(lblRecordCount);
        recordCountPanel.add(txtRecordCount);

        JPanel blockSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        blockSizePanel.setBackground(new Color(245, 245, 245));
        JLabel lblBlockSize = new JLabel("Tamaño bloque (bytes):");
        txtBlockSize = new JTextField(8);
        txtBlockSize.setText("1024");
        blockSizePanel.add(lblBlockSize);
        blockSizePanel.add(txtBlockSize);

        JPanel dataRecordSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dataRecordSizePanel.setBackground(new Color(245, 245, 245));
        JLabel lblDataRecordSize = new JLabel("Tamaño reg. dato (bytes):");
        txtDataRecordSize = new JTextField(8);
        txtDataRecordSize.setText("64");
        dataRecordSizePanel.add(lblDataRecordSize);
        dataRecordSizePanel.add(txtDataRecordSize);

        JPanel indexRecordSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        indexRecordSizePanel.setBackground(new Color(245, 245, 245));
        JLabel lblIndexRecordSize = new JLabel("Tamaño reg. índice (bytes):");
        txtIndexRecordSize = new JTextField(8);
        txtIndexRecordSize.setText("12");
        indexRecordSizePanel.add(lblIndexRecordSize);
        indexRecordSizePanel.add(txtIndexRecordSize);

        JPanel indexPerBlockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        indexPerBlockPanel.setBackground(new Color(245, 245, 245));
        JLabel lblIndexPerBlock = new JLabel("Registros índice/bloque:");
        txtIndexPerBlock = new JTextField(8);
        txtIndexPerBlock.setText("85");
        indexPerBlockPanel.add(lblIndexPerBlock);
        indexPerBlockPanel.add(txtIndexPerBlock);

        JPanel dataPerBlockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dataPerBlockPanel.setBackground(new Color(245, 245, 245));
        JLabel lblDataPerBlock = new JLabel("Registros dato/bloque:");
        txtDataPerBlock = new JTextField(8);
        txtDataPerBlock.setText("16");
        dataPerBlockPanel.add(lblDataPerBlock);
        dataPerBlockPanel.add(txtDataPerBlock);

        btnConfigure = createStyledButton("Generar Estructura", new Color(46, 125, 50));

        configSection.add(recordCountPanel);
        configSection.add(blockSizePanel);
        configSection.add(dataRecordSizePanel);
        configSection.add(indexRecordSizePanel);
        configSection.add(indexPerBlockPanel);
        configSection.add(dataPerBlockPanel);
        configSection.add(btnConfigure);

        controlPanel.add(configSection);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Inserción
        JPanel insertSection = createSectionPanel("Insertar Registro");

        JPanel insertIdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        insertIdPanel.setBackground(new Color(245, 245, 245));
        insertIdPanel.add(new JLabel("ID:"));
        txtInsertId = new JTextField(8);
        insertIdPanel.add(txtInsertId);

        JPanel insertNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        insertNamePanel.setBackground(new Color(245, 245, 245));
        insertNamePanel.add(new JLabel("Nombre:"));
        txtInsertName = new JTextField(10);
        insertNamePanel.add(txtInsertName);

        JPanel insertAgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        insertAgePanel.setBackground(new Color(245, 245, 245));
        insertAgePanel.add(new JLabel("Edad:"));
        txtInsertAge = new JTextField(8);
        insertAgePanel.add(txtInsertAge);

        btnInsertRecord = createStyledButton("Insertar", new Color(33, 150, 243));

        insertSection.add(insertIdPanel);
        insertSection.add(insertNamePanel);
        insertSection.add(insertAgePanel);
        insertSection.add(btnInsertRecord);

        controlPanel.add(insertSection);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Búsqueda
        JPanel searchSection = createSectionPanel("Búsqueda");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.add(new JLabel("ID a buscar:"));
        txtSearchValue = new JTextField(8);
        searchPanel.add(txtSearchValue);

        chkVisualizeProcess = new JCheckBox("Visualizar proceso");
        chkVisualizeProcess.setBackground(new Color(245, 245, 245));
        chkVisualizeProcess.setSelected(true);

        btnSearch = createStyledButton("Buscar", new Color(33, 150, 243));

        lblSearchProgress = new JLabel("");
        lblSearchProgress.setFont(new Font("Arial", Font.ITALIC, 10));

        searchSection.add(searchPanel);
        searchSection.add(chkVisualizeProcess);
        searchSection.add(btnSearch);
        searchSection.add(lblSearchProgress);

        controlPanel.add(searchSection);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Eliminación
        JPanel deleteSection = createSectionPanel("Eliminar Registro");

        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deletePanel.setBackground(new Color(245, 245, 245));
        deletePanel.add(new JLabel("ID a eliminar:"));
        txtDeleteId = new JTextField(8);
        deletePanel.add(txtDeleteId);

        btnDeleteRecord = createStyledButton("Eliminar", new Color(244, 67, 54));

        deleteSection.add(deletePanel);
        deleteSection.add(btnDeleteRecord);

        controlPanel.add(deleteSection);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Resultado
        lblResult = new JLabel("");
        lblResult.setFont(new Font("Arial", Font.BOLD, 11));
        lblResult.setBorder(new EmptyBorder(10, 0, 10, 0));

        controlPanel.add(lblResult);
        controlPanel.add(Box.createVerticalGlue());

        // Botón volver
        btnBack = createStyledButton("Volver", new Color(158, 158, 158));
        controlPanel.add(btnBack);

        return controlPanel;
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
                title,
                0, 0,
                new Font("Arial", Font.BOLD, 11),
                Color.BLACK
        ));
        return panel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(220, 220, 220));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("Vista Simplificada de Índices Multinivel - Propósito Educativo");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(100, 100, 100));

        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Métodos para agregar listeners
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

    // Getters para valores de entrada
    public String getSearchValue() {
        return txtSearchValue.getText().trim();
    }

    public String getRecordCount() {
        return txtRecordCount.getText().trim();
    }

    public String getBlockSize() {
        return txtBlockSize.getText().trim();
    }

    public String getDataRecordSize() {
        return txtDataRecordSize.getText().trim();
    }

    public String getIndexRecordSize() {
        return txtIndexRecordSize.getText().trim();
    }

    public String getIndexPerBlock() {
        return txtIndexPerBlock.getText().trim();
    }

    public String getDataPerBlock() {
        return txtDataPerBlock.getText().trim();
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

    public boolean isVisualizationEnabled() {
        return chkVisualizeProcess.isSelected();
    }

    // Métodos para configurar datos en las tablas
    public void setDataTableData(Object[][] data) {
        dataTableModel.setRowCount(0);
        for (Object[] row : data) {
            dataTableModel.addRow(row);
        }
    }

    public void setIndexTableData(int level, Object[][] data) {
        if (level < indexTableModels.size()) {
            DefaultTableModel model = indexTableModels.get(level);
            model.setRowCount(0);
            for (Object[] row : data) {
                model.addRow(row);
            }
        }
    }

    // Métodos para highlighting durante búsqueda
    public void highlightDataRecord(int rowIndex) {
        this.highlightedDataRecord = rowIndex;
        this.foundRecordIndex = -1;
        dataTable.repaint();

        if (rowIndex >= 0 && rowIndex < dataTable.getRowCount()) {
            dataTable.scrollRectToVisible(dataTable.getCellRect(rowIndex, 0, true));
        }
    }

    public void highlightIndexRecord(int level, int rowIndex) {
        if (level < highlightedIndexRecords.size()) {
            highlightedIndexRecords.set(level, rowIndex);
            if (level < indexTables.size()) {
                indexTables.get(level).repaint();

                if (rowIndex >= 0 && rowIndex < indexTables.get(level).getRowCount()) {
                    indexTables.get(level).scrollRectToVisible(
                            indexTables.get(level).getCellRect(rowIndex, 0, true));
                }
            }
        }
    }

    public void highlightFoundRecord(int rowIndex) {
        this.highlightedDataRecord = -1;
        for (int i = 0; i < highlightedIndexRecords.size(); i++) {
            highlightedIndexRecords.set(i, -1);
        }
        this.foundRecordIndex = rowIndex;

        dataTable.repaint();
        for (JTable table : indexTables) {
            table.repaint();
        }

        if (rowIndex >= 0 && rowIndex < dataTable.getRowCount()) {
            dataTable.scrollRectToVisible(dataTable.getCellRect(rowIndex, 0, true));
        }
    }

    public void clearHighlights() {
        this.highlightedDataRecord = -1;
        for (int i = 0; i < highlightedIndexRecords.size(); i++) {
            highlightedIndexRecords.set(i, -1);
        }
        this.foundRecordIndex = -1;

        dataTable.repaint();
        for (JTable table : indexTables) {
            table.repaint();
        }
    }

    // Métodos para mostrar mensajes
    public void setResultMessage(String message, boolean isSuccess) {
        lblResult.setText("<html><div style='width: 250px;'>" + message + "</div></html>");
        lblResult.setForeground(isSuccess ? new Color(46, 125, 50) : new Color(198, 40, 40));
    }

    public void setSearchProgress(String message) {
        lblSearchProgress.setText("<html><div style='width: 250px;'>" + message + "</div></html>");
    }

    public void showWindow() {
        setVisible(true);
    }
}