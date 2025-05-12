package model;

import javax.swing.JTextArea;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Implementación de un Árbol de Residuo Simple.
 * Almacena caracteres usando su representación binaria y maneja colisiones.
 */
public class ArbolResiduoSimple extends ArbolBase {
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
        area.append("\n=== TRIE RESIDUO SIMPLE ===\n");
        for (char c : palabra.toCharArray()) {
            String binario = String.format("%5s", Integer.toBinaryString(c % 32)).replace(' ', '0');
            area.append("Insertando '" + c + "': " + binario + "\n");

            NodoArbol actual = raiz;
            NodoArbol padre = null;  // Inicializamos como null para evitar confusiones
            int nivel = 0;

            for (int i = 0; i < BITS; i++) {
                String bit = String.valueOf(binario.charAt(i));
                padre = actual;  // Guardamos el nodo padre antes de avanzar

                if (!actual.getHijos().containsKey(bit)) {
                    NodoArbol nuevo = new NodoArbol(' ', bit);
                    actual.agregarHijo(bit, nuevo);
                    area.append("Nivel " + i + ": Creando nodo para bit " + bit + "\n");
                    actual = nuevo;
                } else {
                    actual = actual.getHijos().get(bit);

                    // Verificar colisión en el último nivel
                    if (i == BITS - 1 && actual.getCaracter() != ' ' && actual.getCaracter() != c) {
                        area.append("¡Colisión! en el último nivel con '" + actual.getCaracter() + "'\n");
                        // En el último nivel, simplemente reemplazamos el carácter
                        area.append("Reemplazando '" + actual.getCaracter() + "' con '" + c + "'\n");
                    }
                }

                nivel = i;
            }

            // Ahora 'actual' es el nodo final donde almacenaremos el carácter
            actual.setCaracter(c);
            area.append("Carácter '" + c + "' almacenado en nivel " + nivel + "\n");
        }
    }

    /**
     * Dibuja el árbol en el contexto gráfico proporcionado.
     *
     * @param g Contexto gráfico para dibujar
     */
    @Override
    public void dibujar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Ajustar propiedades de dibujo
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dibujar el árbol con un offset inicial grande para permitir espacio horizontal
        dibujarArbol(g2d, getWidth());
    }

    /**
     * Dibuja el árbol con parámetros ajustados.
     *
     * @param g2d Contexto gráfico
     * @param width Ancho disponible
     */
    private void dibujarArbol(Graphics2D g2d, int width) {
        // Si el árbol está vacío (solo tiene raíz), dibujamos solo el nodo raíz
        if (raiz.getHijos().isEmpty()) {
            int centerX = width / 2;
            int centerY = 100;

            g2d.setColor(new Color(173, 216, 230)); // Color celeste claro
            g2d.fillOval(centerX - 30, centerY - 30, 60, 60);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(centerX - 30, centerY - 30, 60, 60);

            return;
        }

        // Calcular qué tan profundo es el árbol para ajustar el offset
        int profundidad = calcularProfundidad(raiz);
        int offsetInicial = Math.min(width / 3, 200);  // El offset no debe ser demasiado grande

        // Comenzar a dibujar desde la raíz
        dibujarNodo(g2d, raiz, width / 2, 100, offsetInicial);
    }

    /**
     * Calcula la profundidad máxima del árbol.
     *
     * @param nodo Nodo desde el que calcular la profundidad
     * @return Número de niveles desde el nodo hasta la hoja más profunda
     */
    private int calcularProfundidad(NodoArbol nodo) {
        if (nodo == null || nodo.getHijos().isEmpty()) {
            return 0;
        }

        int maxProfundidad = 0;
        for (NodoArbol hijo : nodo.getHijos().values()) {
            int profundidadHijo = calcularProfundidad(hijo);
            if (profundidadHijo > maxProfundidad) {
                maxProfundidad = profundidadHijo;
            }
        }

        return maxProfundidad + 1;
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

        // Dibujar nodo
        g.setColor(new Color(173, 216, 230)); // Color azul claro
        g.fillOval(x - 30, y - 30, 60, 60);
        g.setColor(Color.BLACK);
        g.drawOval(x - 30, y - 30, 60, 60);

        // Dibujar carácter en el nodo
        if (nodo.getCaracter() != ' ') {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            String text = String.valueOf(nodo.getCaracter());
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, x - fm.stringWidth(text)/2, y + 6);
        }

        // No seguir dibujando si no hay hijos
        if (nodo.getHijos().isEmpty()) {
            return;
        }

        // Calcular offset para hijos basado en cuántos hijos hay
        int nuevoOffset = Math.max(offset / 2, 80); // Garantizar un offset mínimo

        // Espaciado vertical constante
        int espacioVertical = 100;

        // Dibujar conexiones y nodos hijos
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2.0f));

        // Dibujar hijo izquierdo (bit 0) si existe
        if (nodo.getHijos().containsKey("0")) {
            NodoArbol hijoIzquierdo = nodo.getHijos().get("0");
            int leftX = x - nuevoOffset;
            int leftY = y + espacioVertical;

            // Dibujar línea
            g.drawLine(x, y + 30, leftX, leftY - 30);

            // Dibujar etiqueta del bit
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("0", (x + leftX)/2 - 10, (y + leftY)/2 - 5);

            // Dibujar recursivamente el subárbol izquierdo
            dibujarNodo(g, hijoIzquierdo, leftX, leftY, nuevoOffset);
        }

        // Dibujar hijo derecho (bit 1) si existe
        if (nodo.getHijos().containsKey("1")) {
            NodoArbol hijoDerecho = nodo.getHijos().get("1");
            int rightX = x + nuevoOffset;
            int rightY = y + espacioVertical;

            // Dibujar línea
            g.drawLine(x, y + 30, rightX, rightY - 30);

            // Dibujar etiqueta del bit
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.drawString("1", (x + rightX)/2 + 5, (y + rightY)/2 - 5);

            // Dibujar recursivamente el subárbol derecho
            dibujarNodo(g, hijoDerecho, rightX, rightY, nuevoOffset);
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
        if (nodo == null) return null;

        if (Math.abs(nodo.getX() - x) <= 30 && Math.abs(nodo.getY() - y) <= 30) {
            return nodo;
        }

        for (NodoArbol hijo : nodo.getHijos().values()) {
            NodoArbol encontrado = buscarNodo(hijo, x, y);
            if (encontrado != null) return encontrado;
        }

        return null;
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

    /**
     * Elimina una clave del árbol.
     *
     * @param clave La clave a eliminar
     * @param area Área de texto para mostrar información del proceso
     */
    @Override
    public void eliminar(String clave, JTextArea area) {
        boolean claveExiste = existeClave(clave); // Verificar existencia primero
        if (!claveExiste) {
            area.append("\nLa clave '" + clave + "' no existe en el árbol.\n");
            return;
        }

        area.append("\n=== ELIMINANDO '" + clave + "' ===\n");

        for (char c : clave.toCharArray()) {
            String binario = String.format("%5s", Integer.toBinaryString(c % 32)).replace(' ', '0');
            NodoArbol actual = raiz;
            Stack<NodoArbol> pila = new Stack<>();
            Stack<String> bits = new Stack<>();

            // Recorrer todos los niveles (BITS)
            for (int nivel = 0; nivel < BITS; nivel++) {
                String bit = String.valueOf(binario.charAt(nivel));
                if (!actual.getHijos().containsKey(bit)) break;
                pila.push(actual);
                bits.push(bit);
                actual = actual.getHijos().get(bit);
            }

            // Eliminar carácter si existe
            if (actual.getCaracter() == c) {
                actual.setCaracter(' ');
                area.append("Carácter '" + c + "' eliminado.\n");
            }

            // Eliminar nodos vacíos desde el último nivel hacia la raíz
            while (!pila.isEmpty()) {
                NodoArbol padre = pila.pop();
                String bit = bits.pop();
                NodoArbol hijo = padre.getHijos().get(bit);

                if (hijo.getHijos().isEmpty() && hijo.getCaracter() == ' ') {
                    padre.getHijos().remove(bit);
                    area.append("Nodo eliminado en nivel " + (BITS - pila.size() - 1) + " (bit: " + bit + ")\n");
                } else {
                    break; // Detener si el nodo tiene hijos o carácter
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
}