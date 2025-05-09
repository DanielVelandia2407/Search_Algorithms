package model;

import java.util.*;

/**
 * Modelo mejorado del Árbol Digital (Trie).
 * Esta implementación ahora genera un árbol binario dinámicamente basado en las palabras insertadas.
 */
public class DigitalTreeModel {

    /**
     * Clase interna para representar un nodo del Trie
     */
    private class TrieNode {
        private Map<Character, TrieNode> children;
        private boolean isEndOfWord;
        private char character;  // Carácter representado por este nodo

        public TrieNode() {
            this('-');  // Carácter especial para la raíz
        }

        public TrieNode(char c) {
            children = new HashMap<>();
            isEndOfWord = false;
            character = c;
        }
    }

    /**
     * Clase para representar un nodo del árbol binario para visualización
     */
    public static class BinaryNode {
        public String label;
        public BinaryNode left;
        public BinaryNode right;
        public char bit; // 0 o 1 para la visualización

        public BinaryNode(String label) {
            this.label = label;
            this.left = null;
            this.right = null;
        }
    }

    // Mapa para almacenar la codificación de cada palabra/letra
    private Map<String, String> codificacion;

    private TrieNode root;
    private int wordCount;
    private BinaryNode binaryRoot; // Raíz del árbol binario para visualización

    public DigitalTreeModel() {
        root = new TrieNode();
        wordCount = 0;

        // Inicializar codificación estándar
        codificacion = new HashMap<>();
        codificacion.put("A", "00100");
        codificacion.put("B", "01000");
        codificacion.put("C", "01111");
        codificacion.put("D", "11000");
        codificacion.put("E", "11101");

        updateBinaryTree(); // Construir árbol binario inicial
    }

    /**
     * Actualiza el árbol binario en función de las palabras en el Trie
     */
    private void updateBinaryTree() {
        // Inicializar el árbol binario
        binaryRoot = new BinaryNode("");

        // Obtener todas las palabras almacenadas
        String[] words = getAllWords();

        // Si no hay palabras, mostrar un árbol de ejemplo
        if (words.length == 0) {
            buildExampleBinaryTree();
            return;
        }

        // Añadir cada letra del alfabeto que se utiliza
        Set<String> letrasUtilizadas = new HashSet<>();
        for (String word : words) {
            for (char c : word.toCharArray()) {
                letrasUtilizadas.add(String.valueOf(Character.toUpperCase(c)));
            }
        }

        // Asegurarnos de que tenemos letras para mostrar
        if (letrasUtilizadas.isEmpty()) {
            // Si no hay letras, usamos las letras de ejemplo
            letrasUtilizadas.add("A");
            letrasUtilizadas.add("B");
            letrasUtilizadas.add("C");
            letrasUtilizadas.add("D");
            letrasUtilizadas.add("E");
        }

        // Construir el árbol binario basado en los códigos
        for (String letra : letrasUtilizadas) {
            // Obtener el código de la letra (si no existe, crear uno)
            String codigo = codificacion.getOrDefault(letra, generarCodigo(letra));

            // Insertar en el árbol binario
            insertarEnArbolBinario(binaryRoot, letra, codigo, 0);
        }
    }

    /**
     * Inserta una letra en el árbol binario basado en su código
     */
    private void insertarEnArbolBinario(BinaryNode node, String letra, String codigo, int posicion) {
        // Si hemos llegado al final del código, este es el nodo para la letra
        if (posicion >= codigo.length()) {
            node.label = letra;
            return;
        }

        // Obtener el bit actual y avanzar a lo largo del código
        char bit = codigo.charAt(posicion);

        if (bit == '0') {
            // Si el bit es 0, ir a la izquierda
            if (node.left == null) {
                node.left = new BinaryNode("");
                node.left.bit = '0';
            }
            insertarEnArbolBinario(node.left, letra, codigo, posicion + 1);
        } else {
            // Si el bit es 1, ir a la derecha
            if (node.right == null) {
                node.right = new BinaryNode("");
                node.right.bit = '1';
            }
            insertarEnArbolBinario(node.right, letra, codigo, posicion + 1);
        }
    }

    /**
     * Genera un código binario para una letra que no tiene código predefinido
     */
    private String generarCodigo(String letra) {
        // Para simplificar, generar un código basado en la posición en el alfabeto
        char c = letra.charAt(0);
        int pos = Character.toUpperCase(c) - 'A';

        // Convertir a binario y asegurar que tenga al menos 5 bits
        String codigo = Integer.toBinaryString(pos + 1);
        while (codigo.length() < 5) {
            codigo = "0" + codigo;
        }

        // Guardar el código generado
        codificacion.put(letra, codigo);
        return codigo;
    }

