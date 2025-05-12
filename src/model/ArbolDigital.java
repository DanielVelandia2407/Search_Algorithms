package model;

import javax.swing.JTextArea;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Implementación de un Árbol Digital (Trie binario).
 * Almacena caracteres usando su representación binaria.
 */
public class ArbolDigital extends ArbolBase {
    private NodoArbol raiz = new NodoArbol(' ', "Raíz");
    private final int BITS = 5;
    private int anchura = 1000; // Para el cálculo de posiciones en la visualización

    /**
     * Inserta una palabra en el árbol, codificando cada carácter en binario.
     *
     * @param palabra La palabra a insertar
     * @param area Área de texto para mostrar información del proceso
     */
    @Override
    public void insertar(String palabra, JTextArea area) {
        area.append("\n=== ÁRBOL DIGITAL ===\n");
        for (char c : palabra.toCharArray()) {
            String binario = String.format("%5s", Integer.toBinaryString(c % 32)).replace(' ', '0');
            area.append("Insertando '" + c + "': " + binario + "\n");

            NodoArbol actual = raiz;
            for (int i = 0; i < BITS; i++) {
                String bit = String.valueOf(binario.charAt(i));

                if (!actual.getHijos().containsKey(bit)) {
                    NodoArbol nuevo = new NodoArbol(' ', bit);
                    actual.agregarHijo(bit, nuevo);
                    area.append("Creando nodo para bit " + bit + " en nivel " + i + "\n");
                }
                actual = actual.getHijos().get(bit);
            }
            actual.setCaracter(c);
            area.append("Carácter '" + c + "' almacenado en nodo final\n");
        }
    }

    /**
     * Dibuja el árbol en el contexto gráfico proporcionado.
     *
     * @param g Contexto gráfico para dibujar
     */
    @Override
    public void dibujar(Graphics g) {
        dibujarNodo((Graphics2D) g, raiz, getWidth()/2, 50, getWidth()/4);
    }

    /**
     * Método recursivo para dibujar un nodo y sus hijos.
     *
     * @param g Contexto gráfico
     * @param nodo Nodo actual a dibujar
     * @param x Coordenada X del nodo
     * @param y Coordenada Y del nodo
     * @param offset Desplazamiento horizontal para los hijos
     */
    private void dibujarNodo(Graphics2D g, NodoArbol nodo, int x, int y, int offset) {
        if (nodo == null) return;

        // Guardar la posición del nodo para futura referencia
        nodo.setPosicion(x, y);

        // Dibujar conexiones
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(3));
        if (nodo.getHijos().containsKey("0")) {
            int leftX = x - offset;
            g.drawLine(x, y + 30, leftX, y + 100 - 30);
            g.drawString("0", (x + leftX)/2 - 10, (y + (y + 100))/2 - 10);
            dibujarNodo(g, nodo.getHijos().get("0"), leftX, y + 100, offset/2);
        }
        if (nodo.getHijos().containsKey("1")) {
            int rightX = x + offset;
            g.drawLine(x, y + 30, rightX, y + 100 - 30);
            g.drawString("1", (x + rightX)/2 + 5, (y + (y + 100))/2 - 10);
            dibujarNodo(g, nodo.getHijos().get("1"), rightX, y + 100, offset/2);
        }

        // Dibujar nodo
        g.setColor(new Color(173, 216, 230));
        g.fillOval(x - 30, y - 30, 60, 60);
        g.setColor(Color.BLACK);
        g.drawOval(x - 30, y - 30, 60, 60);

        if (nodo.getCaracter() != ' ') {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            String text = String.valueOf(nodo.getCaracter());
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, x - fm.stringWidth(text)/2, y + 6);
        }
    }

    /**
     * Establece el ancho del área de dibujo.
     *
     * @param width Ancho en píxeles
     */
    public void setWidth(int width) {
        this.anchura = width;
    }

    /**
     * Obtiene el ancho del área de dibujo.
     *
     * @return Ancho en píxeles
     */
    private int getWidth() {
        return anchura;
    }

    /**
     * Obtiene el nodo en las coordenadas especificadas.
     *
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return El nodo en esa posición, o null si no hay ninguno
     */
    @Override
    public NodoArbol obtenerNodoEn(int x, int y) {
        return buscarNodo(raiz, x, y);
    }

    /**
     * Método recursivo para buscar un nodo en las coordenadas especificadas.
     *
     * @param nodo Nodo actual
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return El nodo encontrado, o null si no hay ninguno
     */
    private NodoArbol buscarNodo(NodoArbol nodo, int x, int y) {
        if (Math.abs(nodo.getX() - x) < 15 && Math.abs(nodo.getY() - y) < 15) {
            return nodo;
        }
        for (NodoArbol hijo : nodo.getHijos().values()) {
            NodoArbol encontrado = buscarNodo(hijo, x, y);
            if (encontrado != null) return encontrado;
        }
        return null;
    }

    /**
     * Elimina una clave del árbol.
     *
     * @param clave La clave a eliminar
     * @param area Área de texto para mostrar información del proceso
     */
    @Override
    public void eliminar(String clave, JTextArea area) {
        Stack<NodoArbol> pila = new Stack<>();
        for (char c : clave.toCharArray()) {
            String binario = String.format("%5s", Integer.toBinaryString(c % 32)).replace(' ', '0');
            NodoArbol actual = raiz;
            pila.clear();
            pila.push(raiz);

            for (int i = 0; i < BITS; i++) {
                String bit = String.valueOf(binario.charAt(i));
                if (!actual.getHijos().containsKey(bit)) return;
                actual = actual.getHijos().get(bit);
                pila.push(actual);
            }

            if (actual.getCaracter() == c) {
                actual.setCaracter(' ');
                area.append("Carácter '" + c + "' eliminado\n");

                // Eliminar ruta redundante
                while (pila.size() > 1) {
                    NodoArbol hijo = pila.pop();
                    NodoArbol padre = pila.peek();
                    if (hijo.getHijos().isEmpty() && hijo.getCaracter() == ' ') {
                        padre.getHijos().remove(hijo.getClave());
                    }
                }
            }
        }
    }

    /**
     * Borra todos los nodos del árbol, dejando solo la raíz.
     */
    @Override
    public void borrarTodo() {
        raiz = new NodoArbol(' ', "Raíz");
    }

    /**
     * Verifica si una clave existe en el árbol.
     *
     * @param clave La clave a verificar
     * @return true si la clave existe, false en caso contrario
     */
    @Override
    public boolean existeClave(String clave) {
        for (char c : clave.toCharArray()) {
            String binario = String.format("%5s", Integer.toBinaryString(c % 32)).replace(' ', '0');
            NodoArbol actual = raiz;
            for (int i = 0; i < BITS; i++) {
                String bit = String.valueOf(binario.charAt(i));
                if (!actual.getHijos().containsKey(bit)) return false;
                actual = actual.getHijos().get(bit);
            }
            if (actual.getCaracter() != c) return false;
        }
        return true;
    }
}