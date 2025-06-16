package controller.trees;

import controller.menu.TreeController;
import model.trees.ResidueHashModel;
import view.trees.ResidueHashView;
import view.trees.ResidueHashView.HashVisualizer;

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
    private JFrame parentView; // Cambiado de TreeView a JFrame

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
                                 TreeController parentController, JFrame parentView) {
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
    public void setParentController(TreeController parentController, JFrame parentView) {
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

            // Validar que la clave sea positiva
            if (key < 0) {
                view.setResultMessage("La clave debe ser un número positivo", false);
                return;
            }

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
            highlightTableRow(lastInsertedIndex);

        } catch (NumberFormatException ex) {
            view.setResultMessage("La clave debe ser un número entero válido", false);
        } catch (Exception ex) {
            view.setResultMessage("Error al insertar: " + ex.getMessage(), false);
            System.err.println("Error insertando elemento: " + ex.getMessage());
            ex.printStackTrace();
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

            // Validar que la clave sea positiva
            if (key < 0) {
                view.setResultMessage("La clave debe ser un número positivo", false);
                return;
            }

            Object value = model.get(key);
            int index = key % model.getCapacity();

            lastInsertedKey = key;
            lastInsertedIndex = index;

            if (value != null) {
                view.setResultMessage("Valor encontrado: " + value + " (índice " + index + ")", true);
                highlightTableRow(index);
            } else {
                view.setResultMessage("No existe un valor con esa clave (índice calculado: " + index + ")", false);
            }

            updateVisualization();

        } catch (NumberFormatException ex) {
            view.setResultMessage("La clave debe ser un número entero válido", false);
        } catch (Exception ex) {
            view.setResultMessage("Error al buscar: " + ex.getMessage(), false);
            System.err.println("Error buscando elemento: " + ex.getMessage());
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

            // Validar que la clave sea positiva
            if (key < 0) {
                view.setResultMessage("La clave debe ser un número positivo", false);
                return;
            }

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
                    // Limpiar las variables de último acceso después de eliminar
                    lastInsertedKey = null;
                    lastInsertedIndex = null;
                    updateVisualization();
                } else {
                    view.setResultMessage("Error al eliminar el elemento", false);
                }
            } else {
                view.setResultMessage("No se encontró un elemento con esa clave", false);
            }

            view.clearInputFields();

        } catch (NumberFormatException ex) {
            view.setResultMessage("La clave debe ser un número entero válido", false);
        } catch (Exception ex) {
            view.setResultMessage("Error al eliminar: " + ex.getMessage(), false);
            System.err.println("Error eliminando elemento: " + ex.getMessage());
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
     * Método auxiliar para resaltar una fila en la tabla.
     *
     * @param index Índice a resaltar
     */
    private void highlightTableRow(int index) {
        try {
            for (int i = 0; i < view.getTableRowCount(); i++) {
                Object cellValue = view.getTableValueAt(i, 0);
                if (cellValue != null && Integer.parseInt(cellValue.toString()) == index) {
                    view.highlightRow(i);
                    break;
                }
            }
        } catch (Exception ex) {
            System.err.println("Error resaltando fila: " + ex.getMessage());
        }
    }

    /**
     * Actualiza la visualización de la tabla hash y el árbol.
     * Este método es público para poder ser accedido desde el TreeController.
     */
    public void updateVisualization() {
        try {
            view.updateHashTable(model.getTable());
            view.updateStatistics(model.getCapacity(), model.size(), model.getLoadFactor());

            // Solicitar al panel de visualización que se repinte
            view.repaintTreePanel();
        } catch (Exception ex) {
            System.err.println("Error actualizando visualización: " + ex.getMessage());
            ex.printStackTrace();
        }
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
        try {
            // Configurar antialiasing para mejor calidad visual
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Limpiar el panel
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // Configurar el ancho del árbol
            model.setArbolWidth(width);

            // Verificar si el modelo tiene árbol inicializado
            if (model.getArbol() != null) {
                // Dibujar el árbol de residuo simple
                model.getArbol().dibujar(g2d);
            } else {
                // Mostrar mensaje cuando no hay árbol inicializado
                drawEmptyTreeMessage(g2d, width, height);
            }

            // Si no hay elementos en el modelo, mostrar información adicional
            if (model.size() == 0) {
                drawEmptyHashInfo(g2d, width, height);
            }

            // Dibujar información de la función hash si hay un último elemento insertado
            if (lastInsertedKey != null && lastInsertedIndex != null) {
                drawHashFunctionInfo(g2d, width, height);
            }

        } catch (Exception ex) {
            // Si ocurre algún error en el dibujo, mostrar un mensaje de error
            drawErrorMessage(g2d, width, height, ex);
        }
    }

    /**
     * Dibuja un mensaje cuando el árbol está vacío.
     */
    private void drawEmptyTreeMessage(Graphics2D g2d, int width, int height) {
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        FontMetrics fm = g2d.getFontMetrics();
        String message = "Inserte elementos para ver el árbol de residuo";
        int messageWidth = fm.stringWidth(message);
        g2d.drawString(message, (width - messageWidth) / 2, height / 2);
    }

    /**
     * Dibuja información adicional cuando la tabla hash está vacía.
     */
    private void drawEmptyHashInfo(Graphics2D g2d, int width, int height) {
        int centerX = width / 2;
        int centerY = 100;

        // Dibujar el círculo del nodo raíz vacío
        g2d.setColor(new Color(173, 216, 230)); // Color celeste claro
        g2d.fillOval(centerX - 30, centerY - 30, 60, 60);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawOval(centerX - 30, centerY - 30, 60, 60);

        // Añadir texto en el nodo
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String text = "Vacío";
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, centerX - textWidth / 2, centerY + 5);
    }

    /**
     * Dibuja información sobre la función hash utilizada.
     */
    private void drawHashFunctionInfo(Graphics2D g2d, int width, int height) {
        // Dibujar información de la función hash en la parte inferior
        g2d.setColor(new Color(52, 152, 219));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));

        String hashInfo = "Función Hash: " + lastInsertedKey + " mod " + model.getCapacity() + " = " + lastInsertedIndex;
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(hashInfo);

        // Dibujar fondo para el texto
        g2d.setColor(new Color(52, 152, 219, 50));
        g2d.fillRect((width - textWidth - 20) / 2, height - 50, textWidth + 20, 25);

        // Dibujar el texto
        g2d.setColor(new Color(52, 152, 219));
        g2d.drawString(hashInfo, (width - textWidth) / 2, height - 35);
    }

    /**
     * Dibuja un mensaje de error cuando ocurre una excepción durante el dibujo.
     */
    private void drawErrorMessage(Graphics2D g2d, int width, int height, Exception ex) {
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        String errorMsg = "Error al dibujar el árbol: " + ex.getMessage();
        g2d.drawString(errorMsg, 20, height / 2);

        System.err.println("Error dibujando árbol: " + ex.getMessage());
        ex.printStackTrace();
    }

    /**
     * Inicializa la vista.
     */
    public void initView() {
        SwingUtilities.invokeLater(() -> {
            view.showWindow();
        });
    }

    // Métodos auxiliares para debugging y testing
    public Integer getLastInsertedKey() {
        return lastInsertedKey;
    }

    public Integer getLastInsertedIndex() {
        return lastInsertedIndex;
    }

    public void resetLastAccessed() {
        lastInsertedKey = null;
        lastInsertedIndex = null;
        updateVisualization();
    }

    /**
     * Método principal para pruebas.
     *
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Error configurando Look and Feel: " + ex.getMessage());
            }

            try {
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
            } catch (Exception ex) {
                System.err.println("Error inicializando aplicación: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error al inicializar la aplicación: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}