    /**
     * Construye un árbol binario de ejemplo similar al de la imagen
     */
    private void buildExampleBinaryTree() {
        // Crear el árbol binario de ejemplo
        binaryRoot = new BinaryNode("");

        // Crear los nodos según la imagen proporcionada
        BinaryNode nodeLeft = new BinaryNode("");
        BinaryNode nodeRight = new BinaryNode("");

        BinaryNode nodeA = new BinaryNode("A");
        BinaryNode nodeLeftRight = new BinaryNode("");

        BinaryNode nodeRightLeft = new BinaryNode("");
        BinaryNode nodeRightRight = new BinaryNode("");

        BinaryNode nodeB = new BinaryNode("B");
        BinaryNode nodeC = new BinaryNode("C");

        BinaryNode nodeD = new BinaryNode("D");
        BinaryNode nodeE = new BinaryNode("E");

        // Configurar la estructura del árbol
        binaryRoot.left = nodeLeft;
        binaryRoot.right = nodeRight;

        nodeLeft.left = nodeA;
        nodeLeft.right = nodeLeftRight;

        nodeRight.left = nodeRightLeft;
        nodeRight.right = nodeRightRight;

        nodeLeftRight.left = nodeB;
        nodeLeftRight.right = nodeC;

        nodeRightLeft.left = nodeD;
        nodeRightRight.left = nodeE;

        // Configurar bits de las ramas (0 para izquierda, 1 para derecha)
        nodeLeft.bit = '0';
        nodeRight.bit = '1';

        nodeA.bit = '0';
        nodeLeftRight.bit = '1';

        nodeRightLeft.bit = '0';
        nodeRightRight.bit = '1';

        nodeB.bit = '0';
        nodeC.bit = '1';

        nodeD.bit = '0';
        nodeE.bit = '0';
    }

    /**
     * Obtiene el árbol binario para visualización
     *
     * @return La raíz del árbol binario
     */
    public BinaryNode getBinaryRoot() {
        return binaryRoot;
    }

    /**
     * Inserta una palabra en el árbol.
     *
     * @param word Palabra a insertar
     * @return true si la palabra es nueva, false si ya existía
     */
    public boolean insert(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        TrieNode current = root;
        boolean isNewWord = false;

        for (char c : word.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode(c));
            current = current.children.get(c);
        }

        if (!current.isEndOfWord) {
            isNewWord = true;
            wordCount++;
        }

        current.isEndOfWord = true;

        // Actualizar el árbol binario después de insertar
        updateBinaryTree();

