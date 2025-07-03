package controller.graph;

import model.graph.GraphModel;
import view.graph.GraphView;
import java.util.List;

public class GraphController {
    private GraphModel graph;
    private GraphView view;

    public GraphController() {
        this.view = new GraphView();
        setupEventListeners();
    }

    private void setupEventListeners() {
        view.addCreateGraphListener(e -> createGraphFromInput());
        view.addAnalyzeGraphListener(e -> analyzeGraph());
        view.addShowMatricesListener(e -> showMatrices());
        view.addClearGraphListener(e -> clearGraph());
        view.addBackListener(e -> exitApplication());
    }

    private void createGraphFromInput() {
        try {
            String verticesInput = view.getVerticesInput();
            String edgesInput = view.getEdgesInput();

            // Validar entrada de vértices
            if (verticesInput.isEmpty()) {
                view.showError("Por favor ingrese los vértices del grafo.\n" +
                        "Ejemplo: A,B,C,D o 1,2,3,4");
                return;
            }

            // Crear nuevo grafo
            graph = new GraphModel();

            // Inicializar con vértices
            graph.initializeWithVertices(verticesInput);

            // Validar número de vértices para visualización
            if (graph.getNumVertices() > 20) {
                view.showError("Por razones de visualización, máximo 20 vértices permitidos.\n" +
                        "Usted ingresó: " + graph.getNumVertices() + " vértices.");
                graph = null;
                return;
            }

            // Agregar aristas si se proporcionaron
            if (!edgesInput.isEmpty()) {
                graph.addEdgesFromString(edgesInput);
            }

            // Mostrar mensaje de éxito
            String message = String.format("Grafo creado exitosamente!\n" +
                            "Vértices: %d\n" +
                            "Aristas: %d",
                    graph.getNumVertices(),
                    graph.getEdges().size());
            view.showSuccess(message);

            // Actualizar visualización
            updateDisplay();

        } catch (IllegalArgumentException ex) {
            view.showError("Error en los datos ingresados:\n" + ex.getMessage() + "\n\n" +
                    "Formatos válidos:\n" +
                    "Vértices: A,B,C,D o 1,2,3,4\n" +
                    "Aristas: A-B,A-C,B-D (no dirigido) o A->B,A->C,B->D (dirigido)");
        } catch (Exception ex) {
            view.showError("Error inesperado: " + ex.getMessage());
        }
    }

    private void analyzeGraph() {
        if (graph == null) {
            view.showError("Primero debe crear un grafo.\n" +
                    "Ingrese los vértices y opcionalmente las aristas, luego presione 'Crear Grafo'.");
            return;
        }

        try {
            String graphType = graph.determineGraphType();

            // Información adicional sobre el grafo
            StringBuilder analysis = new StringBuilder();
            analysis.append("=== ANÁLISIS COMPLETO DEL GRAFO ===\n\n");
            analysis.append("Características: ").append(graphType).append("\n\n");
            analysis.append("Estadísticas:\n");
            analysis.append("• Número de vértices: ").append(graph.getNumVertices()).append("\n");
            analysis.append("• Número de aristas: ").append(graph.getEdges().size()).append("\n\n");

            // Información detallada de grados
            analysis.append(graph.getDegreeInfo());

            // Información sobre densidad
            boolean isDirected = graph.isDirectedGraph();
            int maxPossibleEdges = isDirected ?
                    graph.getNumVertices() * (graph.getNumVertices() - 1) :
                    graph.getNumVertices() * (graph.getNumVertices() - 1) / 2;

            if (maxPossibleEdges > 0) {
                double density = (double) graph.getEdges().size() / maxPossibleEdges * 100;
                analysis.append(String.format("\nDensidad del grafo: %.2f%%\n", density));
            }

            // Verificar si hay lazos
            boolean hasLoops = false;
            for (GraphModel.Edge edge : graph.getEdges()) {
                if (edge.getSource() == edge.getDestination()) {
                    hasLoops = true;
                    break;
                }
            }

            if (hasLoops) {
                analysis.append("\n⚠️ El grafo contiene lazos (self-loops)\n");
            }

            view.showGraphType(analysis.toString());

        } catch (Exception ex) {
            view.showError("Error al analizar el grafo: " + ex.getMessage());
        }
    }

