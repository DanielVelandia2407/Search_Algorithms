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

    public List<String> eliminar(int clave, boolean permitirReduccion) {
        List<String> pasos = new ArrayList<>();

        if (!claves.contains(clave)) {
            pasos.add("La clave no existe en la estructura.");
            return pasos;
        }

        // Calcular DOR hipotético ANTES de eliminar
        int clavesTemporales = claves.size() - 1;
        double dorHipotetico = (double) clavesTemporales / n;

        pasos.add(String.format("DOR actual: %.2f", (double) claves.size() / n));
        pasos.add(String.format("DOR hipotético tras eliminar: %.2f", dorHipotetico));

        // Validar contra la densidad mínima DEL USUARIO
        if (dorHipotetico < densMinDelete && permitirReduccion) {
            pasos.add("Se caerá por debajo de la densidad mínima.");
        } else {
            pasos.add("La densidad se mantiene por encima del mínimo. Eliminación permitida.");
        }

        // Eliminación real solo si no es una previsualización
        if (!permitirReduccion) {
            int col = clave % n;
            if (tabla[0][col] != null && tabla[0][col] == clave) {
                tabla[0][col] = null;
            } else if (tabla[1][col] != null && tabla[1][col] == clave) {
                tabla[1][col] = null;
            }
            claves.remove((Integer) clave);
            pasos.add("Eliminada clave " + clave + " de columna " + col);

            // Reorganizar claves y colisiones
            if (tabla[0][col] == null && tabla[1][col] != null) {
                tabla[0][col] = tabla[1][col];
                tabla[1][col] = null;
                pasos.add("Clave " + tabla[0][col] + " subida de fila 2 a fila 1");
            }

            if (!colisiones.get(col).isEmpty()) {
                Integer nueva = colisiones.get(col).remove(0);
                if (tabla[0][col] == null) {
                    tabla[0][col] = nueva;
                    claves.add(nueva);
                    pasos.add("Clave colisionada " + nueva + " reubicada en fila 1");
                } else if (tabla[1][col] == null) {
                    tabla[1][col] = nueva;
                    claves.add(nueva);
                    pasos.add("Clave colisionada " + nueva + " reubicada en fila 2");
                } else {
                    colisiones.get(col).add(0, nueva);
                }
            }
        }

        return pasos;
    }

    public void expandir(List<Integer> anteriores) {
        int nuevoN;
        if (historialTamanos.size() == 1) {
            nuevoN = (int) (nOriginal * 1.5); // Primera expansión: 1.5N
        } else {
            nuevoN = historialTamanos.get(historialTamanos.size() - 2) * 2;
        }
        historialTamanos.add(nuevoN);
        n = nuevoN;
        reinicializar();
        for (int k : anteriores) insertar(k);
    }

    public void reducir(List<Integer> anteriores) {
        if (historialTamanos.size() > 1) {
            historialTamanos.remove(historialTamanos.size() - 1);
            n = historialTamanos.get(historialTamanos.size() - 1);
        } else {
            n = Math.max(1, n / 2);
        }
        reinicializar();
        for (int k : anteriores) insertar(k);
    }

    private int contarColisiones() {
        int total = 0;
        for (List<Integer> lista : colisiones) total += lista.size();
        return total;
    }

    public double obtenerDensidad() {
        int total = claves.size() + contarColisiones();
        return (double) total / (filas * n);
    }

    // Get ySet
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
    public double obtenerDensidadReduccion() {
        return (double) claves.size() / n; // DOR = claves / columnas (sin filas)
    }
}