        return isNewWord;
    }

    /**
     * Busca una palabra en el árbol.
     *
     * @param word Palabra a buscar
     * @return true si la palabra existe, false en caso contrario
     */
    public boolean search(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        TrieNode node = getNode(word);
        return node != null && node.isEndOfWord;
    }

    /**
     * Elimina una palabra del árbol.
     * Esta versión mejorada elimina correctamente los nodos que ya no forman
     * parte de ninguna palabra.
     *
     * @param word Palabra a eliminar
     * @return true si la palabra se eliminó correctamente, false si no existía
     */
    public boolean delete(String word) {
        if (word == null || word.isEmpty() || !search(word)) {
            return false;
        }

        deleteWord(root, word, 0);
        wordCount--;

        // Actualizar el árbol binario después de eliminar
        updateBinaryTree();

        return true;
    }

    /**
     * Método recursivo para eliminar una palabra del árbol.
     *
     * @param current Nodo actual
     * @param word    Palabra a eliminar
     * @param index   Índice del carácter actual en la palabra
     * @return true si el nodo actual debería ser eliminado
     */
    private boolean deleteWord(TrieNode current, String word, int index) {
        // Caso base: hemos llegado al final de la palabra
        if (index == word.length()) {
            // Si no es fin de palabra, la palabra no existe en el árbol
            if (!current.isEndOfWord) {
                return false;
            }

            // Marcar como no fin de palabra
            current.isEndOfWord = false;

            // Si este nodo no tiene hijos, puede ser eliminado
            return current.children.isEmpty();
        }

        // Obtener el carácter actual y el nodo hijo correspondiente
        char ch = word.charAt(index);
        TrieNode child = current.children.get(ch);

        // Si no existe el nodo hijo, la palabra no existe
        if (child == null) {
            return false;
        }

        // Verificar recursivamente si el nodo hijo debe ser eliminado
        boolean shouldDeleteChild = deleteWord(child, word, index + 1);

        // Si el nodo hijo debe ser eliminado
        if (shouldDeleteChild) {
            // Eliminar el nodo hijo
            current.children.remove(ch);

            // Este nodo actual puede ser eliminado si:
            // 1. No es fin de otra palabra Y
            // 2. No tiene otros hijos después de eliminar el hijo actual
            return !current.isEndOfWord && current.children.isEmpty();
        }

        // El nodo hijo no se eliminó, por lo que este nodo tampoco debe eliminarse
        return false;
    }

    /**
     * Obtiene el nodo correspondiente a una palabra.
     *
     * @param word Palabra a buscar
     * @return El nodo correspondiente a la palabra o null si no existe
     */
    private TrieNode getNode(String word) {
        TrieNode current = root;

        for (char c : word.toCharArray()) {
            TrieNode child = current.children.get(c);
            if (child == null) {
                return null;
            }
            current = child;
        }

        return current;
    }

    /**
     * Obtiene todas las palabras almacenadas en el árbol.
     *
     * @return Array con todas las palabras
     */
    public String[] getAllWords() {
        List<String> words = new ArrayList<>();
        findAllWords(root, "", words);
        return words.toArray(new String[0]);
    }

    /**
     * Método recursivo para encontrar todas las palabras.
     *
     * @param node   Nodo actual
     * @param prefix Prefijo formado hasta el momento
     * @param words  Lista donde se irán agregando las palabras encontradas
     */
    private void findAllWords(TrieNode node, String prefix, List<String> words) {
        if (node.isEndOfWord) {
            words.add(prefix);
        }

        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            findAllWords(entry.getValue(), prefix + entry.getKey(), words);
        }
    }

    /**
     * Limpia el árbol eliminando todos los nodos.
     */
    public void clear() {
        root = new TrieNode();
        wordCount = 0;
        // Reconstruir el árbol binario de ejemplo
        updateBinaryTree();
    }

    /**
     * Obtiene la cantidad de palabras almacenadas.
     *
     * @return Número de palabras
     */
    public int getWordCount() {
        return wordCount;
    }

    /**
     * Obtiene la estructura del árbol para visualización.
     *
     * @return Mapa con la estructura del árbol
     */
    public Map<String, Object> getTrieStructure() {
        Map<String, Object> trieData = new HashMap<>();
        trieData.put("children", getNodeStructure(root));

        // Añadir codificaciones
        trieData.put("wordCodes", codificacion);

        return trieData;
    }

    /**
     * Método recursivo para obtener la estructura de un nodo.
     *
     * @param node Nodo actual
     * @return Mapa con la estructura del nodo
     */
    private Map<Character, Object> getNodeStructure(TrieNode node) {
        Map<Character, Object> nodeStructure = new HashMap<>();

        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            Character c = entry.getKey();
            TrieNode childNode = entry.getValue();

            Map<String, Object> childData = new HashMap<>();
            childData.put("isEndOfWord", childNode.isEndOfWord);

            if (!childNode.children.isEmpty()) {
                childData.put("children", getNodeStructure(childNode));
            }

            nodeStructure.put(c, childData);
        }

        return nodeStructure;
    }

    /**
     * Obtiene todas las palabras que comienzan con un prefijo dado.
     *
     * @param prefix Prefijo a buscar
     * @return Array con las palabras que comienzan con el prefijo
     */
    public String[] getWordsWithPrefix(String prefix) {
        List<String> words = new ArrayList<>();
        TrieNode node = getNode(prefix);

        if (node != null) {
            findAllWords(node, prefix, words);
        }

        return words.toArray(new String[0]);
    }

    /**
     * Obtiene la altura del árbol.
     *
     * @return Altura del árbol (número de niveles)
     */
    public int getHeight() {
        return calculateHeight(root, 0);
    }

    /**
     * Método recursivo para calcular la altura del árbol.
     *
     * @param node  Nodo actual
     * @param level Nivel actual
     * @return Altura máxima encontrada
     */
    private int calculateHeight(TrieNode node, int level) {
        if (node == null || node.children.isEmpty()) {
            return level;
        }

        int maxHeight = level;
        for (TrieNode child : node.children.values()) {
            int childHeight = calculateHeight(child, level + 1);
            maxHeight = Math.max(maxHeight, childHeight);
        }

        return maxHeight;
    }

    /**
     * Obtiene el número total de nodos en el árbol.
     *
     * @return Número de nodos
     */
    public int getNodeCount() {
        return countNodes(root);
    }

    /**
     * Método recursivo para contar nodos.
     *
     * @param node Nodo actual
     * @return Número de nodos en el subárbol
     */
    private int countNodes(TrieNode node) {
        if (node == null) {
            return 0;
        }

        int count = 1; // Contar el nodo actual
        for (TrieNode child : node.children.values()) {
            count += countNodes(child);
        }

        return count;
    }
}