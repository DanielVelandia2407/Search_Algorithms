package view.trees;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vista específica para residuo múltiple.
 */
public class MultipleResiduoView extends JFrame {

    public interface TreeVisualizer {
        void paintTreeVisualization(Graphics2D g2d, int width, int height);
    }

    private final JButton btnInsert;
    private final JButton btnSearch;
    private final JButton btnDelete;
    private final JButton btnClear;
    private final JButton btnBack;
    private final JTextField txtInsert;
    private final JTextField txtSearch;
    private final JTextField txtDelete;
    private final JLabel lblResult;
    private final JTextArea txtLog;
    private final TreeVisualizationPanel treeVisualizationPanel;

    private TreeVisualizer treeVisualizer;
    private final int tamGrupo;

    // Paleta de colores personalizada (misma que DigitalTreeView)
    private static final Color DARK_NAVY = new Color(0, 1, 13);      // #0001DD
    private static final Color WARM_BROWN = new Color(115, 73, 22);   // #734916
    private static final Color LIGHT_BROWN = new Color(166, 133, 93); // #A6855D
    private static final Color CREAM = new Color(242, 202, 153);      // #F2CA99
    private static final Color VERY_DARK = new Color(13, 13, 13);     // #0D0D0D
    private static final Color SOFT_WHITE = new Color(248, 248, 248); // Blanco suave para contraste

    public MultipleResiduoView(int tamGrupo) {
        this.tamGrupo = tamGrupo;
        setTitle("Residuos Múltiples");
        setSize(1000, 800);
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

        JLabel lblTitle = new JLabel("Algoritmo de Residuos Múltiples");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(SOFT_WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSubtitle = new JLabel("Grupo de tamaño: " + tamGrupo + " - Visualización y operaciones");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitle.setForeground(CREAM);
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        titlePanel.add(lblTitle, BorderLayout.CENTER);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Split pane central
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplitPane.setResizeWeight(0.7);
        centerSplitPane.setBackground(CREAM);

        // Panel de visualización del árbol
        treeVisualizationPanel = new TreeVisualizationPanel();
        JScrollPane treeScrollPane = new JScrollPane(treeVisualizationPanel);
        treeScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(WARM_BROWN, 2),
                "Visualización del Arbol"));
        treeScrollPane.getViewport().setBackground(SOFT_WHITE);

        // Panel de log de operaciones
        txtLog = new JTextArea();
        txtLog.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtLog.setEditable(false);
        txtLog.setBackground(SOFT_WHITE);
        txtLog.setForeground(VERY_DARK);
        txtLog.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BROWN, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        JScrollPane logScrollPane = new JScrollPane(txtLog);
        logScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(WARM_BROWN, 2),
                "Log de Operaciones"));
        logScrollPane.getViewport().setBackground(SOFT_WHITE);

        centerSplitPane.setLeftComponent(treeScrollPane);
        centerSplitPane.setRightComponent(logScrollPane);
        centerSplitPane.setDividerLocation(700);

        mainPanel.add(centerSplitPane, BorderLayout.CENTER);

        // Panel de controles
        JPanel controlPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        controlPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        controlPanel.setBackground(CREAM);

        // Crear labels con estilo
        JLabel lblInsert = createStyledLabel("Clave a insertar:");
        txtInsert = createStyledTextField();
        btnInsert = createStyledButton("Insertar", LIGHT_BROWN);

        controlPanel.add(lblInsert);
        controlPanel.add(txtInsert);
        controlPanel.add(btnInsert);

        JLabel lblSearch = createStyledLabel("Clave a buscar:");
        txtSearch = createStyledTextField();
        btnSearch = createStyledButton("Buscar", DARK_NAVY);

        controlPanel.add(lblSearch);
        controlPanel.add(txtSearch);
        controlPanel.add(btnSearch);

        JLabel lblDelete = createStyledLabel("Clave a eliminar:");
        txtDelete = createStyledTextField();
        btnDelete = createStyledButton("Eliminar", new Color(180, 67, 67)); // Rojo más suave

        controlPanel.add(lblDelete);
        controlPanel.add(txtDelete);
        controlPanel.add(btnDelete);

        btnClear = createStyledButton("Limpiar Trie", WARM_BROWN);
        lblResult = new JLabel("");
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResult.setForeground(VERY_DARK);
        btnBack = createStyledButton("Volver", VERY_DARK);

        controlPanel.add(btnClear);
        controlPanel.add(lblResult);
        controlPanel.add(btnBack);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);
    }

    private class TreeVisualizationPanel extends JPanel {
        public TreeVisualizationPanel() {
            setPreferredSize(new Dimension(800, 500));
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

    // Método para crear labels estilizados
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(VERY_DARK);
        return label;
    }

    // Método para crear campos de texto estilizados
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(SOFT_WHITE);
        textField.setForeground(VERY_DARK);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_BROWN, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return textField;
    }

    // Método para crear botones estilizados
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
    public void addInsertListener(ActionListener listener) {
        btnInsert.addActionListener(listener);
    }

    public void addSearchListener(ActionListener listener) {
        btnSearch.addActionListener(listener);
    }

    public void addDeleteListener(ActionListener listener) {
        btnDelete.addActionListener(listener);
    }

    public void addClearListener(ActionListener listener) {
        btnClear.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    // Métodos para obtener datos de los campos
    public String getInsertKey() {
        return txtInsert.getText().trim();
    }

    public String getSearchKey() {
        return txtSearch.getText().trim();
    }

    public String getDeleteKey() {
        return txtDelete.getText().trim();
    }

    // Método para establecer mensaje de resultado
    public void setResult(String message, boolean isSuccess) {
        lblResult.setText(message);
        lblResult.setForeground(isSuccess ? new Color(76, 175, 80) : new Color(183, 28, 28));
    }

    // Método para actualizar el log
    public void setLogLines(String[] lines) {
        StringBuilder sb = new StringBuilder();
        sb.append("Historial de Operaciones:\n\n");
        for (String line : lines) {
            sb.append(line).append("\n");
        }
        txtLog.setText(sb.toString());
    }

    // Método para limpiar campos de entrada
    public void clearInputFields() {
        txtInsert.setText("");
        txtSearch.setText("");
        txtDelete.setText("");
        lblResult.setText("");
    }

    // Método para establecer el visualizador del árbol
    public void setVisualizer(TreeVisualizer visualizer) {
        this.treeVisualizer = visualizer;
    }

    // Método para refrescar la visualización del árbol
    public void refreshTree() {
        treeVisualizationPanel.repaint();
    }

    // Método para obtener el tamaño del grupo
    public int getTamGrupo() {
        return tamGrupo;
    }

    // Método para mostrar la ventana
    public void showWindow() {
        setVisible(true);
    }
}