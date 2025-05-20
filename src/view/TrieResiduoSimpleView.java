package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;

public class TrieResiduoSimpleView extends JFrame {

    // Interfaz para que el controlador pinte el árbol
    public interface TreeVisualizer {
        void paintTreeVisualization(Graphics2D g2d, int width, int height);
    }

    private TreeVisualizer treeVisualizer;

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

    private final TreePanel treePanel;

    public TrieResiduoSimpleView() {
        setTitle("Visualizador de Árboles");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel superior con título
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(70, 130, 180));
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel lblTitle = new JLabel("Árbol de Búsqueda");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lblTitle, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Panel central dividido: dibujo y log
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.7);

        treePanel = new TreePanel();
        JScrollPane treeScroll = new JScrollPane(treePanel);
        treeScroll.setBorder(BorderFactory.createTitledBorder("Visualización"));
        split.setLeftComponent(treeScroll);

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(txtLog);
        logScroll.setBorder(BorderFactory.createTitledBorder("Log de Operaciones"));
        split.setRightComponent(logScroll);

        add(split, BorderLayout.CENTER);

        // Panel inferior con controles
        JPanel controls = new JPanel(new GridLayout(4, 3, 10, 10));
        controls.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Insertar
        controls.add(new JLabel("Clave a insertar:"));
        txtInsert = new JTextField();
        controls.add(txtInsert);
        btnInsert = createButton("Insertar", new Color(46, 204, 113));
        controls.add(btnInsert);

        // Buscar
        controls.add(new JLabel("Clave a buscar:"));
        txtSearch = new JTextField();
        controls.add(txtSearch);
        btnSearch = createButton("Buscar", new Color(52, 152, 219));
        controls.add(btnSearch);

        // Eliminar
        controls.add(new JLabel("Clave a eliminar:"));
        txtDelete = new JTextField();
        controls.add(txtDelete);
        btnDelete = createButton("Eliminar", new Color(231, 76, 60));
        controls.add(btnDelete);

        // Botones extra
        btnClear = createButton("Limpiar Árbol", new Color(155, 89, 182));
        controls.add(btnClear);
        lblResult = new JLabel(" ");
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 14));
        controls.add(lblResult);
        btnBack = createButton("Volver", new Color(241, 196, 15));
        controls.add(btnBack);

        add(controls, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setFocusPainted(false);
        return b;
    }

    // Listeners
    public void addInsertWordListener(ActionListener l) { btnInsert.addActionListener(l); }
    public void addSearchWordListener(ActionListener l) { btnSearch.addActionListener(l); }
    public void addDeleteWordListener(ActionListener l) { btnDelete.addActionListener(l); }
    public void addClearTrieListener(ActionListener l) { btnClear.addActionListener(l); }
    public void addBackListener(ActionListener l) { btnBack.addActionListener(l); }

    // Obtener texto
    public String getWordToInsert() { return txtInsert.getText().trim(); }
    public String getWordToSearch() { return txtSearch.getText().trim(); }
    public String getWordToDelete() { return txtDelete.getText().trim(); }

    // Mensajes
    public void setResultMessage(String msg, boolean success) {
        lblResult.setText(msg);
        lblResult.setForeground(success ? new Color(46, 125, 50) : new Color(198, 40, 40));
    }

    // Actualizar log
    public void updateWordList(String[] lines) {
        txtLog.setText("");
        for (String l : lines) {
            txtLog.append(l + "\n");
        }
    }

    // Para la visualización gráfica
    public void setTreeVisualizer(TreeVisualizer tv) {
        this.treeVisualizer = tv;
    }
    public void updateTrieVisualization(Map<String,Object> data) {
        treePanel.repaint();
    }

    public void showWindow() { setVisible(true); }

    private class TreePanel extends JPanel {
        TreePanel() { setPreferredSize(new Dimension(800, 500)); setBackground(Color.WHITE); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (treeVisualizer != null) {
                treeVisualizer.paintTreeVisualization((Graphics2D)g, getWidth(), getHeight());
            }
        }
    }
}
