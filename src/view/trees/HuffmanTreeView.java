package view.trees;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;

public class HuffmanTreeView extends JFrame {

    public interface TreeVisualizer {
        void paintTreeVisualization(Graphics2D g2d, int width, int height);
    }

    private final JButton btnBuildTree;
    private final JButton btnCompress;
    private final JButton btnDecompress;
    private final JButton btnClear;
    private final JButton btnBack;
    private final JButton btnSaveImage;
    private JTextArea txtInputText;
    private JTextArea txtCompressedText;
    private JTextArea txtDecompressedText;
    private final JLabel lblResult;
    private JLabel lblStatistics;
    private JTable tblCodes;
    private DefaultTableModel tableModel;
    private final TreeVisualizationPanel treeVisualizationPanel;

    private TreeVisualizer treeVisualizer;

    // Paleta de colores personalizada (misma que DigitalTreeView)
    private static final Color DARK_NAVY = new Color(0, 1, 13);      // #0001DD
    private static final Color WARM_BROWN = new Color(115, 73, 22);   // #734916
    private static final Color LIGHT_BROWN = new Color(166, 133, 93); // #A6855D
    private static final Color CREAM = new Color(242, 202, 153);      // #F2CA99
    private static final Color VERY_DARK = new Color(13, 13, 13);     // #0D0D0D
    private static final Color SOFT_WHITE = new Color(248, 248, 248); // Blanco suave para contraste

