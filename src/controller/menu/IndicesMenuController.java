package controller.menu;

import controller.indexes.MultilevelPrimaryIndexController;
import controller.indexes.PrimaryIndexController;
import controller.indexes.SecondaryIndexController;
import controller.indexes.ImprovedMultilevelIndexController;
import view.menu.IndicesMenuView;
import view.menu.MainView;
import view.indexes.ImprovedMultilevelIndexView;
import view.indexes.MultilevelPrimaryIndexView;
import view.indexes.SecondaryIndexView;
import view.indexes.PrimaryIndexView;  // Nueva importación

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

        // Primary Index button - IMPLEMENTADO
        this.indicesMenuView.addPrimaryIndexListener(e -> openPrimaryIndex());

        // Secondary Index button - IMPLEMENTADO
        this.indicesMenuView.addSecondaryIndexListener(e -> openSecondaryIndex());

        // Multilevel Primary button - IMPLEMENTADO
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
     * IMPLEMENTACIÓN COMPLETA - Conecta con la nueva funcionalidad de índice primario
     */
    private void openPrimaryIndex() {
        indicesMenuView.setVisible(false);

        try {
            // Crear vista y controlador del índice primario directamente
            PrimaryIndexView primaryIndexView = new PrimaryIndexView();
            PrimaryIndexController controller = new PrimaryIndexController(primaryIndexView);

            // Configurar navegación de regreso al menú de índices
            controller.setIndicesMenuView(indicesMenuView);

            // Mostrar la ventana del índice primario
            primaryIndexView.showWindow();

        } catch (Exception ex) {
            // Manejar cualquier error durante la inicialización
            JOptionPane.showMessageDialog(indicesMenuView,
                    "Error al inicializar el Simulador de Índice Primario:\n" +
                            ex.getMessage(),
                    "Error de Inicialización",
                    JOptionPane.ERROR_MESSAGE);
            indicesMenuView.setVisible(true);
            ex.printStackTrace();
        }
    }

    /**
     * Open Secondary Index view with its controller
     * IMPLEMENTACIÓN COMPLETA - Conecta con la funcionalidad desarrollada
     */
    private void openSecondaryIndex() {
        indicesMenuView.setVisible(false);

        try {
            // Crear vista y controlador del índice secundario
            SecondaryIndexView secondaryIndexView = new SecondaryIndexView();
            SecondaryIndexController controller = new SecondaryIndexController(secondaryIndexView);

            // Configurar navegación de regreso al menú de índices
            controller.setIndicesMenuView(indicesMenuView);

            // Mostrar la ventana del índice secundario
            secondaryIndexView.showWindow();

        } catch (Exception ex) {
            // Manejar cualquier error durante la inicialización
            JOptionPane.showMessageDialog(indicesMenuView,
                    "Error al inicializar el Simulador de Índice Secundario:\n" +
                            ex.getMessage(),
                    "Error de Inicialización",
                    JOptionPane.ERROR_MESSAGE);
            indicesMenuView.setVisible(true);
            ex.printStackTrace();
        }
    }

    /**
     * Open Multilevel Primary view with its controller
     * IMPLEMENTACIÓN COMPLETA - Conecta con la nueva funcionalidad de índices multinivel primarios
     */
    private void openMultilevelPrimary() {
        indicesMenuView.setVisible(false);

        try {
            // Crear vista y controlador mejorados para índices multinivel primarios directamente
            MultilevelPrimaryIndexView multilevelPrimaryView = new MultilevelPrimaryIndexView();
            MultilevelPrimaryIndexController controller = new MultilevelPrimaryIndexController(multilevelPrimaryView);

            // Configurar navegación de regreso al menú de índices
            controller.setIndicesMenuView(indicesMenuView);

            // Configurar el botón de detalles de niveles
            multilevelPrimaryView.addShowLevelDetailsListener(e -> showPrimaryLevelDetailsDialog(controller));

            // Mostrar la ventana
            multilevelPrimaryView.showWindow();

        } catch (Exception ex) {
            // Manejar cualquier error durante la inicialización
            JOptionPane.showMessageDialog(indicesMenuView,
                    "Error al inicializar el sistema de Índices Multinivel Primarios:\n" +
                            ex.getMessage(),
                    "Error de Inicialización",
                    JOptionPane.ERROR_MESSAGE);
            indicesMenuView.setVisible(true);
            ex.printStackTrace();
        }
    }

    /**
     * Open Multilevel Secondary view with improved controller
     * VERSIÓN MEJORADA - Integra el sistema de índices multinivel dinámicos
     */
    private void openMultilevelSecondary() {
        indicesMenuView.setVisible(false);

        try {
            // Mostrar información previa sobre índices multinivel secundarios
            showSecondaryMultilevelInfo();

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
                    "Error al inicializar el sistema de Índices Multinivel Secundarios:\n" +
                            ex.getMessage(),
                    "Error de Inicialización",
                    JOptionPane.ERROR_MESSAGE);
            indicesMenuView.setVisible(true);
            ex.printStackTrace();
        }
    }

    /**
     * Muestra información sobre los índices multinivel secundarios
     */
    private void showSecondaryMultilevelInfo() {
        String info = """
                🔍 ÍNDICES MULTINIVEL SECUNDARIOS 🔍
                
                CARACTERÍSTICAS PRINCIPALES:
                ✅ Organización independiente de los datos principales
                ✅ Flexibilidad en campos de indexación
                ✅ Múltiples índices sobre el mismo conjunto de datos
                ✅ Búsqueda eficiente sin alterar orden físico
                
                VENTAJAS:
                • Búsquedas por cualquier campo indexado
                • No requiere reorganización física de datos
                • Soporte para múltiples criterios de búsqueda
                • Flexibilidad en la estructura de acceso
                
                APLICACIONES:
                • Consultas por campos no clave
                • Sistemas con múltiples patrones de acceso
                • Bases de datos con consultas diversas
                
                El sistema generará automáticamente la estructura
                multinivel óptima según la configuración.
                """;

        JOptionPane.showMessageDialog(indicesMenuView, info,
                "Información: Índices Multinivel Secundarios", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra el diálogo de detalles de niveles para índices primarios
     */
    private void showPrimaryLevelDetailsDialog(MultilevelPrimaryIndexController controller) {
        try {
            StringBuilder details = new StringBuilder();

            details.append("=== ANÁLISIS DETALLADO - ÍNDICES MULTINIVEL PRIMARIOS ===\n\n");

            details.append("CARACTERÍSTICAS DEL ÍNDICE PRIMARIO:\n");
            details.append("• Organización: Secuencial por clave principal\n");
            details.append("• Acceso: Directo mediante estructura multinivel\n");
            details.append("• Ordenamiento: Físico de los datos por campo clave\n");
            details.append("• Eficiencia: O(log n) para búsquedas\n\n");

            details.append("ESTRUCTURA DE NIVELES:\n");
            details.append("• Nivel 0 (Datos): Registros ordenados físicamente\n");
            details.append("• Nivel 1 (Índice Primario): Apunta a bloques de datos\n");
            details.append("• Niveles Superiores: Índices sobre índices\n");
            details.append("• Nivel Raíz: Entrada única al sistema\n\n");

            details.append("VENTAJAS TÉCNICAS:\n");
            details.append("✓ Acceso secuencial eficiente\n");
            details.append("✓ Búsquedas por rango optimizadas\n");
            details.append("✓ Localidad de referencia mejorada\n");
            details.append("✓ Menor fragmentación de datos\n\n");

            details.append("CONSIDERACIONES:\n");
            details.append("• Mantenimiento del orden en inserciones\n");
            details.append("• Reorganización en eliminaciones masivas\n");
            details.append("• Eficiencia máxima en consultas por clave\n");
            details.append("• Ideal para patrones de acceso secuencial\n\n");

            details.append("MÉTRICAS DE RENDIMIENTO:\n");
            details.append("• Comparaciones por búsqueda: log₂(n)\n");
            details.append("• Accesos a disco: Altura del árbol\n");
            details.append("• Espacio adicional: ~15% para índices\n");
            details.append("• Eficiencia de almacenamiento: Alta\n\n");

            details.append("Para análisis específico de su configuración,\n");
            details.append("utilice las herramientas de configuración técnica.");

            // Mostrar el diálogo usando el método de la vista
            MultilevelPrimaryIndexView view = (MultilevelPrimaryIndexView) controller.getClass()
                    .getDeclaredField("view").get(controller);
            view.showLevelDetailsDialog(details.toString());

        } catch (Exception e) {
            // Fallback si hay error accediendo a la vista
            JOptionPane.showMessageDialog(indicesMenuView,
                    "Detalles técnicos de índices multinivel primarios disponibles " +
                            "en la interfaz principal mediante el botón 'Ver Detalles Niveles'.",
                    "Información Técnica", JOptionPane.INFORMATION_MESSAGE);
        }
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
     * Muestra análisis comparativo entre índices primarios y secundarios
     * MÉTODO ACTUALIZADO - Incluye comparación con índice primario simple
     */
    private void showComparativeAnalysis() {
        StringBuilder analysis = new StringBuilder();

        analysis.append("=== ANÁLISIS COMPARATIVO: TIPOS DE ÍNDICES ===\n\n");

        analysis.append("ÍNDICE PRIMARIO SIMPLE:\n");
        analysis.append("✅ Datos físicamente ordenados\n");
        analysis.append("✅ Una entrada por bloque de datos\n");
        analysis.append("✅ Acceso directo muy eficiente\n");
        analysis.append("✅ Menor overhead de almacenamiento\n");
        analysis.append("❌ Solo para consultas por clave principal\n");
        analysis.append("❌ Mantenimiento del orden requerido\n\n");

        analysis.append("ÍNDICE SECUNDARIO:\n");
        analysis.append("✅ Flexibilidad en campos de búsqueda\n");
        analysis.append("✅ No altera organización física\n");
        analysis.append("✅ Una entrada por registro\n");
        analysis.append("✅ Múltiples índices posibles\n");
        analysis.append("❌ Mayor overhead de almacenamiento\n");
        analysis.append("❌ Acceso indirecto a los datos\n\n");

        analysis.append("ÍNDICES MULTINIVEL PRIMARIOS:\n");
        analysis.append("✅ Escalabilidad para grandes volúmenes\n");
        analysis.append("✅ Acceso secuencial óptimo\n");
        analysis.append("✅ Estructura jerárquica eficiente\n");
        analysis.append("❌ Complejidad en mantenimiento\n\n");

        analysis.append("ÍNDICES MULTINIVEL SECUNDARIOS:\n");
        analysis.append("✅ Flexibilidad máxima en consultas\n");
        analysis.append("✅ Escalabilidad independiente\n");
        analysis.append("✅ Múltiples criterios de acceso\n");
        analysis.append("❌ Mayor complejidad de gestión\n\n");

        analysis.append("RECOMENDACIONES DE USO:\n");
        analysis.append("• Primario Simple: Aplicaciones pequeñas/medianas con acceso por clave\n");
        analysis.append("• Secundario: Consultas por campos diversos\n");
        analysis.append("• Multinivel Primario: Grandes volúmenes con acceso secuencial\n");
        analysis.append("• Multinivel Secundario: Sistemas complejos con múltiples patrones\n\n");

        analysis.append("Todas las implementaciones incluyen simuladores\n");
        analysis.append("y calculadoras automáticas de estructura.");

        JOptionPane.showMessageDialog(indicesMenuView, analysis.toString(),
                "Análisis Comparativo Completo", JOptionPane.INFORMATION_MESSAGE);
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