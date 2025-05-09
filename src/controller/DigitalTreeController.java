package controller;

import model.DigitalTreeModel;
import view.DigitalTreeView;
import view.TreeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

public class DigitalTreeController implements DigitalTreeView.TreeVisualizer {
    private final DigitalTreeModel model;
    private final DigitalTreeView view;
    private TreeController parentController;
    private TreeView parentView;

    // Constantes para la visualización
    private static final int NODE_SIZE = 40;
    private static final int VERTICAL_SPACING = 70;
    private static final Color NODE_COLOR = new Color(255, 255, 255);
    private static final Color NODE_BORDER_COLOR = new Color(70, 130, 180);
    private static final Color LINE_COLOR = new Color(100, 100, 100);
    private static final Font NODE_FONT = new Font("Segoe UI", Font.BOLD, 16);

    // Palabra actualmente resaltada
    private String highlightedWord = null;

    // Mapa para almacenar posiciones de nodos
    private Map<DigitalTreeModel.BinaryNode, Point> nodePositions = new HashMap<>();

    public DigitalTreeController(DigitalTreeModel model, DigitalTreeView view) {
        this.model = model;
        this.view = view;

        // Configurar la vista para usar este controlador como visualizador
        this.view.setTreeVisualizer(this);

        // Agregar listeners a los botones
        this.view.addInsertWordListener(this::insertWord);
        this.view.addSearchWordListener(this::searchWord);
        this.view.addDeleteWordListener(this::deleteWord);
        this.view.addClearTrieListener(this::clearTrie);
        this.view.addBackListener(this::goBack);

        // Actualizar visualización inicial
        updateVisualization();
    }

    // Constructor adicional para el controlador padre
    public DigitalTreeController(DigitalTreeModel model, DigitalTreeView view, TreeController parentController, TreeView parentView) {
        this(model, view);
        this.parentController = parentController;
        this.parentView = parentView;
    }

    // Setter para el controlador padre
    public void setParentController(TreeController parentController, TreeView parentView) {
        this.parentController = parentController;
        this.parentView = parentView;
    }

    private void insertWord(ActionEvent e) {
        String word = view.getWordToInsert();

        if (word.isEmpty()) {
            view.setResultMessage("Por favor, ingrese una palabra", false);
            return;
        }

        boolean isNewWord = model.insert(word);

        if (isNewWord) {
            view.setResultMessage("Palabra \"" + word + "\" insertada correctamente", true);
            // Destacar la palabra recién insertada
            highlightedWord = word;
        } else {
            view.setResultMessage("La palabra \"" + word + "\" ya existe en el árbol", false);
        }

        view.clearInputFields();
        updateVisualization();
    }

    private void searchWord(ActionEvent e) {
        String word = view.getWordToSearch();

        if (word.isEmpty()) {
            view.setResultMessage("Por favor, ingrese una palabra para buscar", false);
            return;
        }

        boolean found = model.search(word);

        if (found) {
            view.setResultMessage("Palabra \"" + word + "\" encontrada en el árbol", true);
            // Destacar la palabra encontrada
            highlightedWord = word;
        } else {
            view.setResultMessage("Palabra \"" + word + "\" no encontrada", false);
            highlightedWord = null;
        }

        updateVisualization();
    }

    private void deleteWord(ActionEvent e) {
        String word = view.getWordToDelete();

        if (word.isEmpty()) {
            view.setResultMessage("Por favor, ingrese una palabra para eliminar", false);
            return;
        }

        boolean deleted = model.delete(word);

        if (deleted) {
            view.setResultMessage("Palabra \"" + word + "\" eliminada correctamente", true);
        } else {
            view.setResultMessage("Palabra \"" + word + "\" no encontrada", false);
        }

        view.clearInputFields();
        highlightedWord = null;
        updateVisualization();
    }

    private void clearTrie(ActionEvent e) {
        if (model.getWordCount() == 0) {
            view.setResultMessage("El árbol ya está vacío", false);
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                view,
                "¿Está seguro de que desea eliminar todas las palabras del árbol?",
                "Confirmar limpieza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            model.clear();
            view.setResultMessage("El árbol ha sido limpiado correctamente", true);
            view.clearInputFields();
            highlightedWord = null;
            updateVisualization();
        }
    }

    private void goBack(ActionEvent e) {
        view.dispose();
        if (parentView != null) {
            SwingUtilities.invokeLater(() -> {
                parentView.setVisible(true);
            });
        }
    }

    private void updateVisualization() {
        view.updateTrieVisualization(model.getTrieStructure());
        view.updateWordList(model.getAllWords());
        // Limpiar posiciones de nodos cuando se actualiza la visualización
        nodePositions.clear();
    }

    public void initView() {
        SwingUtilities.invokeLater(() -> {
            view.showWindow();
        });
    }

