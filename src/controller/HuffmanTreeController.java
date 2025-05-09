package controller;

import model.HuffmanTreeModel;
import model.HuffmanTreeModel.TreeNode;
import view.HuffmanTreeView;
import view.TreeView;
import view.HuffmanTreeView.TreeVisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

public class HuffmanTreeController implements TreeVisualizer {
    private final HuffmanTreeModel model;
    private final HuffmanTreeView view;

    // Referencias para la navegación hacia atrás
    private TreeController parentController;
    private TreeView parentView;

    // Variables para la visualización del árbol
    private Character lastAccessedChar = null;
    private TreeNode lastAccessedNode = null;

    /**
     * Constructor del controlador
     *
     * @param model Modelo del árbol de Huffman
     * @param view  Vista del árbol de Huffman
     */
    public HuffmanTreeController(HuffmanTreeModel model, HuffmanTreeView view) {
        this.model = model;
        this.view = view;

        // Registrar este controlador como el visualizador del árbol para la vista
        this.view.setTreeVisualizer(this);

        // Añadir listeners a los botones
        this.view.addBuildTreeListener(this::buildTree);
        this.view.addCompressListener(this::compressText);
        this.view.addDecompressListener(this::decompressText);
        this.view.addClearListener(this::clearAll);
        this.view.addBackListener(this::goBack);
    }

    /**
     * Constructor adicional que recibe la referencia al controlador padre
     *
     * @param model            Modelo del árbol de Huffman
     * @param view             Vista del árbol de Huffman
     * @param parentController Controlador padre
     * @param parentView       Vista padre
     */
    public HuffmanTreeController(HuffmanTreeModel model, HuffmanTreeView view,
                                 TreeController parentController, TreeView parentView) {
        this(model, view); // Llama al constructor principal
        this.parentController = parentController;
        this.parentView = parentView;
    }

    /**
     * Método setter para el controlador padre (opcional)
     */
    public void setParentController(TreeController parentController, TreeView parentView) {
        this.parentController = parentController;
        this.parentView = parentView;
    }

    // Método para construir el árbol de Huffman
    private void buildTree(ActionEvent e) {
        String text = view.getInputText();

        if (text.isEmpty()) {
            view.setResultMessage("Por favor, ingrese un texto para construir el árbol", false);
            return;
        }

        model.buildTree(text);

        if (model.getRoot() == null) {
            view.setResultMessage("No se pudo construir el árbol", false);
            return;
        }

        view.setResultMessage("Árbol de Huffman construido correctamente", true);
        updateVisualization();
    }

    // Método para comprimir texto
    private void compressText(ActionEvent e) {
        String text = view.getInputText();

        if (text.isEmpty()) {
            view.setResultMessage("Por favor, ingrese un texto para comprimir", false);
            return;
        }

        if (model.getRoot() == null) {
            // Construir el árbol primero si no existe
            model.buildTree(text);
            updateVisualization();
        }

        String compressed = model.compress(text);
        view.setCompressedText(compressed);

        // Calcular tasa de compresión
        double originalBits = text.length() * 8; // Asumiendo 8 bits por caracter
        double compressedBits = compressed.length(); // Cada bit como un caracter
        double compressionRate = 100 - (compressedBits / originalBits * 100);

        view.setResultMessage(String.format("Texto comprimido correctamente (Ahorro: %.2f%%)", compressionRate), true);
        updateVisualization();
    }

    // Método para descomprimir texto
    private void decompressText(ActionEvent e) {
        String compressed = view.getCompressedText();

        if (compressed.isEmpty()) {
            view.setResultMessage("No hay texto comprimido para descomprimir", false);
            return;
        }

        if (model.getRoot() == null) {
            view.setResultMessage("No existe un árbol para descomprimir", false);
            return;
        }

        String decompressed = model.decompress(compressed);
        view.setDecompressedText(decompressed);

        view.setResultMessage("Texto descomprimido correctamente", true);
    }

    // Método para limpiar todo
    private void clearAll(ActionEvent e) {
        int option = JOptionPane.showConfirmDialog(
                view,
                "¿Está seguro de que desea limpiar todo?",
                "Confirmar limpieza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            model.clear();
            view.clearFields();
            lastAccessedChar = null;
            lastAccessedNode = null;
            view.setResultMessage("Todo ha sido limpiado", true);
        }
    }

