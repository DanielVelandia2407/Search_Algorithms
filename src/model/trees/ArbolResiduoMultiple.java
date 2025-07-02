package model.trees;

import javax.swing.JTextArea;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
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

    private void dibujarNodo(Graphics2D g, NodoArbol nodo, int x, int y, int offset) {
        if (nodo == null) return;
        nodo.setPosicion(x, y);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1)); // Grosor uniforme

        // Ordenar hijos por clave
        List<Map.Entry<String, NodoArbol>> hijos = new ArrayList<>(nodo.getHijos().entrySet());
        Collections.sort(hijos, (e1, e2) -> e1.getKey().compareTo(e2.getKey()));

        for (Map.Entry<String, NodoArbol> entry : hijos) {
            String key = entry.getKey();
            NodoArbol child = entry.getValue();
            
            int childX = x;
            int childY = y + 150;
            
            // Determinar posición basada en el valor binario
            if (key.length() == 1) {
                // Posicionamiento para 1 bit
                if (key.equals("0")) childX = x - offset;
                else if (key.equals("1")) childX = x + offset;
            } else if (key.length() == 2) {
                // Posicionamiento para 2 bits
                if (key.equals("00")) childX = x - offset;
                else if (key.equals("01")) childX = x - offset / 2;
                else if (key.equals("10")) childX = x + offset / 2;
                else if (key.equals("11")) childX = x + offset;
            }

            g.drawLine(x, y + 30, childX, childY - 30);
            
            // Etiqueta sin negrita
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            int labelX = (x + childX) / 2 - (g.getFontMetrics().stringWidth(key)) / 2;
            g.drawString(key, labelX, (y + childY) / 2 - 10);

            dibujarNodo(g, child, childX, childY, offset / 2);
        }

        // Dibujar nodo actual
        g.setColor(new Color(173, 216, 230));
        g.fillOval(x - 30, y - 30, 60, 60);
        g.setColor(Color.BLACK);
        g.drawOval(x - 30, y - 30, 60, 60);

        // Contenido del nodo
        String texto = (nodo.getCaracter() != ' ') 
            ? String.valueOf(nodo.getCaracter()) 
            : nodo.getClave();
        
        g.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        int textX = x - fm.stringWidth(texto) / 2;
        int textY = y + fm.getAscent() / 2 - 5;
        g.drawString(texto, textX, textY);
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