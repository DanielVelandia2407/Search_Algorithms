package controller;

import model.ResidueHashModel;
import view.ResidueHashView;
import view.TreeView;
import view.ResidueHashView.HashVisualizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Controlador para la visualización y manipulación del árbol de residuo simple.
 * Implementa la interfaz HashVisualizer para dibujar la visualización del hash.
 */
public class ResidueHashController implements HashVisualizer {
    private final ResidueHashModel model;
    private final ResidueHashView view;
    private TreeController parentController;
    private TreeView parentView;

    // Variables para la visualización del hash
    private Integer lastInsertedKey = null;
    private Integer lastInsertedIndex = null;
    private boolean showArbolVisualization = true;  // Siempre true para mostrar el árbol

    /**
     * Constructor principal.
     *
     * @param model Modelo del árbol de residuo simple
     * @param view Vista para la visualización
     */
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

    /**
     * Constructor adicional que recibe la referencia al controlador padre.
     *
     * @param model Modelo del árbol de residuo simple
     * @param view Vista para la visualización
     * @param parentController Controlador padre
     * @param parentView Vista padre
     */
    public ResidueHashController(ResidueHashModel model, ResidueHashView view,
                                 TreeController parentController, TreeView parentView) {
        this(model, view); // Llama al constructor principal
        this.parentController = parentController;
        this.parentView = parentView;
    }

    /**
     * Método setter para el controlador padre.
     *
     * @param parentController Controlador padre
     * @param parentView Vista padre
     */
    public void setParentController(TreeController parentController, TreeView parentView) {
        this.parentController = parentController;
        this.parentView = parentView;
    }

    /**
     * Maneja el evento de insertar un elemento en la tabla hash.
     *
     * @param e Evento de acción
     */
    private void insertEntry(ActionEvent e) {
        try {
            String keyStr = view.getKey();
            String value = view.getValue();

            if (keyStr.isEmpty() || value.isEmpty()) {
                view.setResultMessage("Por favor, ingrese clave y valor", false);
                return;
            }

            int key = Integer.parseInt(keyStr);

            // Verificar si ya existe esta clave
            Object existingValue = model.get(key);

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
                if (view.getTableValueAt(i, 0) != null &&
                        Integer.parseInt(view.getTableValueAt(i, 0).toString()) == lastInsertedIndex) {
                    view.highlightRow(i);
                    break;
                }
            }

        } catch (NumberFormatException ex) {
            view.setResultMessage("La clave debe ser un número entero", false);
        } catch (Exception ex) {
            view.setResultMessage("Error al insertar: " + ex.getMessage(), false);
            ex.printStackTrace(); // Debug
        }
    }

    /**
     * Maneja el evento de buscar un elemento en la tabla hash.
     *
     * @param e Evento de acción
     */
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
                    if (view.getTableValueAt(i, 0) != null &&
                            Integer.parseInt(view.getTableValueAt(i, 0).toString()) == index) {
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
        } catch (Exception ex) {
            view.setResultMessage("Error al buscar: " + ex.getMessage(), false);
        }
    }

    /**
     * Maneja el evento de eliminar un elemento de la tabla hash.
     *
     * @param e Evento de acción
     */
    private void deleteEntry(ActionEvent e) {
        try {
            String keyStr = view.getDeleteKey();

            if (keyStr.isEmpty()) {
                view.setResultMessage("Por favor, ingrese una clave para eliminar", false);
                return;
            }

            int key = Integer.parseInt(keyStr);
            int index = key % model.getCapacity();

            // Verificar si existe antes de eliminar
            Object value = model.get(key);
            boolean elementExists = (value != null);

            if (elementExists) {
                lastInsertedKey = key;
                lastInsertedIndex = index;

                // Mostrar visualización con el elemento antes de eliminarlo
                updateVisualization();

                // Eliminar el elemento
                boolean deleted = model.remove(key);
                if (deleted) {
                    view.setResultMessage("Elemento eliminado correctamente del índice " + index, true);
                    updateVisualization();
                }
            } else {
                view.setResultMessage("No se encontró un elemento con esa clave", false);
            }

            view.clearInputFields();

        } catch (NumberFormatException ex) {
            view.setResultMessage("La clave debe ser un número entero", false);
        } catch (Exception ex) {
            view.setResultMessage("Error al eliminar: " + ex.getMessage(), false);
        }
    }

    /**
     * Maneja el evento de limpiar la tabla hash.
     *
     * @param e Evento de acción
     */
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

    /**
     * Maneja el evento de volver a la pantalla anterior.
     *
     * @param e Evento de acción
     */
    private void goBack(ActionEvent e) {
        view.dispose();

        // Verificar si existe una vista padre y mostrarla
        if (parentView != null) {
            SwingUtilities.invokeLater(() -> {
                parentView.setVisible(true);
            });
        }
    }

    /**
     * Actualiza la visualización de la tabla hash y el árbol.
     * Este método es público para poder ser accedido desde el TreeController.
     */
    public void updateVisualization() {
        view.updateHashTable(model.getTable());
        view.updateStatistics(model.getCapacity(), model.size(), model.getLoadFactor());

        // Solicitar al panel de visualización que se repinte
        view.repaintTreePanel();
    }

    /**
     * Implementación del método de la interfaz HashVisualizer.
     * Este método se llama cuando la vista necesita dibujar la visualización del hash.
     *
     * @param g2d Contexto gráfico 2D
     * @param width Ancho del panel
     * @param height Alto del panel
     */
    @Override
    public void paintHashVisualization(Graphics2D g2d, int width, int height) {
        // Limpiar el panel
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        try {
            // Configurar el ancho del árbol
            model.setArbolWidth(width);

            // Dibujar el árbol de residuo simple
            model.getArbol().dibujar(g2d);

            // Si no hay elementos en el modelo, dibujamos un nodo raíz vacío
            if (model.size() == 0) {
                // Dibujamos un nodo raíz vacío
                int centerX = width / 2;
                int centerY = 100;

                // Dibujar el círculo
                g2d.setColor(new Color(173, 216, 230)); // Color celeste claro
                g2d.fillOval(centerX - 30, centerY - 30, 60, 60);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(centerX - 30, centerY - 30, 60, 60);
            }
        } catch (Exception ex) {
            // Si ocurre algún error en el dibujo, mostrar un mensaje
            g2d.setColor(Color.RED);
            g2d.drawString("Error al dibujar el árbol: " + ex.getMessage(), 20, 50);
            ex.printStackTrace(); // Debug
        }
    }

    /**
     * Inicializa la vista.
     */
    public void initView() {
        SwingUtilities.invokeLater(() -> {
            view.showWindow();
        });
    }

    /**
     * Método principal para pruebas.
     *
     * @param args Argumentos de línea de comandos
     */
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