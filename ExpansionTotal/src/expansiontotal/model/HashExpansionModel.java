package expansiontotal.model;

import java.util.ArrayList;
import java.util.List;

public class HashExpansionModel {
    private int columnas;
    private final int filas = 2;
    private int n;
    private double densidadMaxima;
    private Integer[][] estructura;
    private List<List<Integer>> colisiones;
    private int numInsertados;

    public HashExpansionModel(int columnas, double densidadMaxima) {
        this.columnas = columnas;
        this.n = columnas;
        this.densidadMaxima = densidadMaxima;
        this.estructura = new Integer[filas][columnas];
        this.colisiones = new ArrayList<>();
        for (int i = 0; i < columnas; i++) {
            colisiones.add(new ArrayList<>());
        }
        this.numInsertados = 0;
    }

    public List<String> insertarClave(int clave) {
        List<String> pasos = new ArrayList<>();
        double densidadActual = obtenerDensidad();
        pasos.add("Densidad actual: " + String.format("%.2f", densidadActual));
        pasos.add("H(" + clave + ") = " + clave + " mod " + n + " = " + (clave % n));

        int columna = clave % n;

        for (int i = 0; i < filas; i++) {
            if (estructura[i][columna] != null && estructura[i][columna] == clave) {
                pasos.add("La estructura no admite claves repetidas.");
                return pasos;
            }
        }
        if (colisiones.get(columna).contains(clave)) {
            pasos.add("La estructura no admite claves repetidas.");
            return pasos;
        }

        if (estaLlena()) {
            pasos.add("La densidad máxima ha sido alcanzada. No se pueden insertar más claves.");
            return pasos;
        }

        if (estructura[0][columna] == null) {
            estructura[0][columna] = clave;
            numInsertados++;
            pasos.add("Insertado en fila 1, columna " + columna);
            return pasos;
        }

        if (estructura[1][columna] == null) {
            estructura[1][columna] = clave;
            numInsertados++;
            pasos.add("Insertado en fila 2, columna " + columna);
            return pasos;
        }

        colisiones.get(columna).add(clave);
        pasos.add("Colisiones llenas. Clave colocada externamente en columna " + columna);
        return pasos;
    }

    public boolean estaLlena() {
        return obtenerDensidad() >= densidadMaxima;
    }

    public double obtenerDensidad() {
        return (double) numInsertados / (filas * columnas);
    }

    public Integer[][] getEstructura() {
        return estructura;
    }

    public List<List<Integer>> getColisiones() {
        return colisiones;
    }

    public int getColumnas() {
        return columnas;
    }

    public void expandirEstructura(List<Integer> claves) {
        this.columnas *= 2;
        this.n = columnas;
        this.estructura = new Integer[filas][columnas];
        this.colisiones = new ArrayList<>();
        for (int i = 0; i < columnas; i++) {
            colisiones.add(new ArrayList<>());
        }
        this.numInsertados = 0;

        for (int clave : claves) {
            insertarClave(clave);
        }
    }
}
