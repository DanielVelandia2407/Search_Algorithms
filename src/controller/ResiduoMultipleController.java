package controller;

import model.ResiduoMultipleModel;
import view.MultipleResiduoView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ResiduoMultipleController implements MultipleResiduoView.TreeVisualizer {
    private final ResiduoMultipleModel model;
    private final MultipleResiduoView view;
    private String logText = "";

    public ResiduoMultipleController(ResiduoMultipleModel model, MultipleResiduoView view) {
        this.model = model;
        this.view = view;
        view.setVisualizer(this);
        view.addInsertListener(this::onInsert);
        view.addSearchListener(this::onSearch);
        view.addDeleteListener(this::onDelete);
        view.addClearListener(this::onClear);
        view.addBackListener(e -> view.dispose());
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
