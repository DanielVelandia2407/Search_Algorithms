package view.trees;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Vista específica para residuo múltiple.
 */
public class MultipleResiduoView extends JFrame {
    private final JTextField txtInsert;
    private final JTextField txtSearch;
    private final JTextField txtDelete;
    private final JButton btnInsert;
    private final JButton btnSearch;
    private final JButton btnDelete;
    private final JButton btnClear;
    private final JButton btnBack;
    private final JTextArea txtLog;
    private final TreePanel treePanel;
    private final JLabel lblResult;

    public interface TreeVisualizer {
        void paintTreeVisualization(Graphics2D g2d, int width, int height);
    }
    private TreeVisualizer visualizer;

    public MultipleResiduoView(int tamGrupo) {
        setTitle("Trie Residuos Múltiples (grupo=" + tamGrupo + ")");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        // Header
        JLabel header = new JLabel("Trie de Residuos Múltiples", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setBorder(new EmptyBorder(15,0,15,0));
        add(header, BorderLayout.NORTH);

        // Split: dibujo / log
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.7);
        treePanel = new TreePanel();
        JScrollPane panTree = new JScrollPane(treePanel);
        panTree.setBorder(BorderFactory.createTitledBorder("Visualización"));
        split.setLeftComponent(panTree);

        txtLog = new JTextArea();
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtLog.setEditable(false);
        JScrollPane panLog = new JScrollPane(txtLog);
        panLog.setBorder(BorderFactory.createTitledBorder("Log de Operaciones"));
        split.setRightComponent(panLog);

        add(split, BorderLayout.CENTER);

        // Controls
        JPanel ctrl = new JPanel(new GridLayout(4,3,10,10));
        ctrl.setBorder(new EmptyBorder(10,10,10,10));

        ctrl.add(new JLabel("Clave a insertar:"));
        txtInsert = new JTextField();
        ctrl.add(txtInsert);
        btnInsert = new JButton("Insertar");
        ctrl.add(btnInsert);

        ctrl.add(new JLabel("Clave a buscar:"));
        txtSearch = new JTextField();
        ctrl.add(txtSearch);
        btnSearch = new JButton("Buscar");
        ctrl.add(btnSearch);

        ctrl.add(new JLabel("Clave a eliminar:"));
        txtDelete = new JTextField();
        ctrl.add(txtDelete);
        btnDelete = new JButton("Eliminar");
        ctrl.add(btnDelete);

        btnClear = new JButton("Limpiar Trie");
        ctrl.add(btnClear);
        lblResult = new JLabel(" ");
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ctrl.add(lblResult);
        btnBack = new JButton("Volver");
        ctrl.add(btnBack);

        add(ctrl, BorderLayout.SOUTH);

        styleButtons();
    }

    private void styleButtons() {
        Color green = new Color(46,204,113), blue = new Color(52,152,219),
              red = new Color(231,76,60), purple = new Color(155,89,182);
        btnInsert.setBackground(green); btnInsert.setForeground(Color.WHITE);
        btnSearch.setBackground(blue);  btnSearch.setForeground(Color.WHITE);
        btnDelete.setBackground(red);   btnDelete.setForeground(Color.WHITE);
        btnClear.setBackground(purple); btnClear.setForeground(Color.WHITE);
        btnBack.setBackground(Color.GRAY); btnBack.setForeground(Color.WHITE);
    }

    // Listeners
    public void addInsertListener(ActionListener l) { btnInsert.addActionListener(l); }
    public void addSearchListener(ActionListener l) { btnSearch.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { btnDelete.addActionListener(l); }
    public void addClearListener(ActionListener l) { btnClear.addActionListener(l); }
    public void addBackListener(ActionListener l) { btnBack.addActionListener(l); }

    // Inputs
    public String getInsertKey() { return txtInsert.getText().trim(); }
    public String getSearchKey() { return txtSearch.getText().trim(); }
    public String getDeleteKey() { return txtDelete.getText().trim(); }

    // Result label
    public void setResult(String msg, boolean ok) {
        lblResult.setText(msg);
        lblResult.setForeground(ok ? new Color(46,125,50) : new Color(198,40,40));
    }

    // Log
    public void setLogLines(String[] lines) {
        txtLog.setText("");
        for (String l : lines) txtLog.append(l + "\n");
    }

    // Visualization hook
    public void setVisualizer(TreeVisualizer v) { this.visualizer = v; }
    public void refreshTree() { treePanel.repaint(); }

    private class TreePanel extends JPanel {
        TreePanel() { setBackground(Color.WHITE); setPreferredSize(new Dimension(800,500)); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (visualizer != null) {
                visualizer.paintTreeVisualization((Graphics2D)g, getWidth(), getHeight());
            }
        }
    }
}