    private void showMatrices() {
        if (graph == null) {
            view.showError("Primero debe crear un grafo.");
            return;
        }

        try {
            // Mostrar matriz de adyacencia
            view.showAdjacencyMatrix(graph.getAdjacencyMatrix(), graph);

            // Crear y mostrar matriz de incidencia
            if (!graph.getEdges().isEmpty()) {
                int[][] incidenceMatrix = createIncidenceMatrix();
                view.showIncidenceMatrix(incidenceMatrix, graph);
            } else {
                // Mostrar matriz de incidencia vacía
                int[][] emptyMatrix = new int[graph.getNumVertices()][0];
                view.showIncidenceMatrix(emptyMatrix, graph);
            }

            view.showMessage("Matrices actualizadas en las pestañas correspondientes.");

        } catch (Exception ex) {
            view.showError("Error al generar las matrices: " + ex.getMessage());
        }
    }

    private void clearGraph() {
        int option = javax.swing.JOptionPane.showConfirmDialog(
                view,
                "¿Está seguro que desea limpiar todos los datos del grafo actual?",
                "Confirmar limpieza",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE
        );

        if (option == javax.swing.JOptionPane.YES_OPTION) {
            graph = null;
            view.clearAll();
            view.showMessage("Todos los datos han sido eliminados. Puede crear un nuevo grafo.");
        }
    }

    private void exitApplication() {
        int option = javax.swing.JOptionPane.showConfirmDialog(
                view,
                "¿Está seguro que desea salir de la aplicación?",
                "Confirmar salida",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE
        );

        if (option == javax.swing.JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void updateDisplay() {
        if (graph != null) {
            // Actualizar visualización gráfica
            view.showVisualRepresentation(graph);
            // Actualizar matrices automáticamente
            showMatrices();
        }
    }

    private int[][] createIncidenceMatrix() {
        List<GraphModel.Edge> edges = graph.getEdges();
        int[][] incidenceMatrix = new int[graph.getNumVertices()][edges.size()];

        for (int j = 0; j < edges.size(); j++) {
            GraphModel.Edge edge = edges.get(j);
            incidenceMatrix[edge.getSource()][j] = 1;

            // Para grafos no dirigidos, ambos vértices tienen incidencia 1
            // Para grafos dirigidos, solo el vértice origen tiene incidencia 1
            if (edge.getSource() != edge.getDestination()) {
                if (!isDirectedGraph()) {
                    incidenceMatrix[edge.getDestination()][j] = 1;
                }
            }
        }

        return incidenceMatrix;
    }

    private boolean isDirectedGraph() {
        if (graph == null) return false;
        return graph.isDirectedGraph();
    }

    public void run() {
        view.showWindow();

        // Mostrar mensaje de bienvenida con instrucciones usando SwingUtilities
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String welcomeMessage =
                        "¡Bienvenido al Sistema de Análisis de Grafos Avanzado!\n\n" +
                                "Instrucciones de uso:\n\n" +
                                "1. VÉRTICES: Ingrese los vértices separados por comas\n" +
                                "   Ejemplos: A,B,C,D  o  1,2,3,4  o  v1,v2,v3\n\n" +
                                "2. ARISTAS: Ingrese las aristas separadas por comas\n" +
                                "   • Grafos NO dirigidos: A-B,A-C,B-D\n" +
                                "   • Grafos dirigidos: A->B,A->C,B->D\n" +
                                "   • Lazos (self-loops): A-A o A->A\n" +
                                "   • Puede mezclar: A->B,C-D,E->E\n\n" +
                                "3. Presione 'Crear Grafo' para generar el grafo\n\n" +
                                "4. Use 'Analizar Grafo' para ver:\n" +
                                "   • Grados de valencia (entrada/salida)\n" +
                                "   • Características del grafo\n" +
                                "   • Densidad y estadísticas\n\n" +
                                "5. Explore las pestañas para diferentes representaciones\n\n" +
                                "NUEVAS CARACTERÍSTICAS:\n" +
                                "✓ Grados de valencia detallados\n" +
                                "✓ Soporte completo para grafos dirigidos\n" +
                                "✓ Visualización de lazos (self-loops)";

                view.showMessage(welcomeMessage);
            }
        });
    }

    // Método principal para ejecutar el programa
    public static void main(String[] args) {
        // Asegurar que la GUI se ejecute en el Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GraphController controller = new GraphController();
                controller.run();
            }
        });
    }
}