package model.trees;

import javax.swing.JTextArea;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Implementación de un Trie de Residuos Múltiples.
 * Agrupa bits de tamaño tamGrupo, resolviendo colisiones según avanza el nivel.
 */
public class ArbolResiduoMultiple extends ArbolBase {
    private NodoArbol raiz = new NodoArbol(' ', "Raíz");
    private int tamGrupo;
    private int anchura = 1000; // para dibujo

    public ArbolResiduoMultiple(int tamGrupo) {
        this.tamGrupo = tamGrupo;
    }

    @Override
    public boolean existeClave(String clave) {
        for (char c : clave.toCharArray()) {
            String binario = String.format("%5s",
                    Integer.toBinaryString(c % 32)).replace(' ', '0');
            NodoArbol actual = raiz;
            int nivel = 0;
            while (nivel < 5) {
                int fin = Math.min(nivel + tamGrupo, 5);
                String grupo = binario.substring(nivel, fin);
                if (!actual.getHijos().containsKey(grupo)) return false;
                actual = actual.getHijos().get(grupo);
                nivel += tamGrupo;
            }
            if (actual.getCaracter() != c) return false;
        }
        return true;
    }

    @Override
    public void insertar(String palabra, JTextArea area) {
        area.append("\n=== RESIDUO MÚLTIPLE (Grupos de " 
                    + tamGrupo + ") ===\n");
        for (char c : palabra.toCharArray()) {
            String binario = String.format("%5s",
                    Integer.toBinaryString(c % 32)).replace(' ', '0');
            area.append("Insertando '" + c + "': " + binario + "\n");

            NodoArbol actual = raiz;
            int nivel = 0;
            while (nivel < 5) {
                int fin = Math.min(nivel + tamGrupo, 5);
                String grupo = binario.substring(nivel, fin);

                if (!actual.getHijos().containsKey(grupo)) {
                    NodoArbol nuevo = new NodoArbol(' ', grupo);
                    actual.agregarHijo(grupo, nuevo);
                    area.append("Creando grupo: " + grupo + "\n");
                }
                actual = actual.getHijos().get(grupo);
                nivel += tamGrupo;
            }
            actual.setCaracter(c);
            area.append("Carácter '" + c + "' almacenado\n");
        }
    }

    @Override
    public void eliminar(String clave, JTextArea area) {
        for (char c : clave.toCharArray()) {
            String binario = String.format("%5s",
                    Integer.toBinaryString(c % 32)).replace(' ', '0');
            Stack<NodoArbol> pila = new Stack<>();
            NodoArbol actual = raiz;
            pila.push(actual);
            int nivel = 0;
            boolean existe = true;

            while (nivel < 5) {
                int fin = Math.min(nivel + tamGrupo, 5);
                String grupo = binario.substring(nivel, fin);
                if (!actual.getHijos().containsKey(grupo)) {
                    existe = false;
                    break;
                }
                actual = actual.getHijos().get(grupo);
                pila.push(actual);
                nivel += tamGrupo;
            }

            if (existe && actual.getCaracter() == c) {
                actual.setCaracter(' ');
                area.append("Carácter '" + c + "' eliminado\n");
                // eliminar ramas redundantes
                while (pila.size() > 1) {
                    NodoArbol hijo = pila.pop();
                    NodoArbol padre = pila.peek();
                    if (hijo.getHijos().isEmpty() 
                        && hijo.getCaracter() == ' ') {
                        padre.getHijos().remove(hijo.getClave());
                    } else break;
                }
            } else {
                area.append("Error: Carácter '" + c + "' no existe\n");
            }
        }
    }

    @Override
    public void dibujar(Graphics g) {
        dibujarNodo((Graphics2D) g, raiz, anchura / 2, 50, anchura / 4);
    }

    private void dibujarNodo(Graphics2D g, NodoArbol nodo,
                             int x, int y, int offset) {
        if (nodo == null) return;
        nodo.setPosicion(x, y);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(3));

        List<Map.Entry<String, NodoArbol>> hijos =
            new ArrayList<>(nodo.getHijos().entrySet());
        int n = hijos.size();
        for (int i = 0; i < n; i++) {
            String key = hijos.get(i).getKey();
            NodoArbol child = hijos.get(i).getValue();
            int childX = x - offset + (2 * offset * i) 
                         / (Math.max(n - 1, 1));
            int childY = y + 150;
            g.drawLine(x, y + 30, childX, childY - 30);
            g.drawString(key, (x + childX) / 2 - 10,
                         (y + childY) / 2 - 10);
            dibujarNodo(g, child, childX, childY, offset / 2);
        }

        // nodo
        g.setColor(new Color(173, 216, 230));
        g.fillOval(x - 30, y - 30, 60, 60);
        g.setColor(Color.BLACK);
        g.drawOval(x - 30, y - 30, 60, 60);
        if (nodo.getCaracter() != ' ') {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            String texto = nodo.getClave() + 
                           (nodo.getCaracter() != ' ' 
                            ? "\n" + nodo.getCaracter() : "");
            FontMetrics fm = g.getFontMetrics();
            String[] lines = texto.split("\n");
            int dy = -fm.getHeight() * (lines.length - 1) / 2;
            for (String line : lines) {
                g.drawString(line,
                    x - fm.stringWidth(line) / 2,
                    y + dy + 6);
                dy += fm.getHeight();
            }
        }
    }

    @Override
    public NodoArbol obtenerNodoEn(int x, int y) {
        return buscarNodo(raiz, x, y);
    }

    private NodoArbol buscarNodo(NodoArbol nodo, int x, int y) {
        if (nodo == null) return null;
        if (Math.abs(nodo.getX() - x) < 15 
            && Math.abs(nodo.getY() - y) < 15) {
            return nodo;
        }
        for (NodoArbol h : nodo.getHijos().values()) {
            NodoArbol found = buscarNodo(h, x, y);
            if (found != null) return found;
        }
        return null;
    }

    @Override
    public void borrarTodo() {
        raiz = new NodoArbol(' ', "Raíz");
    }

    /** Ajusta el ancho del área de dibujo. */
    public void setWidth(int width) {
        this.anchura = width;
    }
}
