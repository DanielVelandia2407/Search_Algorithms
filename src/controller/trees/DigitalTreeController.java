package controller.trees;

import controller.menu.TreeController;
import model.trees.DigitalTreeModel;
import view.trees.DigitalTreeView;

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
    private JFrame parentView; // Cambiado de TreeView a JFrame
    private StringBuilder logText = new StringBuilder();

    public DigitalTreeController(DigitalTreeModel model, DigitalTreeView view) {
        this.model = model;
        this.view = view;

        this.view.setTreeVisualizer(this);

        this.view.addInsertWordListener(this::insertWord);
        this.view.addSearchWordListener(this::searchWord);
        this.view.addDeleteWordListener(this::deleteWord);
        this.view.addClearTrieListener(this::clearTrie);
        this.view.addBackListener(this::goBack);

        updateVisualization();
    }

    public DigitalTreeController(DigitalTreeModel model, DigitalTreeView view,
                                 TreeController parentController, JFrame parentView) {
        this(model, view);
        this.parentController = parentController;
        this.parentView = parentView;
    }

    public void setParentController(TreeController parentController, JFrame parentView) {
        this.parentController = parentController;
        this.parentView = parentView;
    }

    private void insertWord(ActionEvent e) {
        String word = view.getWordToInsert();
        if (word.isEmpty()) {
            view.setResultMessage("Por favor, ingrese una clave", false);
            return;
        }

        // Validar que la palabra solo contenga caracteres válidos (letras y números)
        if (!word.matches("[a-zA-Z0-9]+")) {
            view.setResultMessage("La clave solo puede contener letras y números", false);
            return;
        }

        JTextArea logArea = new JTextArea();
        boolean isNewWord = model.insert(word, logArea);
        view.setResultMessage(
                isNewWord ? "Clave \"" + word + "\" insertada correctamente"
                        : "La clave \"" + word + "\" ya existe en el árbol",
                isNewWord
        );
        logText.append(logArea.getText()).append("\n\n");
        updateVisualization();
        // view.clearInputFields(); // Comentado como en el original
    }

    private void searchWord(ActionEvent e) {
        String word = view.getWordToSearch();
        if (word.isEmpty()) {
            view.setResultMessage("Por favor, ingrese una clave para buscar", false);
            return;
        }

        // Validar que la palabra solo contenga caracteres válidos
        if (!word.matches("[a-zA-Z0-9]+")) {
            view.setResultMessage("La clave solo puede contener letras y números", false);
            return;
        }

        JTextArea logArea = new JTextArea();
        int nivel = model.getArbol().buscarYNivel(word, logArea);

        boolean found = (nivel != -1);
        String message = found ? "Clave \"" + word + "\" encontrada en nivel " + nivel
                : "Clave \"" + word + "\" no encontrada";
        view.setResultMessage(message, found);
        logText.append(logArea.getText()).append("\n");
        updateVisualization();
    }

    private void deleteWord(ActionEvent e) {
        String word = view.getWordToDelete();
        if (word.isEmpty()) {
            view.setResultMessage("Por favor, ingrese una clave para eliminar", false);
            return;
        }

        // Validar que la palabra solo contenga caracteres válidos
        if (!word.matches("[a-zA-Z0-9]+")) {
            view.setResultMessage("La clave solo puede contener letras y números", false);
            return;
        }

        JTextArea logArea = new JTextArea();
        boolean deleted = model.delete(word, logArea);
        view.setResultMessage(
                deleted ? "Clave \"" + word + "\" eliminada correctamente"
                        : "Clave \"" + word + "\" no encontrada",
                deleted
        );
        logText.append(logArea.getText()).append("\n");
        view.clearInputFields();
        updateVisualization();
    }

    private void clearTrie(ActionEvent e) {
        int option = JOptionPane.showConfirmDialog(
                view,
                "¿Está seguro de que desea eliminar todas las claves del árbol?",
                "Confirmar limpieza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (option == JOptionPane.YES_OPTION) {
            model.clear();
            view.setResultMessage("El árbol ha sido limpiado correctamente", true);
            view.clearInputFields();
            logText.setLength(0); // Limpiar el log también
            logText.append("Árbol limpiado\n");
            updateVisualization();
        }
    }

    private void goBack(ActionEvent e) {
        view.dispose();
        if (parentView != null) {
            SwingUtilities.invokeLater(() -> parentView.setVisible(true));
        }
    }

    private void updateVisualization() {
        try {
            String[] lines = logText.toString().split("\\r?\\n");
            view.updateWordList(lines);
            view.updateTrieVisualization(model.getTrieStructure());
        } catch (Exception ex) {
            // Manejar cualquier excepción durante la actualización
            System.err.println("Error actualizando visualización: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void paintTreeVisualization(Graphics2D g2d, int width, int height) {
        try {
            // Configurar antialias para mejor calidad visual
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Limpiar el área de dibujo
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            // Configurar el ancho para el modelo
            model.setWidth(width);

            // Verificar si el árbol tiene contenido antes de dibujar
            if (model.getArbol() != null) {
                model.getArbol().dibujar(g2d);
            } else {
                // Mostrar mensaje cuando el árbol está vacío
                g2d.setColor(Color.GRAY);
                g2d.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                FontMetrics fm = g2d.getFontMetrics();
                String message = "Inserte palabras para ver el árbol digital";
                int messageWidth = fm.stringWidth(message);
                g2d.drawString(message, (width - messageWidth) / 2, height / 2);
            }
        } catch (Exception ex) {
            // Manejar cualquier excepción durante el dibujo
            System.err.println("Error dibujando árbol: " + ex.getMessage());
            ex.printStackTrace();

            // Dibujar mensaje de error
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2d.drawString("Error al dibujar el árbol", 10, height / 2);
        }
    }

    public void initView() {
        SwingUtilities.invokeLater(view::showWindow);
    }

    // Método para limpiar el log (útil para testing o reset)
    public void clearLog() {
        logText.setLength(0);
        updateVisualization();
    }

    // Método para obtener el log actual (útil para debugging)
    public String getCurrentLog() {
        return logText.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Error configurando Look and Feel: " + ex.getMessage());
                ex.printStackTrace();
            }

            try {
                DigitalTreeModel model = new DigitalTreeModel();
                DigitalTreeView view = new DigitalTreeView();
                new DigitalTreeController(model, view).initView();
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