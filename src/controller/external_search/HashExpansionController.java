package controller.external_search;

import model.external_search.HashExpansionModel;
import view.external_search.HashExpansionView;
import view.menu.MainView;
import view.menu.SearchMenuView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class HashExpansionController {
    private HashExpansionModel model;
    private HashExpansionView view;
    private List<Integer> clavesInsertadas = new ArrayList<>();
    private MainView mainView; // Referencia al menú principal
    private SearchMenuView searchMenuView; // NUEVA: Referencia al menú de búsquedas

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
     * Establece la referencia al menú de búsquedas (NUEVO)
     */
    public void setSearchMenuView(SearchMenuView searchMenuView) {
        this.searchMenuView = searchMenuView;
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
            // Valores fijos para densidad
            double dMax = 0.74;
            double dMin = 0.20;

            model = new HashExpansionModel(n, dMax, dMin);
            view = new HashExpansionView();

            view.setTabla(model.getTabla(), model.getColisiones());
            view.getBotonInsertar().addActionListener(this::onInsert);
            view.getBotonEliminar().addActionListener(this::onDelete);

            // MODIFICADO: Botón "Volver" ahora regresa al menú de búsquedas si está disponible
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

        // Verificar si se superará la densidad máxima ANTES de insertar
        int ocupActual = clavesInsertadas.size() + contarColisionesActuales();
        double densDesp = (double) (ocupActual + 1) / (2 * model.getColumnas());
        boolean excederaDensidad = densDesp > model.getDensidadMaxInsert();

        // Insertar la clave primero, independientemente de la densidad
        List<String> pasos = model.insertar(clave);
        view.mostrarPasos(pasos.toArray(new String[0]));
        
        // Agregar la clave a la lista de insertadas
        clavesInsertadas.add(clave);
        
        // Actualizar la vista con la clave insertada
        view.setTabla(model.getTabla(), model.getColisiones());

        // DESPUÉS de insertar y mostrar, verificar si necesita expansión
        if (excederaDensidad) {
            // Mostrar mensaje de que se va a expandir
            JOptionPane.showMessageDialog(
                    view,
                    "Se ha superado la densidad máxima.\nLa estructura se expandirá automáticamente.",
                    "Expansión automática",
                    JOptionPane.INFORMATION_MESSAGE
            );
            
            // Expandir la estructura
            model.expandir(clavesInsertadas);
            
            // Limpiar la lista temporal para reinsertar
            List<Integer> clavesTemp = new ArrayList<>(clavesInsertadas);
            clavesInsertadas.clear();
            
            // Reinsertar todas las claves en la estructura expandida
            for (int k : clavesTemp) {
                List<String> p2 = model.insertar(k);
                view.mostrarPasos(p2.toArray(new String[0]));
                clavesInsertadas.add(k);
            }
            
            // Actualizar la vista final
            view.setTabla(model.getTabla(), model.getColisiones());
        }
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
    
    /**
     * Método auxiliar para contar las colisiones actuales
     */
    private int contarColisionesActuales() {
        int total = 0;
        for (List<Integer> lista : model.getColisiones()) {
            total += lista.size();
        }
        return total;
    }
}