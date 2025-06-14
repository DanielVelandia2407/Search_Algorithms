package model.trees;

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
    //private final int BITS = 5;
    private int anchura = 1000;

    @Override
public void insertar(String palabra, JTextArea area) {
    String binarioTotal = obtenerBinario(palabra);
    area.append("Representación binaria (5 bits por carácter): " + binarioTotal + "\n");

    area.append("\n=== ÁRBOL DIGITAL ===\n");
    area.append("Insertando: " + palabra + "\n");

    if (raiz == null) {
        raiz = new NodoArbol('\0', palabra);
        area.append("Primera clave insertada en la raíz: " + palabra + "\n");
        return;
    }

    NodoArbol actual = raiz;
    //String binarioTotal = obtenerBinario(palabra);
    int nivel = 0;

    for (int i = 0; i < binarioTotal.length(); i++) {
        String bit = String.valueOf(binarioTotal.charAt(i));
        nivel++;
        area.append("Nivel " + nivel + " - Bit " + bit + ": ");

        // Si no existe el hijo, se crea
        if (!actual.getHijos().containsKey(bit)) {
            NodoArbol nuevo = new NodoArbol('\0', null);
            actual.agregarHijo(bit, nuevo);
            area.append("Nuevo nodo creado\n");
        } else {
            area.append("Nodo existente\n");
        }

        actual = actual.getHijos().get(bit);

        // Si el nodo actual no tiene clave, se puede insertar aquí
        if (actual.getClave() == null) {
            actual.setClave(palabra);
            area.append("Clave insertada en nivel " + nivel + "\n");
            return;
        } else if (actual.getClave().equals(palabra)) {
            area.append("La clave ya existe exactamente en el nodo\n");
            return;
        }
        // Si hay colisión, continuar buscando profundidad (ya lo hace naturalmente en el bucle)
    }

    // Si llegó al final y aún no se insertó (colisión profunda), extender
    area.append("Colisión profunda detectada, extendiendo camino\n");
    NodoArbol temp = actual;
    for (int i = 0; i < 3; i++) { // solo 3 bits de extensión extra como ejemplo
        NodoArbol nuevo = new NodoArbol('\0', null);
        temp.agregarHijo("0", nuevo);
        temp = nuevo;
    }
    temp.setClave(palabra);
    area.append("Clave insertada al final del camino extendido\n");
}




    private String obtenerBinario(String palabra) {
        StringBuilder sb = new StringBuilder();
        for (char c : palabra.toCharArray()) {
            String bin = Integer.toBinaryString(c % 32);
            bin = String.format("%5s", bin).replace(' ', '0');
            sb.append(bin);
        }
        return sb.toString();
    }

    /**
 * Busca la clave en el árbol digital y devuelve el nivel donde se encontró,
 * o -1 si no existe. También escribe en el JTextArea un log detallado.
 *
 * @param palabra La clave a buscar
 * @param area    El JTextArea donde se appendan los mensajes (puede ser null)
 * @return Nivel donde se encontró (0 = raíz), o -1 si no existe
 */
    public int buscarYNivel(String palabra, JTextArea area) {
        if (raiz == null) {
            if (area != null) area.append("El árbol está vacío\n");
            return -1;
        }

        String binario = obtenerBinario(palabra);
        int nivel = 0;
        NodoArbol actual = raiz;

        // 1) Compruebo la raíz
        if (palabra.equals(actual.getClave())) {
            if (area != null) area.append("Clave \"" + palabra + "\" encontrada en la raíz (nivel 0)\n");
            return 0;
        }

        // 2) Recorro bit a bit
        for (int i = 0; i < binario.length(); i++) {
            String bit = String.valueOf(binario.charAt(i));
            nivel++;

            if (!actual.getHijos().containsKey(bit)) {
                if (area != null) area.append(
                    "Clave \"" + palabra + "\" no encontrada (falta nodo en nivel " + nivel + ")\n"
                );
                return -1;
            }

            actual = actual.getHijos().get(bit);

            if (palabra.equals(actual.getClave())) {
                if (area != null) area.append(
                    "Clave \"" + palabra + "\" encontrada en nivel " + nivel + "\n"
                );
                return nivel;
            }
        }

        // 3) Si recorro todos los bits y no la hallo:
        if (area != null) area.append("Clave \"" + palabra + "\" no encontrada tras recorrer todos los bits\n");
            return -1;
        }




        @Override
        public boolean existeClave(String palabra) {
            return buscarYNivel(palabra, null) != -1;
    }


    /**
 * Elimina la clave del árbol digital, poda nodos sin hijos ni clave,
 * y escribe un log en el JTextArea.
 *
 * @param palabra La clave a eliminar
 * @param area    El JTextArea donde se appendan los mensajes
 */
    @Override
    public void eliminar(String palabra, JTextArea area) {
        if (raiz == null) {
            area.append("El árbol está vacío\n");
            return;
        }

        String binario = obtenerBinario(palabra);
        Stack<NodoArbol> stack = new Stack<>();
        Stack<String> bitsStack = new Stack<>();

        // 1) Empiezo en la raíz
        NodoArbol actual = raiz;
        stack.push(actual);

        // Caso especial: la clave está en la raíz
        if (palabra.equals(actual.getClave())) {
            actual.setClave(null);
            area.append("Clave \"" + palabra + "\" eliminada de la raíz\n");
            // Si la raíz ya no tiene clave y solo un hijo, promovemos ese hijo a raíz
            if (raiz.getClave() == null && raiz.getHijos().size() == 1) {
                NodoArbol únicoHijo = raiz.getHijos().values().iterator().next();
                raiz = únicoHijo;
                area.append("Compactando: promovido único hijo a la raíz\n");
            }
            return;
        }

        // 2) Recorro bit a bit hasta encontrar la clave
        int nivel = 0;
        boolean encontrado = false;
        for (int i = 0; i < binario.length(); i++) {
            String bit = String.valueOf(binario.charAt(i));
            nivel++;

            if (!actual.getHijos().containsKey(bit)) {
                area.append("Clave \"" + palabra + "\" no encontrada\n");
                return;
            }

            actual = actual.getHijos().get(bit);
            stack.push(actual);
            bitsStack.push(bit);

            if (palabra.equals(actual.getClave())) {
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            area.append("Clave \"" + palabra + "\" no encontrada\n");
            return;
        }

        // 3) Elimino la clave del nodo
        actual.setClave(null);
        area.append("Clave \"" + palabra + "\" eliminada en nivel " + nivel + "\n");

        // 4) Compactación: si el nodo ahora no tiene clave pero sí un único hijo,
        //    lo promovemos al nivel del padre
        if (actual.getClave() == null && actual.getHijos().size() == 1) {
            String bitDesdePadre = bitsStack.peek();
            NodoArbol padre = stack.get(stack.size() - 2);
            NodoArbol únicoHijo = actual.getHijos().values().iterator().next();

            // Reemplazamos en el padre: 
            //   padre.hijos.remove(bitDesdePadre);
            //   padre.hijos.put(bitDesdePadre, únicoHijo);
            padre.getHijos().put(bitDesdePadre, únicoHijo);
            area.append("Compactando: promovido único hijo al nivel del padre\n");
        }

        // 5) Poda de nodos huérfanos hacia arriba
        while (!stack.isEmpty() && !bitsStack.isEmpty()) {
            NodoArbol nodo = stack.pop();
            String bitPadre = bitsStack.pop();
            NodoArbol padre = stack.peek();

            if (nodo.getClave() == null && nodo.getHijos().isEmpty()) {
                padre.getHijos().remove(bitPadre);
                area.append("Podando nodo huérfano en bit " + bitPadre + "\n");
            } else {
                break;
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
        
        nodo.getHijos().forEach((bit, hijo) -> {
            int childX = "0".equals(bit) ? x - offset : x + offset;
            g.drawLine(x, y + 30, childX, y + 100 - 30);
            g.drawString(bit, 
                (x + childX) / 2 + ("0".equals(bit) ? -10 : 5), 
                (y + (y + 100)) / 2 - 10
            );
            dibujarNodo(g, hijo, childX, y + 100, offset / 2);
        });
        
        g.setColor(new Color(173, 216, 230));
        g.fillOval(x - 30, y - 30, 60, 60);
        g.setColor(Color.BLACK);
        g.drawOval(x - 30, y - 30, 60, 60);
        
        if (nodo.getClave() != null) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            String label = nodo.getClave();
            g.drawString(label, x - g.getFontMetrics().stringWidth(label) / 2, y + 6);
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
