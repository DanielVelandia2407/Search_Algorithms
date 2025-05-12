package model;

import javax.swing.JTextArea;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Modelo adaptado para utilizar ArbolResiduoSimple en lugar de una tabla hash convencional.
 */
public class ResidueHashModel {

    // Capacidad inicial de la tabla
    private int capacity;

    // Factor de carga máximo antes de redimensionar
    private final double LOAD_FACTOR = 0.75;

    // Estructura principal: array de listas para manejar colisiones
    private LinkedList<HashEntry>[] table;

    // Árbol de residuo simple para visualización y operaciones
    private ArbolResiduoSimple arbol;

    // Contador de elementos
    private int size;

    /**
     * Clase interna para representar una entrada en la tabla hash
     */
    public static class HashEntry {
        private final int key;
        private Object value;
        private final int originalHashCode;

        public HashEntry(int key, Object value, int originalHashCode) {
            this.key = key;
            this.value = value;
            this.originalHashCode = originalHashCode;
        }

        public int getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public int getOriginalHashCode() {
            return originalHashCode;
        }
    }

    /**
     * Constructor con capacidad inicial especificada
     *
     * @param initialCapacity Capacidad inicial de la tabla hash
     */
    @SuppressWarnings("unchecked")
    public ResidueHashModel(int initialCapacity) {
        this.capacity = initialCapacity;
        this.table = new LinkedList[capacity];
        this.size = 0;
        this.arbol = new ArbolResiduoSimple();

        // Inicializar todas las listas
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
    }

    /**
     * Constructor por defecto con capacidad inicial de 11
     */
    public ResidueHashModel() {
        this(11); // Un número primo es buena elección para tabla hash
    }

    /**
     * Función hash: retorna el resto de dividir la clave por la capacidad
     *
     * @param key Clave a transformar
     * @return Índice en la tabla hash
     */
    private int hash(int key) {
        int hashCode = Math.abs(key); // Aseguramos valores positivos
        return hashCode % capacity;
    }

    /**
     * Inserta un nuevo par clave-valor en la tabla hash y en el árbol
     *
     * @param key Clave (debe ser un entero)
     * @param value Valor asociado a la clave
     * @return true si es una inserción nueva, false si es una actualización
     */
    public boolean put(int key, Object value) {
        // Redimensionar si es necesario
        if ((double) size / capacity >= LOAD_FACTOR) {
            resize();
        }

        // Obtener el índice hash
        int hashCode = key;  // Guardamos el hash original para visualización
        int index = hash(key);

        // Buscar si la clave ya existe
        for (HashEntry entry : table[index]) {
            if (entry.getKey() == key) {
                // Actualizar valor existente
                entry.setValue(value);
                return false; // No es inserción nueva
            }
        }

        // Si llegamos aquí, es una nueva clave
        HashEntry newEntry = new HashEntry(key, value, hashCode);
        table[index].add(newEntry);
        size++;

        // Insertar en el árbol de residuo simple
        JTextArea tempArea = new JTextArea(); // Área temporal para capturar logs

        // Convertimos el valor numérico a string para insertar en el árbol
        String valueStr = value.toString();

        // Para asegurar que se inserte correctamente, usamos cada carácter
        arbol.insertar(valueStr, tempArea);

        return true;
    }

    /**
     * Busca un valor por su clave
     *
     * @param key Clave a buscar
     * @return El valor asociado a la clave, o null si no existe
     */
    public Object get(int key) {
        int index = hash(key);

        // Buscar en la lista en ese índice
        for (HashEntry entry : table[index]) {
            if (entry.getKey() == key) {
                return entry.getValue();
            }
        }

        // No se encontró la clave
        return null;
    }

    /**
     * Elimina una entrada por su clave
     *
     * @param key Clave a eliminar
     * @return true si se encontró y eliminó la clave, false en caso contrario
     */
    public boolean remove(int key) {
        int index = hash(key);

        // Buscar y eliminar en la lista
        for (HashEntry entry : table[index]) {
            if (entry.getKey() == key) {
                // Eliminar del árbol también
                JTextArea tempArea = new JTextArea();
                String valueStr = entry.getValue().toString();
                arbol.eliminar(valueStr, tempArea);

                // Eliminar de la tabla hash
                table[index].remove(entry);
                size--;
                return true;
            }
        }

        // No se encontró para eliminar
        return false;
    }

    /**
     * Redimensiona la tabla hash cuando el factor de carga supera el límite
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        // Guardar la tabla antigua
        LinkedList<HashEntry>[] oldTable = table;
        int oldCapacity = capacity;

        // Crear nueva tabla con el doble de capacidad (siguiente primo aproximado)
        capacity = nextPrime(capacity * 2);
        table = new LinkedList[capacity];
        size = 0;

        // Inicializar nuevas listas
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }

        // Rehash de todos los elementos
        for (int i = 0; i < oldCapacity; i++) {
            for (HashEntry entry : oldTable[i]) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Encuentra el siguiente número primo a partir de n
     *
     * @param n Número base
     * @return El siguiente número primo >= n
     */
    private int nextPrime(int n) {
        while (!isPrime(n)) {
            n++;
        }
        return n;
    }

    /**
     * Verifica si un número es primo
     *
     * @param n Número a verificar
     * @return true si es primo, false en caso contrario
     */
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;

        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtiene todas las entradas de la tabla hash
     *
     * @return Lista con todas las entradas
     */
    public List<HashEntry> getAllEntries() {
        List<HashEntry> entries = new ArrayList<>();

        for (int i = 0; i < capacity; i++) {
            entries.addAll(table[i]);
        }

        return entries;
    }

    /**
     * Limpia la tabla hash y el árbol
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        table = new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
        size = 0;

        // Limpiar el árbol también
        arbol.borrarTodo();
    }

    /**
     * Obtiene el número de elementos en la tabla hash
     *
     * @return Número de elementos
     */
    public int size() {
        return size;
    }

    /**
     * Obtiene la capacidad actual de la tabla hash
     *
     * @return Capacidad de la tabla
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Obtiene la tabla hash completa
     *
     * @return Array de listas con las entradas hash
     */
    public LinkedList<HashEntry>[] getTable() {
        return table;
    }

    /**
     * Verifica si hay colisiones en un índice específico
     *
     * @param index Índice a verificar
     * @return true si hay colisiones (más de una entrada), false en caso contrario
     */
    public boolean hasCollisionAt(int index) {
        if (index >= 0 && index < capacity) {
            return table[index].size() > 1;
        }
        return false;
    }

    /**
     * Calcula el factor de carga actual de la tabla hash
     *
     * @return Factor de carga (elementos / capacidad)
     */
    public double getLoadFactor() {
        return (double) size / capacity;
    }

    /**
     * Obtiene el árbol de residuo simple
     *
     * @return Instancia del árbol
     */
    public ArbolResiduoSimple getArbol() {
        return arbol;
    }

    /**
     * Establece el ancho del área de dibujo para el árbol
     *
     * @param width Ancho en píxeles
     */
    public void setArbolWidth(int width) {
        arbol.setWidth(width);
    }
}