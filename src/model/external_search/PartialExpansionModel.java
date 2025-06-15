package model.external_search;

import java.util.ArrayList;
import java.util.List;

public class PartialExpansionModel {
    private int nOriginal;
    private int n;
    private final int filas = 2;
    private double densMaxInsert;
    private double densMinDelete;
    private Integer[][] tabla;
    private List<List<Integer>> colisiones;
    private List<Integer> claves;
    private List<Integer> historialTamanos = new ArrayList<>();

    public PartialExpansionModel(int n, double densMaxInsert, double densMinDelete) {
        this.nOriginal = n;
        this.n = n;
        this.historialTamanos.add(n);
        this.densMaxInsert = densMaxInsert;
        this.densMinDelete = densMinDelete;
        reinicializar();
    }

    private void reinicializar() {
        tabla = new Integer[filas][n];
        colisiones = new ArrayList<>();
        for (int i = 0; i < n; i++) colisiones.add(new ArrayList<>());
        claves = new ArrayList<>();
    }

    public List<String> insertar(int clave) {
        List<String> pasos = new ArrayList<>();

        int ocupActual = claves.size() + contarColisiones();
        double densAntes = (double) ocupActual / (filas * n);
        pasos.add(String.format("Densidad (Expansión) antes: %.2f", densAntes));

        double densDesp = (double) (ocupActual + 1) / (filas * n);
        pasos.add(String.format("Densidad (Expansión) tras insertar: %.2f", densDesp));
        pasos.add("H(" + clave + ") = " + clave + " mod " + n + " = " + (clave % n));

        if (claves.contains(clave)) {
            pasos.add("La estructura no admite claves repetidas.");
            return pasos;
        }

        if (densDesp > densMaxInsert) {
            pasos.add("Se superará la densidad máxima.");
            return pasos;
        }

        int col = clave % n;
        if (tabla[0][col] == null) {
            tabla[0][col] = clave;
            claves.add(clave);
            pasos.add("Insertado en fila 1, columna " + col);
        } else if (tabla[1][col] == null) {
            tabla[1][col] = clave;
            claves.add(clave);
            pasos.add("Insertado en fila 2, columna " + col);
        } else {
            colisiones.get(col).add(clave);
            pasos.add("Colisión externa en columna " + col);
        }

        return pasos;
    }

    /**
     * Método para insertar una clave sin verificar la densidad máxima
     * Se usa cuando se quiere forzar la inserción antes de expandir
     */
    public List<String> insertarForzado(int clave) {
        List<String> pasos = new ArrayList<>();

        int ocupActual = claves.size() + contarColisiones();
        double densAntes = (double) ocupActual / (filas * n);
        pasos.add(String.format("Densidad (Expansión) antes: %.2f", densAntes));

        double densDesp = (double) (ocupActual + 1) / (filas * n);
        pasos.add(String.format("Densidad (Expansión) tras insertar: %.2f", densDesp));
        pasos.add("H(" + clave + ") = " + clave + " mod " + n + " = " + (clave % n));

        if (claves.contains(clave)) {
            pasos.add("La estructura no admite claves repetidas.");
            return pasos;
        }

        // Realizar la inserción física
        int col = clave % n;
        if (tabla[0][col] == null) {
            tabla[0][col] = clave;
            claves.add(clave);
            pasos.add("✓ Clave " + clave + " insertada en fila 1, columna " + col);
        } else if (tabla[1][col] == null) {
            tabla[1][col] = clave;
            claves.add(clave);
            pasos.add("✓ Clave " + clave + " insertada en fila 2, columna " + col);
        } else {
            colisiones.get(col).add(clave);
            pasos.add("✓ Clave " + clave + " agregada a colisiones en columna " + col);
        }

        // Mostrar el estado de la densidad después de la inserción
        if (densDesp > densMaxInsert) {
            pasos.add("⚠️ Densidad máxima superada: " + String.format("%.2f", densDesp) + 
                     " > " + String.format("%.2f", densMaxInsert));
            pasos.add("Se requiere expansión de la estructura.");
        }

        return pasos;
    }

