package controller;

import model.IndicePrimarioModelo;
import view.PrimaryIndexView;
import view.IndicesMenuView;

import javax.swing.JOptionPane;

/**
 * Controlador para el simulador de índice primario
 * Maneja la lógica de la aplicación y la comunicación entre vista y modelo
 */
public class PrimaryIndexController {
    private PrimaryIndexView view;
    private IndicesMenuView indicesMenuView;
    private IndicePrimarioModelo modelo;

    public PrimaryIndexController(PrimaryIndexView view) {
        this.view = view;
        initializeListeners();
        // Inicialmente deshabilitar simulación hasta calcular
        view.habilitarSimulacion(false);
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

        // Listener para el botón simular
        view.addSimularListener(e -> iniciarSimulacion());

        // Listener para el botón buscar
        view.addBuscarListener(e -> realizarBusqueda());

        // Listener para el botón limpiar
        view.addLimpiarListener(e -> limpiarFormulario());

        // Listener para el botón volver
        view.addVolverListener(e -> volverAlMenu());
    }

    /**
     * Realiza el cálculo del índice primario
     */
    private void calcularIndice() {
        try {
            // Validar y obtener datos de entrada
            int registros = validarYObtenerEntero(view.getRegistros(), "Número de registros");
            int bloque = validarYObtenerEntero(view.getBloque(), "Tamaño del bloque");
            int dato = validarYObtenerEntero(view.getDato(), "Longitud del registro de datos");
            int indice = validarYObtenerEntero(view.getIndice(), "Longitud de entrada de índice");

            // Validaciones adicionales
            validarParametros(registros, bloque, dato, indice);

            // Crear modelo y realizar cálculos
            modelo = new IndicePrimarioModelo(registros, bloque, dato, indice);

            if (!modelo.esConfiguracionValida()) {
                throw new IllegalArgumentException("La configuración proporcionada no es válida");
            }

            // Obtener todos los cálculos
            int bfr = modelo.calcularBfr();
            int bfri = modelo.calcularBfri();
            int bloquesDatos = modelo.calcularBloquesDatos();
            int bloquesIndice = modelo.calcularBloquesIndice();
            int totalEntradas = modelo.calcularTotalEntradas();
            int accesos = modelo.calcularAccesos();
            double mejora = modelo.calcularMejoraRendimiento();
            long espacioDatos = modelo.calcularEspacioDatos();
            long espacioIndice = modelo.calcularEspacioIndice();
            long espacioTotal = modelo.calcularEspacioTotal();
            double overhead = modelo.calcularOverheadIndice();
            double eficiencia = modelo.calcularEficienciaAlmacenamiento();

            // Formatear resultados detallados
            StringBuilder resultado = new StringBuilder();
            resultado.append("=== ANÁLISIS COMPLETO DEL ÍNDICE PRIMARIO ===\n\n");

            resultado.append("📋 PARÁMETROS DE ENTRADA:\n");
            resultado.append(String.format("• Registros totales: %,d\n", registros));
            resultado.append(String.format("• Tamaño de bloque: %,d bytes (%.1f KB)\n", bloque, bloque/1024.0));
            resultado.append(String.format("• Tamaño registro datos: %d bytes\n", dato));
            resultado.append(String.format("• Tamaño entrada índice: %d bytes\n\n", indice));

            resultado.append("⚙️ FACTORES DE BLOQUEO:\n");
            resultado.append(String.format("• bfr (registros datos/bloque): %d\n", bfr));
            resultado.append(String.format("• bfri (entradas índice/bloque): %d\n\n", bfri));

            resultado.append("🏗️ ESTRUCTURA DEL ÍNDICE:\n");
            resultado.append(String.format("• Bloques de datos necesarios: %,d\n", bloquesDatos));
            resultado.append(String.format("• Bloques de índice necesarios: %,d\n", bloquesIndice));
            resultado.append(String.format("• Total entradas de índice: %,d\n", totalEntradas));
            resultado.append(String.format("• Relación índice/datos: 1 entrada por cada bloque de datos\n\n"));

            resultado.append("🚀 ANÁLISIS DE RENDIMIENTO:\n");
            resultado.append(String.format("• Accesos a disco por búsqueda: %d\n", accesos));
            if (bloquesIndice == 1) {
                resultado.append("  └─ 1 acceso al índice + 1 acceso a datos\n");
            } else {
                resultado.append(String.format("  └─ %d acceso(s) búsqueda en índice + 1 acceso a datos\n", accesos - 1));
            }
            resultado.append(String.format("• Mejora vs búsqueda secuencial: %.1fx más rápido\n", mejora));
            resultado.append(String.format("• Peor caso búsqueda secuencial: %,d accesos\n\n", bloquesDatos));

            resultado.append("💾 ANÁLISIS DE ALMACENAMIENTO:\n");
            resultado.append(String.format("• Espacio archivo de datos: %,d bytes (%.2f MB)\n",
                    espacioDatos, espacioDatos / (1024.0 * 1024.0)));
            resultado.append(String.format("• Espacio archivo de índice: %,d bytes (%.2f MB)\n",
                    espacioIndice, espacioIndice / (1024.0 * 1024.0)));
            resultado.append(String.format("• Espacio total del sistema: %,d bytes (%.2f MB)\n",
                    espacioTotal, espacioTotal / (1024.0 * 1024.0)));
            resultado.append(String.format("• Overhead del índice: %.2f%%\n", overhead));
            resultado.append(String.format("• Eficiencia almacenamiento datos: %.1f%%\n\n", eficiencia));

            resultado.append("📊 CARACTERÍSTICAS DEL ÍNDICE PRIMARIO:\n");
            resultado.append("• ✅ Archivo de datos ordenado por clave primaria\n");
            resultado.append("• ✅ Una entrada de índice por bloque de datos\n");
            resultado.append("• ✅ Índice denso a nivel de bloques\n");
            resultado.append("• ✅ Acceso directo al bloque correcto\n");
            resultado.append("• ✅ Eficiente para rangos y búsquedas exactas\n");

            // Mostrar resultados en la vista
            view.mostrarResultados(resultado.toString());
            view.mostrarEstructura(bloquesDatos, bloquesIndice, bfr, bfri);

            // Habilitar simulación
            view.habilitarSimulacion(true);

            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(view,
                    String.format("Cálculo completado exitosamente.\n\n" +
                                    "Estructura generada:\n" +
                                    "• %,d bloques de datos\n" +
                                    "• %,d bloques de índice\n" +
                                    "• %d accesos máximos por búsqueda\n\n" +
                                    "La simulación de búsqueda ya está disponible.",
                            bloquesDatos, bloquesIndice, accesos),
                    "Índice Primario Calculado",
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
     * Inicia la simulación con datos de ejemplo
     */
    private void iniciarSimulacion() {
        if (modelo == null) {
            JOptionPane.showMessageDialog(view,
                    "Primero debe calcular el índice antes de simular búsquedas.",
                    "Simulación no disponible",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder simulacion = new StringBuilder();
        simulacion.append("=== SIMULACIÓN DE BÚSQUEDA EN ÍNDICE PRIMARIO ===\n\n");

        simulacion.append("🔧 CONFIGURACIÓN DEL SISTEMA:\n");
        simulacion.append(modelo.obtenerResumenConfiguracion()).append("\n");
        simulacion.append(String.format("• Bloques de datos: %,d\n", modelo.calcularBloquesDatos()));
        simulacion.append(String.format("• Bloques de índice: %,d\n", modelo.calcularBloquesIndice()));
        simulacion.append(String.format("• Registros por bloque datos: %d\n", modelo.calcularBfr()));
        simulacion.append(String.format("• Entradas por bloque índice: %d\n\n", modelo.calcularBfri()));

        simulacion.append("🎯 PROCESO DE BÚSQUEDA:\n");
        simulacion.append("El índice primario permite localizar directamente el bloque\n");
        simulacion.append("que contiene el registro buscado mediante los siguientes pasos:\n\n");

        simulacion.append("1️⃣ BÚSQUEDA EN EL ÍNDICE:\n");
        if (modelo.calcularBloquesIndice() == 1) {
            simulacion.append("   • Acceder al único bloque de índice\n");
            simulacion.append("   • Buscar la entrada correspondiente\n");
        } else {
            simulacion.append("   • Realizar búsqueda binaria en los bloques de índice\n");
            simulacion.append(String.format("   • Máximo %d accesos para encontrar la entrada\n",
                    modelo.calcularAccesos() - 1));
        }
        simulacion.append("   • Obtener el puntero al bloque de datos\n\n");

        simulacion.append("2️⃣ ACCESO A LOS DATOS:\n");
        simulacion.append("   • Acceder directamente al bloque identificado\n");
        simulacion.append("   • Buscar el registro específico dentro del bloque\n");
        simulacion.append("   • Retornar el registro encontrado\n\n");

        simulacion.append("📈 VENTAJAS DEL ÍNDICE PRIMARIO:\n");
        simulacion.append("• ⚡ Acceso directo al bloque correcto\n");
        simulacion.append("• 💾 Menor overhead de almacenamiento\n");
        simulacion.append("• 🔍 Eficiente para búsquedas exactas y por rango\n");
        simulacion.append("• 📊 Mantiene el orden natural de los datos\n\n");

        simulacion.append("🔍 USE EL CAMPO DE BÚSQUEDA PARA SIMULAR BÚSQUEDAS ESPECÍFICAS");

        view.mostrarSimulacion(simulacion.toString());

        JOptionPane.showMessageDialog(view,
                "Simulación inicializada correctamente.\n\n" +
                        "Puede probar búsquedas específicas usando\n" +
                        "el campo 'Buscar registro ID' en la pestaña de simulación.",
                "Simulación Lista",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Realiza una búsqueda específica simulada
     */
    private void realizarBusqueda() {
        if (modelo == null) {
            JOptionPane.showMessageDialog(view,
                    "Primero debe calcular el índice.",
                    "Búsqueda no disponible",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idBuscado = validarYObtenerEntero(view.getBuscar(), "ID a buscar");

            if (idBuscado < 1 || idBuscado > modelo.getRegistros()) {
                throw new IllegalArgumentException(
                        String.format("El ID debe estar entre 1 y %,d", modelo.getRegistros()));
            }

            // Simular búsqueda paso a paso
            StringBuilder busqueda = new StringBuilder();
            busqueda.append("=== SIMULACIÓN DE BÚSQUEDA ESPECÍFICA ===\n\n");

            busqueda.append(String.format("🎯 BUSCANDO REGISTRO ID: %,d\n\n", idBuscado));

            // Calcular en qué bloque está el registro
            int bfr = modelo.calcularBfr();
            int bloqueObjetivo = (idBuscado - 1) / bfr + 1;
            int posicionEnBloque = (idBuscado - 1) % bfr + 1;

            busqueda.append("📊 ANÁLISIS PREVIO:\n");
            busqueda.append(String.format("• Registros por bloque de datos: %d\n", bfr));
            busqueda.append(String.format("• Bloque objetivo calculado: %d\n", bloqueObjetivo));
            busqueda.append(String.format("• Posición dentro del bloque: %d\n\n", posicionEnBloque));

            // Simular accesos
            int accesoActual = 0;

            busqueda.append("🔍 PROCESO DE BÚSQUEDA:\n\n");

            // Paso 1: Búsqueda en el índice
            int bloquesIndice = modelo.calcularBloquesIndice();
            if (bloquesIndice == 1) {
                accesoActual++;
                busqueda.append(String.format("ACCESO %d - ÍNDICE:\n", accesoActual));
                busqueda.append("• Leer el único bloque de índice\n");
                busqueda.append(String.format("• Buscar entrada para bloque %d\n", bloqueObjetivo));
                busqueda.append("• ✅ Entrada encontrada\n");
                busqueda.append(String.format("• Puntero obtenido: Bloque de datos %d\n\n", bloqueObjetivo));
            } else {
                // Simular búsqueda binaria en el índice
                int accesosIndice = (int) (Math.log(bloquesIndice) / Math.log(2)) + 1;
                for (int i = 1; i <= accesosIndice; i++) {
                    accesoActual++;
                    busqueda.append(String.format("ACCESO %d - ÍNDICE (búsqueda binaria):\n", accesoActual));
                    if (i == accesosIndice) {
                        busqueda.append(String.format("• ✅ Entrada encontrada en bloque de índice %d\n", i));
                        busqueda.append(String.format("• Puntero obtenido: Bloque de datos %d\n\n", bloqueObjetivo));
                    } else {
                        busqueda.append(String.format("• Comparar en bloque de índice %d\n", i));
                        busqueda.append("• Continuar búsqueda...\n\n");
                    }
                }
            }

            // Paso 2: Acceso a los datos
            accesoActual++;
            busqueda.append(String.format("ACCESO %d - DATOS:\n", accesoActual));
            busqueda.append(String.format("• Leer bloque de datos %d\n", bloqueObjetivo));
            busqueda.append(String.format("• Buscar registro ID %,d en posición %d\n", idBuscado, posicionEnBloque));
            busqueda.append("• ✅ Registro encontrado\n\n");

            // Resumen final
            busqueda.append("📋 RESUMEN DE LA BÚSQUEDA:\n");
            busqueda.append(String.format("• Total de accesos a disco: %d\n", accesoActual));
            busqueda.append(String.format("• Accesos al índice: %d\n", accesoActual - 1));
            busqueda.append("• Accesos a datos: 1\n");
            busqueda.append(String.format("• Eficiencia: %.1fx mejor que búsqueda secuencial\n",
                    (double) modelo.calcularBloquesDatos() / accesoActual));
            busqueda.append(String.format("• Estado: ✅ REGISTRO %,d ENCONTRADO EXITOSAMENTE\n", idBuscado));

            view.mostrarSimulacion(busqueda.toString());

        } catch (NumberFormatException e) {
            mostrarError("Error en el ID de búsqueda",
                    "Por favor, ingrese un número entero válido para el ID.");
        } catch (IllegalArgumentException e) {
            mostrarError("Error de validación", e.getMessage());
        } catch (Exception e) {
            mostrarError("Error en la búsqueda",
                    "Ocurrió un error durante la simulación: " + e.getMessage());
        }
    }

    /**
     * Valida los parámetros de entrada
     */
    private void validarParametros(int registros, int bloque, int dato, int indice) {
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
            throw new IllegalArgumentException("La longitud de entrada de índice debe ser mayor que 0");
        }
        if (dato >= bloque) {
            throw new IllegalArgumentException(
                    "La longitud del registro de datos debe ser menor que el tamaño del bloque");
        }
        if (indice >= bloque) {
            throw new IllegalArgumentException(
                    "La longitud de entrada de índice debe ser menor que el tamaño del bloque");
        }
        if (bloque / dato < 1) {
            throw new IllegalArgumentException(
                    "El bloque debe poder almacenar al menos un registro de datos");
        }
        if (bloque / indice < 1) {
            throw new IllegalArgumentException(
                    "El bloque debe poder almacenar al menos una entrada de índice");
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
            int resultado = Integer.parseInt(valor.trim());
            if (resultado < 0) {
                throw new IllegalArgumentException("El campo '" + nombreCampo + "' debe ser positivo");
            }
            return resultado;
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
            view.habilitarSimulacion(false);
            modelo = null;
            JOptionPane.showMessageDialog(view,
                    "Formulario limpiado correctamente.\n" +
                            "Recalcule el índice para habilitar la simulación.",
                    "Limpieza Completada",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Regresa al menú de índices
     */
    private void volverAlMenu() {
        int opcion = JOptionPane.showConfirmDialog(view,
                "¿Desea volver al menú de índices?\n" +
                        "Se perderán los cálculos actuales.",
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
    public PrimaryIndexView getView() {
        return view;
    }

    /**
     * Obtiene el modelo actual (puede ser null si no se ha calculado)
     */
    public IndicePrimarioModelo getModelo() {
        return modelo;
    }
}
