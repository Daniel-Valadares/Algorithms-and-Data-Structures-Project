package src;

/**
 * @author Daniel Valadares, Gustavo Silvestre e Larissa Valadares
 */

class HuffmanFolha extends HuffmanArv {
    public final char value; // A letra é atribuida a um nó folha 
 
    public HuffmanFolha(int freq, char val) {
        super(freq);
        value = val;
    }
}