package controller;

import view.IndicesMenuView;
import view.MainView;
import view.ImprovedMultilevelIndexView;
import view.ConfigurationDialog;

import javax.swing.JOptionPane;

public class IndicesMenuController {
    private IndicesMenuView indicesMenuView;
    private MainView mainView;

    public IndicesMenuController(IndicesMenuView indicesMenuView) {
        this.indicesMenuView = indicesMenuView;
        initializeListeners();
    }

    /**
     * Sets the reference to the main view for navigation purposes
     */
    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    /**
     * Initialize all button listeners for the indices menu
     */
    private void initializeListeners() {
        // Back button - returns to main menu
        this.indicesMenuView.addBackListener(e -> goBackToMainMenu());

        // Primary Index button
        this.indicesMenuView.addPrimaryIndexListener(e -> openPrimaryIndex());

        // Secondary Index button
        this.indicesMenuView.addSecondaryIndexListener(e -> openSecondaryIndex());

        // Multilevel Primary button
        this.indicesMenuView.addMultilevelPrimaryListener(e -> openMultilevelPrimary());

        // Multilevel Secondary button - MEJORADO
        this.indicesMenuView.addMultilevelSecondaryListener(e -> openMultilevelSecondary());
    }

    /**
     * Navigate back to the main menu
     */
    private void goBackToMainMenu() {
        if (mainView != null) {
            indicesMenuView.setVisible(false);
            mainView.setVisible(true);
        }
    }

    /**
     * Open Primary Index view with its controller
     */
    private void openPrimaryIndex() {
        indicesMenuView.setVisible(false);

        // TODO: Uncomment when PrimaryIndexView is created
        /*
        PrimaryIndexView primaryIndexView = new PrimaryIndexView();
        PrimaryIndexController controller = new PrimaryIndexController(primaryIndexView);
        controller.setIndicesMenuView(indicesMenuView);
        primaryIndexView.showWindow();
        */

        // Temporary message - remove when implementing the actual view
        JOptionPane.showMessageDialog(indicesMenuView,
                "Funcionalidad de Índice Principal en desarrollo",
                "En construcción",
                JOptionPane.INFORMATION_MESSAGE);
        indicesMenuView.setVisible(true);
    }

    /**
     * Open Secondary Index view with its controller
     */
    private void openSecondaryIndex() {
        indicesMenuView.setVisible(false);

        // TODO: Uncomment when SecondaryIndexView is created
        /*
        SecondaryIndexView secondaryIndexView = new SecondaryIndexView();
        SecondaryIndexController controller = new SecondaryIndexController(secondaryIndexView);
        controller.setIndicesMenuView(indicesMenuView);
        secondaryIndexView.showWindow();
        */

        // Temporary message - remove when implementing the actual view
        JOptionPane.showMessageDialog(indicesMenuView,
                "Funcionalidad de Índice Secundario en desarrollo",
                "En construcción",
                JOptionPane.INFORMATION_MESSAGE);
        indicesMenuView.setVisible(true);
    }

    /**
     * Open Multilevel Primary view with its controller
     */
    private void openMultilevelPrimary() {
        indicesMenuView.setVisible(false);

        // TODO: Uncomment when MultilevelPrimaryView is created
        /*
        MultilevelPrimaryView multilevelPrimaryView = new MultilevelPrimaryView();
        MultilevelPrimaryController controller = new MultilevelPrimaryController(multilevelPrimaryView);
        controller.setIndicesMenuView(indicesMenuView);
        multilevelPrimaryView.showWindow();
        */

        // Temporary message - remove when implementing the actual view
        JOptionPane.showMessageDialog(indicesMenuView,
                "Funcionalidad de Multinivel con Principal en desarrollo",
                "En construcción",
                JOptionPane.INFORMATION_MESSAGE);
        indicesMenuView.setVisible(true);
    }

    /**
     * Open Multilevel Secondary view with improved controller
     * VERSIÓN MEJORADA - Integra el sistema de índices multinivel dinámicos
     */
    private void openMultilevelSecondary() {
        indicesMenuView.setVisible(false);

        try {
            // Crear vista y controlador mejorados
            ImprovedMultilevelIndexView multilevelView = new ImprovedMultilevelIndexView();
            ImprovedMultilevelIndexController controller = new ImprovedMultilevelIndexController(multilevelView);

            // Configurar navegación de regreso al menú de índices
            controller.setIndicesMenuView(indicesMenuView);

            // Mostrar la ventana
            multilevelView.showWindow();

        } catch (Exception ex) {
            // Manejar cualquier error durante la inicialización
            JOptionPane.showMessageDialog(indicesMenuView,
                    "Error al inicializar el sistema de Índices Multinivel:\n" +
                            ex.getMessage(),
                    "Error de Inicialización",
                    JOptionPane.ERROR_MESSAGE);
            indicesMenuView.setVisible(true);
            ex.printStackTrace();
        }
    }

