package model;

import java.util.*;

public class HuffmanTreeModel {

    // Clase interna para los nodos del árbol de Huffman
    public static class TreeNode implements Comparable<TreeNode> {
        public Character character; // Puede ser null para nodos internos
        public int frequency;
        public TreeNode left;
        public TreeNode right;
        public String code; // Código de Huffman (secuencia de 0s y 1s)
        public int level; // Nivel en el árbol (para visualización)

        // Constructor para hojas (con caracteres)
        public TreeNode(Character character, int frequency) {
            this.character = character;
            this.frequency = frequency;
            this.left = null;
            this.right = null;
            this.code = "";
            this.level = 1;
        }

        // Constructor para nodos internos (suma de frecuencias)
        public TreeNode(TreeNode left, TreeNode right) {
            this.character = null; // Nodo interno
            this.frequency = left.frequency + right.frequency;
            this.left = left;
            this.right = right;
            this.code = "";
            this.level = 1;
        }

        // Para ordenar nodos por frecuencia (cola de prioridad)
        @Override
        public int compareTo(TreeNode other) {
            return this.frequency - other.frequency;
        }

        // Representación en string para depuración
        @Override
        public String toString() {
            if (character != null) {
                return "'" + character + "' (" + frequency + ")";
            } else {
                return "[Interno: " + frequency + "]";
            }
        }
    }

    private TreeNode root;
    private Map<Character, String> encodingMap; // Mapeo caracter -> código
    private Map<String, Character> decodingMap; // Mapeo código -> caracter
    private Map<Character, Integer> frequencyMap; // Mapeo caracter -> frecuencia

    public HuffmanTreeModel() {
        this.root = null;
        this.encodingMap = new HashMap<>();
        this.decodingMap = new HashMap<>();
        this.frequencyMap = new HashMap<>();
    }

    // Método para construir el árbol a partir de un texto
    public void buildTree(String text) {
        // Contar frecuencias
        frequencyMap.clear();
        for (char c : text.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        // Crear nodos hoja
        PriorityQueue<TreeNode> queue = new PriorityQueue<>();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            queue.add(new TreeNode(entry.getKey(), entry.getValue()));
        }

        // Construir el árbol
        while (queue.size() > 1) {
            TreeNode left = queue.poll();
            TreeNode right = queue.poll();

            TreeNode parent = new TreeNode(left, right);
            queue.add(parent);
        }

        // La raíz es el último nodo en la cola
        if (!queue.isEmpty()) {
            root = queue.poll();

            // Asignar los niveles correctos en el árbol
            assignLevels(root, 1);

            // Generar códigos de Huffman
            generateCodes();
        } else {
            root = null;
        }
    }

    // Asignar niveles a los nodos (para visualización)
    private void assignLevels(TreeNode node, int level) {
        if (node != null) {
            node.level = level;
            assignLevels(node.left, level + 1);
            assignLevels(node.right, level + 1);
        }
    }

    // Generar códigos para cada caracter
    private void generateCodes() {
        encodingMap.clear();
        decodingMap.clear();

        if (root != null) {
            generateCodesRecursive(root, "");
        }
    }

    private void generateCodesRecursive(TreeNode node, String code) {
        if (node != null) {
            // Guardar el código actual en el nodo
            node.code = code;

            // Si es una hoja, guardar el código
            if (node.character != null) {
                encodingMap.put(node.character, code);
                decodingMap.put(code, node.character);
            }

            // Recursión: izquierda -> 0, derecha -> 1
            generateCodesRecursive(node.left, code + "0");
            generateCodesRecursive(node.right, code + "1");
        }
    }

    // Comprimir un texto usando el árbol actual
    public String compress(String text) {
        if (root == null) {
            return "";
        }

        StringBuilder compressed = new StringBuilder();
        for (char c : text.toCharArray()) {
            String code = encodingMap.get(c);
            if (code != null) {
                compressed.append(code);
            }
        }

        return compressed.toString();
    }

    // Descomprimir texto codificado
    public String decompress(String compressed) {
        if (root == null || compressed.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        StringBuilder currentCode = new StringBuilder();

        for (char bit : compressed.toCharArray()) {
            currentCode.append(bit);

            Character c = decodingMap.get(currentCode.toString());
            if (c != null) {
                result.append(c);
                currentCode.setLength(0); // Reiniciar el código actual
            }
        }

        return result.toString();
    }

    // Obtener todos los nodos para visualización
    public List<TreeNode> getAllNodes() {
        List<TreeNode> nodes = new ArrayList<>();
        collectNodes(root, nodes);
        return nodes;
    }

    private void collectNodes(TreeNode node, List<TreeNode> nodes) {
        if (node != null) {
            nodes.add(node);
            collectNodes(node.left, nodes);
            collectNodes(node.right, nodes);
        }
    }

    // Encontrar un nodo específico en el árbol
    public TreeNode findNode(TreeNode node, Character character) {
        if (node == null) {
            return null;
        }

        if (node.character != null && node.character.equals(character)) {
            return node;
        }

        TreeNode leftResult = findNode(node.left, character);
        if (leftResult != null) {
            return leftResult;
        }

        return findNode(node.right, character);
    }

    // Método para obtener la altura del árbol
    public int getHeight() {
        return calculateHeight(root);
    }

    private int calculateHeight(TreeNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(calculateHeight(node.left), calculateHeight(node.right));
    }

    // Método para obtener el tamaño del árbol (número de nodos)
    public int size() {
        return getAllNodes().size();
    }

    // Métodos getter
    public TreeNode getRoot() {
        return root;
    }

    public Map<Character, String> getEncodingMap() {
        return encodingMap;
    }

    public Map<String, Character> getDecodingMap() {
        return decodingMap;
    }

    public Map<Character, Integer> getFrequencyMap() {
        return frequencyMap;
    }

    // Limpiar todo el árbol
    public void clear() {
        root = null;
        encodingMap.clear();
        decodingMap.clear();
        frequencyMap.clear();
    }
}