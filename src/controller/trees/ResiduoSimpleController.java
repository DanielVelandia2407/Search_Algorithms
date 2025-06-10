package controller.trees;

import model.trees.ResiduoSimpleModel;
import view.trees.TrieResiduoSimpleView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ResiduoSimpleController implements TrieResiduoSimpleView.TreeVisualizer {
    private final ResiduoSimpleModel model;
    private final TrieResiduoSimpleView view;
    private String logText = "";

    public ResiduoSimpleController(ResiduoSimpleModel model, TrieResiduoSimpleView view) {
        this.model = model;
        this.view = view;

        view.setTreeVisualizer(this);
        view.addInsertWordListener(this::insertKey);
        view.addSearchWordListener(this::searchKey);
        view.addDeleteWordListener(this::deleteKey);
        view.addClearTrieListener(this::clearAll);
        view.addBackListener(e -> view.dispose());
        updateView();
    }

    private void insertKey(ActionEvent e) {
        String key = view.getWordToInsert();
        if (key.isEmpty()) {
            view.setResultMessage("Ingrese una clave", false);
            return;
        }
        JTextArea area = new JTextArea();
        boolean ok = model.insert(key, area);
        logText = area.getText();

        view.setResultMessage(
            ok ? "Clave '" + key + "' insertada" : "Clave '" + key + "' ya existe",
            ok
        );
        view.clearInputFields();
        updateView();
    }

    private void searchKey(ActionEvent e) {
        String key = view.getWordToSearch();
        if (key.isEmpty()) {
            view.setResultMessage("Ingrese una clave para buscar", false);
            return;
        }
        boolean found = model.search(key);
        view.setResultMessage(
            found ? "Clave '" + key + "' encontrada" : "Clave '" + key + "' no encontrada",
            found
        );
        logText = "";
        updateView();
    }

    private void deleteKey(ActionEvent e) {
        String key = view.getWordToDelete();
        if (key.isEmpty()) {
            view.setResultMessage("Ingrese una clave para eliminar", false);
            return;
        }
        JTextArea area = new JTextArea();
        boolean ok = model.delete(key, area);
        logText = area.getText();

        view.setResultMessage(
            ok ? "Clave '" + key + "' eliminada" : "Clave '" + key + "' no existe",
            ok
        );
        view.clearInputFields();
        updateView();
    }

    private void clearAll(ActionEvent e) {
        int resp = JOptionPane.showConfirmDialog(
            view, "¿Borrar todo el Trie de Residuos?", "Confirmar", JOptionPane.YES_NO_OPTION
        );
        if (resp == JOptionPane.YES_OPTION) {
            model.clear();
            logText = "";
            view.setResultMessage("Trie limpio", true);
            updateView();
        }
    }

    private void updateView() {
        // mostramos el log de operaciones en lugar de lista de claves
        String[] lines = logText.isEmpty()
            ? new String[] { "(sin operaciones recientes)" }
            : logText.split("\\r?\\n");
        view.updateWordList(lines);
        view.updateTrieVisualization(null); // aquí pasaría estructura si quisiéramos
    }

    @Override
    public void paintTreeVisualization(Graphics2D g2d, int width, int height) {
        model.setWidth(width);
        model.getArbol().dibujar(g2d);
    }

    public void init() {
        SwingUtilities.invokeLater(view::showWindow);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ResiduoSimpleModel model = new ResiduoSimpleModel();
            TrieResiduoSimpleView view = new TrieResiduoSimpleView();
            new ResiduoSimpleController(model, view).init();
        });
    }
}

