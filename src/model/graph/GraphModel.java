package model.graph;

import java.util.*;

public class GraphModel {
    private int numVertices;
    private List<List<Integer>> adjacencyList;
    private int[][] adjacencyMatrix;
    private List<Edge> edges;
    private Map<String, Integer> vertexMap; // Mapea nombres de vértices a índices
    private Map<Integer, String> indexMap;  // Mapea índices a nombres de vértices

    public static class Edge {
        public int source, destination;
        public String sourceName, destinationName;

        public Edge(int source, int destination, String sourceName, String destinationName) {
            this.source = source;
            this.destination = destination;
            this.sourceName = sourceName;
            this.destinationName = destinationName;
        }

        // Getters
        public int getSource() {
            return source;
        }

        public int getDestination() {
            return destination;
        }

        public String getSourceName() {
            return sourceName;
        }

        public String getDestinationName() {
            return destinationName;
        }

        @Override
        public String toString() {
            return "(" + sourceName + " -> " + destinationName + ")";
        }
    }

    public GraphModel() {
        this.vertexMap = new HashMap<>();
        this.indexMap = new HashMap<>();
        this.edges = new ArrayList<>();
        this.numVertices = 0;
    }

    // Método para inicializar el grafo con vértices dados como string
    public void initializeWithVertices(String verticesInput) {
        if (verticesInput == null || verticesInput.trim().isEmpty()) {
            throw new IllegalArgumentException("La entrada de vértices no puede estar vacía");
        }

        // Limpiar datos anteriores
        vertexMap.clear();
        indexMap.clear();
        edges.clear();

        // Procesar vértices
        String[] vertexNames = verticesInput.split(",");
        this.numVertices = vertexNames.length;

        // Crear mapas de vértices
        for (int i = 0; i < vertexNames.length; i++) {
            String vertexName = vertexNames[i].trim();
            if (vertexName.isEmpty()) {
                throw new IllegalArgumentException("Nombre de vértice vacío encontrado");
            }
            if (vertexMap.containsKey(vertexName)) {
                throw new IllegalArgumentException("Vértice duplicado: " + vertexName);
            }
            vertexMap.put(vertexName, i);
            indexMap.put(i, vertexName);
        }

        // Inicializar estructuras de datos
        this.adjacencyList = new ArrayList<>();
        this.adjacencyMatrix = new int[numVertices][numVertices];

        for (int i = 0; i < numVertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    // Método para agregar aristas desde string
    public void addEdgesFromString(String edgesInput) {
        if (edgesInput == null || edgesInput.trim().isEmpty()) {
            return; // No hay aristas que agregar
        }

        String[] edgeStrings = edgesInput.split(",");
        for (String edgeString : edgeStrings) {
            String trimmedEdge = edgeString.trim();
            if (trimmedEdge.isEmpty()) {
                continue;
            }

            // Soportar tanto "-" como "->" como separadores
            String[] vertices;
            boolean isDirectedEdge = false;

            if (trimmedEdge.contains("->")) {
                vertices = trimmedEdge.split("->");
                isDirectedEdge = true;
            } else if (trimmedEdge.contains("-")) {
                vertices = trimmedEdge.split("-");
                isDirectedEdge = false;
            } else {
                throw new IllegalArgumentException("Formato de arista inválido: " + trimmedEdge + ". Use formato 'A-B' o 'A->B'");
            }

            if (vertices.length != 2) {
                throw new IllegalArgumentException("Formato de arista inválido: " + trimmedEdge);
            }

            String sourceVertex = vertices[0].trim();
            String destVertex = vertices[1].trim();

            if (!vertexMap.containsKey(sourceVertex)) {
                throw new IllegalArgumentException("Vértice no encontrado: " + sourceVertex);
            }
            if (!vertexMap.containsKey(destVertex)) {
                throw new IllegalArgumentException("Vértice no encontrado: " + destVertex);
            }

            int sourceIndex = vertexMap.get(sourceVertex);
            int destIndex = vertexMap.get(destVertex);

            // Agregar arista dirigida
            if (isDirectedEdge) {
                // Verificar si la arista ya existe
                if (adjacencyMatrix[sourceIndex][destIndex] == 0) {
                    adjacencyList.get(sourceIndex).add(destIndex);
                    adjacencyMatrix[sourceIndex][destIndex] = 1;
                    edges.add(new Edge(sourceIndex, destIndex, sourceVertex, destVertex));
                }
            } else {
                // Arista no dirigida - agregar en ambas direcciones
                if (sourceIndex == destIndex) {
                    // Es un lazo
                    if (adjacencyMatrix[sourceIndex][destIndex] == 0) {
                        adjacencyList.get(sourceIndex).add(destIndex);
                        adjacencyMatrix[sourceIndex][destIndex] = 1;
                        edges.add(new Edge(sourceIndex, destIndex, sourceVertex, destVertex));
                    }
                } else {
                    // Arista normal no dirigida
                    if (adjacencyMatrix[sourceIndex][destIndex] == 0) {
                        adjacencyList.get(sourceIndex).add(destIndex);
                        adjacencyMatrix[sourceIndex][destIndex] = 1;
                        edges.add(new Edge(sourceIndex, destIndex, sourceVertex, destVertex));
                    }
                    if (adjacencyMatrix[destIndex][sourceIndex] == 0) {
                        adjacencyList.get(destIndex).add(sourceIndex);
                        adjacencyMatrix[destIndex][sourceIndex] = 1;
                        edges.add(new Edge(destIndex, sourceIndex, destVertex, sourceVertex));
                    }
                }
            }
        }
    }

    // Método original para agregar arista individual (mantenido para compatibilidad)
    public void addEdge(int source, int destination) {
        if (source >= 0 && source < numVertices && destination >= 0 && destination < numVertices) {
            if (adjacencyMatrix[source][destination] == 0) {
                adjacencyList.get(source).add(destination);
                adjacencyMatrix[source][destination] = 1;
                String sourceName = indexMap.get(source);
                String destName = indexMap.get(destination);
                edges.add(new Edge(source, destination, sourceName, destName));
            }
        }
    }

    // Determinar tipo de grafo
    public String determineGraphType() {
        List<String> characteristics = new ArrayList<>();

        // Check if directed or undirected
        boolean isDirected = false;
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (adjacencyMatrix[i][j] != adjacencyMatrix[j][i]) {
                    isDirected = true;
                    break;
                }
            }
            if (isDirected) break;
        }

        if (isDirected) {
            characteristics.add("Dirigido (Digrafo)");
        } else {
            characteristics.add("No dirigido");
        }

        // Check if simple (no loops or multiple edges)
        boolean isSimple = true;
        boolean hasLoops = false;
        for (int i = 0; i < numVertices; i++) {
            if (adjacencyMatrix[i][i] >= 1) {
                isSimple = false;
                hasLoops = true;
                break;
            }
        }

        if (isSimple) {
            characteristics.add("Simple");
        } else if (hasLoops) {
            characteristics.add("Con lazos/bucles");
        }

        // Check if complete
        boolean isComplete = true;

        if (isDirected) {
            for (int i = 0; i < numVertices; i++) {
                for (int j = 0; j < numVertices; j++) {
                    if (i != j && adjacencyMatrix[i][j] == 0) {
                        isComplete = false;
                        break;
                    }
                }
                if (!isComplete) break;
            }
        } else {
            for (int i = 0; i < numVertices; i++) {
                for (int j = i + 1; j < numVertices; j++) {
                    if (adjacencyMatrix[i][j] == 0) {
                        isComplete = false;
                        break;
                    }
                }
                if (!isComplete) break;
            }
        }

        if (isComplete && numVertices > 1) {
            characteristics.add("Completo");
        }

        // Check connectivity (only for undirected graphs)
        if (!isDirected) {
            if (isConnected()) {
                characteristics.add("Conectado");
            } else {
                characteristics.add("Desconectado");
            }
        }

        // Check if regular
        if (isRegular()) {
            characteristics.add("Regular");
        }

        return String.join(", ", characteristics);
    }

