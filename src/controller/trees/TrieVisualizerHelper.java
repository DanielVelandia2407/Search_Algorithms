package controller.trees;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.*;

/**
 * Clase auxiliar para la visualización del árbol digital (Trie).
 * Proporciona funcionalidades adicionales como destacar nodos específicos
 * y calcular la distribución óptima de nodos en el espacio disponible.
 */
public class TrieVisualizerHelper {

    // Mapa de nodos destacados (ID del nodo -> color de destacado)
    private Map<String, Color> highlightedNodes;

    // Mapa de conexiones destacadas (ID del nodo padre + "-" + ID del nodo hijo -> color)
    private Map<String, Color> highlightedConnections;

    // Lista de nodos visitados en orden durante una búsqueda
    private List<String> visitedNodeSequence;

    // Índice actual en la secuencia de nodos visitados (para animación)
    private int currentVisitIndex;

    // Colores para diferentes estados de nodos y conexiones
    public static final Color HIGHLIGHT_SEARCH_COLOR = new Color(255, 165, 0); // Naranja
    public static final Color HIGHLIGHT_FOUND_COLOR = new Color(50, 205, 50);  // Verde lima
    public static final Color HIGHLIGHT_NOT_FOUND_COLOR = new Color(255, 69, 0); // Rojo naranja

    public TrieVisualizerHelper() {
        highlightedNodes = new HashMap<>();
        highlightedConnections = new HashMap<>();
        visitedNodeSequence = new ArrayList<>();
        currentVisitIndex = -1;
    }

    /**
     * Limpia todos los nodos y conexiones destacados.
     */
    public void clearHighlights() {
        highlightedNodes.clear();
        highlightedConnections.clear();
        visitedNodeSequence.clear();
        currentVisitIndex = -1;
    }

    /**
     * Destaca un nodo con un color específico.
     *
     * @param nodeId ID del nodo a destacar
     * @param color  Color para destacar
     */
    public void highlightNode(String nodeId, Color color) {
        highlightedNodes.put(nodeId, color);
    }

    /**
     * Destaca una conexión entre dos nodos.
     *
     * @param parentId ID del nodo padre
     * @param childId  ID del nodo hijo
     * @param color    Color para destacar
     */
    public void highlightConnection(String parentId, String childId, Color color) {
        highlightedConnections.put(parentId + "-" + childId, color);
    }

    /**
     * Verifica si un nodo está destacado.
     *
     * @param nodeId ID del nodo
     * @return true si el nodo está destacado
     */
    public boolean isNodeHighlighted(String nodeId) {
        return highlightedNodes.containsKey(nodeId);
    }

    /**
     * Obtiene el color de destacado de un nodo.
     *
     * @param nodeId ID del nodo
     * @return Color de destacado o null si no está destacado
     */
    public Color getNodeHighlightColor(String nodeId) {
        return highlightedNodes.get(nodeId);
    }

    /**
     * Verifica si una conexión está destacada.
     *
     * @param parentId ID del nodo padre
     * @param childId  ID del nodo hijo
     * @return true si la conexión está destacada
     */
    public boolean isConnectionHighlighted(String parentId, String childId) {
        return highlightedConnections.containsKey(parentId + "-" + childId);
    }

    /**
     * Obtiene el color de destacado de una conexión.
     *
     * @param parentId ID del nodo padre
     * @param childId  ID del nodo hijo
     * @return Color de destacado o null si no está destacada
     */
    public Color getConnectionHighlightColor(String parentId, String childId) {
        return highlightedConnections.get(parentId + "-" + childId);
    }

    /**
     * Establece la secuencia de nodos visitados durante una operación.
     *
     * @param sequence Lista de IDs de nodos en el orden que fueron visitados
     */
    public void setVisitedSequence(List<String> sequence) {
        this.visitedNodeSequence = new ArrayList<>(sequence);
        this.currentVisitIndex = sequence.isEmpty() ? -1 : 0;
    }

    /**
     * Avanza en la secuencia de nodos visitados y destaca el siguiente nodo.
     *
     * @param color Color para destacar el nodo
     * @return true si se avanzó a un nuevo nodo, false si se llegó al final
     */
    public boolean advanceVisitedSequence(Color color) {
        if (currentVisitIndex < 0 || currentVisitIndex >= visitedNodeSequence.size()) {
            return false;
        }

        String nodeId = visitedNodeSequence.get(currentVisitIndex);
        highlightNode(nodeId, color);

        // Si hay un nodo previo, destacar la conexión
        if (currentVisitIndex > 0) {
            String prevNodeId = visitedNodeSequence.get(currentVisitIndex - 1);
            highlightConnection(prevNodeId, nodeId, color);
        }

        currentVisitIndex++;
        return currentVisitIndex < visitedNodeSequence.size();
    }