    // Método para volver atrás
    private void goBack(ActionEvent e) {
        view.dispose();

        // Verificar si existe una vista padre y mostrarla
        if (parentView != null) {
            SwingUtilities.invokeLater(() -> {
                parentView.setVisible(true);
            });
        }
    }

    // Método para actualizar la visualización
    public void updateVisualization() {
        // Actualizar la tabla de códigos
        Map<Character, String> encodingMap = model.getEncodingMap();
        Map<Character, Integer> frequencyMap = model.getFrequencyMap();
        view.updateCodesTable(encodingMap, frequencyMap);

        // Calcular tasa de compresión para las estadísticas
        String original = view.getInputText();
        String compressed = view.getCompressedText();
        double compressionRate = 0;

        if (!original.isEmpty() && !compressed.isEmpty()) {
            double originalBits = original.length() * 8;
            double compressedBits = compressed.length();
            compressionRate = 100 - (compressedBits / originalBits * 100);
        }

        // Actualizar estadísticas
        view.updateStatistics(model.size(), model.getHeight(), compressionRate);

        // Repintar el panel de visualización
        JPanel visualizationPanel = view.getTreeVisualizationPanel();
        visualizationPanel.repaint();
    }

    /**
     * Método para ejecutar pruebas automáticas con textos predefinidos
     *
     * @param testTexts Array de textos de prueba
     */
    public void runTestCases(String[] testTexts) {
        if (testTexts == null || testTexts.length == 0) {
            view.setResultMessage("No hay casos de prueba para ejecutar", false);
            return;
        }

        StringBuilder results = new StringBuilder();
        results.append("Resultados de pruebas automáticas:\n\n");

        for (int i = 0; i < testTexts.length; i++) {
            String text = testTexts[i];
            results.append("Caso de prueba #").append(i + 1).append(":\n");
            results.append("Texto original (").append(text.length()).append(" caracteres): ");

            // Si el texto es muy largo, truncarlo para la visualización
            if (text.length() > 50) {
                results.append(text.substring(0, 47)).append("...\n");
            } else {
                results.append(text).append("\n");
            }

            // Construir árbol y comprimir
            model.buildTree(text);
            String compressed = model.compress(text);
            String decompressed = model.decompress(compressed);

            // Calcular estadísticas
            double originalBits = text.length() * 8;
            double compressedBits = compressed.length();
            double compressionRate = 100 - (compressedBits / originalBits * 100);

            results.append("Bits originales: ").append((int) originalBits).append("\n");
            results.append("Bits comprimidos: ").append((int) compressedBits).append("\n");
            results.append("Tasa de compresión: ").append(String.format("%.2f%%", compressionRate)).append("\n");

            // Verificar que la descompresión es correcta
            boolean verificationOk = text.equals(decompressed);
            results.append("Verificación: ").append(verificationOk ? "CORRECTA" : "FALLIDA").append("\n\n");

            // Si es el último caso de prueba, mostrar el resultado en la UI
            if (i == testTexts.length - 1) {
                view.setInputText(text);
                view.setCompressedText(compressed);
                view.setDecompressedText(decompressed);
                updateVisualization();
            }
        }

        // Mostrar resultados en un diálogo
        JTextArea textArea = new JTextArea(results.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(
                view,
                scrollPane,
                "Resultados de Pruebas Automáticas",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Ejecuta pruebas de rendimiento comparando diferentes textos y tamaños
     */
    public void runPerformanceTests() {
        // Textos de prueba de diferentes características
        String[] testTexts = {
                generateRepeatedText("a", 1000),             // Texto con un solo carácter repetido
                generateRepeatedText("ab", 500),             // Texto con dos caracteres alternados
                generateRandomText(1000),                    // Texto aleatorio
                generateRandomText(5000),                    // Texto aleatorio más grande
                "Este es un ejemplo de texto en español con acentos, signos de puntuación y números: 12345."
        };

        StringBuilder results = new StringBuilder();
        results.append("Resultados de pruebas de rendimiento:\n\n");

        for (int i = 0; i < testTexts.length; i++) {
            String text = testTexts[i];
            results.append("Prueba #").append(i + 1).append(":\n");

            // Descripción del texto
            if (i == 0) {
                results.append("Tipo: Un solo carácter repetido\n");
            } else if (i == 1) {
                results.append("Tipo: Dos caracteres alternados\n");
            } else if (i == 2) {
                results.append("Tipo: Texto aleatorio (1000 caracteres)\n");
            } else if (i == 3) {
                results.append("Tipo: Texto aleatorio (5000 caracteres)\n");
            } else {
                results.append("Tipo: Texto en español con diversos caracteres\n");
            }

            // Medir tiempo de construcción del árbol
            long startTime = System.nanoTime();
            model.buildTree(text);
            long treeTime = System.nanoTime() - startTime;

            // Medir tiempo de compresión
            startTime = System.nanoTime();
            String compressed = model.compress(text);
            long compressTime = System.nanoTime() - startTime;

            // Medir tiempo de descompresión
            startTime = System.nanoTime();
            model.decompress(compressed);
            long decompressTime = System.nanoTime() - startTime;

            // Calcular estadísticas
            double originalBits = text.length() * 8;
            double compressedBits = compressed.length();
            double compressionRate = 100 - (compressedBits / originalBits * 100);

            results.append("Longitud original: ").append(text.length()).append(" caracteres\n");
            results.append("Longitud comprimida: ").append(compressed.length()).append(" bits\n");
            results.append("Tasa de compresión: ").append(String.format("%.2f%%", compressionRate)).append("\n");
            results.append("Tiempo de construcción del árbol: ").append(treeTime / 1_000_000.0).append(" ms\n");
            results.append("Tiempo de compresión: ").append(compressTime / 1_000_000.0).append(" ms\n");
            results.append("Tiempo de descompresión: ").append(decompressTime / 1_000_000.0).append(" ms\n\n");

            // Si es el último caso, mostrar en la UI
            if (i == testTexts.length - 1) {
                view.setInputText(text);
                view.setCompressedText(compressed);
                view.setDecompressedText(model.decompress(compressed));
                updateVisualization();
            }
        }

        // Mostrar resultados en un diálogo
        JTextArea textArea = new JTextArea(results.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));

        JOptionPane.showMessageDialog(
                view,
                scrollPane,
                "Resultados de Pruebas de Rendimiento",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Genera un texto con un patrón repetido
     */
    private String generateRepeatedText(String pattern, int repetitions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repetitions; i++) {
            sb.append(pattern);
        }
        return sb.toString();
    }

    /**
     * Genera un texto aleatorio
     */
    private String generateRandomText(int length) {
        StringBuilder sb = new StringBuilder();
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ,.;:!?-";
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Carga un conjunto de ejemplos predefinidos para demostración
     */
    public void loadDemoExamples() {
        // Definir varios ejemplos
        String[] examples = {
                "Este es un ejemplo sencillo para el algoritmo de Huffman.",
                "En un lugar de la Mancha, de cuyo nombre no quiero acordarme, no ha mucho tiempo que vivía un hidalgo...",
                "AAAAAAABBBBBCCCCDDDEEF", // Texto con frecuencias muy dispares
                "1010101010101010" // Texto con pocos caracteres únicos
        };

        // Mostrar opciones en un diálogo
        String[] options = {
                "Ejemplo básico",
                "Texto literario",
                "Frecuencias dispares",
                "Caracteres repetidos"
        };

        int selected = JOptionPane.showOptionDialog(
                view,
                "Seleccione un ejemplo para cargar:",
                "Cargar Ejemplo",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Si el usuario seleccionó un ejemplo, cargarlo
        if (selected >= 0 && selected < examples.length) {
            String selectedText = examples[selected];
            view.setInputText(selectedText);
            view.setCompressedText("");
            view.setDecompressedText("");

            // Construir el árbol automáticamente
            model.buildTree(selectedText);
            updateVisualization();

            view.setResultMessage("Ejemplo cargado y árbol construido", true);
        }
    }

    // Implementación del método de la interfaz TreeVisualizer para dibujar el árbol
    @Override
    public void paintTreeVisualization(Graphics2D g2d, int width, int height) {
        // Limpiar el panel
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Si no hay nodos para visualizar, mostrar mensaje
        if (model.getRoot() == null) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            g2d.drawString("Construya el árbol para ver la visualización", width / 2 - 150, height / 2);
            return;
        }

        // Dibujar el árbol
        drawTree(g2d, width, height);

        // Si se ha accedido a un nodo específico, resaltarlo
        if (lastAccessedChar != null && lastAccessedNode != null) {
            drawHighlightPath(g2d, width, height);
        }
    }

    // Método para dibujar el árbol de Huffman
    private void drawTree(Graphics2D g2d, int width, int height) {
        // Configuración para dibujar el árbol
        int nodeWidth = 50;
        int nodeHeight = 50;
        int verticalSpace = 80;
        int initialY = 50;

        // Calcular la altura del árbol
        int treeHeight = model.getHeight();

        // Obtener todos los nodos
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

        // Crear un mapa temporal para rastrear la posición X,Y de cada nodo
        int[][] nodePositions = new int[allNodes.size()][2];

        // Primera pasada: calcular posiciones
        for (int i = 0; i < allNodes.size(); i++) {
            TreeNode node = allNodes.get(i);
            int level = node.level;
            int x = levelWidth[level - 1] * levelCount[level - 1] + levelWidth[level - 1] / 2;
            int y = initialY + (level - 1) * verticalSpace;

            // Guardar posición
            nodePositions[i][0] = x;
            nodePositions[i][1] = y;

            // Incrementar el contador de nodos en este nivel
            levelCount[level - 1]++;
        }

        // Segunda pasada: dibujar conexiones
        for (int i = 0; i < allNodes.size(); i++) {
            TreeNode node = allNodes.get(i);

            // Dibujar conexiones con hijos
            if (node.left != null) {
                int childIndex = findNodeIndex(allNodes, node.left);
                if (childIndex != -1) {
                    int startX = nodePositions[i][0];
                    int startY = nodePositions[i][1] + nodeHeight;
                    int endX = nodePositions[childIndex][0];
                    int endY = nodePositions[childIndex][1];

                    // Dibujar línea
                    g2d.setColor(Color.BLACK);
                    g2d.drawLine(startX, startY, endX, endY);

                    // Dibujar etiqueta "0" en la línea izquierda
                    g2d.setColor(Color.BLUE);
                    g2d.drawString("0", (startX + endX) / 2 - 10, (startY + endY) / 2);
                }
            }

            if (node.right != null) {
                int childIndex = findNodeIndex(allNodes, node.right);
                if (childIndex != -1) {
                    int startX = nodePositions[i][0];
                    int startY = nodePositions[i][1] + nodeHeight;
                    int endX = nodePositions[childIndex][0];
                    int endY = nodePositions[childIndex][1];

                    // Dibujar línea
                    g2d.setColor(Color.BLACK);
                    g2d.drawLine(startX, startY, endX, endY);

                    // Dibujar etiqueta "1" en la línea derecha
                    g2d.setColor(Color.RED);
                    g2d.drawString("1", (startX + endX) / 2 + 5, (startY + endY) / 2);
                }
            }
        }

        // Tercera pasada: dibujar nodos
        for (int i = 0; i < allNodes.size(); i++) {
            TreeNode node = allNodes.get(i);
            int x = nodePositions[i][0];
            int y = nodePositions[i][1];

            drawNode(g2d, node, x, y, nodeWidth, nodeHeight);
        }
    }

    // Método para encontrar el índice de un nodo en la lista
    private int findNodeIndex(List<TreeNode> nodes, TreeNode target) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i) == target) {
                return i;
            }
        }
        return -1;
    }