    public HuffmanTreeView() {
        setTitle("Árbol de Huffman");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(CREAM);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // Title panel con paleta de colores
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(DARK_NAVY);
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Algoritmo de Árbol de Huffman");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(SOFT_WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSubtitle = new JLabel("Compresión y visualización del árbol");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitle.setForeground(CREAM);
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        titlePanel.add(lblTitle, BorderLayout.CENTER);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Split pane central principal
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setResizeWeight(0.6);
        mainSplitPane.setBackground(CREAM);

        // Panel superior con visualización y códigos
        JSplitPane topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        topSplitPane.setResizeWeight(0.6);
        topSplitPane.setBackground(CREAM);

        // Panel de visualización del árbol
        treeVisualizationPanel = new TreeVisualizationPanel();
        JScrollPane treeScrollPane = new JScrollPane(treeVisualizationPanel);
        treeScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(WARM_BROWN, 2),
                "Visualización del Árbol de Huffman"));
        treeScrollPane.getViewport().setBackground(SOFT_WHITE);

        // Panel para códigos y estadísticas
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(CREAM);

        // Panel de códigos
        JPanel codesPanel = createCodesPanel();
        rightPanel.add(codesPanel, BorderLayout.CENTER);

        // Panel de estadísticas
        JPanel statsPanel = createStatsPanel();
        rightPanel.add(statsPanel, BorderLayout.SOUTH);

        topSplitPane.setLeftComponent(treeScrollPane);
        topSplitPane.setRightComponent(rightPanel);

        // Panel inferior para textos
        JPanel textPanel = createTextPanel();

        mainSplitPane.setTopComponent(topSplitPane);
        mainSplitPane.setBottomComponent(textPanel);

        mainPanel.add(mainSplitPane, BorderLayout.CENTER);

        // Panel de controles
        JPanel controlPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        controlPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        controlPanel.setBackground(CREAM);

        // Primera fila de botones
        btnBuildTree = createStyledButton("Construir Árbol", LIGHT_BROWN);
        btnCompress = createStyledButton("Comprimir", DARK_NAVY);
        btnDecompress = createStyledButton("Descomprimir", WARM_BROWN);
        btnSaveImage = createStyledButton("Guardar Árbol", new Color(76, 175, 80));

        controlPanel.add(btnBuildTree);
        controlPanel.add(btnCompress);
        controlPanel.add(btnDecompress);
        controlPanel.add(btnSaveImage);

        // Segunda fila
        btnClear = createStyledButton("Limpiar", new Color(180, 67, 67));
        lblResult = new JLabel("");
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResult.setForeground(VERY_DARK);
        lblResult.setHorizontalAlignment(SwingConstants.CENTER);
        btnBack = createStyledButton("Volver", VERY_DARK);

        // Espacio vacío para balance
        JLabel emptyLabel = new JLabel("");

        controlPanel.add(btnClear);
        controlPanel.add(lblResult);
        controlPanel.add(emptyLabel);
        controlPanel.add(btnBack);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        // Inicializar tabla
        String[] columnNames = {"Carácter", "Frecuencia", "Código"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCodes = new JTable(tableModel);
        setupTable();
    }

    private class TreeVisualizationPanel extends JPanel {
        public TreeVisualizationPanel() {
            setPreferredSize(new Dimension(600, 400));
            setBackground(SOFT_WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (treeVisualizer != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                treeVisualizer.paintTreeVisualization(g2d, getWidth(), getHeight());
            } else {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(LIGHT_BROWN);
                g2d.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                String message = "No hay árbol para visualizar";
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(message)) / 2;
                int y = getHeight() / 2;
                g2d.drawString(message, x, y);
            }
        }
    }

    private JPanel createCodesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(WARM_BROWN, 2),
                "Códigos de Huffman"));
        panel.setBackground(CREAM);

        JScrollPane scrollPane = new JScrollPane(tblCodes);
        scrollPane.getViewport().setBackground(SOFT_WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setupTable() {
        tblCodes.setBackground(SOFT_WHITE);
        tblCodes.setForeground(VERY_DARK);
        tblCodes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblCodes.getTableHeader().setBackground(LIGHT_BROWN);
        tblCodes.getTableHeader().setForeground(SOFT_WHITE);
        tblCodes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        tblCodes.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblCodes.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblCodes.getColumnModel().getColumn(2).setPreferredWidth(300);

        // Centrar contenido de las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblCodes.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblCodes.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(WARM_BROWN, 2),
                "Estadísticas"));
        panel.setBackground(CREAM);

        lblStatistics = new JLabel("<html><b>Nodos:</b> 0<br><b>Altura:</b> 0<br><b>Tasa de compresión:</b> 0%</html>");
        lblStatistics.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblStatistics.setForeground(VERY_DARK);
        lblStatistics.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(lblStatistics, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTextPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));
        panel.setBackground(CREAM);

        // Panel para texto de entrada
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(WARM_BROWN, 2),
                "Texto Original"));
        inputPanel.setBackground(CREAM);

        txtInputText = createStyledTextArea(5, 20);
        txtInputText.setEditable(true);
        JScrollPane inputScrollPane = new JScrollPane(txtInputText);
        inputScrollPane.getViewport().setBackground(SOFT_WHITE);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        // Panel para texto comprimido
        JPanel compressedPanel = new JPanel(new BorderLayout(5, 5));
        compressedPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(WARM_BROWN, 2),
                "Texto Comprimido"));
        compressedPanel.setBackground(CREAM);

        txtCompressedText = createStyledTextArea(5, 20);
        JScrollPane compressedScrollPane = new JScrollPane(txtCompressedText);
        compressedScrollPane.getViewport().setBackground(SOFT_WHITE);
        compressedPanel.add(compressedScrollPane, BorderLayout.CENTER);

        // Panel para texto descomprimido
        JPanel decompressedPanel = new JPanel(new BorderLayout(5, 5));
        decompressedPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(WARM_BROWN, 2),
                "Texto Descomprimido"));
        decompressedPanel.setBackground(CREAM);

        txtDecompressedText = createStyledTextArea(5, 20);
        JScrollPane decompressedScrollPane = new JScrollPane(txtDecompressedText);
        decompressedScrollPane.getViewport().setBackground(SOFT_WHITE);
        decompressedPanel.add(decompressedScrollPane, BorderLayout.CENTER);

        panel.add(inputPanel);
        panel.add(compressedPanel);
        panel.add(decompressedPanel);

        return panel;
    }

    // Método para crear áreas de texto estilizadas
    private JTextArea createStyledTextArea(int rows, int cols) {
        JTextArea textArea = new JTextArea(rows, cols);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(SOFT_WHITE);
        textArea.setForeground(VERY_DARK);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BROWN, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return textArea;
    }

    // Método para crear botones estilizados (mismo patrón que DigitalTreeView)
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(SOFT_WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

    // Métodos para añadir listeners
    public void addBuildTreeListener(ActionListener listener) {
        btnBuildTree.addActionListener(listener);
    }

    public void addCompressListener(ActionListener listener) {
        btnCompress.addActionListener(listener);
    }

    public void addDecompressListener(ActionListener listener) {
        btnDecompress.addActionListener(listener);
    }

    public void addClearListener(ActionListener listener) {
        btnClear.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    public void addSaveImageListener(ActionListener listener) {
        btnSaveImage.addActionListener(listener);
    }

    // Métodos para obtener datos
    public String getInputText() {
        return txtInputText.getText();
    }

    public String getCompressedText() {
        return txtCompressedText.getText();
    }

    // Métodos para establecer datos
    public void setInputText(String text) {
        txtInputText.setText(text);
    }

    public void setCompressedText(String text) {
        txtCompressedText.setText(text);
    }

    public void setDecompressedText(String text) {
        txtDecompressedText.setText(text);
    }

    public void setResultMessage(String message, boolean isSuccess) {
        lblResult.setText(message);
        lblResult.setForeground(isSuccess ? new Color(76, 175, 80) : new Color(183, 28, 28));
    }

    // Método para actualizar la tabla de códigos
    public void updateCodesTable(Map<Character, String> encodingMap, Map<Character, Integer> frequencyMap) {
        tableModel.setRowCount(0);

        for (Map.Entry<Character, String> entry : encodingMap.entrySet()) {
            Character character = entry.getKey();
            String code = entry.getValue();
            Integer frequency = frequencyMap.get(character);

            String displayChar = formatCharacter(character);
            tableModel.addRow(new Object[]{displayChar, frequency, code});
        }
    }

    private String formatCharacter(Character c) {
        if (c == ' ') {
            return "[espacio]";
        } else if (c == '\n') {
            return "[salto de línea]";
        } else if (c == '\t') {
            return "[tabulador]";
        } else if (c == '\r') {
            return "[retorno]";
        } else {
            return c.toString();
        }
    }

    public void updateStatistics(int nodes, int height, double compressionRate) {
        lblStatistics.setText(String.format(
                "<html><b>Nodos:</b> %d<br><b>Altura:</b> %d<br><b>Tasa de compresión:</b> %.2f%%</html>",
                nodes, height, compressionRate));
    }

    public void clearFields() {
        txtInputText.setText("");
        txtCompressedText.setText("");
        txtDecompressedText.setText("");
        tableModel.setRowCount(0);
        lblResult.setText("");
        updateStatistics(0, 0, 0);
        treeVisualizationPanel.repaint();
    }

    public void setTreeVisualizer(TreeVisualizer visualizer) {
        this.treeVisualizer = visualizer;
    }

    public JPanel getTreeVisualizationPanel() {
        return treeVisualizationPanel;
    }

    public void highlightRow(int row) {
        tblCodes.setRowSelectionInterval(row, row);
        tblCodes.scrollRectToVisible(tblCodes.getCellRect(row, 0, true));
    }

    public int getTableRowCount() {
        return tableModel.getRowCount();
    }

    public Object getTableValueAt(int row, int col) {
        return tableModel.getValueAt(row, col);
    }

    public void showWindow() {
        setVisible(true);
    }
}