    @Override
    public void paintTreeVisualization(Graphics2D g2d, int width, int height) {
        // Obtener el árbol binario del modelo
        DigitalTreeModel.BinaryNode root = model.getBinaryRoot();

        // Si no hay árbol binario, mostrar mensaje
        if (root == null) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            String message = "Árbol vacío";
            g2d.drawString(message, width / 2 - 40, height / 2);
            return;
        }

        // Mostrar la codificación en la parte superior (ya está mostrándose)
        // displayCodification(g2d, width);

        // Limpiar posiciones de nodos cuando se dibuja
        nodePositions.clear();

        // Calcular posiciones de los nodos
        calculateNodePositions(root, width / 2, 120, width / 4);

        // Dibujar el árbol
        drawBinaryTree(g2d, root);
    }

    // Método para mostrar la codificación en la parte superior (si necesitas añadirla)
    private void displayCodification(Graphics2D g2d, int width) {
        Map<String, Object> trieData = model.getTrieStructure();
        if (trieData.containsKey("wordCodes")) {
            @SuppressWarnings("unchecked")
            Map<String, String> wordCodes = (Map<String, String>) trieData.get("wordCodes");

            // Configurar fuente y color
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2d.setColor(Color.BLACK);

            // Posición inicial
            int startX = 20;
            int startY = 20;

            // Título
            g2d.drawString("Codificación:", startX, startY);
            startY += 15;

            // Mostrar cada código
            for (Map.Entry<String, String> entry : wordCodes.entrySet()) {
                String label = entry.getKey() + " = " + entry.getValue();
                g2d.drawString(label, startX, startY);
                startY += 15;
            }
        }
    }

    // Método para calcular posiciones de los nodos
    private void calculateNodePositions(DigitalTreeModel.BinaryNode node, int x, int y, int offset) {
        if (node == null) return;

        // Almacenar la posición del nodo en el mapa
        nodePositions.put(node, new Point(x, y));

        if (node.left != null) {
            calculateNodePositions(node.left, x - offset, y + VERTICAL_SPACING, offset / 2);
        }

        if (node.right != null) {
            calculateNodePositions(node.right, x + offset, y + VERTICAL_SPACING, offset / 2);
        }
    }

    // Método para dibujar el árbol binario
    private void drawBinaryTree(Graphics2D g2d, DigitalTreeModel.BinaryNode node) {
        if (node == null) return;

        // Obtener posición del nodo actual
        Point nodePoint = nodePositions.get(node);
        if (nodePoint == null) return;

        int nodeX = nodePoint.x;
        int nodeY = nodePoint.y;

        // Dibujar conexiones primero (líneas a los hijos)
        if (node.left != null) {
            // Obtener posición del hijo izquierdo
            Point leftPoint = nodePositions.get(node.left);
            if (leftPoint != null) {
                int leftX = leftPoint.x;
                int leftY = leftPoint.y;

                g2d.setColor(LINE_COLOR);
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.draw(new Line2D.Double(nodeX, nodeY + NODE_SIZE / 2, leftX, leftY - NODE_SIZE / 2));

                // Dibujar el valor de bit (0) en la línea
                int textX = (nodeX + leftX) / 2 - 10;
                int textY = (nodeY + leftY) / 2;
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.setColor(Color.BLACK);
                g2d.drawString("0", textX, textY);
            }
        }

        if (node.right != null) {
            // Obtener posición del hijo derecho
            Point rightPoint = nodePositions.get(node.right);
            if (rightPoint != null) {
                int rightX = rightPoint.x;
                int rightY = rightPoint.y;

                g2d.setColor(LINE_COLOR);
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.draw(new Line2D.Double(nodeX, nodeY + NODE_SIZE / 2, rightX, rightY - NODE_SIZE / 2));

                // Dibujar el valor de bit (1) en la línea
                int textX = (nodeX + rightX) / 2 + 5;
                int textY = (nodeY + rightY) / 2;
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.setColor(Color.BLACK);
                g2d.drawString("1", textX, textY);
            }
        }

        // Dibujar el nodo
        g2d.setColor(NODE_COLOR);
        g2d.fillOval(nodeX - NODE_SIZE / 2, nodeY - NODE_SIZE / 2, NODE_SIZE, NODE_SIZE);
        g2d.setColor(NODE_BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawOval(nodeX - NODE_SIZE / 2, nodeY - NODE_SIZE / 2, NODE_SIZE, NODE_SIZE);

        // Dibujar etiqueta del nodo
        g2d.setFont(NODE_FONT);
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        String label = node.label;
        if (label != null && !label.isEmpty()) {
            g2d.drawString(label, nodeX - fm.stringWidth(label) / 2, nodeY + fm.getAscent() / 2 - 2);
        }

        // Recursivamente dibujar subárboles
        drawBinaryTree(g2d, node.left);
        drawBinaryTree(g2d, node.right);
    }

    // Main para pruebas
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            DigitalTreeModel model = new DigitalTreeModel();
            DigitalTreeView view = new DigitalTreeView();
            DigitalTreeController controller = new DigitalTreeController(model, view);

            controller.updateVisualization();
            controller.initView();
        });
    }
}