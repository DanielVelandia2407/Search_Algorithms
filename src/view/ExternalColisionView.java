package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class ExternalColisionView extends JFrame {

    private JButton btnSequentialSolution;
    private JButton btnExponentialSolution;
    private JButton btnOverflowAreaSolution;
    private JButton btnCancel;
    private JLabel lblCollisionInfo;
    private JLabel lblBlockInfo;

    public ExternalColisionView() {
        // Basic window configuration
        setTitle("Resolución de Colisión Externa");
        setSize(550, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        // Set background color
        getContentPane().setBackground(new Color(240, 248, 255));

        // Top panel with title and collision information
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(231, 76, 60)); // Red for collision
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("¡Colisión Detectada en Tabla Hash Externa!");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblCollisionInfo = new JLabel("Información de colisión");
        lblCollisionInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCollisionInfo.setForeground(new Color(255, 230, 230));
        lblCollisionInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblBlockInfo = new JLabel("Información del bloque");
        lblBlockInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblBlockInfo.setForeground(new Color(255, 230, 230));
        lblBlockInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(lblCollisionInfo);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(lblBlockInfo);

        add(titlePanel, BorderLayout.NORTH);

        // Center panel with explanation and solution options
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Explanation panel
        JPanel explanationPanel = new JPanel();
        explanationPanel.setLayout(new BoxLayout(explanationPanel, BoxLayout.Y_AXIS));
        explanationPanel.setBackground(new Color(255, 248, 220));
        explanationPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 193, 7), 2),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblExplanationTitle = new JLabel("📋 Métodos de Resolución de Colisiones Externas:");
        lblExplanationTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblExplanationTitle.setForeground(new Color(133, 100, 4));

        JLabel lblSequentialExp = new JLabel("• Sondeo Secuencial: Buscar en el siguiente bloque disponible");
        lblSequentialExp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSequentialExp.setForeground(new Color(133, 100, 4));

        JLabel lblExponentialExp = new JLabel("• Sondeo Cuadrático: Usar incrementos cuadráticos entre bloques");
        lblExponentialExp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblExponentialExp.setForeground(new Color(133, 100, 4));

        JLabel lblOverflowExp = new JLabel("• Área de Desbordamiento: Crear bloque adicional para colisiones");
        lblOverflowExp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblOverflowExp.setForeground(new Color(133, 100, 4));

        explanationPanel.add(lblExplanationTitle);
        explanationPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        explanationPanel.add(lblSequentialExp);
        explanationPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        explanationPanel.add(lblExponentialExp);
        explanationPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        explanationPanel.add(lblOverflowExp);

        centerPanel.add(explanationPanel, BorderLayout.NORTH);

        // Solution buttons panel
        JPanel solutionPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        solutionPanel.setBackground(new Color(240, 248, 255));
        solutionPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnSequentialSolution = createStyledButton("Sondeo Secuencial",
                new Color(52, 152, 219), "Buscar en bloques consecutivos");

        btnExponentialSolution = createStyledButton("Sondeo Cuadrático",
                new Color(155, 89, 182), "Usar incrementos cuadráticos");

        btnOverflowAreaSolution = createStyledButton("Área de Desbordamiento",
                new Color(46, 204, 113), "Crear bloque de overflow");

        btnCancel = createStyledButton("Cancelar Inserción",
                new Color(231, 76, 60), "No insertar la clave");

        solutionPanel.add(btnSequentialSolution);
        solutionPanel.add(btnExponentialSolution);
        solutionPanel.add(btnOverflowAreaSolution);
        solutionPanel.add(btnCancel);

        centerPanel.add(solutionPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with additional information
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(220, 220, 220));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("⚠️ Las colisiones en tablas hash externas requieren accesos adicionales a disco");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(new Color(100, 100, 100));

        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color backgroundColor, String tooltip) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50));
        button.setToolTipText(tooltip);

        // Add hover effect
        Color originalColor = backgroundColor;
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    // Method to set collision information specific to external hash tables
    public void setExternalCollisionInfo(int valueToInsert, int blockPosition, int slotsUsed, int totalSlots) {
        lblCollisionInfo.setText("Clave " + valueToInsert + " → Bloque " + (blockPosition + 1) + " (lleno)");
        lblBlockInfo.setText("Bloque ocupado: " + slotsUsed + "/" + totalSlots + " slots utilizados");
    }

    // Method to set collision information (for backward compatibility)
    public void setCollisionInfo(int valueToInsert, int position) {
        setExternalCollisionInfo(valueToInsert, position, 5, 5); // Default values
    }

    // Methods to assign external actions to buttons
    public void addSequentialSolutionListener(ActionListener listener) {
        btnSequentialSolution.addActionListener(listener);
    }

    public void addExponentialSolutionListener(ActionListener listener) {
        btnExponentialSolution.addActionListener(listener);
    }

    public void addOverflowAreaSolutionListener(ActionListener listener) {
        btnOverflowAreaSolution.addActionListener(listener);
    }

    public void addCancelListener(ActionListener listener) {
        btnCancel.addActionListener(listener);
    }

    // Method to show the window
    public void showWindow() {
        setVisible(true);
    }
}