package model;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class MultipleResidueTreeModel {
    private TreeNode root;
    private final int[] divisors; // Múltiples divisores para el método de residuos múltiples
    private int size;
    private Map<Integer, String> keyValueMap; // Para almacenar los valores asociados a las claves

    // Clase interna para los nodos del árbol
    public static class TreeNode {
        public int key;
        public String value;
        public TreeNode left;
        public TreeNode right;
        public int level; // Nivel del nodo en el árbol (útil para visualización)

        public TreeNode(int key, String value, int level) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.level = level;
        }

        // Getters para acceder a los campos desde fuera del paquete
        public int getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public TreeNode getLeft() {
            return left;
        }

        public TreeNode getRight() {
            return right;
        }

        public int getLevel() {
            return level;
        }
    }

    public MultipleResidueTreeModel(int[] divisors) {
        this.root = null;
        this.divisors = divisors;
        this.size = 0;
        this.keyValueMap = new HashMap<>();
    }

    // Método para insertar un elemento en el árbol
    public boolean put(int key, String value) {
        boolean isNew = !keyValueMap.containsKey(key);
        keyValueMap.put(key, value);

        if (root == null) {
            root = new TreeNode(key, value, 1);
            size++;
            return true;
        }

        if (isNew) {
            insertNode(key, value);
            size++;
        } else {
            // Actualizar el valor del nodo existente
            TreeNode currentNode = findNode(root, key);
            if (currentNode != null) {
                currentNode.value = value;
            }
        }

        return isNew;
    }

    // Método auxiliar para insertar un nodo en el árbol
    private void insertNode(int key, String value) {
        TreeNode current = root;
        int level = 1;

        while (true) {
            level++;
            // Determinar el divisor según el nivel actual (módulo para no exceder el tamaño del arreglo)
            int divisorIndex = (level - 2) % divisors.length;
            int residue = key % divisors[divisorIndex];

            // Direccionar a la izquierda o derecha según el residuo
            if (residue % 2 == 0) { // Par va a la derecha
                if (current.right == null) {
                    current.right = new TreeNode(key, value, level);
                    return;
                }
                current = current.right;
            } else { // Impar va a la izquierda
                if (current.left == null) {
                    current.left = new TreeNode(key, value, level);
                    return;
                }
                current = current.left;
            }
        }
    }

    // Método para buscar un elemento en el árbol
    public String get(int key) {
        return keyValueMap.getOrDefault(key, null);
    }

    // Método auxiliar para encontrar un nodo con una clave específica
    public TreeNode findNode(TreeNode current, int key) {
        if (current == null) {
            return null;
        }

        if (current.key == key) {
            return current;
        }

        // Calcular la ruta basada en los residuos múltiples
        int level = current.level;
        int divisorIndex = (level - 1) % divisors.length;
        int residue = key % divisors[divisorIndex];

        if (residue % 2 == 0) { // Par va a la derecha
            return findNode(current.right, key);
        } else { // Impar va a la izquierda
            return findNode(current.left, key);
        }
    }

    // Método para eliminar un elemento del árbol
    public boolean remove(int key) {
        if (!keyValueMap.containsKey(key)) {
            return false;
        }

        // Para simplificar, sólo eliminamos la entrada del mapa
        // En un escenario real, también deberíamos reestructurar el árbol
        keyValueMap.remove(key);

        // Reconstruir el árbol desde cero
        TreeNode oldRoot = root;
        root = null;
        size = 0;

        // Reconstruir el árbol con los elementos restantes
        for (Map.Entry<Integer, String> entry : keyValueMap.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }

        return true;
    }

    // Método para limpiar el árbol
    public void clear() {
        root = null;
        keyValueMap.clear();
        size = 0;
    }

    // Método para obtener todos los nodos en un formato recorrible
    public List<TreeNode> getAllNodes() {
        List<TreeNode> nodes = new ArrayList<>();
        collectNodes(root, nodes);
        return nodes;
    }

    // Método auxiliar para recolectar todos los nodos
    private void collectNodes(TreeNode node, List<TreeNode> nodes) {
        if (node != null) {
            nodes.add(node);
            collectNodes(node.left, nodes);
            collectNodes(node.right, nodes);
        }
    }

    // Métodos getter para acceder a las propiedades
    public TreeNode getRoot() {
        return root;
    }

    public int[] getDivisors() {
        return divisors;
    }

    public int size() {
        return size;
    }

    public Map<Integer, String> getKeyValueMap() {
        return new HashMap<>(keyValueMap); // Devuelve una copia para evitar modificaciones externas
    }

    // Método para calcular la altura del árbol
    public int getHeight() {
        return calculateHeight(root);
    }

    private int calculateHeight(TreeNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(calculateHeight(node.left), calculateHeight(node.right));
    }
}