package view.external_search;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class HashExpansionView extends JFrame {
    private JTextField campoClave;
    private JButton btnInsertar, btnEliminar, btnVolver;
    private JTextArea areaProcedimiento;
    private JPanel panelTabla;
    
    // Paleta de colores elegante
    private static final Color COLOR_FONDO = new Color(240, 235, 220);          // Beige claro
    private static final Color COLOR_PANEL_PRINCIPAL = new Color(245, 240, 225); // Beige más claro
    private static final Color COLOR_ENCABEZADO = new Color(139, 101, 74);       // Marrón oscuro
    private static final Color COLOR_BOTON_PRINCIPAL = new Color(165, 124, 94);  // Marrón medio
    private static final Color COLOR_BOTON_SECUNDARIO = new Color(200, 170, 140); // Marrón claro
    private static final Color COLOR_BOTON_ELIMINAR = new Color(180, 100, 100);  // Rojo suave
    private static final Color COLOR_BOTON_VOLVER = new Color(60, 60, 60);       // Gris oscuro
    private static final Color COLOR_TEXTO = new Color(60, 45, 35);              // Marrón muy oscuro
    private static final Color COLOR_CAMPO = new Color(255, 252, 245);           // Blanco cremoso
    private static final Color COLOR_BORDE = new Color(139, 101, 74);            // Marrón para bordes
    private static final Color COLOR_COLISION = new Color(186, 85, 211);         // Morado para colisiones

    public HashExpansionView() {
        setTitle("Expansión y Reducción Total - Hash Dinámico");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Configurar colores generales
        getContentPane().setBackground(COLOR_FONDO);
        
        inicializarInterfaz();
        aplicarEstilos();
    }

    private void inicializarInterfaz() {
        setLayout(new BorderLayout(15, 15));
        
        // Panel principal con padding
        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Título elegante
        JLabel titulo = new JLabel("Algoritmo de Hash con Expansión Dinámica", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(COLOR_ENCABEZADO);
        titulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        panelPrincipal.add(titulo, BorderLayout.NORTH);
        
        // Panel de controles
        JPanel panelControles = crearPanelControles();
        panelPrincipal.add(panelControles, BorderLayout.NORTH);
        
        // Panel central para la tabla
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(COLOR_FONDO);
        
        // Título de la estructura
        JLabel tituloEstructura = new JLabel("Estructura Hash", SwingConstants.CENTER);
        tituloEstructura.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tituloEstructura.setForeground(COLOR_ENCABEZADO);
        tituloEstructura.setBorder(new EmptyBorder(10, 0, 10, 0));
        panelCentral.add(tituloEstructura, BorderLayout.NORTH);
        
        // Panel de la tabla con borde elegante
        panelTabla = new JPanel();
        panelTabla.setBackground(COLOR_PANEL_PRINCIPAL);
        panelTabla.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JScrollPane scrollTabla = new JScrollPane(panelTabla);
        scrollTabla.setBackground(COLOR_PANEL_PRINCIPAL);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder());
        scrollTabla.getViewport().setBackground(COLOR_PANEL_PRINCIPAL);
        panelCentral.add(scrollTabla, BorderLayout.CENTER);
        
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        
        // Panel de procedimientos
        JPanel panelProcedimientos = crearPanelProcedimientos();
        panelPrincipal.add(panelProcedimientos, BorderLayout.SOUTH);
        
        add(panelPrincipal, BorderLayout.CENTER);
    }
    
    private JPanel crearPanelControles() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Primera fila: Campo de clave
        JPanel filaClave = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filaClave.setBackground(COLOR_FONDO);
        
        JLabel lblClave = new JLabel("Clave (2 dígitos):");
        lblClave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblClave.setForeground(COLOR_TEXTO);
        
        campoClave = new JTextField(8);
        campoClave.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoClave.setBackground(COLOR_CAMPO);
        campoClave.setForeground(COLOR_TEXTO);
        campoClave.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            new EmptyBorder(5, 8, 5, 8)
        ));
        
        filaClave.add(lblClave);
        filaClave.add(campoClave);
        
        // Segunda fila: Botones
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filaBotones.setBackground(COLOR_FONDO);
        
        btnInsertar = crearBoton("Insertar Clave", COLOR_BOTON_PRINCIPAL);
        btnEliminar = crearBoton("Eliminar Clave", COLOR_BOTON_ELIMINAR);
        btnVolver = crearBoton("Volver al Menú", COLOR_BOTON_VOLVER);
        
        filaBotones.add(btnInsertar);
        filaBotones.add(btnEliminar);
        filaBotones.add(btnVolver);
        
        panel.add(filaClave);
        panel.add(filaBotones);
        
        return panel;
    }
    
    private JPanel crearPanelProcedimientos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel titulo = new JLabel("Procedimiento de la Operación");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(COLOR_ENCABEZADO);
        titulo.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        areaProcedimiento = new JTextArea(8, 50);
        areaProcedimiento.setEditable(false);
        areaProcedimiento.setFont(new Font("Consolas", Font.PLAIN, 12));
        areaProcedimiento.setBackground(COLOR_CAMPO);
        areaProcedimiento.setForeground(COLOR_TEXTO);
        areaProcedimiento.setBorder(new EmptyBorder(10, 10, 10, 10));
        areaProcedimiento.setLineWrap(true);
        areaProcedimiento.setWrapStyleWord(true);
        
        JScrollPane scroll = new JScrollPane(areaProcedimiento);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDE, 1),
            BorderFactory.createEmptyBorder()
        ));
        scroll.getViewport().setBackground(COLOR_CAMPO);
        
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(colorFondo.darker(), 1),
            new EmptyBorder(8, 16, 8, 16)
        ));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo.brighter());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo);
            }
        });
        
        return boton;
    }
    
    private void aplicarEstilos() {
        // Configurar look and feel si es posible
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Continuar con el look and feel por defecto
        }
    }

    public String getClaveTexto() {
        return campoClave.getText().trim();
    }

    public void limpiarCampo() {
        campoClave.setText("");
    }

    public JButton getBotonInsertar() { return btnInsertar; }
    public JButton getBotonEliminar() { return btnEliminar; }
    public JButton getBotonVolver()   { return btnVolver; }

    public void setTabla(Integer[][] datos, List<List<Integer>> colisiones) {
        panelTabla.removeAll();

        int filas = datos.length + 1 + obtenerMaxColisiones(colisiones);
        int columnas = datos[0].length + 1;
        panelTabla.setLayout(new GridLayout(filas, columnas, 2, 2));

        // Encabezado superior izquierdo (vacío)
        JLabel esquina = new JLabel("", SwingConstants.CENTER);
        esquina.setBackground(COLOR_ENCABEZADO);
        esquina.setOpaque(true);
        panelTabla.add(esquina);

        // Encabezado de columnas
        for (int c = 0; c < columnas - 1; c++) {
            JLabel label = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setBackground(COLOR_ENCABEZADO);
            label.setForeground(Color.WHITE);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
            panelTabla.add(label);
        }

        // Estructura de 2 filas
        for (int f = 0; f < datos.length; f++) {
            // Encabezado de fila
            JLabel fila = new JLabel("Fila " + (f + 1), SwingConstants.CENTER);
            fila.setFont(new Font("Segoe UI", Font.BOLD, 12));
            fila.setBackground(COLOR_ENCABEZADO);
            fila.setForeground(Color.WHITE);
            fila.setOpaque(true);
            fila.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
            panelTabla.add(fila);

            // Celdas de datos
            for (int c = 0; c < datos[0].length; c++) {
                Integer val = datos[f][c];
                JLabel celda = new JLabel(val != null ? val.toString() : "", SwingConstants.CENTER);
                celda.setFont(new Font("Segoe UI", Font.BOLD, 13));
                celda.setBackground(COLOR_CAMPO);
                celda.setForeground(COLOR_TEXTO);
                celda.setOpaque(true);
                celda.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
                
                // Resaltar celdas ocupadas
                if (val != null) {
                    celda.setBackground(COLOR_BOTON_SECUNDARIO);
                    celda.setForeground(Color.WHITE);
                }
                
                panelTabla.add(celda);
            }
        }

        // Colisiones externas
        int max = obtenerMaxColisiones(colisiones);
        for (int i = 0; i < max; i++) {
            // Etiqueta de colisión
            JLabel lblColision = new JLabel("Col. " + (i + 1), SwingConstants.CENTER);
            lblColision.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblColision.setBackground(COLOR_COLISION);
            lblColision.setForeground(Color.WHITE);
            lblColision.setOpaque(true);
            lblColision.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
            panelTabla.add(lblColision);

            for (int c = 0; c < datos[0].length; c++) {
                List<Integer> col = colisiones.get(c);
                String text = i < col.size() ? col.get(i).toString() : "";
                JLabel celda = new JLabel(text, SwingConstants.CENTER);
                celda.setFont(new Font("Segoe UI", Font.BOLD, 12));
                celda.setBackground(COLOR_CAMPO);
                celda.setOpaque(true);
                celda.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
                
                if (!text.isEmpty()) {
                    celda.setBackground(COLOR_COLISION);
                    celda.setForeground(Color.WHITE);
                }
                
                panelTabla.add(celda);
            }
        }

        panelTabla.revalidate();
        panelTabla.repaint();
    }

    private int obtenerMaxColisiones(List<List<Integer>> colisiones) {
        int max = 0;
        for (List<Integer> lista : colisiones) {
            if (lista.size() > max) max = lista.size();
        }
        return max;
    }

    public void mostrarPasos(String[] pasos) {
        areaProcedimiento.setText("");
        for (int i = 0; i < pasos.length; i++) {
            areaProcedimiento.append((i + 1) + ". " + pasos[i] + "\n");
        }
        areaProcedimiento.setCaretPosition(0);
    }
}