    /**
     * Muestra información sobre los niveles que se calcularán
     */
    private void showCalculatedLevelsInfo(ConfigurationDialog.ConfigurationData config) {
        try {
            int dataPerBlock = config.getBlockSizeBytes() / config.getDataRecordSizeBytes();
            int indexPerBlock = config.getBlockSizeBytes() / config.getIndexRecordSizeBytes();
            int levels = ImprovedMultilevelIndexController.MultilevelCalculator.calculateRequiredLevels(
                    config.getRecordCount(), dataPerBlock, indexPerBlock);

            String levelInfo = String.format(
                    "📊 CONFIGURACIÓN SELECCIONADA 📊\n\n" +
                            "Registros: %,d\n" +
                            "Tamaño de bloque: %,d bytes\n" +
                            "Tamaño registro datos: %d bytes\n" +
                            "Tamaño registro índice: %d bytes\n\n" +
                            "📈 ESTRUCTURA CALCULADA 📈\n\n" +
                            "Registros por bloque (datos): %d\n" +
                            "Registros por bloque (índice): %d\n" +
                            "Niveles de índice necesarios: %d\n" +
                            "Niveles totales (con datos): %d\n\n" +
                            "🚀 RENDIMIENTO ESTIMADO 🚀\n\n" +
                            "Accesos máximos por búsqueda: %d\n" +
                            "Mejora vs búsqueda secuencial: %.1fx más rápido\n\n" +
                            "¡El sistema generará automáticamente la estructura óptima!",
                    config.getRecordCount(),
                    config.getBlockSizeBytes(),
                    config.getDataRecordSizeBytes(),
                    config.getIndexRecordSizeBytes(),
                    dataPerBlock,
                    indexPerBlock,
                    levels - 1,
                    levels,
                    levels,
                    (double) config.getRecordCount() / 2 / levels
            );

            JOptionPane.showMessageDialog(indicesMenuView, levelInfo,
                    "Estructura de Índices Multinivel", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            // Si hay error en el cálculo, continuar sin mostrar info
            System.err.println("Error calculando información de niveles: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información inicial en la vista
     */
    private void updateInitialViewInfo(ImprovedMultilevelIndexView view,
                                       ImprovedMultilevelIndexController controller) {
        try {
            // Información básica del sistema
            String levelInfo = "Sistema de Índices Multinivel Dinámicos iniciado correctamente. " +
                    "Use 'Ver Detalles Niveles' para análisis completo de la estructura.";
            view.setLevelInfo(levelInfo);

        } catch (Exception e) {
            System.err.println("Error actualizando información inicial: " + e.getMessage());
        }
    }

    /**
     * Muestra análisis detallado del sistema (integrado con la arquitectura MVC)
     */
    private void showDetailedSystemAnalysis(ImprovedMultilevelIndexController controller) {
        StringBuilder analysis = new StringBuilder();

        analysis.append("=== ANÁLISIS DETALLADO DEL SISTEMA ===\n\n");

        analysis.append("INFORMACIÓN DEL SISTEMA:\n");
        analysis.append("- Versión: Índices Multinivel Dinámicos v2.0\n");
        analysis.append("- Integración: Arquitectura MVC completa\n");
        analysis.append("- Navegación: Main → Menú Principal → Menú Índices → Multinivel\n\n");

        analysis.append("MEJORAS IMPLEMENTADAS:\n");
        analysis.append("✅ Cálculo dinámico de niveles según volumen\n");
        analysis.append("✅ Generación automática de estructura multinivel\n");
        analysis.append("✅ Terminología técnica estándar (Nivel 1, 2, 3...)\n");
        analysis.append("✅ Soporte para volúmenes ilimitados de datos\n");
        analysis.append("✅ Análisis de rendimiento en tiempo real\n");
        analysis.append("✅ Integración perfecta con arquitectura existente\n\n");

        analysis.append("CAPACIDADES TÉCNICAS:\n");
        analysis.append("• Configuración flexible de parámetros\n");
        analysis.append("• Visualización paso a paso de búsquedas\n");
        analysis.append("• Inserción y eliminación de registros\n");
        analysis.append("• Recálculo automático de estructura\n");
        analysis.append("• Métricas detalladas de eficiencia\n\n");

        analysis.append("FLUJO DE NAVEGACIÓN:\n");
        analysis.append("1. Aplicación Principal\n");
        analysis.append("2. ↓ Menú Principal (MainView)\n");
        analysis.append("3. ↓ Menú de Índices (IndicesMenuView)\n");
        analysis.append("4. ↓ Multinivel Secundario (ImprovedMultilevelIndexView)\n");
        analysis.append("5. ← Navegación de regreso mantenida\n\n");

        analysis.append("Para más detalles técnicos, use el botón\n");
        analysis.append("'Ver Detalles Niveles' en la interfaz principal.");

        // Mostrar análisis en diálogo
        JOptionPane.showMessageDialog(indicesMenuView, analysis.toString(),
                "Análisis del Sistema Integrado", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show the indices menu view
     */
    public void showView() {
        indicesMenuView.showWindow();
    }

    /**
     * Get the indices menu view instance
     */
    public IndicesMenuView getView() {
        return indicesMenuView;
    }

    /**
     * Adapter class to handle navigation back to indices menu
     * This allows the MultilevelIndexController to return to the indices menu
     * instead of the original AlgorithmMenuView
     */
    private class AlgorithmMenuAdapter {

        public void setVisible(boolean visible) {
            if (visible) {
                indicesMenuView.setVisible(true);
            }
        }

        public void dispose() {
            // No action needed for the adapter
        }
    }
}