package controller.external_search;

import model.external_search.PartialExpansionModel;
import view.external_search.HashExpansionView;
import view.menu.MainView;
import view.menu.SearchMenuView;

import javax.swing.*;
import java.awt.*;
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
     * Muestra un aviso informativo de que se va a expandir la estructura
     */
    private void mostrarAvisoExpansion() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Expansión de Estructura", true);
        dialog.setLayout(new BorderLayout());
        
        // Panel principal con color de fondo beige
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 222, 179)); // Color beige como en la imagen
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel del mensaje
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(new Color(245, 222, 179));
        
        // Icono de advertencia
        JLabel iconLabel = new JLabel();
        try {
            // Usar icono de advertencia del sistema
            iconLabel.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
        } catch (Exception ex) {
            iconLabel.setText("⚠️");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        }
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        
        // Mensaje principal
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<b>La densidad máxima ha sido superada.</b><br><br>" +
                "Se procederá a expandir la estructura<br>" +
                "para mantener un rendimiento óptimo." +
                "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(139, 69, 19)); // Color marrón oscuro
        
        messagePanel.add(iconLabel, BorderLayout.WEST);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        // Panel de botón
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(245, 222, 179));
        
        // Botón Continuar - estilo azul
        JButton continueButton = new JButton("Continuar");
        continueButton.setBackground(new Color(70, 130, 180)); // Azul acero
        continueButton.setForeground(Color.WHITE);
        continueButton.setFont(new Font("Arial", Font.BOLD, 12));
        continueButton.setPreferredSize(new Dimension(120, 35));
        continueButton.setBorder(BorderFactory.createRaisedBevelBorder());
        continueButton.setFocusPainted(false);
        
        continueButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(continueButton);
        
        // Ensamblar el diálogo
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(view);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Mostrar el diálogo
        dialog.setVisible(true);
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
            double dMax = 0.74;
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

            // Mostrar aviso de expansión
            mostrarAvisoExpansion();
            
            // Expandir la estructura
            view.mostrarPasos(new String[]{"Expandiendo estructura..."});
            model.expandir(clavesInsertadas);
            
            // Reinsertar todas las claves
            for (int k : new ArrayList<>(clavesInsertadas)) {
                List<String> p2 = model.insertar(k);
                view.mostrarPasos(p2.toArray(new String[0]));
            }
            
            view.mostrarPasos(new String[]{"✅ Expansión completada exitosamente."});
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
            // Eliminar clave primero
            List<String> pasosEliminar = model.eliminar(clave, false);
            view.mostrarPasos(pasosEliminar.toArray(new String[0]));
            clavesInsertadas.remove((Integer) clave);
            view.setTabla(model.getTabla(), model.getColisiones());

            // Mostrar aviso de reducción
            mostrarAvisoReduccion();
            
            // Proceder con la reducción automática
            view.mostrarPasos(new String[]{"Reduciendo estructura..."});
            model.reducir(clavesInsertadas);
            
            for (int k : new ArrayList<>(clavesInsertadas)) {
                List<String> p2 = model.insertar(k);
                view.mostrarPasos(p2.toArray(new String[0]));
            }
            
            view.mostrarPasos(new String[]{"✅ Reducción completada exitosamente."});
        } else {
            // Eliminar normalmente
            List<String> pasos = model.eliminar(clave, false);
            view.mostrarPasos(pasos.toArray(new String[0]));
            clavesInsertadas.remove((Integer) clave);
        }

        view.setTabla(model.getTabla(), model.getColisiones());
    }

    /**
     * Muestra un aviso informativo de que se va a reducir la estructura
     */
    private void mostrarAvisoReduccion() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Reducción de Estructura", true);
        dialog.setLayout(new BorderLayout());
        
        // Panel principal con color de fondo beige
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 222, 179));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel del mensaje
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(new Color(245, 222, 179));
        
        // Icono de información
        JLabel iconLabel = new JLabel();
        try {
            iconLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        } catch (Exception ex) {
            iconLabel.setText("ℹ️");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        }
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        
        // Mensaje principal
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<b>La densidad ha caído por debajo del mínimo.</b><br><br>" +
                "Se procederá a reducir la estructura<br>" +
                "para optimizar el uso de memoria." +
                "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(139, 69, 19));
        
        messagePanel.add(iconLabel, BorderLayout.WEST);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        // Panel de botón
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(245, 222, 179));
        
        // Botón Continuar - estilo verde
        JButton continueButton = new JButton("Continuar");
        continueButton.setBackground(new Color(34, 139, 34)); // Verde
        continueButton.setForeground(Color.WHITE);
        continueButton.setFont(new Font("Arial", Font.BOLD, 12));
        continueButton.setPreferredSize(new Dimension(120, 35));
        continueButton.setBorder(BorderFactory.createRaisedBevelBorder());
        continueButton.setFocusPainted(false);
        
        continueButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(continueButton);
        
        // Ensamblar el diálogo
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setSize(400, 180);
        dialog.setLocationRelativeTo(view);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Mostrar el diálogo
        dialog.setVisible(true);
    }

    /**
     * MÉTODO OBSOLETO - Mantenido para compatibilidad pero no se usa
     * La reducción ahora es automática con aviso informativo
     */
    private int mostrarDialogoReduccion() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Reducción de Estructura", true);
        dialog.setLayout(new BorderLayout());
        
        // Panel principal con color de fondo beige
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 222, 179));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel del mensaje
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(new Color(245, 222, 179));
        
        // Icono de información
        JLabel iconLabel = new JLabel();
        try {
            iconLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        } catch (Exception ex) {
            iconLabel.setText("ℹ️");
            iconLabel.setFont(new Font("Arial", Font.BOLD, 24));
        }
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        
        // Mensaje principal
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<b>La densidad es baja.</b><br><br>" +
                "¿Desea reducir la estructura para<br>" +
                "optimizar el uso de memoria?" +
                "</div></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(139, 69, 19));
        
        messagePanel.add(iconLabel, BorderLayout.WEST);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(245, 222, 179));
        
        // Botón Sí
        JButton yesButton = new JButton("Sí, Reducir");
        yesButton.setBackground(new Color(34, 139, 34)); // Verde
        yesButton.setForeground(Color.WHITE);
        yesButton.setFont(new Font("Arial", Font.BOLD, 12));
        yesButton.setPreferredSize(new Dimension(120, 35));
        yesButton.setBorder(BorderFactory.createRaisedBevelBorder());
        yesButton.setFocusPainted(false);
        
        // Botón No
        JButton noButton = new JButton("No, Mantener");
        noButton.setBackground(new Color(105, 105, 105));
        noButton.setForeground(Color.WHITE);
        noButton.setFont(new Font("Arial", Font.BOLD, 12));
        noButton.setPreferredSize(new Dimension(120, 35));
        noButton.setBorder(BorderFactory.createRaisedBevelBorder());
        noButton.setFocusPainted(false);
        
        final int[] result = {JOptionPane.CANCEL_OPTION};
        
        yesButton.addActionListener(e -> {
            result[0] = JOptionPane.YES_OPTION;
            dialog.dispose();
        });
        
        noButton.addActionListener(e -> {
            result[0] = JOptionPane.NO_OPTION;
            dialog.dispose();
        });
        
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(view);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        dialog.setVisible(true);
        
        return result[0];
    }
}