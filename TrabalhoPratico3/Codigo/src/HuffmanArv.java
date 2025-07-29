package src;

/**
 * @author Daniel Valadares, Gustavo Silvestre e Larissa Valadares
 */

abstract class HuffmanArv implements Comparable<HuffmanArv> {
    public final int frequency; // Frequência da árvore
    //
    public HuffmanArv(int freq) { 
    	frequency = freq; 
    }
    
    // Compara as frequências - Implementação da Interface Comparable para a ordenação na fila
    public int compareTo(HuffmanArv arvore) {
        return frequency - arvore.frequency;
    }
}
