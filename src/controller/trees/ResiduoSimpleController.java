package controller.trees;

import model.trees.ResiduoSimpleModel;
import model.trees.ArbolResiduoSimple;
import view.trees.TrieResiduoSimpleView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ResiduoSimpleController implements TrieResiduoSimpleView.TreeVisualizer {

    private final ResiduoSimpleModel model;
    private final TrieResiduoSimpleView view;
    private String logText = "";
    private final controller.menu.TreeController treeController;
    private final view.menu.TreeView parentView;

    public ResiduoSimpleController(ResiduoSimpleModel model, TrieResiduoSimpleView view) {
        this(model, view, null, null);
    }

    public ResiduoSimpleController(ResiduoSimpleModel model, TrieResiduoSimpleView view,
                                   controller.menu.TreeController treeController, view.menu.TreeView parentView) {
        this.model = model;
        this.view = view;
        this.treeController = treeController;
        this.parentView = parentView;

        view.setTreeVisualizer(this);
        view.addInsertWordListener(this::insertKey);
        view.addSearchWordListener(this::searchKey);
        view.addDeleteWordListener(this::deleteKey);
        view.addClearTrieListener(this::clearAll);
        view.addBackListener(this::goBack);
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

        JTextArea area = new JTextArea();
        ArbolResiduoSimple.ResultadoBusquedaDetallada resultado =
                model.buscarConDetalle(key, area);

        logText = area.getText();

        if (resultado.encontrado) {
            view.setResultMessage(
                    "Clave '" + key + "' encontrada en nivel " + resultado.nivel,
                    true
            );
        } else {
            view.setResultMessage(
                    "Clave '" + key + "' no encontrada",
                    false
            );
        }

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

    private void goBack(ActionEvent e) {
        // Cerrar la vista actual
        view.dispose();

        // Si tenemos referencia al controlador padre, mostrar la vista del menú de árboles
        if (treeController != null && parentView != null) {
            parentView.setVisible(true);
        }
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