    /**
     * Obtiene la ocupación actual (claves + colisiones)
     */
    public int obtenerOcupacionActual() {
        return claves.size() + contarColisiones();
    }

public List<String> eliminar(int clave, boolean esSimulacion) {
    List<String> pasos = new ArrayList<>();

    if (!claves.contains(clave)) {
        pasos.add("La clave " + clave + " no existe en la estructura.");
        return pasos;
    }

    pasos.add(String.format("DOR actual (antes de eliminar): %.2f", (double) claves.size() / n));

    // Eliminación real solo si no es una simulación
    if (!esSimulacion) {
        realizarEliminacionFisica(clave, pasos);
    }

    // DOR actualizado después de eliminar
    double dorActual = (double) claves.size() / n;
    pasos.add(String.format("DOR actual (después de eliminar): %.2f", dorActual));

    boolean puedeReducir = puedeReducirEstructura();

    if (dorActual < densMinDelete && puedeReducir) {
        pasos.add("⚠️ Se caerá por debajo de la densidad mínima (" +
                String.format("%.2f", densMinDelete) + ")");
        pasos.add("✓ Es posible reducir la estructura (tamaño actual > original)");
        if (esSimulacion) {
            pasos.add("Se recomienda reducir la estructura.");
        } else {
            pasos.add("↘ Reducción automática tras la eliminación.");
            reducir(claves);
        }
    } else if (dorActual < densMinDelete && !puedeReducir) {
        pasos.add("⚠️ Se caerá por debajo de la densidad mínima (" +
                String.format("%.2f", densMinDelete) + ")");
        pasos.add("⚠️ No es posible reducir más (ya está en tamaño mínimo)");
        if (esSimulacion) {
            pasos.add("Eliminación permitida sin reducción.");
        }
    } else if (esSimulacion) {
        pasos.add("✓ La densidad se mantiene por encima del mínimo. Eliminación permitida.");
    }

    return pasos;
}

    /**
     * Realiza la eliminación física de la clave y reorganiza la estructura
     */
    private void realizarEliminacionFisica(int clave, List<String> pasos) {
        int col = clave % n;
        
        // Eliminar la clave de la tabla
        if (tabla[0][col] != null && tabla[0][col].equals(clave)) {
            tabla[0][col] = null;
            pasos.add("✓ Eliminada clave " + clave + " de fila 1, columna " + col);
        } else if (tabla[1][col] != null && tabla[1][col].equals(clave)) {
            tabla[1][col] = null;
            pasos.add("✓ Eliminada clave " + clave + " de fila 2, columna " + col);
        }
        
        claves.remove((Integer) clave);

        // Reorganizar: subir clave de fila 2 a fila 1 si está vacía
        if (tabla[0][col] == null && tabla[1][col] != null) {
            tabla[0][col] = tabla[1][col];
            tabla[1][col] = null;
            pasos.add("↑ Clave " + tabla[0][col] + " subida de fila 2 a fila 1");
        }

        // Reorganizar colisiones: mover primera colisión a la tabla principal
        if (!colisiones.get(col).isEmpty()) {
            Integer nuevaClave = colisiones.get(col).remove(0);
            if (tabla[0][col] == null) {
                tabla[0][col] = nuevaClave;
                claves.add(nuevaClave);
                pasos.add("↑ Clave colisionada " + nuevaClave + " reubicada en fila 1, columna " + col);
            } else if (tabla[1][col] == null) {
                tabla[1][col] = nuevaClave;
                claves.add(nuevaClave);
                pasos.add("↑ Clave colisionada " + nuevaClave + " reubicada en fila 2, columna " + col);
            } else {
                // Esto no debería pasar si la reorganización es correcta
                colisiones.get(col).add(0, nuevaClave);
                pasos.add("→ Clave " + nuevaClave + " permanece en colisiones");
            }
        }
    }

