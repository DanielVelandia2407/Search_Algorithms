package model.trees;

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

            insertarCaracter(raiz, c, palabra, binario, 0, area);
        }
    }

    private void insertarCaracter(NodoArbol actual, char c, String palabra,
                                  String binario, int nivel, JTextArea area) {
        if (nivel >= BITS) {
            area.append("Se alcanzó el máximo de bits para el carácter '" + c + "'\n");
            return;
        }

        String bit = String.valueOf(binario.charAt(nivel));
        NodoArbol hijo = actual.getHijos().get(bit);

        if (hijo == null) {
            // No hay nodo, insertamos aquí
            NodoArbol nuevo = new NodoArbol(c, palabra);
            nuevo.setCaracter(c);
            actual.agregarHijo(bit, nuevo);
            area.append("Clave '" + palabra + "' insertada en bit " + bit +
                    " nivel " + nivel + "\n");
            return;
        }

        // Si el hijo es un separador (carácter vacío), continuamos bajando
        if (hijo.getCaracter() == ' ') {
            insertarCaracter(hijo, c, palabra, binario, nivel + 1, area);
            return;
        }

        // Si es el mismo carácter y la misma palabra, ya existe
        if (hijo.getCaracter() == c && palabra.equals(hijo.getClave())) {
            area.append("La clave '" + palabra + "' ya existe.\n");
            return;
        }

        // Si es el mismo carácter pero diferente palabra, continuamos bajando
        if (hijo.getCaracter() == c) {
            insertarCaracter(hijo, c, palabra, binario, nivel + 1, area);
            return;
        }

        // COLISIÓN: diferentes caracteres en la misma posición
        area.append("¡Colisión! Nivel " + nivel + " entre '" +
                hijo.getCaracter() + "' y '" + c + "', separando...\n");

        // Obtenemos el binario del carácter existente
        String binExistente = String.format("%5s",
                Integer.toBinaryString(hijo.getCaracter() % 32)).replace(' ', '0');

        // Removemos el hijo actual y creamos un separador
        actual.getHijos().remove(bit);
        NodoArbol separador = new NodoArbol(' ', "sep" + nivel);
        actual.agregarHijo(bit, separador);

        // Insertamos recursivamente el nodo existente en el separador
        insertarCaracter(separador, hijo.getCaracter(), hijo.getClave(),
                binExistente, nivel + 1, area);

        // Insertamos recursivamente el nuevo carácter en el separador
        insertarCaracter(separador, c, palabra, binario, nivel + 1, area);

        area.append("Separación completada en nivel " + (nivel + 1) + "\n");
    }

    @Override
    public boolean existeClave(String palabra) {
        return buscarClaveConInfo(palabra, null).encontrado;
    }

    /**
     * Busca una clave en el árbol y retorna información detallada de la búsqueda
     */
    public ResultadoBusquedaDetallada buscarClaveConInfo(String palabra, JTextArea area) {
        if (raiz == null) {
            if (area != null) {
                area.append("\nEl árbol está vacío.\n");
            }
            return new ResultadoBusquedaDetallada(false, -1);
        }

        if (area != null) {
            area.append("\n=== BÚSQUEDA EN TRIE RESIDUO SIMPLE ===\n");
            area.append("Buscando palabra: '" + palabra + "'\n");
        }

        for (char c : palabra.toCharArray()) {
            String binario = String.format("%5s", Integer.toBinaryString(c % 32))
                    .replace(' ', '0');

            if (area != null) {
                area.append("Buscando carácter '" + c + "': " + binario + "\n");
            }

            ResultadoBusqueda resultado = buscarCaracter(raiz, c, palabra, binario, 0);

            if (!resultado.encontrado) {
                if (area != null) {
                    area.append("Carácter '" + c + "' no encontrado.\n");
                }
                return new ResultadoBusquedaDetallada(false, -1);
            }

            if (area != null) {
                area.append("Carácter '" + c + "' encontrado en nivel " + resultado.nivel + "\n");
            }
        }

        // Calculamos el nivel final donde se encontró la palabra completa
        int nivelFinal = calcularNivelPalabra(palabra);

        if (area != null) {
            area.append("¡Palabra '" + palabra + "' encontrada completamente en nivel " + nivelFinal + "!\n");
        }

        return new ResultadoBusquedaDetallada(true, nivelFinal);
    }

    /**
     * Calcula en qué nivel se encuentra una palabra específica
     */
    private int calcularNivelPalabra(String palabra) {
        if (raiz == null) return -1;

        NodoArbol actual = raiz;
        int nivel = 0;

        for (char c : palabra.toCharArray()) {
            String binario = String.format("%5s", Integer.toBinaryString(c % 32))
                    .replace(' ', '0');

            for (int i = 0; i < BITS; i++) {
                String bit = String.valueOf(binario.charAt(i));
                NodoArbol hijo = actual.getHijos().get(bit);

                if (hijo == null) return -1;

                nivel++;
                actual = hijo;

                // Si encontramos el carácter y la palabra coincide
                if (hijo.getCaracter() == c && palabra.equals(hijo.getClave())) {
                    return nivel;
                }

                // Si es separador, continuamos
                if (hijo.getCaracter() == ' ') {
                    continue;
                }

                // Si es el mismo carácter pero diferente palabra, continuamos
                if (hijo.getCaracter() == c) {
                    break; // Salimos del bucle de bits para continuar con el siguiente carácter
                }

                return -1; // Carácter diferente, no encontrado
            }
        }

        return -1;
    }

    private static class ResultadoBusqueda {
        boolean encontrado;
        int nivel;

        ResultadoBusqueda(boolean encontrado, int nivel) {
            this.encontrado = encontrado;
            this.nivel = nivel;
        }
    }

    public static class ResultadoBusquedaDetallada {
        public final boolean encontrado;
        public final int nivel;

        public ResultadoBusquedaDetallada(boolean encontrado, int nivel) {
            this.encontrado = encontrado;
            this.nivel = nivel;
        }
    }

    private ResultadoBusqueda buscarCaracter(NodoArbol actual, char c, String palabra,
                                             String binario, int nivel) {
        if (nivel >= BITS || actual == null) {
            return new ResultadoBusqueda(false, -1);
        }

        String bit = String.valueOf(binario.charAt(nivel));
        NodoArbol hijo = actual.getHijos().get(bit);

        if (hijo == null) {
            return new ResultadoBusqueda(false, -1);
        }

        // Si es un separador, continuamos bajando
        if (hijo.getCaracter() == ' ') {
            return buscarCaracter(hijo, c, palabra, binario, nivel + 1);
        }

        // Si encontramos el carácter y la palabra coincide
        if (hijo.getCaracter() == c && palabra.equals(hijo.getClave())) {
            return new ResultadoBusqueda(true, nivel);
        }

        // Si es el mismo carácter pero diferente palabra, continuamos bajando
        if (hijo.getCaracter() == c) {
            return buscarCaracter(hijo, c, palabra, binario, nivel + 1);
        }

        return new ResultadoBusqueda(false, -1);
    }

    @Override
    public void eliminar(String palabra, JTextArea area) {
        if (raiz == null) {
            area.append("\nEl árbol está vacío\n");
            return;
        }

        area.append("\n=== ELIMINACIÓN TRIE RESIDUO SIMPLE ===\n");

        // Verificar si la palabra existe
        if (!existeClave(palabra)) {
            area.append("La clave '" + palabra + "' no existe.\n");
            return;
        }

        // Procesar cada carácter de la palabra
        for (char c : palabra.toCharArray()) {
            String binario = String.format("%5s", Integer.toBinaryString(c % 32))
                    .replace(' ', '0');

            area.append("Eliminando carácter '" + c + "': " + binario + "\n");

            Stack<NodoArbol> pila = new Stack<>();
            Stack<String> bitsPila = new Stack<>();

            NodoArbol actual = raiz;
            pila.push(actual);

            // Recorrer hasta encontrar el carácter
            boolean encontrado = false;
            int nivelActual = 0;

            for (int i = 0; i < BITS && actual != null; i++) {
                String bit = String.valueOf(binario.charAt(i));
                NodoArbol hijo = actual.getHijos().get(bit);

                if (hijo == null) break;

                nivelActual++;
                pila.push(hijo);
                bitsPila.push(bit);
                actual = hijo;

                // Si encontramos el carácter y la palabra coincide
                if (hijo.getCaracter() == c && palabra.equals(hijo.getClave())) {
                    encontrado = true;
                    break;
                }

                // Si es separador, continuamos
                if (hijo.getCaracter() == ' ') {
                    continue;
                }

                // Si es el mismo carácter pero diferente palabra, continuamos
                if (hijo.getCaracter() == c) {
                    break;
                }
            }

            if (encontrado && actual != null) {
                // Marcar el nodo como eliminado (quitar la clave)
                actual.setClave(null);
                actual.setCaracter(' '); // Convertir en separador
                area.append("Carácter '" + c + "' eliminado del nivel " + nivelActual + "\n");

                // Compactación: si el nodo ahora es separador y tiene un solo hijo,
                // promover el hijo al nivel del padre
                if (actual.getHijos().size() == 1 && !bitsPila.isEmpty()) {
                    String bitPadre = bitsPila.peek();
                    NodoArbol padre = pila.get(pila.size() - 2);
                    NodoArbol unicoHijo = actual.getHijos().values().iterator().next();

                    // Reemplazar el nodo actual con su único hijo
                    padre.getHijos().put(bitPadre, unicoHijo);
                    area.append("Compactando: promovido único hijo al nivel del padre\n");
                }

                // Poda de nodos huérfanos hacia arriba
                while (pila.size() > 1 && !bitsPila.isEmpty()) {
                    NodoArbol nodo = pila.pop();
                    String bitPadre = bitsPila.pop();
                    NodoArbol padre = pila.peek();

                    // Si el nodo no tiene clave, no tiene carácter útil y no tiene hijos
                    if ((nodo.getClave() == null || nodo.getClave().isEmpty()) &&
                            nodo.getCaracter() == ' ' &&
                            nodo.getHijos().isEmpty()) {

                        padre.getHijos().remove(bitPadre);
                        area.append("Podando nodo huérfano en bit " + bitPadre + "\n");
                    } else {
                        break; // Si el nodo tiene contenido útil, no podamos más
                    }
                }
            }
        }

        // Verificar si la raíz quedó sin hijos y sin clave
        if (raiz != null && raiz.getHijos().isEmpty() &&
                (raiz.getClave() == null || raiz.getClave().isEmpty())) {
            raiz = null;
            area.append("Árbol quedó vacío tras la eliminación\n");
        }

        area.append("Eliminación completada\n");
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

        // Color diferente para separadores
        if (nodo.getCaracter() == ' ') {
            g.setColor(new Color(255, 255, 224)); // Amarillo claro para separadores
        } else {
            g.setColor(new Color(173, 216, 230)); // Azul claro para nodos con datos
        }

        g.fillOval(x - 30, y - 30, 60, 60);
        g.setColor(Color.BLACK);
        g.drawOval(x - 30, y - 30, 60, 60);

        if (nodo.getCaracter() != ' ' && nodo.getClave() != null && !nodo.getClave().isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(nodo.getClave(), x - fm.stringWidth(nodo.getClave()) / 2, y + 6);
        } else if (nodo.getCaracter() == ' ') {
            // Mostrar "SEP" para separadores
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            FontMetrics fm = g.getFontMetrics();
            g.drawString("", x - fm.stringWidth("") / 2, y + 3);
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