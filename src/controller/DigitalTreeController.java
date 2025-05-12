package controller;

import model.DigitalTreeModel;
import view.DigitalTreeView;
import view.TreeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Controlador para el Árbol Digital.
 * Se encarga de coordinar el modelo y la vista, y de manejar los eventos de usuario.
 */
public class DigitalTreeController implements DigitalTreeView.TreeVisualizer {
    private final DigitalTreeModel model;
    private final DigitalTreeView view;
    private TreeController parentController;
    private TreeView parentView;

    // Variable para el texto que se mostrará en el panel de visualización
    private String logText = "";

    /**
     * Constructor principal.
     *
     * @param model Modelo del árbol digital
     * @param view Vista del árbol digital
     */
    public DigitalTreeController(DigitalTreeModel model, DigitalTreeView view) {
        this.model = model;
        this.view = view;

        // Configurar la vista para usar este controlador como visualizador
        this.view.setTreeVisualizer(this);

        // Agregar listeners a los botones
        this.view.addInsertWordListener(this::insertWord);
        this.view.addSearchWordListener(this::searchWord);
        this.view.addDeleteWordListener(this::deleteWord);
        this.view.addClearTrieListener(this::clearTrie);
        this.view.addBackListener(this::goBack);

        // Actualizar visualización inicial
        updateVisualization();
    }

    /**
     * Constructor adicional que recibe referencias al controlador y vista padre.
     *
     * @param model Modelo del árbol digital
     * @param view Vista del árbol digital
     * @param parentController Controlador padre
     * @param parentView Vista padre
     */
    public DigitalTreeController(DigitalTreeModel model, DigitalTreeView view,
                                 TreeController parentController, TreeView parentView) {
        this(model, view);
        this.parentController = parentController;
        this.parentView = parentView;
    }

    /**
     * Configura el controlador y vista padre.
     *
     * @param parentController Controlador padre
     * @param parentView Vista padre
     */
    public void setParentController(TreeController parentController, TreeView parentView) {
        this.parentController = parentController;
        this.parentView = parentView;
    }

    /**
     * Maneja el evento de insertar una palabra.
     *
     * @param e Evento de acción
     */
    private void insertWord(ActionEvent e) {
        String word = view.getWordToInsert();

        if (word.isEmpty()) {
            view.setResultMessage("Por favor, ingrese una palabra", false);
            return;
        }

        // Crear un área de texto temporal para capturar los mensajes de log
        JTextArea logArea = new JTextArea();

        // Insertar la palabra y obtener si es nueva
        boolean isNewWord = model.insert(word);

        if (isNewWord) {
            view.setResultMessage("Palabra \"" + word + "\" insertada correctamente", true);
            logText = logArea.getText();
        } else {
            view.setResultMessage("La palabra \"" + word + "\" ya existe en el árbol", false);
        }

        view.clearInputFields();
        updateVisualization();
    }

    /**
     * Maneja el evento de buscar una palabra.
     *
     * @param e Evento de acción
     */
    private void searchWord(ActionEvent e) {
        String word = view.getWordToSearch();

        if (word.isEmpty()) {
            view.setResultMessage("Por favor, ingrese una palabra para buscar", false);
            return;
        }

        boolean found = model.search(word);

        if (found) {
            view.setResultMessage("Palabra \"" + word + "\" encontrada en el árbol", true);
        } else {
            view.setResultMessage("Palabra \"" + word + "\" no encontrada", false);
        }

        updateVisualization();
    }

    /**
     * Maneja el evento de eliminar una palabra.
     *
     * @param e Evento de acción
     */
    private void deleteWord(ActionEvent e) {
        String word = view.getWordToDelete();

        if (word.isEmpty()) {
            view.setResultMessage("Por favor, ingrese una palabra para eliminar", false);
            return;
        }

        // Crear un área de texto temporal para capturar los mensajes de log
        JTextArea logArea = new JTextArea();

        boolean deleted = model.delete(word);

        if (deleted) {
            view.setResultMessage("Palabra \"" + word + "\" eliminada correctamente", true);
            logText = logArea.getText();
        } else {
            view.setResultMessage("Palabra \"" + word + "\" no encontrada", false);
        }

        view.clearInputFields();
        updateVisualization();
    }

    /**
     * Maneja el evento de limpiar todo el árbol.
     *
     * @param e Evento de acción
     */
    private void clearTrie(ActionEvent e) {
        int option = JOptionPane.showConfirmDialog(
                view,
                "¿Está seguro de que desea eliminar todas las palabras del árbol?",
                "Confirmar limpieza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            model.clear();
            view.setResultMessage("El árbol ha sido limpiado correctamente", true);
            view.clearInputFields();
            logText = "";
            updateVisualization();
        }
    }

    /**
     * Maneja el evento de volver al menú anterior.
     *
     * @param e Evento de acción
     */
    private void goBack(ActionEvent e) {
        view.dispose();
        if (parentView != null) {
            SwingUtilities.invokeLater(() -> {
                parentView.setVisible(true);
            });
        }
    }

    /**
     * Actualiza la visualización del árbol.
     */
    private void updateVisualization() {
        // Actualizar la estructura del árbol para la vista
        view.updateTrieVisualization(model.getTrieStructure());

        // Actualizar la lista de palabras (vacía en esta implementación)
        view.updateWordList(model.getAllWords());
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
     * Método para pintar la visualización del árbol.
     * Este método es llamado por la vista para dibujar el árbol.
     *
     * @param g2d Contexto gráfico 2D
     * @param width Ancho del área de dibujo
     * @param height Alto del área de dibujo
     */
    @Override
    public void paintTreeVisualization(Graphics2D g2d, int width, int height) {
        // Configura el ancho del área de dibujo en el árbol
        model.setWidth(width);

        // Dibuja el árbol
        model.getArbol().dibujar(g2d);

        // Si hay texto de log, mostrarlo en la parte inferior
        if (logText != null && !logText.isEmpty()) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));

            String[] lines = logText.split("\n");
            int y = height - 20 * lines.length;

            for (String line : lines) {
                g2d.drawString(line, 20, y);
                y += 15;
            }
        }
    }

    /**
     * Método main para pruebas independientes.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            DigitalTreeModel model = new DigitalTreeModel();
            DigitalTreeView view = new DigitalTreeView();
            DigitalTreeController controller = new DigitalTreeController(model, view);

            controller.initView();
        });
    }
}