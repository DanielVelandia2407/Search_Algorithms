package model;

import java.util.ArrayList;
import java.util.List;

public class HashExpansionModel {
    private int n; // columnas
    private final int filas = 2;
    private double densMaxInsert;
    private double densMinDelete;

    private Integer[][] tabla;
    private List<List<Integer>> colisiones;
    private List<Integer> claves;

    public HashExpansionModel(int n, double densMaxInsert, double densMinDelete) {
        this.n = n;
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
        pasos.add(String.format("Densidad antes: %.2f", densAntes));

        double densDesp = (double) (ocupActual + 1) / (filas * n);
        pasos.add(String.format("Densidad tras insertar: %.2f", densDesp));
        pasos.add("H(" + clave + ") = " + clave + " mod " + n + " = " + (clave % n));

        if (claves.contains(clave)) {
            pasos.add("La estructura no admite claves repetidas.");
            return pasos;
        }

        // Verificar si se superará la densidad máxima
        if (densDesp > densMaxInsert) {
            pasos.add("Se superará la densidad máxima.");
            return pasos;
        }

        // Proceder con la inserción
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

        int col = clave % n;
        pasos.add(String.format("Densidad antes: %.2f", obtenerDensidad()));

        // 1. Eliminar la clave
        if (tabla[0][col] != null && tabla[0][col] == clave) {
            tabla[0][col] = null;
        } else if (tabla[1][col] != null && tabla[1][col] == clave) {
            tabla[1][col] = null;
        }
        claves.remove((Integer) clave);
        pasos.add("Eliminada clave " + clave + " de columna " + col);

        // 2. Subir clave de fila 2 a fila 1 si aplica
        if (tabla[0][col] == null && tabla[1][col] != null) {
            tabla[0][col] = tabla[1][col];
            tabla[1][col] = null;
            pasos.add("Clave " + tabla[0][col] + " subida de fila 2 a fila 1");
        }

        // 3. Reubicar colisión externa (si hay)
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
                // no debería ocurrir, pero lo dejamos por seguridad
                colisiones.get(col).add(0, nueva);
            }
        }

        double densDesp = obtenerDensidad();
        pasos.add(String.format("Densidad tras eliminar: %.2f", densDesp));

        if (densDesp < densMinDelete && permitirReduccion) {
            pasos.add("Se caerá por debajo de la densidad mínima.");
        }

        return pasos;
    }

    public void expandir(List<Integer> anteriores) {
        n *= 2;
        double tmpMax = densMaxInsert, tmpMin = densMinDelete;
        reinicializar();
        densMaxInsert = tmpMax;
        densMinDelete = tmpMin;
        for (int k : anteriores) insertar(k);
    }

    public void reducir(List<Integer> anteriores) {
        n = Math.max(1, n / 2);
        double tmpMax = densMaxInsert, tmpMin = densMinDelete;
        reinicializar();
        densMaxInsert = tmpMax;
        densMinDelete = tmpMin;
        for (int k : anteriores) insertar(k);
    }

    private int contarColisiones() {
        int total = 0;
        for (List<Integer> lista : colisiones) {
            total += lista.size();
        }
        return total;
    }

    public Integer[][] getTabla() {
        return tabla;
    }

    public List<List<Integer>> getColisiones() {
        return colisiones;
    }

    public int getColumnas() {
        return n;
    }

    public double obtenerDensidad() {
        int total = claves.size() + contarColisiones();
        return (double) total / (filas * n);
    }

    public int obtenerClavesInsertadas() {
        return claves.size();
    }

    public double getDensidadMaxInsert() {
        return densMaxInsert;
    }

    public double getDensidadMinDelete() {
        return densMinDelete;
    }
}