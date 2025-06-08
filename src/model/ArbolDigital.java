package model;

import javax.swing.JTextArea;
import java.awt.*;
import java.util.Stack;

/**
 * Implementación de un Árbol Digital (binario) por bits.
 * La primera palabra insertada se guarda en la raíz;
 * las siguientes se insertan bit a bit (0=izquierda, 1=derecha).
 */
public class ArbolDigital extends ArbolBase {
    private NodoArbol raiz = null;
    private final int BITS = 5;
    private int anchura = 1000;

    @Override
    public void insertar(String palabra, JTextArea area) {
        area.append("\n=== ÁRBOL DIGITAL ===\n");
        for (char c : palabra.toCharArray()) {
            // cálculo del binario de 5 bits
            String binario = String.format("%5s", Integer.toBinaryString(c % 32)).replace(' ', '0');
            // mostrar código binario
            area.append("Código binario para '" + c + "' = " + binario + "\n");
            // inserción según bits
            area.append("Insertando '" + c + "': " + binario + "\n");

            // si es la primera palabra, crea raíz con ese carácter y clave completa
            if (raiz == null) {
                raiz = new NodoArbol(c, palabra);
                raiz.setCaracter(c);
                area.append("Primera clave '" + palabra + "' insertada en la raíz.\n");
                continue;
            }

            NodoArbol actual = raiz;
            for (int i = 0; i < BITS; i++) {
                String bit = String.valueOf(binario.charAt(i));
                if (!actual.getHijos().containsKey(bit)) {
                    NodoArbol nuevo = new NodoArbol(c, palabra);
                    nuevo.setCaracter(c);
                    actual.agregarHijo(bit, nuevo);
                    area.append("Creando nodo para bit " + bit + " en nivel " + i + "\n");
                }
                actual = actual.getHijos().get(bit);
            }
            actual.setCaracter(c);
            area.append("Carácter '" + c + "' almacenado en nodo final\n");
        }
    }

    private String obtenerBinario(String palabra) {
        StringBuilder sb = new StringBuilder();
        for (char c : palabra.toCharArray()) {
            sb.append(String.format("%5s", Integer.toBinaryString(c % 32)).replace(' ', '0'));
        }
        return sb.toString();
    }

    @Override
    public boolean existeClave(String palabra) {
        if (raiz == null) return false;
        if (palabra.equals(raiz.getClave())) return true;

        String bin = obtenerBinario(palabra);
        NodoArbol actual = raiz;
        int i = 0;
        while (i < bin.length()) {
            String bit = String.valueOf(bin.charAt(i));
            actual = actual.getHijos().get(bit);
            if (actual == null) return false;
            if (palabra.equals(actual.getClave())) return true;
            i++;
        }
        return false;
    }

    @Override
    public void eliminar(String palabra, JTextArea area) {
        if (raiz == null || !existeClave(palabra)) {
            area.append("La clave \"" + palabra + "\" no existe.\n");
            return;
        }

        String bin = obtenerBinario(palabra);
        Stack<NodoArbol> pila = new Stack<>();
        NodoArbol actual = raiz;
        pila.push(actual);

        for (int i = 0; i < bin.length(); i++) {
            actual = actual.getHijos().get(String.valueOf(bin.charAt(i)));
            pila.push(actual);
        }

        if (palabra.equals(actual.getClave())) {
            actual.setCaracter(' ');
            actual.setClave(null);
            area.append("clave \"" + palabra + "\" eliminada.\n");

            // Limpia nodos sobrantes
            while (pila.size() > 1) {
                NodoArbol hijo = pila.pop();
                NodoArbol padre = pila.peek();
                if (hijo.getHijos().isEmpty() && hijo.getClave() == null) {
                    padre.getHijos().entrySet().removeIf(e -> e.getValue() == hijo);
                }
            }
            // Si raíz quedó vacía
            if (raiz.getClave() == null && raiz.getHijos().isEmpty()) {
                raiz = null;
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
        nodo.getHijos().forEach((clave, hijo) -> {
            int childX = "0".equals(clave) ? x - offset : x + offset;
            g.drawLine(x, y + 30, childX, y + 100 - 30);
            g.drawString(clave, (x + childX) / 2 + ("0".equals(clave) ? -10 : 5), (y + (y + 100)) / 2 - 10);
            dibujarNodo(g, hijo, childX, y + 100, offset / 2);
        });
        g.setColor(new Color(173, 216, 230));
        g.fillOval(x - 30, y - 30, 60, 60);
        g.setColor(Color.BLACK);
        g.drawOval(x - 30, y - 30, 60, 60);
        if (nodo.getClave() != null) {
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
            NodoArbol encontrado = buscarNodo(hijo, x, y);
            if (encontrado != null) return encontrado;
        }
        return null;
    }

    @Override
    public void borrarTodo() {
        raiz = null;
    }

    /**
     * Ajusta el ancho del área de dibujo para dibujar el árbol.
     */
    public void setWidth(int width) {
        this.anchura = width;
    }
}
