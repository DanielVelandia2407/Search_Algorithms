package view.graph;

import model.graph.GraphModel;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

public class GraphVisualPanel extends JPanel {
    private GraphModel graph;
    private Map<Integer, Point> vertexPositions;
    private static final int VERTEX_RADIUS = 25;
    private static final int PANEL_MARGIN = 50;

    // Colores para la visualización
    private static final Color VERTEX_COLOR = new Color(70, 130, 180);
    private static final Color VERTEX_BORDER = new Color(25, 25, 112);
    private static final Color EDGE_COLOR = new Color(105, 105, 105);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color BACKGROUND_COLOR = new Color(248, 248, 248);

    public GraphVisualPanel() {
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(600, 400));
        vertexPositions = new HashMap<>();
    }

    public void setGraph(GraphModel graph) {
        this.graph = graph;
        if (graph != null && graph.getNumVertices() > 0) {
            calculateVertexPositions();
        }
        repaint();
    }

    private void calculateVertexPositions() {
        vertexPositions.clear();

        if (graph == null) return;

        int numVertices = graph.getNumVertices();
        if (numVertices == 0) return;

        int panelWidth = getPreferredSize().width;
        int panelHeight = getPreferredSize().height;

        if (numVertices == 1) {
            // Un solo vértice en el centro
            vertexPositions.put(0, new Point(panelWidth / 2, panelHeight / 2));
        } else if (numVertices <= 8) {
            // Disposición circular para pocos vértices
            calculateCircularLayout(panelWidth, panelHeight, numVertices);
        } else {
            // Disposición en grid para muchos vértices
            calculateGridLayout(panelWidth, panelHeight, numVertices);
        }
    }

    private void calculateCircularLayout(int panelWidth, int panelHeight, int numVertices) {
        int centerX = panelWidth / 2;
        int centerY = panelHeight / 2;
        int radius = Math.min(panelWidth, panelHeight) / 3;

        double angleStep = 2 * Math.PI / numVertices;

        for (int i = 0; i < numVertices; i++) {
            double angle = i * angleStep - Math.PI / 2; // Empezar desde arriba
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            vertexPositions.put(i, new Point(x, y));
        }
    }

    private void calculateGridLayout(int panelWidth, int panelHeight, int numVertices) {
        int cols = (int) Math.ceil(Math.sqrt(numVertices));
        int rows = (int) Math.ceil((double) numVertices / cols);

        int cellWidth = (panelWidth - 2 * PANEL_MARGIN) / cols;
        int cellHeight = (panelHeight - 2 * PANEL_MARGIN) / rows;

        for (int i = 0; i < numVertices; i++) {
            int row = i / cols;
            int col = i % cols;

            int x = PANEL_MARGIN + col * cellWidth + cellWidth / 2;
            int y = PANEL_MARGIN + row * cellHeight + cellHeight / 2;

            vertexPositions.put(i, new Point(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (graph == null || graph.getNumVertices() == 0) {
            drawEmptyMessage(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Redibujar posiciones si el tamaño del panel cambió
        if (vertexPositions.isEmpty() || getWidth() != getPreferredSize().width ||
                getHeight() != getPreferredSize().height) {
            setPreferredSize(new Dimension(getWidth(), getHeight()));
            calculateVertexPositions();
        }

        // Dibujar aristas primero (para que queden detrás de los vértices)
        drawEdges(g2);

        // Dibujar vértices
        drawVertices(g2);

        // Dibujar información del grafo
        drawGraphInfo(g2);
    }

    private void drawEmptyMessage(Graphics g) {
        String message = "No hay grafo para mostrar";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;

        g.setColor(Color.GRAY);
        g.drawString(message, x, y);
    }

    private void drawEdges(Graphics2D g2) {
        if (graph == null || graph.getEdges() == null) return;

        g2.setColor(EDGE_COLOR);
        g2.setStroke(new BasicStroke(2.0f));

        for (GraphModel.Edge edge : graph.getEdges()) {
            Point sourcePos = vertexPositions.get(edge.getSource());
            Point destPos = vertexPositions.get(edge.getDestination());

            if (sourcePos != null && destPos != null) {
                if (edge.getSource() == edge.getDestination()) {
                    // Dibujar lazo (self-loop)
                    drawSelfLoop(g2, sourcePos);
                } else {
                    // Dibujar línea normal
                    g2.draw(new Line2D.Double(sourcePos.x, sourcePos.y, destPos.x, destPos.y));

                    // Dibujar flecha si es un grafo dirigido
                    if (isDirectedGraph()) {
                        drawArrowHead(g2, sourcePos, destPos);
                    }
                }
            }
        }
    }

    private void drawSelfLoop(Graphics2D g2, Point vertexPos) {
        // Dibujar un pequeño círculo como lazo
        int loopRadius = 15;
        int loopX = vertexPos.x + VERTEX_RADIUS;
        int loopY = vertexPos.y - VERTEX_RADIUS;

        g2.drawOval(loopX - loopRadius, loopY - loopRadius,
                loopRadius * 2, loopRadius * 2);

        // Si es dirigido, agregar una pequeña flecha en el lazo
        if (isDirectedGraph()) {
            int arrowX = loopX + loopRadius - 3;
            int arrowY = loopY;

            // Pequeña flecha
            g2.drawLine(arrowX, arrowY, arrowX - 5, arrowY - 3);
            g2.drawLine(arrowX, arrowY, arrowX - 5, arrowY + 3);
        }
    }

    private void drawArrowHead(Graphics2D g2, Point from, Point to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double angle = Math.atan2(dy, dx);

        // Calcular punto donde debe empezar la flecha (en el borde del círculo destino)
        double distance = Math.sqrt(dx * dx + dy * dy);
        double arrowX = to.x - (VERTEX_RADIUS + 3) * dx / distance;
        double arrowY = to.y - (VERTEX_RADIUS + 3) * dy / distance;

        // Dibujar flecha
        int arrowLength = 10;
        double arrowAngle = Math.PI / 6;

        int x1 = (int) (arrowX - arrowLength * Math.cos(angle - arrowAngle));
        int y1 = (int) (arrowY - arrowLength * Math.sin(angle - arrowAngle));

        int x2 = (int) (arrowX - arrowLength * Math.cos(angle + arrowAngle));
        int y2 = (int) (arrowY - arrowLength * Math.sin(angle + arrowAngle));

        g2.drawLine((int) arrowX, (int) arrowY, x1, y1);
        g2.drawLine((int) arrowX, (int) arrowY, x2, y2);
    }

    private void drawVertices(Graphics2D g2) {
        if (graph == null) return;

        int numVertices = graph.getNumVertices();
        for (int i = 0; i < numVertices; i++) {
            Point pos = vertexPositions.get(i);
            if (pos != null) {
                // Dibujar círculo del vértice
                Ellipse2D circle = new Ellipse2D.Double(
                        pos.x - VERTEX_RADIUS,
                        pos.y - VERTEX_RADIUS,
                        2 * VERTEX_RADIUS,
                        2 * VERTEX_RADIUS
                );

                g2.setColor(VERTEX_COLOR);
                g2.fill(circle);

                g2.setColor(VERTEX_BORDER);
                g2.setStroke(new BasicStroke(2.0f));
                g2.draw(circle);

                // Dibujar etiqueta del vértice
                String label = graph.getVertexName(i);
                if (label == null) label = String.valueOf(i);

                FontMetrics fm = g2.getFontMetrics();
                int textX = pos.x - fm.stringWidth(label) / 2;
                int textY = pos.y + fm.getAscent() / 2;

                g2.setColor(TEXT_COLOR);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString(label, textX, textY);
            }
        }
    }

    private void drawGraphInfo(Graphics2D g2) {
        if (graph == null) return;

        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));

        String info = String.format("Vértices: %d | Aristas: %d",
                graph.getNumVertices(),
                graph.getEdges().size());

        g2.drawString(info, 10, getHeight() - 10);
    }

    private boolean isDirectedGraph() {
        if (graph == null) return false;
        return graph.isDirectedGraph();
    }
}