    /**
     * Verifica si se debe reducir la estructura después de una eliminación
     */
public boolean debeReducir() {
    // Validación defensiva por si hay corrupción
    if (n == 0) return false;

    int clavesValidas = claves.size(); // Solo claves en tabla principal
    double dorActual = (double) clavesValidas / n;
    return dorActual < densMinDelete && puedeReducirEstructura();
}


    /**
     * Verifica si es posible reducir la estructura
     * Solo se puede reducir si el tamaño actual es mayor al original
     */
    public boolean puedeReducirEstructura() {
        return historialTamanos.size() > 1; // Hay expansiones previas
    }

    /**
     * Calcula el siguiente tamaño para reducción
     */
    public int calcularSiguienteTamanoReduccion() {
        if (historialTamanos.size() <= 1) {
            return n; // No se puede reducir más
        }
        
        // Volver al tamaño anterior en el historial
        return historialTamanos.get(historialTamanos.size() - 2);
    }

    public void expandir(List<Integer> anteriores) {
        int nuevoN;
        if (historialTamanos.size() == 1) {
            nuevoN = (int) Math.ceil(nOriginal * 1.5); // Primera expansión: 1.5N
        } else {
            nuevoN = historialTamanos.get(historialTamanos.size() - 2) * 2;
        }
        
        historialTamanos.add(nuevoN);
        int anteriorN = n;
        n = nuevoN;
        
        // Limpiar la estructura para la expansión
        reinicializar();
        
        System.out.println("Expansión: " + anteriorN + " → " + nuevoN + " columnas");
    }

    public void reducir(List<Integer> anteriores) {
        if (historialTamanos.size() <= 1) {
            System.out.println("No se puede reducir: ya está en el tamaño mínimo");
            return;
        }
        
        int anteriorN = n;
        
        // Volver al tamaño anterior en el historial
        historialTamanos.remove(historialTamanos.size() - 1);
        n = historialTamanos.get(historialTamanos.size() - 1);
        
        // Limpiar la estructura para la reducción
        reinicializar();
        
        System.out.println("Reducción: " + anteriorN + " → " + n + " columnas");
    }

    private int contarColisiones() {
        int total = 0;
        for (List<Integer> lista : colisiones) {
            total += lista.size();
        }
        return total;
    }

    public double obtenerDensidad() {
        int total = claves.size() + contarColisiones();
        return (double) total / (filas * n);
    }

    public double obtenerDensidadReduccion() {
        return (double) claves.size() / n; // DOR = claves / columnas (sin filas)
    }

    /**
     * Obtiene información detallada del estado actual
     */
    public String obtenerEstadoDetallado() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Estado de la Estructura ===\n");
        sb.append("Tamaño actual: ").append(n).append(" columnas\n");
        sb.append("Tamaño original: ").append(nOriginal).append(" columnas\n");
        sb.append("Claves almacenadas: ").append(claves.size()).append("\n");
        sb.append("Colisiones: ").append(contarColisiones()).append("\n");
        sb.append("Densidad actual: ").append(String.format("%.2f", obtenerDensidad())).append("\n");
        sb.append("DOR actual: ").append(String.format("%.2f", obtenerDensidadReduccion())).append("\n");
        sb.append("Historial de tamaños: ").append(historialTamanos).append("\n");
        sb.append("Puede reducir: ").append(puedeReducirEstructura() ? "Sí" : "No").append("\n");
        return sb.toString();
    }

    // Getters
    public Integer[][] getTabla() {
        return tabla;
    }

    public List<List<Integer>> getColisiones() {
        return colisiones;
    }

    public int getColumnas() {
        return n;
    }

    public double getDensidadMaxInsert() {
        return densMaxInsert;
    }

    public double getDensidadMinDelete() {
        return densMinDelete;
    }

    public List<Integer> getClaves() {
        return new ArrayList<>(claves);
    }

    public int getTamanoOriginal() {
        return nOriginal;
    }

    public List<Integer> getHistorialTamanos() {
        return new ArrayList<>(historialTamanos);
    }
}