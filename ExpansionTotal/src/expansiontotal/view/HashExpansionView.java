package expansiontotal.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HashExpansionView extends JFrame {
    private JTextField campoClave;
    private JButton btnInsertar;
    private JButton btnVolver;
    private JTextArea areaProcedimiento;
    private JPanel panelTabla;

    public HashExpansionView() {
        setTitle("Expansión Total");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        setLayout(new BorderLayout(10, 10));

        // Panel superior
        JPanel panelSuperior = new JPanel();
        campoClave = new JTextField(5);
        btnInsertar = new JButton("Insertar clave");
        btnVolver = new JButton("Volver");

        panelSuperior.add(new JLabel("Clave (2 dígitos):"));
        panelSuperior.add(campoClave);
        panelSuperior.add(btnInsertar);
        panelSuperior.add(btnVolver);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel central: tabla
        panelTabla = new JPanel();
        add(panelTabla, BorderLayout.CENTER);

        // Área de procedimiento
        areaProcedimiento = new JTextArea(8, 40);
        areaProcedimiento.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaProcedimiento);
        add(scroll, BorderLayout.SOUTH);
    }

    public String getClaveTexto() {
        return campoClave.getText().trim();
    }

    public void setTabla(Integer[][] datos, List<List<Integer>> colisiones) {
        panelTabla.removeAll();
        int filas = datos.length + 1 + obtenerMaxColisiones(colisiones);
        int columnas = datos[0].length + 1;
        panelTabla.setLayout(new GridLayout(filas, columnas, 2, 2));

        // Fila de encabezados
        panelTabla.add(new JLabel("", SwingConstants.CENTER));
        for (int c = 0; c < columnas - 1; c++) {
            JLabel label = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            panelTabla.add(label);
        }

        // Celdas de estructura
        for (int f = 0; f < datos.length; f++) {
            JLabel rowLabel = new JLabel(String.valueOf(f + 1), SwingConstants.CENTER);
            rowLabel.setFont(rowLabel.getFont().deriveFont(Font.BOLD));
            panelTabla.add(rowLabel);

            for (int c = 0; c < datos[0].length; c++) {
                Integer val = datos[f][c];
                JLabel celda = new JLabel(val != null ? val.toString() : "", SwingConstants.CENTER);
                celda.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                celda.setPreferredSize(new Dimension(40, 40));
                panelTabla.add(celda);
            }
        }

        // Filas de colisiones externas
        int max = obtenerMaxColisiones(colisiones);
        for (int i = 0; i < max; i++) {
            panelTabla.add(new JLabel("", SwingConstants.CENTER));
            for (int c = 0; c < datos[0].length; c++) {
                List<Integer> col = colisiones.get(c);
                String text = i < col.size() ? col.get(i).toString() : "";
                JLabel celda = new JLabel(text, SwingConstants.CENTER);
                celda.setPreferredSize(new Dimension(40, 40));
                if (!text.isEmpty()) {
                    celda.setForeground(new Color(186, 85, 211));
                    celda.setFont(celda.getFont().deriveFont(Font.BOLD));
                }
                panelTabla.add(celda);
            }
        }

        panelTabla.revalidate();
        panelTabla.repaint();
    }

    private int obtenerMaxColisiones(List<List<Integer>> colisiones) {
        return colisiones.stream().mapToInt(List::size).max().orElse(0);
    }

    public void mostrarPasos(String[] pasos) {
        areaProcedimiento.setText("");
        for (String p : pasos) {
            areaProcedimiento.append(p + "\n");
        }
    }

    public void limpiarCampo() {
        campoClave.setText("");
    }

    public JButton getBotonInsertar() {
        return btnInsertar;
    }

    public JButton getBotonVolver() {
        return btnVolver;
    }
}
