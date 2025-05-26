package model;

/**
 * Modelo para el cálculo de índices secundarios
 * Contiene la lógica de negocio para los cálculos matemáticos
 */
public class IndiceSecundarioModelo {
    private int registros;
    private int tamBloque;
    private int longDato;
    private int longIndice;

    /**
     * Constructor del modelo de índice secundario
     * @param registros Número total de registros
     * @param tamBloque Tamaño del bloque en bytes
     * @param longDato Longitud del registro de datos en bytes
     * @param longIndice Longitud del registro de índice en bytes
     */
    public IndiceSecundarioModelo(int registros, int tamBloque, int longDato, int longIndice) {
        this.registros = registros;
        this.tamBloque = tamBloque;
        this.longDato = longDato;
        this.longIndice = longIndice;
    }

    /**
     * Calcula el factor de bloqueo del índice (bfri)
     * @return Número de registros de índice que caben en un bloque
     */
    public int calcularBfri() {
        return tamBloque / longIndice;
    }

    /**
     * Calcula el número de bloques necesarios para el índice
     * @return Número total de bloques de índice
     */
    public int calcularNumBloques() {
        int bfri = calcularBfri();
        return (int) Math.ceil((double) registros / bfri);
    }

    /**
     * Calcula el número de accesos a disco necesarios para una búsqueda
     * Utiliza búsqueda binaria logarítmica
     * @return Número de accesos a disco
     */
    public int calcularAccesos() {
        int numBloques = calcularNumBloques();
        return (int) (Math.log(numBloques) / Math.log(2)) + 1;
    }

    /**
     * Calcula el total de registros de índice que se pueden almacenar
     * @return Total de registros de índice
     */
    public int calcularTotalIndices() {
        return calcularNumBloques() * calcularBfri();
    }

    /**
     * Calcula el factor de bloqueo de datos (bfrd)
     * @return Número de registros de datos que caben en un bloque
     */
    public int calcularBfrd() {
        return tamBloque / longDato;
    }

    /**
     * Calcula el número de bloques de datos necesarios
     * @return Número total de bloques de datos
     */
    public int calcularBloquesDatos() {
        int bfrd = calcularBfrd();
        return (int) Math.ceil((double) registros / bfrd);
    }

    // Getters para acceder a los parámetros
    public int getRegistros() { return registros; }
    public int getTamBloque() { return tamBloque; }
    public int getLongDato() { return longDato; }
    public int getLongIndice() { return longIndice; }
}