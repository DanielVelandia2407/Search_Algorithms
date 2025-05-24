package model;

import javax.swing.JTextArea;

/**
 * Proxy MVC para ArbolResiduoSimple.
 */
public class ResiduoSimpleModel {
    private final ArbolResiduoSimple arbol = new ArbolResiduoSimple();

    public boolean insert(String key, JTextArea area) {
        if (!arbol.existeClave(key)) {
            arbol.insertar(key, area);
            return true;
        }
        arbol.insertar(key, area); // para que imprima “ya existe”
        return false;
    }

    public boolean delete(String key, JTextArea area) {
        if (arbol.existeClave(key)) {
            arbol.eliminar(key, area);
            return true;
        }
        arbol.eliminar(key, area);
        return false;
    }

    public boolean search(String key) {
        return arbol.existeClave(key);
    }

    public void clear() {
        arbol.borrarTodo();
    }

    public ArbolResiduoSimple getArbol() {
        return arbol;
    }

    public void setWidth(int width) {
        arbol.setWidth(width);
    }
}
