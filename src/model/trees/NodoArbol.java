package model.trees;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase que representa un nodo en un árbol.
 * Cada nodo tiene un carácter asociado, una clave que lo identifica,
 * coordenadas para su posición visual, y una colección de nodos hijos.
 */
public class NodoArbol {
    private char caracter;
    private String clave;
    private int x;
    private int y;
    private Map<String, NodoArbol> hijos;

    /**
     * Constructor que inicializa un nodo con un carácter y una clave.
     *
     * @param caracter El carácter asociado al nodo
     * @param clave La clave que identifica al nodo
     */
    public NodoArbol(char caracter, String clave) {
        this.caracter = caracter;
        this.clave = clave;
        this.hijos = new HashMap<>();
    }

    /**
     * Agrega un hijo al nodo actual.
     *
     * @param clave La clave que identifica al hijo
     * @param hijo El nodo hijo a agregar
     */
    public void agregarHijo(String clave, NodoArbol hijo) {
        this.hijos.put(clave, hijo);
    }

    /**
     * Establece la clave del nodo.
     *
     * @param clave La nueva clave
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * Obtiene el carácter asociado al nodo.
     *
     * @return El carácter
     */
    public char getCaracter() {
        return this.caracter;
    }

    /**
     * Obtiene la clave que identifica al nodo.
     *
     * @return La clave
     */
    public String getClave() {
        return this.clave;
    }

    /**
     * Obtiene el mapa de hijos del nodo.
     *
     * @return Mapa con los nodos hijos
     */
    public Map<String, NodoArbol> getHijos() {
        return this.hijos;
    }

    /**
     * Obtiene la coordenada X del nodo para su visualización.
     *
     * @return Coordenada X
     */
    public int getX() {
        return this.x;
    }

    /**
     * Obtiene la coordenada Y del nodo para su visualización.
     *
     * @return Coordenada Y
     */
    public int getY() {
        return this.y;
    }

    /**
     * Establece las coordenadas del nodo para su visualización.
     *
     * @param x Coordenada X
     * @param y Coordenada Y
     */
    public void setPosicion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Establece el carácter asociado al nodo.
     *
     * @param c El nuevo carácter
     */
    public void setCaracter(char c) {
        this.caracter = c;
    }
}