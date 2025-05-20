package model;

import javax.swing.JTextArea;

/**
 * Clase de adaptación que integra ArbolDigital con la arquitectura MVC del proyecto.
 * Sirve como proxy entre el controlador y la implementación real del árbol.
 */
public class DigitalTreeModel {

    private ArbolDigital arbol;

    /**
     * Constructor que inicializa el árbol digital.
     */
    public DigitalTreeModel() {
        this.arbol = new ArbolDigital();
    }

    /**
     * Inserta una palabra en el árbol.
     *
     * @param word La palabra a insertar
     * @return true si la inserción fue exitosa
     */
    public boolean insert(String word, JTextArea area) {
        if (!arbol.existeClave(word)) {
            arbol.insertar(word, area);
            return true;
        }
        // si ya existe, llama igual para que genere el mensaje
        arbol.insertar(word, area);
        return false;
    }

    /**
     * Verifica si una palabra existe en el árbol.
     *
     * @param word La palabra a buscar
     * @return true si la palabra existe
     */
    public boolean search(String word) {
        return arbol.existeClave(word);
    }

    /**
     * Elimina una palabra del árbol.
     *
     * @param word La palabra a eliminar
     * @return true si la palabra se eliminó correctamente
     */
    public boolean delete(String word, JTextArea area) {
        if (arbol.existeClave(word)) {
            arbol.eliminar(word, area);
            return true;
        }
        // para que area reciba el mensaje de “no existe”
        arbol.eliminar(word, area);
        return false;
    }

    /**
     * Limpia el árbol completamente.
     */
    public void clear() {
        arbol.borrarTodo();
    }

    /**
     * Obtiene el árbol para poder dibujarlo.
     *
     * @return La instancia del árbol digital
     */
    public ArbolDigital getArbol() {
        return arbol;
    }

    /**
     * Establece el ancho del área de dibujo del árbol.
     *
     * @param width Ancho en píxeles
     */
    public void setWidth(int width) {
        arbol.setWidth(width);
    }

    /**
     * Método ficticio para mantener compatibilidad con el código original.
     *
     * @return Un array con las palabras insertadas (vacío en esta implementación)
     */
    public String[] getAllWords() {
        // En la implementación actual no tenemos un método para obtener todas las palabras
        // Podríamos implementarlo si fuera necesario
        return new String[0];
    }

    /**
     * Método ficticio para mantener compatibilidad con el código original.
     *
     * @return Número de palabras (0 en esta implementación)
     */
    public int getWordCount() {
        // En la implementación actual no llevamos la cuenta de palabras
        return 0;
    }

    /**
     * Método ficticio para mantener compatibilidad con el código original.
     *
     * @return Un objeto temporal para evitar errores
     */
    public java.util.Map<String, Object> getTrieStructure() {
        // Devolvemos un mapa vacío para mantener compatibilidad
        return new java.util.HashMap<>();
    }

    /**
     * Método ficticio para mantener compatibilidad con el código original.
     * En este caso, el árbol binario no se implementa igual que en el original.
     */
    public static class BinaryNode {
        public String label;
        public BinaryNode left;
        public BinaryNode right;
        public char bit;

        public BinaryNode(String label) {
            this.label = label;
        }
    }

    /**
     * Método ficticio para mantener compatibilidad con el código original.
     *
     * @return null ya que usamos otra estructura
     */
    public BinaryNode getBinaryRoot() {
        return null;
    }
}