    // Método para dibujar un nodo individual
    private void drawNode(Graphics2D g2d, TreeNode node, int x, int y, int width, int height) {
        // Definir color según si es un nodo interno o hoja
        boolean isLeaf = (node.character != null);
        boolean isHighlighted = (lastAccessedNode != null && node == lastAccessedNode);

        Color nodeColor;
        if (isHighlighted) {
            nodeColor = new Color(41, 128, 185); // Azul resaltado
        } else if (isLeaf) {
            nodeColor = new Color(46, 204, 113); // Verde para hojas
        } else {
            nodeColor = new Color(52, 152, 219); // Azul para nodos internos
        }

        // Dibujar el círculo del nodo
        g2d.setColor(nodeColor);
        g2d.fillOval(x - width / 2, y, width, height);

        // Dibujar el borde
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x - width / 2, y, width, height);

        // Dibujar el contenido del nodo
        g2d.setColor(Color.WHITE);
        String nodeText;

        if (isLeaf) {
            // Para caracteres especiales, mostrar de forma legible
            if (node.character == ' ') {
                nodeText = "[esp]";
            } else if (node.character == '\n') {
                nodeText = "[nl]";
            } else if (node.character == '\t') {
                nodeText = "[tab]";
            } else {
                nodeText = node.character.toString();
            }
        } else {
            nodeText = String.valueOf(node.frequency);
        }

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(nodeText);
        int textHeight = fm.getHeight();
        g2d.drawString(nodeText, x - textWidth / 2, y + height / 2 + textHeight / 4);

