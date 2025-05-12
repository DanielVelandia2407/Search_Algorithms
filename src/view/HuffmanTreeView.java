package view;

import model.HuffmanTreeModel.TreeNode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class HuffmanTreeView extends JFrame {

    // Interfaz para comunicar con el controlador para dibujar el árbol
    public interface TreeVisualizer {
        void paintTreeVisualization(Graphics2D g2d, int width, int height);
    }

    // Componentes de la UI
    private JTextArea inputTextArea;
    private JTextArea compressedTextArea;
    private JTextArea decompressedTextArea;
    private JButton buildTreeButton;
    private JButton compressButton;
    private JButton decompressButton;
    private JButton clearButton;
    private JButton backButton;
    private JButton saveImageButton; // Nuevo: botón para guardar imagen
    private JLabel resultMessageLabel;
    private JTable codesTable;
    private DefaultTableModel tableModel;
    private TreeVisualizationPanel treeVisualizationPanel;
    private JLabel statisticsLabel;

    // Visualizador del árbol (será el controlador)
    private TreeVisualizer treeVisualizer;

    public HuffmanTreeView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Árbol de Huffman - Visualizador");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // Panel superior para controles
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // Panel central para visualización del árbol y códigos
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplitPane.setResizeWeight(0.7);

        // Panel de visualización del árbol
        treeVisualizationPanel = new TreeVisualizationPanel();
        centerSplitPane.setLeftComponent(new JScrollPane(treeVisualizationPanel));

        // Panel para los códigos de Huffman
        JPanel codesPanel = createCodesPanel();
        centerSplitPane.setRightComponent(codesPanel);

        mainPanel.add(centerSplitPane, BorderLayout.CENTER);

        // Panel inferior para visualización de texto comprimido/descomprimido
        JPanel textPanel = createTextPanel();
        mainPanel.add(textPanel, BorderLayout.SOUTH);

        // Panel para estadísticas
        JPanel statsPanel = createStatsPanel();
        mainPanel.add(statsPanel, BorderLayout.EAST);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Panel para botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        buildTreeButton = new JButton("Construir Árbol");
        buildTreeButton.setToolTipText("Construir el árbol de Huffman a partir del texto de entrada");
        buttonPanel.add(buildTreeButton);

        compressButton = new JButton("Comprimir");
        compressButton.setToolTipText("Comprimir el texto usando el árbol de Huffman");
        buttonPanel.add(compressButton);

        decompressButton = new JButton("Descomprimir");
        decompressButton.setToolTipText("Descomprimir el texto usando el árbol de Huffman");
        buttonPanel.add(decompressButton);

        clearButton = new JButton("Limpiar");
        clearButton.setToolTipText("Limpiar todo");
        buttonPanel.add(clearButton);

        backButton = new JButton("Atrás");
        backButton.setToolTipText("Volver a la pantalla anterior");
        buttonPanel.add(backButton);

        // Nuevo: Botón para guardar árbol como imagen
        saveImageButton = new JButton("Guardar Árbol");
        saveImageButton.setToolTipText("Guardar el árbol como imagen PNG");
        buttonPanel.add(saveImageButton);

        panel.add(buttonPanel, BorderLayout.WEST);

        // Panel para mensajes de resultado
        resultMessageLabel = new JLabel(" ");
        resultMessageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(resultMessageLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCodesPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Códigos de Huffman"));

        // Crear tabla para mostrar los códigos
        String[] columnNames = {"Carácter", "Frecuencia", "Código"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        codesTable = new JTable(tableModel);
        codesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        codesTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        codesTable.getColumnModel().getColumn(2).setPreferredWidth(300);

        // Centrar contenido de las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        codesTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        codesTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(codesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTextPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Panel para texto de entrada
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Texto Original"));

        inputTextArea = new JTextArea(5, 20);
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        // Panel para texto comprimido
        JPanel compressedPanel = new JPanel(new BorderLayout(5, 5));
        compressedPanel.setBorder(BorderFactory.createTitledBorder("Texto Comprimido"));

        compressedTextArea = new JTextArea(5, 20);
        compressedTextArea.setLineWrap(true);
        compressedTextArea.setWrapStyleWord(true);
        compressedTextArea.setEditable(false);
        JScrollPane compressedScrollPane = new JScrollPane(compressedTextArea);
        compressedPanel.add(compressedScrollPane, BorderLayout.CENTER);

        // Panel para texto descomprimido
        JPanel decompressedPanel = new JPanel(new BorderLayout(5, 5));
        decompressedPanel.setBorder(BorderFactory.createTitledBorder("Texto Descomprimido"));

        decompressedTextArea = new JTextArea(5, 20);
        decompressedTextArea.setLineWrap(true);
        decompressedTextArea.setWrapStyleWord(true);
        decompressedTextArea.setEditable(false);
        JScrollPane decompressedScrollPane = new JScrollPane(decompressedTextArea);
        decompressedPanel.add(decompressedScrollPane, BorderLayout.CENTER);

        // Añadir los tres paneles
        panel.add(inputPanel);
        panel.add(compressedPanel);
        panel.add(decompressedPanel);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Estadísticas"));

        statisticsLabel = new JLabel("<html><b>Nodos:</b> 0<br><b>Altura:</b> 0<br><b>Tasa de compresión:</b> 0%</html>");
        statisticsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(statisticsLabel, BorderLayout.NORTH);

        return panel;
    }

    // Clase para el panel de visualización del árbol
    private class TreeVisualizationPanel extends JPanel {
        public TreeVisualizationPanel() {
            setPreferredSize(new Dimension(600, 400));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createTitledBorder("Visualización del Árbol"));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (treeVisualizer != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                treeVisualizer.paintTreeVisualization(g2d, getWidth(), getHeight());
            }
        }
    }

    // Métodos para añadir listeners a los botones
    public void addBuildTreeListener(ActionListener listener) {
        buildTreeButton.addActionListener(listener);
    }

    public void addCompressListener(ActionListener listener) {
        compressButton.addActionListener(listener);
    }

    public void addDecompressListener(ActionListener listener) {
        decompressButton.addActionListener(listener);
    }

    public void addClearListener(ActionListener listener) {
        clearButton.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }

    // Nuevo: método para agregar listener al botón guardar
    public void addSaveImageListener(ActionListener listener) {
        saveImageButton.addActionListener(listener);
    }

    // Métodos para obtener datos de los campos
    public String getInputText() {
        return inputTextArea.getText();
    }

    public String getCompressedText() {
        return compressedTextArea.getText();
    }

    // Métodos para establecer datos en los campos
    public void setCompressedText(String text) {
        compressedTextArea.setText(text);
    }

    public void setDecompressedText(String text) {
        decompressedTextArea.setText(text);
    }

    public void setResultMessage(String message, boolean success) {
        resultMessageLabel.setText(message);
        resultMessageLabel.setForeground(success ? new Color(0, 128, 0) : Color.RED);
    }

    // Método para actualizar la tabla de códigos
    public void updateCodesTable(Map<Character, String> encodingMap, Map<Character, Integer> frequencyMap) {
        // Limpiar la tabla
        tableModel.setRowCount(0);

        // Añadir filas para cada caracter
        for (Map.Entry<Character, String> entry : encodingMap.entrySet()) {
            Character character = entry.getKey();
            String code = entry.getValue();
            Integer frequency = frequencyMap.get(character);

            // Formatear el caracter para visualización (especialmente para espacios y caracteres invisibles)
            String displayChar = formatCharacter(character);

            tableModel.addRow(new Object[]{displayChar, frequency, code});
        }
    }

    // Formatear caracteres especiales para mejor visualización
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

    // Método para actualizar estadísticas
    public void updateStatistics(int nodes, int height, double compressionRate) {
        statisticsLabel.setText(String.format(
                "<html><b>Nodos:</b> %d<br><b>Altura:</b> %d<br><b>Tasa de compresión:</b> %.2f%%</html>",
                nodes, height, compressionRate));
    }

    // Método para limpiar campos
    public void clearFields() {
        inputTextArea.setText("");
        compressedTextArea.setText("");
        decompressedTextArea.setText("");
        tableModel.setRowCount(0);
        resultMessageLabel.setText(" ");
        updateStatistics(0, 0, 0);
        treeVisualizationPanel.repaint();
    }

    // Método para establecer el visualizador del árbol
    public void setTreeVisualizer(TreeVisualizer visualizer) {
        this.treeVisualizer = visualizer;
    }

    // Método para obtener el panel de visualización
    public JPanel getTreeVisualizationPanel() {
        return treeVisualizationPanel;
    }

    // Método para mostrar la ventana
    public void showWindow() {
        setVisible(true);
    }

    // Método para resaltar una fila en la tabla
    public void highlightRow(int row) {
        codesTable.setRowSelectionInterval(row, row);
        codesTable.scrollRectToVisible(codesTable.getCellRect(row, 0, true));
    }

    // Métodos auxiliares para acceder a la tabla
    public int getTableRowCount() {
        return tableModel.getRowCount();
    }

    public void setInputText(String text) {
        inputTextArea.setText(text);
    }

    public Object getTableValueAt(int row, int col) {
        return tableModel.getValueAt(row, col);
    }
}