package view.trees;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;

public class DigitalTreeView extends JFrame {

    public interface TreeVisualizer {
        void paintTreeVisualization(Graphics2D g2d, int width, int height);
    }

    private final JButton btnInsertWord;
    private final JButton btnSearchWord;
    private final JButton btnDeleteWord;
    private final JButton btnClearTrie;
    private final JButton btnBack;
    private final JTextField txtWordToInsert;
    private final JTextField txtWordToSearch;
    private final JTextField txtWordToDelete;
    private final JLabel lblResult;
    private final JTextArea txtWordList;
    private final TreeVisualizationPanel treeVisualizationPanel;


    private TreeVisualizer treeVisualizer;


    private Map<String, Object> trieData;

    public DigitalTreeView() {
        setTitle("Árbol Digital");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180)); // Azul acero
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Algoritmo de Árbol Digital");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSubtitle = new JLabel("Visualización y operaciones del árbol");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitle.setForeground(new Color(240, 248, 255));
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        titlePanel.add(lblTitle, BorderLayout.CENTER);
        titlePanel.add(lblSubtitle, BorderLayout.SOUTH);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplitPane.setResizeWeight(0.7);

        treeVisualizationPanel = new TreeVisualizationPanel();
        JScrollPane treeScrollPane = new JScrollPane(treeVisualizationPanel);
        treeScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                "Visualización del Árbol Digital"));

        txtWordList = new JTextArea();
        txtWordList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtWordList.setEditable(false);

        JScrollPane wordListScrollPane = new JScrollPane(txtWordList);
        wordListScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 1),
                "Claves en el Arbol Digital"));

        centerSplitPane.setLeftComponent(treeScrollPane);
        centerSplitPane.setRightComponent(wordListScrollPane);
        centerSplitPane.setDividerLocation(700);

        mainPanel.add(centerSplitPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        controlPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        controlPanel.setBackground(new Color(240, 248, 255));

        JLabel lblInsert = new JLabel("Insertar clave:");
        txtWordToInsert = new JTextField(15);
        btnInsertWord = createButton("Insertar", new Color(46, 204, 113));

        controlPanel.add(lblInsert);
        controlPanel.add(txtWordToInsert);
        controlPanel.add(btnInsertWord);

        JLabel lblSearch = new JLabel("Buscar clave:");
        txtWordToSearch = new JTextField(15);
        btnSearchWord = createButton("Buscar", new Color(52, 152, 219));

        controlPanel.add(lblSearch);
        controlPanel.add(txtWordToSearch);
        controlPanel.add(btnSearchWord);

        JLabel lblDelete = new JLabel("Eliminar clave:");
        txtWordToDelete = new JTextField(15);
        btnDeleteWord = createButton("Eliminar", new Color(231, 76, 60));

        controlPanel.add(lblDelete);
        controlPanel.add(txtWordToDelete);
        controlPanel.add(btnDeleteWord);

        btnClearTrie = createButton("Limpiar Árbol", new Color(155, 89, 182));
        lblResult = new JLabel("");
        lblResult.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack = createButton("Volver", new Color(231, 76, 60));

        controlPanel.add(btnClearTrie);
        controlPanel.add(lblResult);
        controlPanel.add(btnBack);

        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        this.trieData = null;
    }

    private class TreeVisualizationPanel extends JPanel {
        public TreeVisualizationPanel() {
            setPreferredSize(new Dimension(700, 500));
            setBackground(Color.WHITE);
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
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                String message = "No hay árbol para visualizar";
                g2d.drawString(message, getWidth() / 2 - 100, getHeight() / 2);
            }
        }
    }

    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return button;
    }

    public void addInsertWordListener(ActionListener listener) {
        btnInsertWord.addActionListener(listener);
    }

    public void addSearchWordListener(ActionListener listener) {
        btnSearchWord.addActionListener(listener);
    }

    public void addDeleteWordListener(ActionListener listener) {
        btnDeleteWord.addActionListener(listener);
    }

    public void addClearTrieListener(ActionListener listener) {
        btnClearTrie.addActionListener(listener);
    }

    public void addBackListener(ActionListener listener) {
        btnBack.addActionListener(listener);
    }

    public String getWordToInsert() {
        return txtWordToInsert.getText().trim().toLowerCase();
    }

    public String getWordToSearch() {
        return txtWordToSearch.getText().trim().toLowerCase();
    }

    public String getWordToDelete() {
        return txtWordToDelete.getText().trim().toLowerCase();
    }

    public void setResultMessage(String message, boolean isSuccess) {
        lblResult.setText(message);
        lblResult.setForeground(isSuccess ? new Color(46, 125, 50) : new Color(198, 40, 40));
    }

    public void updateWordList(String[] words) {
        StringBuilder sb = new StringBuilder();
        sb.append("Claves en el Trie:\n\n");

        if (words.length == 0) {
            sb.append("(No hay claves almacenadas)");
        } else {
            for (String word : words) {
                sb.append("• ").append(word).append("\n");
            }
        }

        txtWordList.setText(sb.toString());
    }

    public void clearInputFields() {
        txtWordToInsert.setText("");
        txtWordToDelete.setText("");
        lblResult.setText("");
    }

    public void setTreeVisualizer(TreeVisualizer visualizer) {
        this.treeVisualizer = visualizer;
    }

    public void updateTrieVisualization(Map<String, Object> trieData) {
        this.trieData = trieData;
        treeVisualizationPanel.repaint();
    }

    public Map<String, Object> getTrieData() {
        return this.trieData;
    }

    public void showWindow() {
        setVisible(true);
    }
}