        // Si es una hoja, mostrar también la frecuencia debajo
        if (isLeaf) {
            String freqText = "(" + node.frequency + ")";
            int freqWidth = fm.stringWidth(freqText);
            g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 10));
            g2d.drawString(freqText, x - freqWidth / 2, y + height + 12);
            g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 12)); // Restaurar fuente
        }
    }

    // Método para dibujar el camino destacado
    private void drawHighlightPath(Graphics2D g2d, int width, int height) {
        if (lastAccessedChar == null || lastAccessedNode == null) {
            return;
        }

        // Dibujar información del código en la parte inferior
        int margin = 20;
        int startY = height - 80;

        // Obtener el código Huffman del caracter
        String huffmanCode = model.getEncodingMap().get(lastAccessedChar);
        if (huffmanCode == null) {
            return;
        }

        // Dibujar un recuadro para la información
        g2d.setColor(new Color(236, 240, 241, 200)); // Color de fondo semi-transparente
        g2d.fillRect(margin, startY - 30, width - margin * 2, 60);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(margin, startY - 30, width - margin * 2, 60);

        // Dibujar texto informativo
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
        String charDisplay = lastAccessedChar == ' ' ? "[espacio]" : lastAccessedChar.toString();
        g2d.drawString("Caracter: " + charDisplay, margin + 10, startY - 10);
        g2d.drawString("Código Huffman: " + huffmanCode, margin + 10, startY + 10);

        // Calcular y mostrar ahorro
        int regularBits = 8; // 8 bits en codificación estándar
        int huffmanBits = huffmanCode.length(); // bits en codificación Huffman
        double savingPercentage = ((regularBits - huffmanBits) / (double) regularBits) * 100;

        g2d.drawString(String.format("Ahorro: %d bits → %d bits (%.1f%%)",
                regularBits, huffmanBits, savingPercentage), margin + 10, startY + 30);
    }

    /**
     * Método para resaltar un nodo con un carácter específico
     */
    public void highlightNode(Character c) {
        lastAccessedChar = c;
        lastAccessedNode = model.findNode(model.getRoot(), c);

        // Resaltar en la tabla si existe
        if (lastAccessedNode != null) {
            for (int i = 0; i < view.getTableRowCount(); i++) {
                String charCell = view.getTableValueAt(i, 0).toString();
                if (matchesCharacter(charCell, c)) {
                    view.highlightRow(i);
                    break;
                }
            }
        }

        JPanel visualizationPanel = view.getTreeVisualizationPanel();
        visualizationPanel.repaint();
    }

    /**
     * Compara un carácter con su representación en la tabla
     */
    private boolean matchesCharacter(String cellText, Character c) {
        if (c == ' ' && cellText.equals("[espacio]")) {
            return true;
        } else if (c == '\n' && cellText.equals("[salto de línea]")) {
            return true;
        } else if (c == '\t' && cellText.equals("[tabulador]")) {
            return true;
        } else if (c == '\r' && cellText.equals("[retorno]")) {
            return true;
        } else {
            return cellText.equals(c.toString());
        }
    }

    // Método para inicializar la vista
    public void initView() {
        SwingUtilities.invokeLater(() -> {
            view.showWindow();
        });
    }

    public static void main(String[] args) {
        HuffmanTreeModel model = new HuffmanTreeModel();
        HuffmanTreeView view = new HuffmanTreeView();
        HuffmanTreeController controller = new HuffmanTreeController(model, view);

        // Menú emergente para seleccionar funcionalidad
        String[] options = {
                "Iniciar aplicación normal",
                "Cargar ejemplos de demostración",
                "Ejecutar pruebas automáticas",
                "Ejecutar pruebas de rendimiento"
        };

        int selected = JOptionPane.showOptionDialog(
                null,
                "Seleccione una opción:",
                "Árbol de Huffman",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        controller.initView();

        // Ejecutar la acción seleccionada
        switch (selected) {
            case 1:
                controller.loadDemoExamples();
                break;
            case 2:
                String[] testTexts = {
                        "Este es un ejemplo simple para comprimir",
                        "AAAAABBBBBCCCCCDDDDD",
                        "En un lugar de la Mancha, de cuyo nombre no quiero acordarme..."
                };
                controller.runTestCases(testTexts);
                break;
            case 3:
                controller.runPerformanceTests();
                break;
            default:
                // Solo iniciar la aplicación sin hacer nada más
                break;
        }
    }
}