    /**
     * Calcula la distribución óptima de los nodos del Trie para visualización.
     *
     * @param trieStructure Estructura del árbol Trie
     * @param width         Ancho disponible para la visualización
     * @param height        Alto disponible para la visualización
     * @param nodeWidth     Ancho de cada nodo
     * @param nodeHeight    Alto de cada nodo
     * @return Mapa con las posiciones (Rectangle2D) de cada nodo por su ID
     */
    public Map<String, Rectangle2D> calculateOptimalNodePositions(
            Map<String, Object> trieStructure,
            int width, int height,
            int nodeWidth, int nodeHeight) {

        Map<String, Rectangle2D> positions = new HashMap<>();

        // Estructuras para almacenar información sobre los nodos
        Map<Integer, List<String>> nodesByLevel = new HashMap<>();
        Map<String, Integer> nodeLevels = new HashMap<>();
        Map<String, List<String>> childrenByParent = new HashMap<>();

        // Inicializar con el nodo raíz (nivel 0)
        nodesByLevel.put(0, Collections.singletonList("root"));
        nodeLevels.put("root", 0);
        positions.put("root", new Rectangle2D.Double(
                (width - nodeWidth) / 2,
                50,
                nodeWidth,
                nodeHeight));

        // Procesar estructura del árbol recursivamente
        if (trieStructure.containsKey("children")) {
            Map<Character, Object> rootChildren = (Map<Character, Object>) trieStructure.get("children");
            processTrieLevel(rootChildren, "root", 1, nodesByLevel, nodeLevels, childrenByParent);
        }

        // Calcular número total de niveles
        int maxLevel = nodesByLevel.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);

        // Calcular espacio vertical entre niveles
        int levelHeight = (height - 100) / (maxLevel + 1);

        // Posicionar nodos por nivel
        for (int level = 1; level <= maxLevel; level++) {
            List<String> nodesInLevel = nodesByLevel.get(level);
            if (nodesInLevel == null || nodesInLevel.isEmpty()) continue;

            // Calcular posición horizontal
            int nodesCount = nodesInLevel.size();
            int levelWidth = width - 100;
            int nodeSpacing = nodesCount > 1 ? levelWidth / (nodesCount - 1) : 0;

            // Posicionar cada nodo en este nivel
            for (int i = 0; i < nodesCount; i++) {
                String nodeId = nodesInLevel.get(i);

                // Calcular posición X (centrada si hay solo un nodo)
                int x = nodesCount > 1
                        ? 50 + (i * nodeSpacing) - (nodeWidth / 2)
                        : (width - nodeWidth) / 2;

                // Calcular posición Y (basada en el nivel)
                int y = 50 + (level * levelHeight);

                // Guardar posición del nodo
                positions.put(nodeId, new Rectangle2D.Double(x, y, nodeWidth, nodeHeight));
            }
        }

        // Ajustar posiciones de los nodos para evitar solapamientos y mejorar la visualización
        optimizeNodePositions(positions, childrenByParent);

        return positions;
    }

    /**
     * Procesa recursivamente los niveles del árbol Trie.
     */
    private void processTrieLevel(
            Map<Character, Object> children,
            String parentId,
            int level,
            Map<Integer, List<String>> nodesByLevel,
            Map<String, Integer> nodeLevels,
            Map<String, List<String>> childrenByParent) {

        if (children == null || children.isEmpty()) return;

        // Crear lista para los hijos de este padre
        List<String> parentChildren = new ArrayList<>();
        childrenByParent.put(parentId, parentChildren);

        // Obtener o crear lista para este nivel
        List<String> nodesInLevel = nodesByLevel.computeIfAbsent(level, k -> new ArrayList<>());

        // Procesar cada hijo
        for (Map.Entry<Character, Object> entry : children.entrySet()) {
            Character c = entry.getKey();
            Map<String, Object> nodeData = (Map<String, Object>) entry.getValue();

            // Crear ID para este nodo
            String nodeId = parentId + "_" + c;
            parentChildren.add(nodeId);

            // Añadir nodo a este nivel
            nodesInLevel.add(nodeId);
            nodeLevels.put(nodeId, level);

            // Procesar hijos recursivamente
            if (nodeData.containsKey("children")) {
                processTrieLevel((Map<Character, Object>) nodeData.get("children"),
                        nodeId, level + 1, nodesByLevel, nodeLevels, childrenByParent);
            }
        }
    }

    /**
     * Optimiza las posiciones de los nodos para evitar solapamientos.
     */
    private void optimizeNodePositions(
            Map<String, Rectangle2D> positions,
            Map<String, List<String>> childrenByParent) {

        // Algoritmo para ajustar posiciones y evitar solapamientos
        // (Esta es una implementación básica que podría mejorarse)

        // Para cada nodo con hijos
        for (Map.Entry<String, List<String>> entry : childrenByParent.entrySet()) {
            String parentId = entry.getKey();
            List<String> children = entry.getValue();

            // Si hay al menos dos hijos
            if (children.size() >= 2) {
                Rectangle2D parentBounds = positions.get(parentId);

                // Calcular el centro X del padre
                double parentCenterX = parentBounds.getX() + (parentBounds.getWidth() / 2);

                // Obtener posiciones extremas de los hijos
                Rectangle2D firstChildBounds = positions.get(children.get(0));
                Rectangle2D lastChildBounds = positions.get(children.get(children.size() - 1));

                // Calcular centro X del grupo de hijos
                double childrenCenterX = (firstChildBounds.getX() + (firstChildBounds.getWidth() / 2) +
                        lastChildBounds.getX() + (lastChildBounds.getWidth() / 2)) / 2;

                // Si los centros no están alineados, ajustar posiciones de los hijos
                if (Math.abs(parentCenterX - childrenCenterX) > 1) {
                    double offsetX = parentCenterX - childrenCenterX;

                    // Ajustar cada hijo
                    for (String childId : children) {
                        Rectangle2D childBounds = positions.get(childId);
                        positions.put(childId, new Rectangle2D.Double(
                                childBounds.getX() + offsetX,
                                childBounds.getY(),
                                childBounds.getWidth(),
                                childBounds.getHeight()));
                    }
                }
            }
        }
    }
}