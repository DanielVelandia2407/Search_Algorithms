package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ResidueHashModel {

    // Capacidad inicial de la tabla
    private int capacity;

    // Factor de carga máximo antes de redimensionar
    private final double LOAD_FACTOR = 0.75;

    // Estructura principal: array de listas para manejar colisiones
    private LinkedList<HashEntry>[] table;

    // Contador de elementos
    private int size;

    // Clase interna para representar una entrada en la tabla hash
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


    @SuppressWarnings("unchecked")
    public ResidueHashModel(int initialCapacity) {
        this.capacity = initialCapacity;
        this.table = new LinkedList[capacity];
        this.size = 0;

        // Inicializar todas las listas
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
    }


    public ResidueHashModel() {
        this(11); // Un número primo es buena elección para tabla hash
    }

    private int hash(int key) {
        int hashCode = Math.abs(key); // Aseguramos valores positivos
        return hashCode % capacity;
    }


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
        return true;
    }


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


    public boolean remove(int key) {
        int index = hash(key);

        // Buscar y eliminar en la lista
        for (HashEntry entry : table[index]) {
            if (entry.getKey() == key) {
                table[index].remove(entry);
                size--;
                return true;
            }
        }

        // No se encontró para eliminar
        return false;
    }

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


    private int nextPrime(int n) {
        while (!isPrime(n)) {
            n++;
        }
        return n;
    }


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

    public List<HashEntry> getAllEntries() {
        List<HashEntry> entries = new ArrayList<>();

        for (int i = 0; i < capacity; i++) {
            entries.addAll(table[i]);
        }

        return entries;
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        table = new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new LinkedList<>();
        }
        size = 0;
    }

    public int size() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public LinkedList<HashEntry>[] getTable() {
        return table;
    }

    public boolean hasCollisionAt(int index) {
        if (index >= 0 && index < capacity) {
            return table[index].size() > 1;
        }
        return false;
    }

    public double getLoadFactor() {
        return (double) size / capacity;
    }
}