package model;

import javax.swing.JTextArea;
import java.awt.*;
import java.util.Stack;

/**
 * Implementación de un Trie de Residuos Simple.
 * - La raíz no guarda clave.
 * - Inserción según primer bit; si colisiona, separa en el siguiente bit.
 */
public class ArbolResiduoSimple extends ArbolBase {
    private NodoArbol raiz = null;
    private final int BITS = 5;
    private int anchura = 1000;

    @Override
    public void insertar(String palabra, JTextArea area) {
        area.append("\n=== TRIE RESIDUO SIMPLE ===\n");
        for (char c : palabra.toCharArray()) {
            String binario = String.format("%5s", Integer.toBinaryString(c % 32))
                                  .replace(' ', '0');
            area.append("Insertando '" + c + "': " + binario + "\n");

            // Si no hay raíz, la creamos (sin clave)
            if (raiz == null) {
                raiz = new NodoArbol(' ', "Raíz");
                area.append("Raíz inicializada (vacía).\n");
            }

            NodoArbol actual = raiz;
            int nivel = 0;

            // Recorremos hasta insertar o separar
            while (nivel < BITS) {
                String bit = String.valueOf(binario.charAt(nivel));
                NodoArbol hijo = actual.getHijos().get(bit);

                if (hijo == null) {
                    // no hay nodo, insertamos aquí
                    NodoArbol nuevo = new NodoArbol(c, palabra);
                    nuevo.setCaracter(c);
                    actual.agregarHijo(bit, nuevo);
                    area.append("Clave '" + palabra + "' insertada en bit " + bit +
                                " nivel " + nivel + "\n");
                    break;
                }

                if (hijo.getCaracter() != c) {
                    // colisión: tenemos que separar ambos caracteres
                    area.append("¡Colisión! Nivel " + nivel + " entre '" +
                                hijo.getCaracter() + "' y '" + c + "', separando...\n");

                    // obtenemos binario del existente
                    String binExist = String.format("%5s", Integer.toBinaryString(hijo.getCaracter() % 32))
                                            .replace(' ', '0');

                    // removemos el hijo y creamos un subárbol en su lugar
                    actual.getHijos().remove(bit);
                    NodoArbol separador = new NodoArbol(' ', "sep" + nivel);
                    actual.agregarHijo(bit, separador);

                    // redistribuimos el existente
                    String bitExist = String.valueOf(binExist.charAt(nivel + 1));
                    separador.agregarHijo(bitExist, hijo);

                    // creamos nuevo nodo para la clave actual
                    String bitNuevo = String.valueOf(binario.charAt(nivel + 1));
                    NodoArbol nuevo = new NodoArbol(c, palabra);
                    nuevo.setCaracter(c);
                    separador.agregarHijo(bitNuevo, nuevo);

                    area.append("Separación completada en nivel " + (nivel + 1) + "\n");
                    break;
                }

                // si es el mismo caracter, ya existe
                if (hijo.getCaracter() == c && palabra.equals(hijo.getClave())) {
                    area.append("La clave '" + palabra + "' ya existe.\n");
                    break;
                }

                // avanzamos
                actual = hijo;
                nivel++;
            }
        }
    }

    @Override
    public boolean existeClave(String palabra) {
        if (raiz == null) return false;
        NodoArbol actual = raiz;
        for (char c : palabra.toCharArray()) {
            String binario = String.format("%5s", Integer.toBinaryString(c % 32))
                                  .replace(' ', '0');
            int nivel = 0;
            boolean encontrado = false;
            NodoArbol nodo = actual;

            while (nivel < BITS && nodo != null) {
                String bit = String.valueOf(binario.charAt(nivel));
                nodo = nodo.getHijos().get(bit);
                if (nodo != null && nodo.getCaracter() == c && palabra.equals(nodo.getClave())) {
                    encontrado = true;
                    break;
                }
                nivel++;
            }

            if (!encontrado) return false;
        }
        return true;
    }

    @Override
    public void eliminar(String palabra, JTextArea area) {
        if (!existeClave(palabra)) {
            area.append("\nLa clave '" + palabra + "' no existe.\n");
            return;
        }
        area.append("\n=== ELIMINACIÓN TRIE RESIDUO SIMPLE ===\n");
        // Recorremos igual que en existe, y limpiamos nodos sin hijos.
        for (char c : palabra.toCharArray()) {
            String binario = String.format("%5s", Integer.toBinaryString(c % 32))
                                  .replace(' ', '0');
            Stack<NodoArbol> pila = new Stack<>();
            NodoArbol actual = raiz;
            pila.push(actual);

            for (int nivel = 0; nivel < BITS; nivel++) {
                String bit = String.valueOf(binario.charAt(nivel));
                actual = actual.getHijos().get(bit);
                if (actual == null) break;
                pila.push(actual);
            }
            if (actual != null && actual.getCaracter() == c) {
                actual.setCaracter(' ');
                area.append("Carácter '" + c + "' eliminado.\n");
            }
            // limpieza hacia atrás
            while (pila.size() > 1) {
                NodoArbol nodo = pila.pop();
                if (nodo.getCaracter() == ' ' && nodo.getHijos().isEmpty()) {
                    NodoArbol padre = pila.peek();
                    padre.getHijos().entrySet().removeIf(e -> e.getValue() == nodo);
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public void dibujar(Graphics g) {
        if (raiz != null) {
            dibujarNodo((Graphics2D) g, raiz, anchura / 2, 50, anchura / 4);
        }
    }

    private void dibujarNodo(Graphics2D g, NodoArbol nodo, int x, int y, int offset) {
        if (nodo == null) return;
        nodo.setPosicion(x, y);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(3));
        for (var entry : nodo.getHijos().entrySet()) {
            String bit = entry.getKey();
            NodoArbol hijo = entry.getValue();
            int childX = bit.equals("0") ? x - offset : x + offset;
            g.drawLine(x, y + 30, childX, y + 100 - 30);
            g.drawString(bit, (x + childX) / 2 + (bit.equals("0") ? -10 : 5),
                         (y + (y + 100)) / 2 - 10);
            dibujarNodo(g, hijo, childX, y + 100, offset / 2);
        }
        g.setColor(new Color(173, 216, 230));
        g.fillOval(x - 30, y - 30, 60, 60);
        g.setColor(Color.BLACK);
        g.drawOval(x - 30, y - 30, 60, 60);
        if (nodo.getCaracter() != ' ') {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(nodo.getClave(), x - fm.stringWidth(nodo.getClave()) / 2, y + 6);
        }
    }

    @Override
    public NodoArbol obtenerNodoEn(int x, int y) {
        return buscarNodo(raiz, x, y);
    }

    private NodoArbol buscarNodo(NodoArbol nodo, int x, int y) {
        if (nodo == null) return null;
        if (Math.abs(nodo.getX() - x) < 15 && Math.abs(nodo.getY() - y) < 15) {
            return nodo;
        }
        for (NodoArbol hijo : nodo.getHijos().values()) {
            var res = buscarNodo(hijo, x, y);
            if (res != null) return res;
        }
        return null;
    }

    @Override
    public void borrarTodo() {
        raiz = null;
    }

    /** Ajusta el ancho del lienzo */
    public void setWidth(int width) {
        this.anchura = width;
    }
}
