package expansiontotal.controller;

import expansiontotal.model.HashExpansionModel;
import expansiontotal.view.HashExpansionView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class HashExpansionController {
    private HashExpansionModel model;
    private HashExpansionView view;
    private List<Integer> clavesInsertadas;

    public HashExpansionController() {
        clavesInsertadas = new ArrayList<>();
        inicializarParametros();
    }

    private void inicializarParametros() {
        try {
            int columnas = Integer.parseInt(JOptionPane.showInputDialog("Ingrese N (tamaño de la estructura - columnas):"));
            double densidad = Double.parseDouble(JOptionPane.showInputDialog("Ingrese densidad máxima de ocupación (ej: 0.75):"));

            this.model = new HashExpansionModel(columnas, densidad);
            this.view = new HashExpansionView();

            view.setTabla(model.getEstructura(), model.getColisiones());
            view.getBotonInsertar().addActionListener(this::insertarClave);
            view.getBotonVolver().addActionListener(e -> view.dispose());

            view.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en los parámetros iniciales.");
        }
    }

    private void insertarClave(ActionEvent e) {
        try {
            String texto = view.getClaveTexto();
            if (texto.length() != 2) {
                JOptionPane.showMessageDialog(view, "La clave debe tener 2 dígitos.");
                return;
            }
            int clave = Integer.parseInt(texto);
            view.limpiarCampo();

            List<String> pasos = model.insertarClave(clave);
            boolean necesitaExpansion = pasos.stream().anyMatch(p -> p.contains("densidad máxima"));

            if (necesitaExpansion) {
                view.setTabla(model.getEstructura(), model.getColisiones());
                view.mostrarPasos(pasos.toArray(new String[0]));
                JOptionPane.showMessageDialog(view, "No se pueden insertar más claves.\n¿Desea expandir la estructura?");
                int respuesta = JOptionPane.showConfirmDialog(view, "¿Desea expandir la estructura?", "Expansión", JOptionPane.YES_NO_OPTION);

                if (respuesta == JOptionPane.YES_OPTION) {
                    model.expandirEstructura(clavesInsertadas);
                    clavesInsertadas.add(clave);
                    pasos = model.insertarClave(clave);
                    view.mostrarPasos(pasos.toArray(new String[0]));
                    view.setTabla(model.getEstructura(), model.getColisiones());
                }
            } else {
                view.mostrarPasos(pasos.toArray(new String[0]));
                view.setTabla(model.getEstructura(), model.getColisiones());
                if (!pasos.contains("La estructura no admite claves repetidas.")) {
                    clavesInsertadas.add(clave);
                } else {
                    JOptionPane.showMessageDialog(view, "La estructura no admite claves repetidas.");
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Clave inválida.");
        }
    }
}
