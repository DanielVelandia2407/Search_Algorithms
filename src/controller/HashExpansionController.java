package controller;

import model.HashExpansionModel;
import view.HashExpansionView;
import view.MainView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class HashExpansionController {
    private HashExpansionModel model;
    private HashExpansionView view;
    private List<Integer> clavesInsertadas = new ArrayList<>();
    private MainView mainView; // Referencia al menú principal

    public HashExpansionController() {
        init();
    }

    /**
     * Establece la referencia al menú principal
     */
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    /**
     * Obtiene la vista de expansión hash
     */
    public HashExpansionView getView() {
        return view;
    }

    private void init() {
        try {
            int n = Integer.parseInt(JOptionPane.showInputDialog("Ingrese N (columnas):"));
            double dMax = Double.parseDouble(JOptionPane.showInputDialog("Ingrese densidad MÁXIMA (0-1):"));
            double dMin = Double.parseDouble(JOptionPane.showInputDialog("Ingrese densidad MÍNIMA (0-1):"));

            model = new HashExpansionModel(n, dMax, dMin);
            view = new HashExpansionView();

            view.setTabla(model.getTabla(), model.getColisiones());
            view.getBotonInsertar().addActionListener(this::onInsert);
            view.getBotonEliminar().addActionListener(this::onDelete);

            // Modificamos el botón "Volver" para regresar al menú principal
            view.getBotonVolver().addActionListener(e -> {
                view.setVisible(false);
                if (mainView != null) {
                    mainView.setVisible(true);
                }
            });

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Parámetros inválidos.");
            // Si hay error en la inicialización, regresar al menú principal
            if (mainView != null) {
                mainView.setVisible(true);
            }
        }
    }

    /**
     * Muestra la ventana de expansión hash
     */
    public void showView() {
        if (view != null) {
            view.setVisible(true);
        }
    }

    private void onInsert(ActionEvent e) {
        String txt = view.getClaveTexto();
        if (txt.length() != 2) {
            JOptionPane.showMessageDialog(view, "La clave debe tener 2 dígitos.");
            return;
        }

        int clave;
        try {
            clave = Integer.parseInt(txt);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Clave inválida.");
            return;
        }

        view.limpiarCampo();

        if (clavesInsertadas.contains(clave)) {
            JOptionPane.showMessageDialog(view, "La estructura no admite claves repetidas.");
            return;
        }

        List<String> pasos = model.insertar(clave);
        view.mostrarPasos(pasos.toArray(new String[0]));

        boolean excede = pasos.stream().anyMatch(p -> p.contains("Se superará la densidad máxima."));

        if (excede) {
            int r = JOptionPane.showConfirmDialog(
                    view,
                    "Se superará la densidad máxima.\n¿Desea expandir la estructura?",
                    "Expansión total",
                    JOptionPane.YES_NO_OPTION
            );
            if (r == JOptionPane.YES_OPTION) {
                model.expandir(clavesInsertadas);
                for (int k : new ArrayList<>(clavesInsertadas)) {
                    List<String> p2 = model.insertar(k);
                    view.mostrarPasos(p2.toArray(new String[0]));
                }
                pasos = model.insertar(clave);
                view.mostrarPasos(pasos.toArray(new String[0]));
                clavesInsertadas.add(clave);
            } else {
                view.mostrarPasos(new String[]{"Operación cancelada. No se insertó la clave."});
            }
        } else {
            clavesInsertadas.add(clave);
        }

        view.setTabla(model.getTabla(), model.getColisiones());
    }

    private void onDelete(ActionEvent e) {
        String txt = view.getClaveTexto();
        if (txt.length() != 2) {
            JOptionPane.showMessageDialog(view, "La clave debe tener 2 dígitos.");
            return;
        }

        int clave;
        try {
            clave = Integer.parseInt(txt);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Clave inválida.");
            return;
        }

        view.limpiarCampo();

        if (!clavesInsertadas.contains(clave)) {
            JOptionPane.showMessageDialog(view, "La clave no existe en la estructura.");
            return;
        }

        List<String> pasosPreview = model.eliminar(clave, true);
        boolean reducible = pasosPreview.stream()
                .anyMatch(p -> p.contains("Se caerá por debajo de la densidad mínima."));

        if (reducible) {
            int r = JOptionPane.showConfirmDialog(
                    view,
                    "La densidad es baja.\n¿Desea reducir la estructura?",
                    "Reducción total",
                    JOptionPane.YES_NO_OPTION
            );

            if (r == JOptionPane.YES_OPTION) {
                model.eliminar(clave, false);
                clavesInsertadas.remove((Integer) clave);

                model.reducir(clavesInsertadas);
                for (int k : new ArrayList<>(clavesInsertadas)) {
                    List<String> p2 = model.insertar(k);
                    view.mostrarPasos(p2.toArray(new String[0]));
                }
            } else {
                view.mostrarPasos(new String[]{"Operación cancelada. No se eliminó la clave."});
                return;
            }
        } else {
            List<String> pasos = model.eliminar(clave, false);
            view.mostrarPasos(pasos.toArray(new String[0]));
            clavesInsertadas.remove((Integer) clave);
        }

        view.setTabla(model.getTabla(), model.getColisiones());
    }
}