package model.indexes;

public class IndiceSecundarioModelo {
    private int registros;
    private int tamBloque;
    private int longDato;
    private int longIndice;

    public IndiceSecundarioModelo(int registros, int tamBloque, int longDato, int longIndice) {
        this.registros = registros;
        this.tamBloque = tamBloque;
        this.longDato = longDato;
        this.longIndice = longIndice;
    }

    public int calcularBfri() {
        return tamBloque / longIndice;
    }

    public int calcularNumBloques() {
        int bfri = calcularBfri();
        return (int) Math.ceil((double) registros / bfri);
    }

    public int calcularAccesos() {
        int numBloques = calcularNumBloques();
        return (int) (Math.log(numBloques) / Math.log(2)) + 1;
    }

    public int calcularTotalIndices() {
        return calcularNumBloques() * calcularBfri();
    }
}