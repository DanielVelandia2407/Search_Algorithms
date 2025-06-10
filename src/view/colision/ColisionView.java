package view.colision;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class ColisionView extends JFrame {

    private JButton btnSequentialSolution;
    private JButton btnExponentialSolution;
    private JButton btnTableSolution;
    private JButton btnCancel;
    private int collidingValue;
    private int hashPosition;

    public ColisionView() {
        // Basic window configuration
        setTitle("Soluciones de Colisión");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Cambio a DISPOSE_ON_CLOSE
        setLayout(new BorderLayout(15, 15));

        // Set soft background color for the entire window
        getContentPane().setBackground(new Color(240, 248, 255)); // Alice Blue

        // Top panel with title and subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(70, 130, 180)); // Steel Blue
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Soluciones de Colisión");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Seleccione como solucionar la colisión");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSubtitle.setForeground(new Color(240, 248, 255));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(lblSubtitle);

        add(titlePanel, BorderLayout.NORTH);

        // Center panel with buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 248, 255));
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBackground(new Color(240, 248, 255));

        // Custom styled buttons
        btnSequentialSolution = createStyledButton("Solución Secuencial", new Color(41, 128, 185));
        btnExponentialSolution = createStyledButton("Solución Exponencial", new Color(46, 134, 193));
        btnTableSolution = createStyledButton("Solución Tablas", new Color(46, 134, 193));
        btnCancel = createStyledButton("Cancelar", new Color(231, 76, 60));

        buttonPanel.add(btnSequentialSolution);
        buttonPanel.add(btnExponentialSolution);
        buttonPanel.add(btnTableSolution);
        buttonPanel.add(btnCancel);

        centerPanel.add(buttonPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with information
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(220, 220, 220));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel("© 2025 - Search Algorithms v1.0 Group 1");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(100, 100, 100));

        bottomPanel.add(lblInfo);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setCollisionInfo(int collidingValue, int hashPosition) {
        this.collidingValue = collidingValue;
        this.hashPosition = hashPosition;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50));
        
        return button;
    }

    // Methods to assign external actions to buttons
    public void addSequentialSolutionListener(ActionListener listener) {
        btnSequentialSolution.addActionListener(listener);
    }

    public void addExponentialSolutionListener(ActionListener listener) {
        btnExponentialSolution.addActionListener(listener);
    }

    public void addTableSolutionListener(ActionListener listener) {
        btnTableSolution.addActionListener(listener);
    }

    public void addCancelListener(ActionListener listener) {
        btnCancel.addActionListener(listener);
    }

    // Method to show the window
    public void showWindow() {
        setVisible(true);
    }
}