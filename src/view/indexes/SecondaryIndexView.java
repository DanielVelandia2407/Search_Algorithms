package view.indexes;

import javax.swing.*;
import java.awt.*;

public class SecondaryIndexView extends JFrame {
    public JTextField txtRegistros = new JTextField(10);
    public JTextField txtBloque = new JTextField(10);
    public JTextField txtDato = new JTextField(10);
    public JTextField txtIndice = new JTextField(10);
    public JButton btnCalcular = new JButton("Calcular");
    public JButton btnVolver = new JButton("Volver al Menú");


    public JTextArea areaResultados = new JTextArea(8, 40);
    public JPanel panelEstructura = new JPanel();

    public SecondaryIndexView() {
        super("Simulador de Índice Secundario");

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(0x734916));

        JPanel panelEntrada = new JPanel(new GridBagLayout());
        panelEntrada.setBackground(new Color(0xF2CA99));
        panelEntrada.setBorder(BorderFactory.createTitledBorder("Parámetros de entrada"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0; panelEntrada.add(new JLabel("Número de registros:"), gbc);
        gbc.gridx = 1; panelEntrada.add(txtRegistros, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panelEntrada.add(new JLabel("Tamaño del bloque:"), gbc);
        gbc.gridx = 1; panelEntrada.add(txtBloque, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panelEntrada.add(new JLabel("Longitud del dato:"), gbc);
        gbc.gridx = 1; panelEntrada.add(txtDato, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panelEntrada.add(new JLabel("Longitud del índice:"), gbc);
        gbc.gridx = 1; panelEntrada.add(txtIndice, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; panelEntrada.add(btnCalcular, gbc);
        btnCalcular.setBackground(Color.BLACK);
        btnCalcular.setForeground(Color.WHITE);
        btnCalcular.setFocusPainted(false);
        btnCalcular.setFont(new Font("SansSerif", Font.BOLD, 14));

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; panelEntrada.add(btnVolver, gbc);
        btnVolver.setBackground(new Color(0x8B4513));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 14));



        Font font = new Font("Consolas", Font.PLAIN, 14);
        areaResultados.setFont(font);
        areaResultados.setEditable(false);
        areaResultados.setBackground(new Color(245, 245, 245));
        areaResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));

        JScrollPane scrollResultados = new JScrollPane(areaResultados);

        panelEstructura.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30));
        panelEstructura.setBackground(new Color(0xF2CA99));
        panelEstructura.setBorder(BorderFactory.createTitledBorder("Visualización de Estructura"));

        add(panelEntrada, BorderLayout.NORTH);
        add(scrollResultados, BorderLayout.CENTER);
        add(panelEstructura, BorderLayout.SOUTH);

        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void mostrarResultados(String resultados) {
        areaResultados.setText(resultados);
    }

    public void mostrarEstructura(int registros, int bfri, int totalIndices, int bloquesIndice, int bfrd, int bloquesDatos) {
        panelEstructura.removeAll();

        JPanel indicePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(2));

                int startX = 50, startY = 30, width = 60, height = 250;
                int boxWidth = 40;
                int middle = startX + width;

                g2.drawRect(startX, startY, width, height);

                // Dibujar líneas horizontales
                int filas = 6;
                int cellHeight = height / filas;
                for (int i = 1; i < filas; i++) {
                    int y = startY + i * cellHeight;
                    g2.drawLine(startX, y, startX + width, y);
                }

                // Etiquetas
                g2.setFont(new Font("Consolas", Font.BOLD, 16));
                g2.drawString("1", startX - 30, startY + 15);
                g2.drawString(String.valueOf(bfri), startX - 30, startY + 15 + cellHeight);
                g2.drawString(String.valueOf(totalIndices), startX - 40, startY + height - 10);
                g2.drawString("B" + bloquesIndice, middle + 10, startY + height - 10);
                g2.drawString("Índice", startX + 5, startY - 10);

            }
        };
        indicePanel.setPreferredSize(new Dimension(200, 300));
        indicePanel.setBackground(new Color(255, 250, 240));

        JPanel datosPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(2));

                int startX = 50, startY = 30, width = 60, height = 250;
                int boxWidth = 40;
                int middle = startX + width;

                g2.drawRect(startX, startY, width, height);


                int filas = 6;
                int cellHeight = height / filas;
                for (int i = 1; i < filas; i++) {
                    int y = startY + i * cellHeight;
                    g2.drawLine(startX, y, startX + width, y);
                }

                // Etiquetas
                g2.setFont(new Font("Consolas", Font.BOLD, 16));
                g2.drawString("1", startX - 30, startY + 15);
                g2.drawString(String.valueOf(bfrd), startX - 30, startY + 15 + cellHeight);
                g2.drawString(String.valueOf(registros), startX - 40, startY + height - 10);
                g2.drawString("B" + bloquesDatos, middle + 10, startY + height - 10);
                g2.drawString("Fichero", startX + 5, startY - 10);

            }
        };
        datosPanel.setPreferredSize(new Dimension(200, 300));
        datosPanel.setBackground(new Color(240, 255, 240));

        panelEstructura.add(indicePanel);
        panelEstructura.add(datosPanel);
        panelEstructura.revalidate();
        panelEstructura.repaint();
    }

    /**
     * Muestra la ventana
     */
    public void showWindow() {
        setVisible(true);
    }
}