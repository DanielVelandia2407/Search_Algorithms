package controller.trees;

import controller.menu.TreeController;
import model.trees.MultipleResidueTreeModel;
import model.trees.MultipleResidueTreeModel.TreeNode;
import view.trees.MultipleResidueTreeView;
import view.trees.TreeView;
import view.trees.MultipleResidueTreeView.TreeVisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class MultipleResidueTreeController implements TreeVisualizer {
    private final MultipleResidueTreeModel model;
    private final MultipleResidueTreeView view;
    private TreeController parentController;
    private TreeView parentView;

    // Variables para la visualización del árbol
    private Integer lastAccessedKey = null;
    private TreeNode lastAccessedNode = null;

    public MultipleResidueTreeController(MultipleResidueTreeModel model, MultipleResidueTreeView view) {
        this.model = model;
        this.view = view;

        // Registrar este controlador como el visualizador del árbol para la vista
        this.view.setTreeVisualizer(this);

        // Añadir listeners a los botones
        this.view.addInsertListener(this::insertEntry);
        this.view.addSearchListener(this::searchEntry);
        this.view.addDeleteListener(this::deleteEntry);
        this.view.addClearListener(this::clearTree);
        this.view.addBackListener(this::goBack);

        // Inicializar la visualización
        updateVisualization();
    }

    // Constructor adicional que recibe la referencia al controlador padre
    public MultipleResidueTreeController(MultipleResidueTreeModel model, MultipleResidueTreeView view,
                                         TreeController parentController, TreeView parentView) {
        this(model, view); // Llama al constructor principal
        this.parentController = parentController;
        this.parentView = parentView;
    }

    // Método setter para el controlador padre (opcional)
    public void setParentController(TreeController parentController, TreeView parentView) {
        this.parentController = parentController;
        this.parentView = parentView;
    }

    // Método para insertar un elemento en el árbol
    private void insertEntry(ActionEvent e) {
        try {
            String keyStr = view.getKey();
            String value = view.getValue();

            if (keyStr.isEmpty() || value.isEmpty()) {
                view.setResultMessage("Por favor, ingrese clave y valor", false);
                return;
            }

            int key = Integer.parseInt(keyStr);

            boolean isNew = model.put(key, value);
            lastAccessedKey = key;
            lastAccessedNode = model.findNode(model.getRoot(), key);

            if (isNew) {
                view.setResultMessage("Elemento insertado correctamente en el árbol", true);
            } else {
                view.setResultMessage("Elemento actualizado en el árbol", true);
            }

            view.clearInputFields();
            updateVisualization();

            // Resaltar fila donde se insertó
            highlightNodeInTable(key);

        } catch (NumberFormatException ex) {
            view.setResultMessage("La clave debe ser un número entero", false);
        }
    }

    // Método para buscar un elemento en el árbol
    private void searchEntry(ActionEvent e) {
        try {
            String keyStr = view.getSearchKey();

            if (keyStr.isEmpty()) {
                view.setResultMessage("Por favor, ingrese una clave para buscar", false);
                return;
            }

            int key = Integer.parseInt(keyStr);
            String value = model.get(key);
            lastAccessedKey = key;
            lastAccessedNode = model.findNode(model.getRoot(), key);

            if (value != null) {
                view.setResultMessage("Valor encontrado: " + value, true);
                highlightNodeInTable(key);
            } else {
                view.setResultMessage("No existe un valor con esa clave", false);
                lastAccessedNode = null;
            }

            updateVisualization();

        } catch (NumberFormatException ex) {
            view.setResultMessage("La clave debe ser un número entero", false);
        }
    }

    // Método para eliminar un elemento del árbol
    private void deleteEntry(ActionEvent e) {
        try {
            String keyStr = view.getDeleteKey();

            if (keyStr.isEmpty()) {
                view.setResultMessage("Por favor, ingrese una clave para eliminar", false);
                return;
            }

            int key = Integer.parseInt(keyStr);
            boolean deleted = model.remove(key);
            lastAccessedKey = null;
            lastAccessedNode = null;

            if (deleted) {
                view.setResultMessage("Elemento eliminado correctamente del árbol", true);
            } else {
                view.setResultMessage("No se encontró un elemento con esa clave", false);
            }

            view.clearInputFields();
            updateVisualization();

        } catch (NumberFormatException ex) {
            view.setResultMessage("La clave debe ser un número entero", false);
        }
    }

    // Método para limpiar el árbol
    private void clearTree(ActionEvent e) {
        if (model.size() == 0) {
            view.setResultMessage("El árbol ya está vacío", false);
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                view,
                "¿Está seguro de que desea eliminar todos los elementos del árbol?",
                "Confirmar limpieza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            model.clear();
            lastAccessedKey = null;
            lastAccessedNode = null;
            view.setResultMessage("El árbol ha sido limpiado correctamente", true);
            view.clearInputFields();
            updateVisualization();
        }
    }

    // Método para volver a la pantalla anterior
    private void goBack(ActionEvent e) {
        view.dispose();

        // Verificar si existe una vista padre y mostrarla
        if (parentView != null) {
            SwingUtilities.invokeLater(() -> {
                parentView.setVisible(true);
            });
        }
    }

    // Método para resaltar un nodo en la tabla
    private void highlightNodeInTable(int key) {
        for (int i = 0; i < view.getTableRowCount(); i++) {
            if (Integer.parseInt(view.getTableValueAt(i, 0).toString()) == key) {
                view.highlightRow(i);
                break;
            }
        }
    }

    // Método para actualizar la visualización del árbol
    public void updateVisualization() {
        // Actualizar la tabla con todos los nodos
        List<TreeNode> nodes = model.getAllNodes();
        view.updateTreeTable(nodes, model.getDivisors());

        // Actualizar estadísticas
        view.updateStatistics(model.size(), model.getHeight());

        // Solicitar al panel de visualización que se repinte
        JPanel visualizationPanel = view.getTreeVisualizationPanel();
        visualizationPanel.repaint();
    }

    // Implementación del método de la interfaz TreeVisualizer
    @Override
    public void paintTreeVisualization(Graphics2D g2d, int width, int height) {
        // Limpiar el panel
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Si no hay nodos para visualizar, mostrar mensaje
        if (model.getRoot() == null) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            g2d.drawString("Inserte valores para ver el árbol", width / 2 - 100, height / 2);
            return;
        }

        // Dibujar el árbol
        drawTree(g2d, width, height);

        // Si se ha accedido a un nodo específico, mostrar la ruta de acceso
        if (lastAccessedKey != null && lastAccessedNode != null) {
            drawAccessPath(g2d, width, height);
        }
    }

    // Método para dibujar el árbol
    private void drawTree(Graphics2D g2d, int width, int height) {
        // Configuración para dibujar el árbol
        int nodeWidth = 40;
        int nodeHeight = 40;
        int verticalSpace = 80;
        int initialY = 50;

        // Calcular la altura del árbol para determinar la distribución
        int treeHeight = model.getHeight();

        // Lista de todos los nodos
        List<TreeNode> allNodes = model.getAllNodes();

        // Ordenar por nivel para dibujar correctamente
        allNodes.sort((n1, n2) -> Integer.compare(n1.level, n2.level));

        // Mapa para rastrear la posición X de cada nodo basado en su nivel
        int[] levelWidth = new int[treeHeight + 1];
        int[] levelCount = new int[treeHeight + 1];

        // Inicializar los arrays
        for (int i = 0; i <= treeHeight; i++) {
            levelWidth[i] = width / (int) Math.pow(2, i);
            levelCount[i] = 0;
        }

        // Dibujar cada nodo
        for (TreeNode node : allNodes) {
            int level = node.level;
            int x = levelWidth[level - 1] * levelCount[level - 1] + levelWidth[level - 1] / 2;
            int y = initialY + (level - 1) * verticalSpace;

            // Incrementar el contador de nodos en este nivel
            levelCount[level - 1]++;

            // Dibujar el nodo
            drawNode(g2d, node, x, y, nodeWidth, nodeHeight);

            // Dibujar la conexión con el padre si no es la raíz
            if (level > 1) {
                TreeNode parent = findParent(node);
                if (parent != null) {
                    int parentLevel = parent.level;
                    int parentIndex = findNodeIndex(allNodes, parent);
                    int parentX = levelWidth[parentLevel - 1] * parentIndex + levelWidth[parentLevel - 1] / 2;
                    int parentY = initialY + (parentLevel - 1) * verticalSpace;

                    // Dibujar la línea
                    g2d.setColor(Color.BLACK);
                    g2d.drawLine(x, y, parentX, parentY + nodeHeight);
                }
            }
        }
    }

    // Método para encontrar el índice de un nodo en la lista
    private int findNodeIndex(List<TreeNode> nodes, TreeNode target) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).key == target.key) {
                return i;
            }
        }
        return -1;
    }

    // Método para encontrar el padre de un nodo
    private TreeNode findParent(TreeNode child) {
        if (child == model.getRoot() || child == null) {
            return null;
        }

        // Buscar el nodo padre recorriendo desde la raíz
        return findParentRecursive(model.getRoot(), child);
    }

    private TreeNode findParentRecursive(TreeNode current, TreeNode child) {
        if (current == null) {
            return null;
        }

        // Comprobar si este nodo es el padre
        if ((current.left != null && current.left.key == child.key) ||
                (current.right != null && current.right.key == child.key)) {
            return current;
        }

        // Buscar en los hijos
        TreeNode foundInLeft = findParentRecursive(current.left, child);
        if (foundInLeft != null) {
            return foundInLeft;
        }

        return findParentRecursive(current.right, child);
    }

    // Método para dibujar un nodo individual
    private void drawNode(Graphics2D g2d, TreeNode node, int x, int y, int width, int height) {
        // Definir el color del nodo según si es el último accedido
        boolean isHighlighted = (lastAccessedNode != null && node.key == lastAccessedNode.key);
        Color nodeColor = isHighlighted ? new Color(41, 128, 185) : new Color(52, 152, 219);

        // Dibujar el círculo del nodo
        g2d.setColor(nodeColor);
        g2d.fillOval(x - width / 2, y, width, height);

        // Dibujar el borde
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x - width / 2, y, width, height);

        // Dibujar la clave del nodo
        g2d.setColor(Color.WHITE);
        String keyStr = String.valueOf(node.key);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(keyStr);
        int textHeight = fm.getHeight();
        g2d.drawString(keyStr, x - textWidth / 2, y + height / 2 + textHeight / 4);
    }

    // Método para dibujar la ruta de acceso al último nodo accedido
    private void drawAccessPath(Graphics2D g2d, int width, int height) {
        if (lastAccessedKey == null || model.getRoot() == null) {
            return;
        }

        // Dibujar el esquema de la función hash con los divisores
        int margin = 20;
        int boxWidth = 60;
        int boxHeight = 40;
        int totalDivisors = model.getDivisors().length;

        // Dibujar en la parte inferior del panel
        int startY = height - 120;

        // Dibujar la entrada (clave)
        g2d.setColor(new Color(41, 128, 185));
        Rectangle2D keyBox = new Rectangle2D.Double(margin, startY - boxHeight / 2, boxWidth, boxHeight);
        g2d.fill(keyBox);

        g2d.setColor(Color.WHITE);
        String keyText = lastAccessedKey.toString();
        int keyTextWidth = g2d.getFontMetrics().stringWidth(keyText);
        g2d.drawString(keyText, margin + boxWidth / 2 - keyTextWidth / 2, startY + 5);

        // Calcular ancho disponible para los divisores
        int availableWidth = width - margin * 2 - boxWidth * 2;
        int divisorSpacing = availableWidth / (totalDivisors + 1);

        // Dibujar las funciones de residuo (una por cada divisor)
        g2d.setColor(Color.BLACK);

        for (int i = 0; i < totalDivisors; i++) {
            int divisorX = margin + boxWidth + divisorSpacing * (i + 1);
            int divisor = model.getDivisors()[i];
            int residue = lastAccessedKey % divisor;
            boolean isEven = (residue % 2 == 0);
            String direction = isEven ? "Derecha" : "Izquierda";

            String functionText = "mod " + divisor + " = " + residue + " → " + direction;
            g2d.drawString(functionText, divisorX - g2d.getFontMetrics().stringWidth(functionText) / 2, startY + (i - totalDivisors / 2) * 25);

            // Dibujar flecha si es el primer divisor
            if (i == 0) {
                g2d.draw(new Line2D.Double(margin + boxWidth, startY, divisorX - g2d.getFontMetrics().stringWidth(functionText) / 2 - 5, startY));
                g2d.fillPolygon(
                        new int[]{divisorX - g2d.getFontMetrics().stringWidth(functionText) / 2 - 5, divisorX - g2d.getFontMetrics().stringWidth(functionText) / 2 - 10, divisorX - g2d.getFontMetrics().stringWidth(functionText) / 2 - 10},
                        new int[]{startY, startY - 5, startY + 5},
                        3
                );
            }
        }
    }

    public void initView() {
        SwingUtilities.invokeLater(() -> {
            view.showWindow();
        });
    }

    public static void main(String[] args) {
        // Usar varios divisores primos para el método de residuos múltiples
        int[] divisors = {11, 13, 17, 19, 23};
        MultipleResidueTreeModel model = new MultipleResidueTreeModel(divisors);
        MultipleResidueTreeView view = new MultipleResidueTreeView();
        MultipleResidueTreeController controller = new MultipleResidueTreeController(model, view);
        controller.initView();

        // Insertar algunos valores de ejemplo para la demostración
        model.put(25, "Veinticinco");
        model.put(42, "Cuarenta y dos");
        model.put(53, "Cincuenta y tres");
        model.put(14, "Catorce");
        model.put(36, "Treinta y seis");
        controller.updateVisualization();
    }
}