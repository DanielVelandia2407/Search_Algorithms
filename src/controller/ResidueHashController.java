package controller;

import model.ResidueHashModel;
import view.ResidueHashView;
import view.TreeView;
import view.ResidueHashView.HashVisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class ResidueHashController implements HashVisualizer {
    private final ResidueHashModel model;
    private final ResidueHashView view;
    private TreeController parentController;
    private TreeView parentView;

    // Variables para la visualización del hash
    private Integer lastInsertedKey = null;
    private Integer lastInsertedIndex = null;

    public ResidueHashController(ResidueHashModel model, ResidueHashView view) {
        this.model = model;
        this.view = view;

        // Registrar este controlador como el visualizador de hash para la vista
        this.view.setHashVisualizer(this);

        // Añadir listeners a los botones
        this.view.addInsertListener(this::insertEntry);
        this.view.addSearchListener(this::searchEntry);
        this.view.addDeleteListener(this::deleteEntry);
        this.view.addClearListener(this::clearTable);
        this.view.addBackListener(this::goBack);

        // Inicializar la visualización
        updateVisualization();
    }

    // Constructor adicional que recibe la referencia al controlador padre
    public ResidueHashController(ResidueHashModel model, ResidueHashView view,
                                 TreeController parentController, TreeView parentView) {
        this(model, view); // Llama al constructor principal
        this.parentController = parentController;
        this.parentView = parentView;
    }

    // Método setter para el controlador padre (opcional, puede ser útil)
    public void setParentController(TreeController parentController, TreeView parentView) {
        this.parentController = parentController;
        this.parentView = parentView;
    }

    // Método para insertar un elemento en la tabla hash
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
            lastInsertedKey = key;
            lastInsertedIndex = key % model.getCapacity();

            if (isNew) {
                view.setResultMessage("Elemento insertado correctamente en índice " + lastInsertedIndex, true);
            } else {
                view.setResultMessage("Elemento actualizado en índice " + lastInsertedIndex, true);
            }

            view.clearInputFields();
            updateVisualization();

            // Resaltar fila donde se insertó
            for (int i = 0; i < view.getTableRowCount(); i++) {
                if (Integer.parseInt(view.getTableValueAt(i, 0).toString()) == lastInsertedIndex) {
                    view.highlightRow(i);
                    break;
                }
            }

        } catch (NumberFormatException ex) {
            view.setResultMessage("La clave debe ser un número entero", false);
        }
    }

    // Método para buscar un elemento en la tabla hash
    private void searchEntry(ActionEvent e) {
        try {
            String keyStr = view.getSearchKey();

            if (keyStr.isEmpty()) {
                view.setResultMessage("Por favor, ingrese una clave para buscar", false);
                return;
            }

            int key = Integer.parseInt(keyStr);
            Object value = model.get(key);
            int index = key % model.getCapacity();

            lastInsertedKey = key;
            lastInsertedIndex = index;

            if (value != null) {
                view.setResultMessage("Valor encontrado: " + value + " (índice " + index + ")", true);

                // Resaltar fila donde se encontró
                for (int i = 0; i < view.getTableRowCount(); i++) {
                    if (Integer.parseInt(view.getTableValueAt(i, 0).toString()) == index) {
                        view.highlightRow(i);
                        break;
                    }
                }
            } else {
                view.setResultMessage("No existe un valor con esa clave (índice calculado: " + index + ")", false);
            }

            updateVisualization();

        } catch (NumberFormatException ex) {
            view.setResultMessage("La clave debe ser un número entero", false);
        }
    }

    // Método para eliminar un elemento de la tabla hash
    private void deleteEntry(ActionEvent e) {
        try {
            String keyStr = view.getDeleteKey();

            if (keyStr.isEmpty()) {
                view.setResultMessage("Por favor, ingrese una clave para eliminar", false);
                return;
            }

            int key = Integer.parseInt(keyStr);
            int index = key % model.getCapacity();

            boolean deleted = model.remove(key);
            lastInsertedKey = null;
            lastInsertedIndex = null;

            if (deleted) {
                view.setResultMessage("Elemento eliminado correctamente del índice " + index, true);
            } else {
                view.setResultMessage("No se encontró un elemento con esa clave", false);
            }

            view.clearInputFields();
            updateVisualization();

        } catch (NumberFormatException ex) {
            view.setResultMessage("La clave debe ser un número entero", false);
        }
    }

    // Método para limpiar la tabla hash
    private void clearTable(ActionEvent e) {
        if (model.size() == 0) {
            view.setResultMessage("La tabla ya está vacía", false);
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                view,
                "¿Está seguro de que desea eliminar todos los elementos de la tabla?",
                "Confirmar limpieza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            model.clear();
            lastInsertedKey = null;
            lastInsertedIndex = null;
            view.setResultMessage("La tabla ha sido limpiada correctamente", true);
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

    // Método para actualizar la visualización de la tabla hash
    // Cambiado a public para que pueda ser accedido desde TreeController
    public void updateVisualization() {
        view.updateHashTable(model.getTable());
        view.updateStatistics(model.getCapacity(), model.size(), model.getLoadFactor());

        // Solicitar al panel de visualización que se repinte
        JPanel visualizationPanel = view.getHashVisualizationPanel();
        visualizationPanel.repaint();
    }

    // Implementación del método de la interfaz HashVisualizer
    @Override
    public void paintHashVisualization(Graphics2D g2d, int width, int height) {
        // Limpiar el panel
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Si no hay ningún elemento para visualizar, salimos
        if (lastInsertedKey == null || lastInsertedIndex == null) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            g2d.drawString("Inserte, busque o elimine valores para ver la visualización del hash", width / 2 - 200, height / 2);
            return;
        }

        // Configuración de la visualización
        int margin = 20;
        int boxWidth = 60;
        int boxHeight = 40;

        // Dibujar el esquema de la función hash
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Dibujar la entrada (clave)
        g2d.setColor(new Color(41, 128, 185));
        Rectangle2D keyBox = new Rectangle2D.Double(margin, height / 2 - boxHeight / 2, boxWidth, boxHeight);
        g2d.fill(keyBox);

        g2d.setColor(Color.WHITE);
        String keyText = lastInsertedKey.toString();
        int keyTextWidth = g2d.getFontMetrics().stringWidth(keyText);
        g2d.drawString(keyText, margin + boxWidth / 2 - keyTextWidth / 2, height / 2 + 5);

        // Dibujar la función hash
        g2d.setColor(Color.BLACK);
        int functionX = margin + boxWidth + 20;
        String functionText = "h(k) = k mod " + model.getCapacity();
        g2d.drawString(functionText, functionX, height / 2 + 5);

        // Dibujar la flecha de la entrada a la función
        g2d.draw(new Line2D.Double(margin + boxWidth, height / 2, functionX - 5, height / 2));
        g2d.fillPolygon(
                new int[]{functionX - 5, functionX - 10, functionX - 10},
                new int[]{height / 2, height / 2 - 5, height / 2 + 5},
                3
        );

        // Dibujar el resultado (índice)
        int resultX = functionX + g2d.getFontMetrics().stringWidth(functionText) + 20;
        g2d.setColor(new Color(46, 204, 113));
        Rectangle2D indexBox = new Rectangle2D.Double(resultX, height / 2 - boxHeight / 2, boxWidth, boxHeight);
        g2d.fill(indexBox);

        g2d.setColor(Color.WHITE);
        String indexText = lastInsertedIndex.toString();
        int indexTextWidth = g2d.getFontMetrics().stringWidth(indexText);
        g2d.drawString(indexText, resultX + boxWidth / 2 - indexTextWidth / 2, height / 2 + 5);

        // Dibujar la flecha de la función al resultado
        g2d.setColor(Color.BLACK);
        int arrowStartX = functionX + g2d.getFontMetrics().stringWidth(functionText) + 5;
        g2d.draw(new Line2D.Double(arrowStartX, height / 2, resultX - 5, height / 2));
        g2d.fillPolygon(
                new int[]{resultX - 5, resultX - 10, resultX - 10},
                new int[]{height / 2, height / 2 - 5, height / 2 + 5},
                3
        );

        // Dibujar una tabla simplificada (opcional)
        int tableX = resultX + boxWidth + 20;
        int tableY = height / 2 - boxHeight * 2;
        int tableWidth = boxWidth;
        int tableHeight = boxHeight * 5;

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(tableX, tableY, tableWidth, tableHeight);

        // Dibujar algunas filas en la tabla
        for (int i = 1; i < 5; i++) {
            int rowY = tableY + i * boxHeight;
            g2d.drawLine(tableX, rowY, tableX + tableWidth, rowY);
        }

        // Destacar la fila donde se insertó el elemento
        g2d.setColor(new Color(46, 204, 113, 100));
        int highlightY = tableY + lastInsertedIndex % 5 * boxHeight;
        g2d.fillRect(tableX, highlightY, tableWidth, boxHeight);

        // Dibujar índices en la tabla
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        for (int i = 0; i < 5; i++) {
            String text = String.valueOf(i);
            int textX = tableX + 5;
            int textY = tableY + i * boxHeight + boxHeight / 2 + 5;
            g2d.drawString(text, textX, textY);
        }

        // Dibujar flecha del índice a la tabla
        g2d.draw(new Line2D.Double(resultX + boxWidth, height / 2, tableX - 5, highlightY + boxHeight / 2));
        g2d.fillPolygon(
                new int[]{tableX - 5, tableX - 10, tableX - 10},
                new int[]{highlightY + boxHeight / 2, highlightY + boxHeight / 2 - 5, highlightY + boxHeight / 2 + 5},
                3
        );
    }

    public void initView() {
        SwingUtilities.invokeLater(() -> {
            view.showWindow();
        });
    }

    public static void main(String[] args) {
        ResidueHashModel model = new ResidueHashModel(11); // Usar un número primo como tamaño inicial
        ResidueHashView view = new ResidueHashView();
        ResidueHashController controller = new ResidueHashController(model, view);
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