package controller;

import model.IndiceSecundarioModelo;
import view.SecondaryIndexView;
import view.IndicesMenuView;

import javax.swing.JOptionPane;

/**
 * Controlador para el simulador de índice secundario
 * Maneja la lógica de la aplicación y la comunicación entre vista y modelo
 */
public class SecondaryIndexController {
    private SecondaryIndexView view;
    private IndicesMenuView indicesMenuView;

    public SecondaryIndexController(SecondaryIndexView view) {
        this.view = view;
        initializeListeners();
    }

    /**
     * Establece la referencia al menú de índices para navegación
     */
    public void setIndicesMenuView(IndicesMenuView indicesMenuView) {
        this.indicesMenuView = indicesMenuView;
    }

    /**
     * Inicializa todos los listeners de los botones
     */
    private void initializeListeners() {
        // Listener para el botón calcular
        view.addCalcularListener(e -> calcularIndice());

        // Listener para el botón limpiar
        view.addLimpiarListener(e -> limpiarFormulario());

        // Listener para el botón volver
        view.addVolverListener(e -> volverAlMenu());
    }

    /**
     * Realiza el cálculo del índice secundario
     */
    private void calcularIndice() {
        try {
            // Validar y obtener datos de entrada
            int registros = validarYObtenerEntero(view.getRegistros(), "Número de registros");
            int bloque = validarYObtenerEntero(view.getBloque(), "Tamaño del bloque");
            int dato = validarYObtenerEntero(view.getDato(), "Longitud del registro de datos");
            int indice = validarYObtenerEntero(view.getIndice(), "Longitud del registro de índice");

            // Validaciones adicionales
            if (registros <= 0) {
                throw new IllegalArgumentException("El número de registros debe ser mayor que 0");
            }
            if (bloque <= 0) {
                throw new IllegalArgumentException("El tamaño del bloque debe ser mayor que 0");
            }
            if (dato <= 0) {
                throw new IllegalArgumentException("La longitud del registro de datos debe ser mayor que 0");
            }
            if (indice <= 0) {
                throw new IllegalArgumentException("La longitud del registro de índice debe ser mayor que 0");
            }
            if (indice >= bloque) {
                throw new IllegalArgumentException("La longitud del registro de índice debe ser menor que el tamaño del bloque");
            }
            if (dato >= bloque) {
                throw new IllegalArgumentException("La longitud del registro de datos debe ser menor que el tamaño del bloque");
            }

            // Crear modelo y realizar cálculos
            IndiceSecundarioModelo modelo = new IndiceSecundarioModelo(registros, bloque, dato, indice);

            int bfri = modelo.calcularBfri();
            int bfrd = modelo.calcularBfrd();
            int bloquesIndice = modelo.calcularNumBloques();
            int bloquesDatos = modelo.calcularBloquesDatos();
            int accesos = modelo.calcularAccesos();
            int totalRegIndices = modelo.calcularTotalIndices();

            // Formatear resultados
            StringBuilder resultado = new StringBuilder();
            resultado.append("=== RESULTADOS DEL CÁLCULO ===\n\n");

            resultado.append("PARÁMETROS DE ENTRADA:\n");
            resultado.append(String.format("• Registros totales: %,d\n", registros));
            resultado.append(String.format("• Tamaño de bloque: %,d bytes\n", bloque));
            resultado.append(String.format("• Tamaño registro datos: %d bytes\n", dato));
            resultado.append(String.format("• Tamaño registro índice: %d bytes\n\n", indice));

            resultado.append("FACTORES DE BLOQUEO:\n");
            resultado.append(String.format("• bfri (registros índice/bloque): %d\n", bfri));
            resultado.append(String.format("• bfrd (registros datos/bloque): %d\n\n", bfrd));

            resultado.append("ESTRUCTURA DEL ÍNDICE:\n");
            resultado.append(String.format("• Bloques de índice necesarios: %,d\n", bloquesIndice));
            resultado.append(String.format("• Registros de índice totales: %,d\n", totalRegIndices));
            resultado.append(String.format("• Bloques de datos necesarios: %,d\n\n", bloquesDatos));

            resultado.append("RENDIMIENTO:\n");
            resultado.append(String.format("• Accesos a disco por búsqueda: %d\n", accesos));
            resultado.append(String.format("• Eficiencia vs búsqueda lineal: %.1fx más rápido\n",
                    (double) registros / (2 * accesos)));

            // Mostrar resultados en la vista
            view.mostrarResultados(resultado.toString());
            view.mostrarEstructura(bloquesIndice, bfri);

            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(view,
                    "Cálculo completado exitosamente.\nVerifique los resultados y la visualización.",
                    "Cálculo Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            mostrarError("Error en los datos de entrada",
                    "Por favor, ingrese solo números enteros válidos en todos los campos.");
        } catch (IllegalArgumentException e) {
            mostrarError("Error de validación", e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado",
                    "Ocurrió un error durante el cálculo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida y convierte una cadena a entero
     */
    private int validarYObtenerEntero(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + nombreCampo + "' no puede estar vacío");
        }

        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("El campo '" + nombreCampo + "' debe ser un número entero válido");
        }
    }

    /**
     * Limpia el formulario y reinicia la vista
     */
    private void limpiarFormulario() {
        int opcion = JOptionPane.showConfirmDialog(view,
                "¿Está seguro de que desea limpiar todos los campos y resultados?",
                "Confirmar Limpieza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            view.limpiarCampos();
            JOptionPane.showMessageDialog(view,
                    "Formulario limpiado correctamente.",
                    "Limpieza Completada",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Regresa al menú de índices
     */
    private void volverAlMenu() {
        int opcion = JOptionPane.showConfirmDialog(view,
                "¿Desea volver al menú de índices?",
                "Confirmar Regreso",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            view.setVisible(false);
            if (indicesMenuView != null) {
                indicesMenuView.setVisible(true);
            }
        }
    }

    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String titulo, String mensaje) {
        JOptionPane.showMessageDialog(view,
                mensaje,
                titulo,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra la vista del controlador
     */
    public void showView() {
        view.showWindow();
    }

    /**
     * Obtiene la vista asociada
     */
    public SecondaryIndexView getView() {
        return view;
    }
}