package model.trees;

import javax.swing.JTextArea;

/**
 * Proxy MVC para ArbolResiduoMultiple.
 */
public class ResiduoMultipleModel {
    private final ArbolResiduoMultiple arbol;

    public ResiduoMultipleModel(int tamGrupo) {
        this.arbol = new ArbolResiduoMultiple(tamGrupo);
    }

    public boolean insert(String key, JTextArea area) {
        // Inserta y siempre genera log en area
        if (!arbol.existeClave(key)) {
            arbol.insertar(key, area);
            return true;
        }
        arbol.insertar(key, area); // para que area imprima “ya existe”
        return false;
    }

    public boolean delete(String key, JTextArea area) {
        if (arbol.existeClave(key)) {
            arbol.eliminar(key, area);
            return true;
        }
        arbol.eliminar(key, area); // para log de “no existe”
        return false;
    }

    public boolean search(String key) {
        return arbol.existeClave(key);
    }

    public void clear() {
        arbol.borrarTodo();
    }

    public ArbolResiduoMultiple getArbol() {
        return arbol;
    }

    public void setWidth(int w) {
        arbol.setWidth(w);
    }
}
