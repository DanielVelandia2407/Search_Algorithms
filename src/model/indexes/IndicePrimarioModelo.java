package model.indexes;

/**
 * Modelo para el cálculo de índices primarios
 * Contiene la lógica de negocio para los cálculos matemáticos de índices primarios
 */
public class IndicePrimarioModelo {
    private int registros;
    private int tamBloque;
    private int longDato;
    private int longIndice;

    /**
     * Constructor del modelo de índice primario
     * @param registros Número total de registros
     * @param tamBloque Tamaño del bloque en bytes
     * @param longDato Longitud del registro de datos en bytes
     * @param longIndice Longitud del registro de índice en bytes
     */
    public IndicePrimarioModelo(int registros, int tamBloque, int longDato, int longIndice) {
        this.registros = registros;
        this.tamBloque = tamBloque;
        this.longDato = longDato;
        this.longIndice = longIndice;
    }

    /**
     * Calcula el factor de bloqueo de datos (bfr)
     * Número de registros de datos que caben en un bloque
     * @return Factor de bloqueo de datos
     */
    public int calcularBfr() {
        return tamBloque / longDato;
    }

    /**
     * Calcula el factor de bloqueo del índice (bfri)
     * Número de entradas de índice que caben en un bloque
     * @return Factor de bloqueo del índice
     */
    public int calcularBfri() {
        return tamBloque / longIndice;
    }

    /**
     * Calcula el número de bloques de datos necesarios
     * @return Número total de bloques de datos
     */
    public int calcularBloquesDatos() {
        int bfr = calcularBfr();
        return (int) Math.ceil((double) registros / bfr);
    }

    /**
     * Calcula el número de bloques de índice necesarios
     * En un índice primario, necesitamos una entrada por cada bloque de datos
     * @return Número total de bloques de índice
     */
    public int calcularBloquesIndice() {
        int bloquesDatos = calcularBloquesDatos();
        int bfri = calcularBfri();
        return (int) Math.ceil((double) bloquesDatos / bfri);
    }

    /**
     * Calcula el total de entradas de índice
     * En un índice primario, hay una entrada por cada bloque de datos
     * @return Total de entradas de índice
     */
    public int calcularTotalEntradas() {
        return calcularBloquesDatos();
    }

    /**
     * Calcula el número de accesos a disco necesarios para una búsqueda
     * 1 acceso para buscar en el índice + 1 acceso para leer el bloque de datos
     * Si el índice tiene más de un bloque, se usa búsqueda binaria
     * @return Número de accesos a disco
     */
    public int calcularAccesos() {
        int bloquesIndice = calcularBloquesIndice();

        if (bloquesIndice == 1) {
            // 1 acceso al índice + 1 acceso a los datos
            return 2;
        } else {
            // Búsqueda binaria en el índice + 1 acceso a los datos
            int accesosBusquedaIndice = (int) (Math.log(bloquesIndice) / Math.log(2)) + 1;
            return accesosBusquedaIndice + 1;
        }
    }

    /**
     * Calcula la mejora de rendimiento vs búsqueda secuencial
     * @return Factor de mejora
     */
    public double calcularMejoraRendimiento() {
        int accesosSecuencial = calcularBloquesDatos(); // Todos los bloques en el peor caso
        int accesosIndice = calcularAccesos();
        return (double) accesosSecuencial / accesosIndice;
    }

    /**
     * Calcula el espacio total ocupado por el archivo de datos
     * @return Espacio en bytes
     */
    public long calcularEspacioDatos() {
        return (long) calcularBloquesDatos() * tamBloque;
    }

    /**
     * Calcula el espacio total ocupado por el archivo de índice
     * @return Espacio en bytes
     */
    public long calcularEspacioIndice() {
        return (long) calcularBloquesIndice() * tamBloque;
    }

    /**
     * Calcula el espacio total del sistema (datos + índice)
     * @return Espacio total en bytes
     */
    public long calcularEspacioTotal() {
        return calcularEspacioDatos() + calcularEspacioIndice();
    }

    /**
     * Calcula el overhead del índice como porcentaje
     * @return Porcentaje de overhead
     */
    public double calcularOverheadIndice() {
        long espacioDatos = calcularEspacioDatos();
        long espacioIndice = calcularEspacioIndice();
        return (double) espacioIndice / espacioDatos * 100;
    }

    /**
     * Calcula la eficiencia de almacenamiento de datos
     * @return Porcentaje de eficiencia
     */
    public double calcularEficienciaAlmacenamiento() {
        long espacioUsadoDatos = (long) registros * longDato;
        long espacioAsignadoDatos = calcularEspacioDatos();
        return (double) espacioUsadoDatos / espacioAsignadoDatos * 100;
    }

    /**
     * Verifica si la configuración es válida
     * @return true si es válida, false en caso contrario
     */
    public boolean esConfiguracionValida() {
        return registros > 0 && tamBloque > 0 && longDato > 0 && longIndice > 0 &&
                longDato < tamBloque && longIndice < tamBloque;
    }

    /**
     * Obtiene un resumen de la configuración
     * @return String con el resumen
     */
    public String obtenerResumenConfiguracion() {
        return String.format(
                "Registros: %,d | Bloque: %,d bytes | Dato: %d bytes | Índice: %d bytes",
                registros, tamBloque, longDato, longIndice
        );
    }

    // Getters para acceder a los parámetros
    public int getRegistros() { return registros; }
    public int getTamBloque() { return tamBloque; }
    public int getLongDato() { return longDato; }
    public int getLongIndice() { return longIndice; }

    // Setters para modificar parámetros (si es necesario)
    public void setRegistros(int registros) { this.registros = registros; }
    public void setTamBloque(int tamBloque) { this.tamBloque = tamBloque; }
    public void setLongDato(int longDato) { this.longDato = longDato; }
    public void setLongIndice(int longIndice) { this.longIndice = longIndice; }
}
