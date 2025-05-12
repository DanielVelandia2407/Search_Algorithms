package model;

import javax.swing.JTextArea;
import java.awt.Graphics;

/**
 * Clase abstracta que define las operaciones básicas que deben implementar todos los árboles.
 * Sirve como base para las diferentes implementaciones de árboles.
 */
public abstract class ArbolBase {
    /**
     * Inserta una clave en el árbol.
     *
     * @param clave La clave a insertar
     * @param area Área de texto para mostrar información del proceso
     */
    public abstract void insertar(String clave, JTextArea area);

    /**
     * Elimina una clave del árbol.
     *
     * @param clave La clave a eliminar
     * @param area Área de texto para mostrar información del proceso
     */
    public abstract void eliminar(String clave, JTextArea area);

    /**
     * Dibuja el árbol en el componente gráfico.
     *
     * @param g Contexto gráfico para dibujar
     */
    public abstract void dibujar(Graphics g);

    /**
     * Obtiene el nodo que se encuentra en las coordenadas especificadas.
     *
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return El nodo en esa posición, o null si no hay ninguno
     */
    public abstract NodoArbol obtenerNodoEn(int x, int y);

    /**
     * Verifica si una clave existe en el árbol.
     *
     * @param clave La clave a verificar
     * @return true si la clave existe, false en caso contrario
     */
    public abstract boolean existeClave(String clave);

    /**
     * Borra todos los nodos del árbol.
     */
    public abstract void borrarTodo();
}