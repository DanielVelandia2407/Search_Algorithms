package controller.trees;

import controller.menu.TreeController;
import model.trees.DigitalTreeModel;
import view.trees.DigitalTreeView;
import view.trees.TreeView;

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
                                 TreeController parentController, TreeView parentView) {
        this(model, view);
        this.parentController = parentController;
        this.parentView = parentView;
    }

    public void setParentController(TreeController parentController, TreeView parentView) {
        this.parentController = parentController;
        this.parentView = parentView;
    }

    private void insertWord(ActionEvent e) {
        String word = view.getWordToInsert();
        if (word.isEmpty()) {
            view.setResultMessage("Por favor, ingrese una clave", false);
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
        //view.clearInputFields();
        
    }

    private void searchWord(ActionEvent e) {
    String word = view.getWordToSearch();
    if (word.isEmpty()) {
        view.setResultMessage("Por favor, ingrese una clave para buscar", false);
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
        String[] lines = logText.toString().split("\\r?\\n");
        view.updateWordList(lines);
        view.updateTrieVisualization(model.getTrieStructure());
    }

    @Override
    public void paintTreeVisualization(Graphics2D g2d, int width, int height) {
        model.setWidth(width);
        model.getArbol().dibujar(g2d);
    }

    public void initView() {
        SwingUtilities.invokeLater(view::showWindow);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            DigitalTreeModel model = new DigitalTreeModel();
            DigitalTreeView view = new DigitalTreeView();
            new DigitalTreeController(model, view).initView();
        });
    }
}
