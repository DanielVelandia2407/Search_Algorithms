package controller;

import model.PartialExpansionModel;
import view.HashExpansionView;
import view.MainView;
import view.SearchMenuView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class PartialExpansionController {
    private PartialExpansionModel model;
    private HashExpansionView view;
    private List<Integer> clavesInsertadas = new ArrayList<>();
    private MainView mainView; // Referencia al menú principal
    private SearchMenuView searchMenuView; // Referencia al menú de búsquedas

    public PartialExpansionController() {
        init();
    }

    /**
     * Establece la referencia al menú principal
     */
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    /**
     * Establece la referencia al menú de búsquedas
     */
    public void setSearchMenuView(SearchMenuView searchMenuView) {
        this.searchMenuView = searchMenuView;
    }

    /**
     * Obtiene la vista de expansión parcial
     */
    public HashExpansionView getView() {
        return view;
    }

    private void init() {
        try {
            int n = Integer.parseInt(JOptionPane.showInputDialog("Ingrese N (columnas):"));
            // Valores fijos para densidad máxima y mínima
            double dMax = 0.75;
            double dMin = 0.25;

            model = new PartialExpansionModel(n, dMax, dMin);
            view = new HashExpansionView();

            // Cambiar el título para diferenciar de la expansión total
            view.setTitle("Expansión y Reducción Parcial");

            view.setTabla(model.getTabla(), model.getColisiones());
            view.getBotonInsertar().addActionListener(this::onInsert);
            view.getBotonEliminar().addActionListener(this::onDelete);

            // Botón "Volver" regresa al menú de búsquedas
            view.getBotonVolver().addActionListener(e -> {
                view.setVisible(false);
                if (searchMenuView != null) {
                    searchMenuView.setVisible(true);
                } else if (mainView != null) {
                    mainView.setVisible(true);
                }
            });

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Parámetros inválidos.");
            // Si hay error en la inicialización, regresar al menú correspondiente
            if (searchMenuView != null) {
                searchMenuView.setVisible(true);
            } else if (mainView != null) {
                mainView.setVisible(true);
            }
        }
    }

    /**
     * Muestra la ventana de expansión parcial
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

        // Verificar si excederá la densidad máxima antes de insertar
        int ocupActual = model.obtenerOcupacionActual();
        double densDesp = (double) (ocupActual + 1) / (2 * model.getColumnas());
        boolean excedeDensidad = densDesp > model.getDensidadMaxInsert();

        if (excedeDensidad) {
            // Primero insertar la clave (forzar inserción)
            List<String> pasos = model.insertarForzado(clave);
            view.mostrarPasos(pasos.toArray(new String[0]));
            clavesInsertadas.add(clave);
            view.setTabla(model.getTabla(), model.getColisiones());

            // Luego preguntar si quiere expandir
            int r = JOptionPane.showConfirmDialog(
                    view,
                    "La densidad máxima ha sido superada.\n¿Desea expandir la estructura?",
                    "Expansión parcial",
                    JOptionPane.YES_NO_OPTION
            );

            if (r == JOptionPane.YES_OPTION) {
                model.expandir(clavesInsertadas);
                view.mostrarPasos(new String[]{"Expandiendo estructura..."});
                for (int k : new ArrayList<>(clavesInsertadas)) {
                    List<String> p2 = model.insertar(k);
                    view.mostrarPasos(p2.toArray(new String[0]));
                }
            }
        } else {
            // Inserción normal
            List<String> pasos = model.insertar(clave);
            view.mostrarPasos(pasos.toArray(new String[0]));
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

        // Paso 1: Simular eliminación para obtener DOR hipotético
        List<String> pasosPreview = model.eliminar(clave, true);
        view.mostrarPasos(pasosPreview.toArray(new String[0])); // Mostrar cálculos

        boolean reducible = pasosPreview.stream()
                .anyMatch(p -> p.contains("Se caerá por debajo de la densidad mínima."));

        if (reducible) {
            int r = JOptionPane.showConfirmDialog(
                    view,
                    "La densidad es baja.\n¿Desea reducir la estructura?",
                    "Reducción parcial",
                    JOptionPane.YES_NO_OPTION
            );

            if (r == JOptionPane.YES_OPTION) {
                // Eliminar clave y reducir
                List<String> pasosEliminar = model.eliminar(clave, false);
                view.mostrarPasos(pasosEliminar.toArray(new String[0]));
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
            // Eliminar normalmente
            List<String> pasos = model.eliminar(clave, false);
            view.mostrarPasos(pasos.toArray(new String[0]));
            clavesInsertadas.remove((Integer) clave);
        }

        view.setTabla(model.getTabla(), model.getColisiones());
    }
}