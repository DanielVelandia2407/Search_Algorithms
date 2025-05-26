package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigurationDialog extends JDialog {

    private JTextField txtRecordCount;
    private JTextField txtBlockSizeBytes;
    private JTextField txtDataRecordSizeBytes;
    private JTextField txtIndexRecordSizeBytes;
    private JButton btnAccept;
    private JButton btnCancel;
    private JButton btnPreset1;
    private JButton btnPreset2;
    private JButton btnPreset3;
    private JLabel lblPreview;
    private JLabel lblEfficiencyPreview;

    private boolean accepted = false;
    private ConfigurationData configurationData;

    public ConfigurationDialog(Frame parent) {
        super(parent, "Configuración Inicial del Sistema", true);
        initializeComponents();
        setupLayout();
        setupListeners();
        updatePreview();

        setSize(550, 650);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        // Input fields with default values - Sin limitaciones restrictivas
        txtRecordCount = new JTextField("50", 8);
        txtBlockSizeBytes = new JTextField("1024", 8);
        txtDataRecordSizeBytes = new JTextField("64", 8);
        txtIndexRecordSizeBytes = new JTextField("12", 8);

        // Buttons
        btnAccept = createStyledButton("Aceptar", new Color(46, 204, 113));
        btnCancel = createStyledButton("Cancelar", new Color(231, 76, 60));
        btnPreset1 = createStyledButton("Sistema Pequeño", new Color(52, 152, 219));
        btnPreset2 = createStyledButton("Sistema Medio", new Color(155, 89, 182));
        btnPreset3 = createStyledButton("Sistema Grande", new Color(230, 126, 34));

        // Preview labels
        lblPreview = new JLabel("");
        lblPreview.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPreview.setVerticalAlignment(SwingConstants.TOP);

        lblEfficiencyPreview = new JLabel("");
        lblEfficiencyPreview.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEfficiencyPreview.setVerticalAlignment(SwingConstants.TOP);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(240, 248, 255));

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Configuración del Sistema de Índices");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSubtitle = new JLabel("Configure los parámetros técnicos del sistema (Sin limitaciones)");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubtitle.setForeground(new Color(240, 248, 255));
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        titlePanel.setLayout(new GridLayout(2, 1, 5, 5));
        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);

        add(titlePanel, BorderLayout.NORTH);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Configuration panel
        JPanel configPanel = createConfigurationPanel();

        // Preview panel
        JPanel previewPanel = createPreviewPanel();

        // Preset panel
        JPanel presetPanel = createPresetPanel();

        mainPanel.add(configPanel, BorderLayout.NORTH);
        mainPanel.add(previewPanel, BorderLayout.CENTER);
        mainPanel.add(presetPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnAccept);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createConfigurationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                "Parámetros del Sistema",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(41, 128, 185)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Record count - Sin límite superior estricto
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Número de registros:", "Cantidad total de registros de datos a generar"), gbc);
        gbc.gridx = 1;
        panel.add(txtRecordCount, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(1 - 500,000+)"), gbc);

        // Block size - Ampliado el rango
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabel("Tamaño del bloque (bytes):", "Tamaño de cada bloque de disco en bytes"), gbc);
        gbc.gridx = 1;
        panel.add(txtBlockSizeBytes, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(512 - 65536)"), gbc);

        // Data record size - Ampliado el rango
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("Tamaño registro de datos (bytes):", "Espacio que ocupa cada registro de datos"), gbc);
        gbc.gridx = 1;
        panel.add(txtDataRecordSizeBytes, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(8 - 1024)"), gbc);

        // Index record size - Ampliado el rango
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(createLabel("Tamaño registro de índice (bytes):", "Espacio que ocupa cada entrada de índice"), gbc);
        gbc.gridx = 1;
        panel.add(txtIndexRecordSizeBytes, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(4 - 128)"), gbc);

        return panel;
    }

    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(39, 174, 96), 2),
                "Vista Previa de la Configuración",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(39, 174, 96)
        ));

        JPanel previewContent = new JPanel(new BorderLayout(5, 5));
        previewContent.setBackground(new Color(240, 248, 255));

        previewContent.add(lblPreview, BorderLayout.CENTER);
        previewContent.add(lblEfficiencyPreview, BorderLayout.SOUTH);

        panel.add(previewContent, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPresetPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(142, 68, 173), 2),
                "Configuraciones Predefinidas",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(142, 68, 173)
        ));

        panel.add(btnPreset1);
        panel.add(btnPreset2);
        panel.add(btnPreset3);
        panel.add(new JLabel("")); // Empty space

        return panel;
    }

    private JLabel createLabel(String text, String tooltip) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setToolTipText(tooltip);
        return label;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setupListeners() {
        // Accept button
        btnAccept.addActionListener(e -> {
            if (validateInput()) {
                accepted = true;
                configurationData = createConfigurationData();
                dispose();
            }
        });

        // Cancel button
        btnCancel.addActionListener(e -> {
            accepted = false;
            dispose();
        });

        // Preset buttons
        btnPreset1.addActionListener(e -> applyPreset1());
        btnPreset2.addActionListener(e -> applyPreset2());
        btnPreset3.addActionListener(e -> applyPreset3());

        // Update preview when fields change
        ActionListener updatePreviewListener = e -> updatePreview();
        txtRecordCount.addActionListener(updatePreviewListener);
        txtBlockSizeBytes.addActionListener(updatePreviewListener);
        txtDataRecordSizeBytes.addActionListener(updatePreviewListener);
        txtIndexRecordSizeBytes.addActionListener(updatePreviewListener);

        // Also update on focus lost
        txtRecordCount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { updatePreview(); }
        });
        txtBlockSizeBytes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { updatePreview(); }
        });
        txtDataRecordSizeBytes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { updatePreview(); }
        });
        txtIndexRecordSizeBytes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) { updatePreview(); }
        });
    }

    private void applyPreset1() {
        // Small system - Educational
        txtRecordCount.setText("30");
        txtBlockSizeBytes.setText("512");
        txtDataRecordSizeBytes.setText("32");
        txtIndexRecordSizeBytes.setText("8");
        updatePreview();
    }

    private void applyPreset2() {
        // Medium system - Realistic
        txtRecordCount.setText("100");
        txtBlockSizeBytes.setText("2048");
        txtDataRecordSizeBytes.setText("128");
        txtIndexRecordSizeBytes.setText("16");
        updatePreview();
    }

    private void applyPreset3() {
        // Large system - Performance testing
        txtRecordCount.setText("500");
        txtBlockSizeBytes.setText("4096");
        txtDataRecordSizeBytes.setText("256");
        txtIndexRecordSizeBytes.setText("24");
        updatePreview();
    }

    private void updatePreview() {
        try {
            int records = Integer.parseInt(txtRecordCount.getText().trim());
            int blockSize = Integer.parseInt(txtBlockSizeBytes.getText().trim());
            int dataRecordSize = Integer.parseInt(txtDataRecordSizeBytes.getText().trim());
            int indexRecordSize = Integer.parseInt(txtIndexRecordSizeBytes.getText().trim());

            // Calculate metrics
            int dataRecordsPerBlock = Math.max(1, blockSize / dataRecordSize);
            int indexRecordsPerBlock = Math.max(1, blockSize / indexRecordSize);
            int totalDataBlocks = (int) Math.ceil((double) records / dataRecordsPerBlock);
            int totalPrimaryIndexEntries = totalDataBlocks;
            int totalPrimaryBlocks = (int) Math.ceil((double) totalPrimaryIndexEntries / indexRecordsPerBlock);
            int totalSecondaryBlocks = Math.max(1, (int) Math.ceil((double) totalPrimaryBlocks / indexRecordsPerBlock));

            double storageEfficiency = calculateStorageEfficiency(records, dataRecordSize, dataRecordsPerBlock, totalDataBlocks, blockSize);

            // Format large numbers with separators
            String preview = String.format(
                    "<html><div style='font-family: monospace; font-size: 11px;'>" +
                            "<b>Distribución calculada:</b><br>" +
                            "• Registros de datos por bloque: %,d<br>" +
                            "• Registros de índice por bloque: %,d<br>" +
                            "• Total de bloques de datos: %,d<br>" +
                            "• Total de bloques de índice primario: %,d<br>" +
                            "• Total de bloques de índice secundario: %,d<br>" +
                            "• Espacio total requerido: %,d bytes (%.1f MB)<br>" +
                            "</div></html>",
                    dataRecordsPerBlock, indexRecordsPerBlock, totalDataBlocks,
                    totalPrimaryBlocks, totalSecondaryBlocks,
                    (totalDataBlocks + totalPrimaryBlocks + totalSecondaryBlocks) * blockSize,
                    (totalDataBlocks + totalPrimaryBlocks + totalSecondaryBlocks) * blockSize / (1024.0 * 1024.0)
            );

            lblPreview.setText(preview);

            // Efficiency preview
            String efficiencyText;
            Color efficiencyColor;
            if (storageEfficiency >= 80) {
                efficiencyText = String.format("Eficiencia: %.1f%% - ¡Excelente!", storageEfficiency);
                efficiencyColor = new Color(39, 174, 96);
            } else if (storageEfficiency >= 60) {
                efficiencyText = String.format("Eficiencia: %.1f%% - Buena", storageEfficiency);
                efficiencyColor = new Color(230, 126, 34);
            } else {
                efficiencyText = String.format("Eficiencia: %.1f%% - Mejorable", storageEfficiency);
                efficiencyColor = new Color(231, 76, 60);
            }

            lblEfficiencyPreview.setText(efficiencyText);
            lblEfficiencyPreview.setForeground(efficiencyColor);

        } catch (NumberFormatException e) {
            lblPreview.setText("<html><i>Ingrese valores numéricos válidos para ver la vista previa</i></html>");
            lblEfficiencyPreview.setText("");
        }
    }

    private double calculateStorageEfficiency(int records, int dataRecordSize, int dataRecordsPerBlock, int totalDataBlocks, int blockSize) {
        int usedDataBytes = records * dataRecordSize;
        int allocatedDataBytes = totalDataBlocks * blockSize;
        return allocatedDataBytes > 0 ? (double) usedDataBytes / allocatedDataBytes * 100 : 0;
    }

    private boolean validateInput() {
        try {
            int records = Integer.parseInt(txtRecordCount.getText().trim());
            int blockSize = Integer.parseInt(txtBlockSizeBytes.getText().trim());
            int dataRecordSize = Integer.parseInt(txtDataRecordSizeBytes.getText().trim());
            int indexRecordSize = Integer.parseInt(txtIndexRecordSizeBytes.getText().trim());

            // Validaciones básicas pero no restrictivas
            if (records < 1) {
                showError("El número de registros debe ser mayor a 0");
                return false;
            }

            if (blockSize < 512) {
                showError("El tamaño del bloque debe ser al menos 512 bytes");
                return false;
            }

            if (dataRecordSize < 8) {
                showError("El tamaño del registro de datos debe ser al menos 8 bytes");
                return false;
            }

            if (indexRecordSize < 4) {
                showError("El tamaño del registro de índice debe ser al menos 4 bytes");
                return false;
            }

            if (blockSize < dataRecordSize) {
                showError("El bloque debe ser lo suficientemente grande para al menos un registro de datos");
                return false;
            }

            if (blockSize < indexRecordSize) {
                showError("El bloque debe ser lo suficientemente grande para al menos un registro de índice");
                return false;
            }

            // Advertencia para configuraciones muy grandes
            if (records > 100000) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Configuración con " + String.format("%,d", records) + " registros puede tomar tiempo en cargar.\n" +
                                "¿Desea continuar?",
                        "Configuración Grande",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (option != JOptionPane.YES_OPTION) {
                    return false;
                }
            }

            return true;

        } catch (NumberFormatException e) {
            showError("Por favor ingrese valores numéricos válidos en todos los campos");
            return false;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error de Validación", JOptionPane.ERROR_MESSAGE);
    }

    private ConfigurationData createConfigurationData() {
        return new ConfigurationData(
                Integer.parseInt(txtRecordCount.getText().trim()),
                Integer.parseInt(txtBlockSizeBytes.getText().trim()),
                Integer.parseInt(txtDataRecordSizeBytes.getText().trim()),
                Integer.parseInt(txtIndexRecordSizeBytes.getText().trim())
        );
    }

    public boolean isAccepted() {
        return accepted;
    }

    public ConfigurationData getConfigurationData() {
        return configurationData;
    }

    // Data class to hold configuration
    public static class ConfigurationData {
        private final int recordCount;
        private final int blockSizeBytes;
        private final int dataRecordSizeBytes;
        private final int indexRecordSizeBytes;

        public ConfigurationData(int recordCount, int blockSizeBytes, int dataRecordSizeBytes, int indexRecordSizeBytes) {
            this.recordCount = recordCount;
            this.blockSizeBytes = blockSizeBytes;
            this.dataRecordSizeBytes = dataRecordSizeBytes;
            this.indexRecordSizeBytes = indexRecordSizeBytes;
        }

        public int getRecordCount() { return recordCount; }
        public int getBlockSizeBytes() { return blockSizeBytes; }
        public int getDataRecordSizeBytes() { return dataRecordSizeBytes; }
        public int getIndexRecordSizeBytes() { return indexRecordSizeBytes; }
    }
}