package controller.indexes;

import model.indexes.IndiceSecundarioModelo;
import view.indexes.SecondaryIndexView;
import view.menu.IndicesMenuView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class SecondaryIndexController {
    private SecondaryIndexView vista;
    private IndicesMenuView indicesMenuView;

    public SecondaryIndexController(SecondaryIndexView vista) {
        this.vista = vista;

        vista.btnCalcular.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int registros = Integer.parseInt(vista.txtRegistros.getText());
                    int bloque = Integer.parseInt(vista.txtBloque.getText());
                    int dato = Integer.parseInt(vista.txtDato.getText());
                    int indice = Integer.parseInt(vista.txtIndice.getText());

                    IndiceSecundarioModelo modelo = new IndiceSecundarioModelo(registros, bloque, dato, indice);

                    int bfri = modelo.calcularBfri();
                    int bloquesIndice = modelo.calcularNumBloques();
                    int accesos = modelo.calcularAccesos();
                    int totalIndices = modelo.calcularTotalIndices();

                    int bfrd = bloque / dato;
                    int bloquesDatos = (int) Math.ceil((double) registros / bfrd);

                    String resultado = "b_fri = " + bfri + " registros por bloque de índice\n" +
                            "Bloques de índice = " + bloquesIndice + "\n" +
                            "Accesos a disco = " + accesos + "\n" +
                            "Registros de índice total = " + totalIndices + "\n" +
                            "b_frd = " + bfrd + " registros por bloque de datos\n" +
                            "Bloques de datos = " + bloquesDatos;

                    vista.mostrarResultados(resultado);
                    vista.mostrarEstructura(registros, bfri, totalIndices, bloquesIndice, bfrd, bloquesDatos);
                } catch (NumberFormatException ex) {
                    vista.mostrarResultados("Error: Verifica que todos los campos estén completos y sean numéricos.");
                }
            }
        });

        vista.btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                volverAlMenu();
            }
        });
    }

    /**
     * Establece la referencia al menú de índices para navegación
     */
    public void setIndicesMenuView(IndicesMenuView indicesMenuView) {
        this.indicesMenuView = indicesMenuView;
    }

    /**
     * Regresa al menú de índices
     */
    public void volverAlMenu() {
        int opcion = JOptionPane.showConfirmDialog(vista,
                "¿Desea volver al menú de índices?",
                "Confirmar Regreso",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            vista.setVisible(false);
            if (indicesMenuView != null) {
                indicesMenuView.setVisible(true);
            }
        }
    }

    /**
     * Muestra la vista del controlador
     */
    public void showView() {
        vista.setVisible(true);
    }

    /**
     * Obtiene la vista asociada
     */
    public SecondaryIndexView getView() {
        return vista;
    }
}