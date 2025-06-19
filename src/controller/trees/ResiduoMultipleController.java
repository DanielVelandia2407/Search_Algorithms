package controller.trees;

import model.trees.ResiduoMultipleModel;
import view.trees.MultipleResiduoView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ResiduoMultipleController implements MultipleResiduoView.TreeVisualizer {
    private final ResiduoMultipleModel model;
    private final MultipleResiduoView view;
    private String logText = "";
    private final controller.menu.TreeController treeController;
    private final view.menu.TreeView parentView;

    // Constructor original para compatibilidad
    public ResiduoMultipleController(ResiduoMultipleModel model, MultipleResiduoView view) {
        this(model, view, null, null);
    }

    // Constructor con navegación
    public ResiduoMultipleController(ResiduoMultipleModel model, MultipleResiduoView view,
                                     controller.menu.TreeController treeController, view.menu.TreeView parentView) {
        this.model = model;
        this.view = view;
        this.treeController = treeController;
        this.parentView = parentView;

        view.setVisualizer(this);
        view.addInsertListener(this::onInsert);
        view.addSearchListener(this::onSearch);
        view.addDeleteListener(this::onDelete);
        view.addClearListener(this::onClear);
        view.addBackListener(this::goBack);
        updateView();
    }

    private void onInsert(ActionEvent e) {
        String key = view.getInsertKey();
        if (key.isEmpty()) {
            view.setResult("Ingrese clave", false);
            return;
        }
        JTextArea area = new JTextArea();
        boolean ok = model.insert(key, area);
        logText = area.getText();
        view.setResult(ok ? "Insertada: " + key : "Ya existe: " + key, ok);
        view.setLogLines(logText.split("\\r?\\n"));
        view.refreshTree();
    }

    private void onSearch(ActionEvent e) {
        String key = view.getSearchKey();
        if (key.isEmpty()) {
            view.setResult("Ingrese clave", false);
            return;
        }
        boolean found = model.search(key);
        view.setResult(found ? "Encontrada: " + key : "No encontrada: " + key, found);
        logText = "";
        view.setLogLines(new String[]{});
    }

    private void onDelete(ActionEvent e) {
        String key = view.getDeleteKey();
        if (key.isEmpty()) {
            view.setResult("Ingrese clave", false);
            return;
        }
        JTextArea area = new JTextArea();
        boolean ok = model.delete(key, area);
        logText = area.getText();
        view.setResult(ok ? "Eliminada: " + key : "No existe: " + key, ok);
        view.setLogLines(logText.split("\\r?\\n"));
        view.refreshTree();
    }

    private void onClear(ActionEvent e) {
        int r = JOptionPane.showConfirmDialog(view, "Limpiar todo el Trie?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            model.clear();
            logText = "";
            view.setResult("Trie limpio", true);
            view.setLogLines(new String[]{});
            view.refreshTree();
        }
    }

    private void goBack(ActionEvent e) {
        // Cerrar la vista actual
        view.dispose();

        // Si tenemos referencia al controlador padre, mostrar la vista del menú de árboles
        if (treeController != null && parentView != null) {
            parentView.setVisible(true);
        }
    }

    private void updateView() {
        view.setLogLines(new String[]{ "(esperando operación)" });
        view.refreshTree();
    }

    @Override
    public void paintTreeVisualization(Graphics2D g2d, int width, int height) {
        model.setWidth(width);
        model.getArbol().dibujar(g2d);
    }

    public void init() {
        SwingUtilities.invokeLater(() -> view.setVisible(true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int tamGrupo = 2; // o el valor que quieras
            ResiduoMultipleModel model = new ResiduoMultipleModel(tamGrupo);
            MultipleResiduoView view = new MultipleResiduoView(tamGrupo);
            new ResiduoMultipleController(model, view).init();
        });
    }
}