    // Check if graph is connected (for undirected graphs)
    private boolean isConnected() {
        if (numVertices == 0) return true;

        boolean[] visited = new boolean[numVertices];
        Queue<Integer> queue = new LinkedList<>();

        // Start from first vertex that has connections
        int start = -1;
        for (int i = 0; i < numVertices; i++) {
            if (!adjacencyList.get(i).isEmpty()) {
                start = i;
                break;
            }
        }

        if (start == -1) return false; // No edges

        queue.offer(start);
        visited[start] = true;
        int visitedCount = 1;

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            for (int neighbor : adjacencyList.get(vertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                    visitedCount++;
                }
            }
        }

        return visitedCount == numVertices;
    }

    // Check if graph is regular
    private boolean isRegular() {
        if (numVertices <= 1) return true;

        int firstDegree = adjacencyList.get(0).size();
        for (int i = 1; i < numVertices; i++) {
            if (adjacencyList.get(i).size() != firstDegree) {
                return false;
            }
        }
        return true;
    }

    // Método para obtener el grado de entrada de un vértice (para grafos dirigidos)
    public int getInDegree(int vertexIndex) {
        if (vertexIndex < 0 || vertexIndex >= numVertices) {
            return -1;
        }

        int inDegree = 0;
        for (int i = 0; i < numVertices; i++) {
            inDegree += adjacencyMatrix[i][vertexIndex];
        }
        return inDegree;
    }

    // Método para obtener el grado de salida de un vértice (para grafos dirigidos)
    public int getOutDegree(int vertexIndex) {
        if (vertexIndex < 0 || vertexIndex >= numVertices) {
            return -1;
        }

        int outDegree = 0;
        for (int j = 0; j < numVertices; j++) {
            outDegree += adjacencyMatrix[vertexIndex][j];
        }
        return outDegree;
    }

    // Método para obtener el grado total de un vértice
    public int getTotalDegree(int vertexIndex) {
        if (vertexIndex < 0 || vertexIndex >= numVertices) {
            return -1;
        }

        // Para grafos dirigidos: grado total = grado entrada + grado salida
        // Para grafos no dirigidos: grado total = número de conexiones
        boolean isDirected = isDirectedGraph();

        if (isDirected) {
            return getInDegree(vertexIndex) + getOutDegree(vertexIndex);
        } else {
            // Para grafos no dirigidos, contar cada arista una vez
            // pero los lazos cuentan doble
            int degree = 0;
            for (int j = 0; j < numVertices; j++) {
                if (vertexIndex == j) {
                    // Lazo cuenta como 2
                    degree += adjacencyMatrix[vertexIndex][j] * 2;
                } else {
                    degree += adjacencyMatrix[vertexIndex][j];
                }
            }
            return degree;
        }
    }

    // Verificar si el grafo es dirigido
    public boolean isDirectedGraph() {
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (adjacencyMatrix[i][j] != adjacencyMatrix[j][i]) {
                    return true;
                }
            }
        }
        return false;
    }

    // Método para obtener información detallada de grados
    public String getDegreeInfo() {
        StringBuilder info = new StringBuilder();
        boolean isDirected = isDirectedGraph();

        info.append("=== INFORMACIÓN DE GRADOS ===\n\n");

        if (isDirected) {
            info.append("Grafo dirigido - Grados de entrada y salida:\n\n");
            for (int i = 0; i < numVertices; i++) {
                String vertexName = getVertexName(i);
                info.append(String.format("Vértice %s:\n", vertexName != null ? vertexName : "v" + i));
                info.append(String.format("  • Grado de entrada: %d\n", getInDegree(i)));
                info.append(String.format("  • Grado de salida: %d\n", getOutDegree(i)));
                info.append(String.format("  • Grado total: %d\n\n", getTotalDegree(i)));
            }
        } else {
            info.append("Grafo no dirigido - Grados:\n\n");
            for (int i = 0; i < numVertices; i++) {
                String vertexName = getVertexName(i);
                int degree = getTotalDegree(i);
                info.append(String.format("Vértice %s: %d\n",
                        vertexName != null ? vertexName : "v" + i, degree));
            }
        }

        return info.toString();
    }

    // Getters - MÉTODOS PÚBLICOS ESENCIALES
    public int getNumVertices() {
        return numVertices;
    }

    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public List<List<Integer>> getAdjacencyList() {
        return adjacencyList;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Map<String, Integer> getVertexMap() {
        return vertexMap;
    }

    public Map<Integer, String> getIndexMap() {
        return indexMap;
    }

    public String getVertexName(int index) {
        return indexMap.get(index);
    }

    public Integer getVertexIndex(String name) {
        return vertexMap.get(name);
    }
}