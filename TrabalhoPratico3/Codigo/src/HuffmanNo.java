package src;

/**
 * @author Daniel Valadares, Gustavo Silvestre e Larissa Valadares
 */

class HuffmanNo extends HuffmanArv {
    public final HuffmanArv left, right; // sub-árvores
 
    public HuffmanNo(HuffmanArv l, HuffmanArv r) {
        super(l.frequency + r.frequency);
        left = l;
        right = r;
    }
}