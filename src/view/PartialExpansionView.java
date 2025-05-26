package view;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.util.List;

/**
 * Adaptador para usar HashExpansionView del paquete expansionparcial
 * en el contexto de la aplicación principal
 */
public class PartialExpansionView extends JFrame {
    private HashExpansionView view;

    public PartialExpansionView() {
        this.view = new HashExpansionView();
        // Cambiar el título para diferenciar
        this.view.setTitle("Expansión y Reducción Parcial");

        // Configurar esta ventana como un proxy invisible
        this.setVisible(false);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public String getClaveTexto() {
        return view.getClaveTexto();
    }

    public void limpiarCampo() {
        view.limpiarCampo();
    }

    public JButton getBotonInsertar() {
        return view.getBotonInsertar();
    }

    public JButton getBotonEliminar() {
        return view.getBotonEliminar();
    }

    public JButton getBotonVolver() {
        return view.getBotonVolver();
    }

    public void setTabla(Integer[][] datos, List<List<Integer>> colisiones) {
        view.setTabla(datos, colisiones);
    }

    public void mostrarPasos(String[] pasos) {
        view.mostrarPasos(pasos);
    }

    @Override
    public void setVisible(boolean visible) {
        // Delegar al view real
        view.setVisible(visible);
    }

    @Override
    public void setTitle(String title) {
        // Delegar al view real
        view.setTitle(title);
    }

    // Método para obtener la vista original si es necesario
    public HashExpansionView getOriginalView() {
        return view;
    }
}