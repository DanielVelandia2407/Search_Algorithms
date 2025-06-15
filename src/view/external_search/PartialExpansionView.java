package view.external_search;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Adaptador mejorado para usar HashExpansionView con estilo personalizado
 * inspirado en el diseño mostrado en la imagen
 */
public class PartialExpansionView extends JFrame {
    private HashExpansionView view;

    public PartialExpansionView() {
        this.view = new HashExpansionView();
        
        // Aplicar el estilo personalizado
        aplicarEstiloPersonalizado();
        
        // Cambiar el título para diferenciar
        this.view.setTitle("Expansión y Reducción Parcial - Algoritmo de Hashing Dinámico");

        // Configurar esta ventana como un proxy invisible
        this.setVisible(false);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Aplica el estilo visual basado en la imagen proporcionada
     */
    private void aplicarEstiloPersonalizado() {
        try {
            // Configurar Look and Feel para obtener mejor apariencia
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Aplicar colores y estilos personalizados
            aplicarTemaPersonalizado();
            
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el Look and Feel: " + e.getMessage());
        }
        
        // Configurar la ventana principal
        SwingUtilities.invokeLater(() -> {
            if (view != null) {
                configurarVentanaPrincipal();
            }
        });
    }
    
    /**
     * Aplica el tema de colores personalizado basado en la imagen
     */
    private void aplicarTemaPersonalizado() {
        Color colorFondoPrincipal = new Color(245, 222, 179);
        Color colorFondoSecundario = new Color(222, 184, 135);
        Color colorTexto = new Color(139, 69, 19);
        Color colorBotonPrincipal = new Color(160, 82, 45);
        Color colorBotonSecundario = new Color(105, 105, 105);
        
        UIManager.put("Panel.background", colorFondoPrincipal);
        UIManager.put("Button.background", colorBotonPrincipal);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 12));
        UIManager.put("Label.foreground", colorTexto);
        UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 12));
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", colorTexto);
        UIManager.put("TextArea.background", Color.WHITE);
        UIManager.put("TextArea.foreground", colorTexto);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.foreground", colorTexto);
        UIManager.put("Table.gridColor", colorFondoSecundario);
        UIManager.put("TitledBorder.titleColor", colorTexto);
    }
    
    /**
     * Configura la ventana principal con el estilo de la imagen
     */
    private void configurarVentanaPrincipal() {
        JFrame frame = view;
        
        frame.setTitle("Expansión y Reducción Parcial - Algoritmo de Hashing Dinámico");
        frame.setBackground(new Color(245, 222, 179));
        
        personalizarComponentesRecursivamente(frame.getContentPane());
        aplicarBordesYEstilos(frame.getContentPane());
    }
    
    /**
     * Personaliza todos los componentes de forma recursiva
     */
    private void personalizarComponentesRecursivamente(Container container) {
        Color colorFondoPrincipal = new Color(245, 222, 179);
        Color colorTexto = new Color(139, 69, 19);
        Color colorBotonInsertar = new Color(160, 82, 45);
        Color colorBotonEliminar = new Color(220, 20, 60);
        Color colorBotonVolver = new Color(105, 105, 105);
        
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(colorFondoPrincipal);
                personalizarComponentesRecursivamente(panel);
                
            } else if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setFont(new Font("Arial", Font.BOLD, 12));
                button.setForeground(Color.WHITE);
                button.setFocusPainted(false);
                button.setBorder(BorderFactory.createRaisedBevelBorder());
                button.setPreferredSize(new Dimension(120, 35));
                
                String texto = button.getText().toLowerCase();
                if (texto.contains("insertar")) {
                    button.setBackground(colorBotonInsertar);
                } else if (texto.contains("eliminar")) {
                    button.setBackground(colorBotonEliminar);
                } else if (texto.contains("volver")) {
                    button.setBackground(colorBotonVolver);
                } else {
                    button.setBackground(colorBotonInsertar);
                }
                
            } else if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                label.setForeground(colorTexto);
                label.setFont(new Font("Arial", Font.PLAIN, 12));
                
            } else if (comp instanceof JTextField) {
                JTextField textField = (JTextField) comp;
                textField.setBackground(Color.WHITE);
                textField.setForeground(colorTexto);
                textField.setFont(new Font("Arial", Font.PLAIN, 12));
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)
                ));
                
            } else if (comp instanceof JTextArea) {
                JTextArea textArea = (JTextArea) comp;
                textArea.setBackground(Color.WHITE);
                textArea.setForeground(colorTexto);
                textArea.setFont(new Font("Consolas", Font.PLAIN, 11));
                
            } else if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                scrollPane.setBackground(colorFondoPrincipal);
                scrollPane.getViewport().setBackground(Color.WHITE);
                personalizarComponentesRecursivamente(scrollPane);
                
            } else if (comp instanceof JTable) {
                JTable table = (JTable) comp;
                table.setBackground(Color.WHITE);
                table.setForeground(colorTexto);
                table.setGridColor(new Color(222, 184, 135));
                table.setFont(new Font("Arial", Font.PLAIN, 11));
                table.getTableHeader().setBackground(new Color(222, 184, 135));
                table.getTableHeader().setForeground(colorTexto);
                table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
                
            } else if (comp instanceof Container) {
                personalizarComponentesRecursivamente((Container) comp);
            }
        }
    }
    
    /**
     * Aplica bordes y estilos especiales a paneles específicos
     */
    private void aplicarBordesYEstilos(Container container) {
        Color colorBorde = new Color(139, 69, 19);
        
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                
                if (panel.getComponentCount() > 3) {
                    TitledBorder border = BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Estructura de Hashing Dinámico",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14),
                        colorBorde
                    );
                    panel.setBorder(BorderFactory.createCompoundBorder(
                        border,
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                }
                
                aplicarBordesYEstilos(panel);
                
            } else if (comp instanceof Container) {
                aplicarBordesYEstilos((Container) comp);
            }
        }
    }
}
