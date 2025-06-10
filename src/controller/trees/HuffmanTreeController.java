package controller.trees;

import controller.menu.TreeController;
import model.trees.HuffmanTreeModel;
import model.trees.HuffmanTreeModel.TreeNode;
import view.trees.HuffmanTreeView;
import view.trees.TreeView;
import view.trees.HuffmanTreeView.TreeVisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

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
        this.view.addSaveImageListener(this::saveTreeAsImage); // Nuevo: listener para guardar imagen
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

    // Nuevo: Método para guardar el árbol como imagen
    private void saveTreeAsImage(ActionEvent e) {
        if (model.getRoot() == null) {
            JOptionPane.showMessageDialog(view, "No hay árbol para guardar.");
            return;
        }

        JPanel treePanel = view.getTreeVisualizationPanel();

        // Crear imagen del panel
        BufferedImage image = new BufferedImage(treePanel.getWidth(), treePanel.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        treePanel.paint(g2d);
        g2d.dispose();

        // Selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar árbol como imagen");
        int userSelection = fileChooser.showSaveDialog(view);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();
                if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                    fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
                }
                ImageIO.write(image, "png", fileToSave);
                view.setResultMessage("Árbol guardado exitosamente como imagen.", true);
            } catch (Exception ex) {
                ex.printStackTrace();
                view.setResultMessage("Error al guardar la imagen: " + ex.getMessage(), false);
            }
        }
    }

// Método para actual
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

        // Usar el estilo mejorado inspirado en HuffmanPanel
        g2d.setStroke(new BasicStroke(3)); // Líneas más gruesas
        g2d.setFont(new Font("Arial", Font.BOLD, 18)); // Texto más grande
        drawTree(g2d, model.getRoot(), width / 2, 50, width / 4);

        // Si se ha accedido a un nodo específico, resaltarlo
        if (lastAccessedChar != null && lastAccessedNode != null) {
            drawHighlightPath(g2d, width, height);
        }
    }

    // Método para dibujar el árbol de Huffman con estilo mejorado
    private void drawTree(Graphics2D g, TreeNode node, int x, int y, int offset) {
        if (node == null) return;

        g.setColor(Color.BLACK);
        if (node.left != null) {
            g.drawLine(x, y, x - offset, y + 100); // Más espacio vertical
            g.setColor(Color.BLUE);
            g.drawString("0", (x + x - offset) / 2 - 15, (y + y + 100) / 2 - 10); // Número 0 más centrado
            g.setColor(Color.BLACK);
            drawTree(g, node.left, x - offset, y + 100, offset / 2);
        }
        if (node.right != null) {
            g.drawLine(x, y, x + offset, y + 100);
            g.setColor(Color.RED);
            g.drawString("1", (x + x + offset) / 2 + 5, (y + y + 100) / 2 - 10); // Número 1 más centrado
            g.setColor(Color.BLACK);
            drawTree(g, node.right, x + offset, y + 100, offset / 2);
        }

        // Dibujar nodo (círculo azul claro grande)
        int nodeRadius = 30; // radio 30 → diámetro 60
        g.setColor(new Color(173, 216, 230)); // Color azul claro
        g.fillOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);
        g.setColor(Color.BLACK);
        g.drawOval(x - nodeRadius, y - nodeRadius, 2 * nodeRadius, 2 * nodeRadius);

        // Mostrar contenido del nodo
        if (node.character != null) {
            // Para nodos hoja, mostrar el carácter
            String charDisplay = formatCharacter(node.character);
            int textWidth = g.getFontMetrics().stringWidth(charDisplay);
            g.drawString(charDisplay, x - textWidth / 2, y + 6);
        } else {
            // Para nodos internos, mostrar la frecuencia
            String freqDisplay = String.valueOf(node.frequency);
            int textWidth = g.getFontMetrics().stringWidth(freqDisplay);
            g.drawString(freqDisplay, x - textWidth / 2, y + 6);
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
        String charDisplay = formatCharacter(lastAccessedChar);
        g2d.drawString("Caracter: " + charDisplay, margin + 10, startY - 10);
        g2d.drawString("Código Huffman: " + huffmanCode, margin + 10, startY + 10);

        // Calcular y mostrar ahorro
        int regularBits = 8; // 8 bits en codificación estándar
        int huffmanBits = huffmanCode.length(); // bits en codificación Huffman
        double savingPercentage = ((regularBits - huffmanBits) / (double) regularBits) * 100;

        g2d.drawString(String.format("Ahorro: %d bits → %d bits (%.1f%%)",
                regularBits, huffmanBits, savingPercentage), margin + 10, startY + 30);
    }

    // Formatear caracteres especiales para visualización
    private String formatCharacter(Character c) {
        if (c == ' ') {
            return "[esp]";
        } else if (c == '\n') {
            return "[nl]";
        } else if (c == '\t') {
            return "[tab]";
        } else if (c == '\r') {
            return "[ret]";
        } else {
            return c.toString();
        }
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

    // Opcional: Agregar método para simplificar la creación de árboles con textos pequeños
    public void buildAndVisualizeTree(String text) {
        if (text.isEmpty()) {
            view.setResultMessage("Por favor, ingrese un texto para construir el árbol", false);
            return;
        }

        // Pasar a minúsculas y limpiar caracteres no alfabéticos (como en HuffmanCompressor)
        text = text.toLowerCase().replaceAll("[^a-z]", "");

        if (text.isEmpty()) {
            view.setResultMessage("El texto debe contener al menos una letra (a-z)", false);
            return;
        }

        // Establecer el texto en la vista
        view.setInputText(text);

        // Construir el árbol
        model.buildTree(text);

        // Comprimir el texto
        String compressed = model.compress(text);
        view.setCompressedText(compressed);

        // Descomprimir el texto como verificación
        String decompressed = model.decompress(compressed);
        view.setDecompressedText(decompressed);

        // Calcular y mostrar estadísticas
        double originalBits = text.length() * 5; // a-z = 5 bits
        double compressedBits = compressed.length();
        double compressionRate = 100 - (compressedBits / originalBits * 100);
        view.updateStatistics(model.size(), model.getHeight(), compressionRate);

        // Actualizar la visualización
        updateVisualization();

        view.setResultMessage("Árbol construido y texto codificado con éxito", true);
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

    // Método para inicializar la vista
    public void initView() {
        SwingUtilities.invokeLater(() -> {
            view.showWindow();
        });
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
}