package src;

/**
 * @author Daniel Valadares, Gustavo Silvestre e Larissa Valadares
 */


public class KMP {
    private String p; // String para armazenar padrao
    private int pLength; // Tamanho do padrao
    private int[] lps; // Array para salvar prefixos

    KMP (String p) {
        // preencher dados
        this.p = p;
        this.pLength = p.length();
        this.lps = new int[p.length()];
        // construir lps
        buildLPS();
    }

    public boolean search(String s) {
        // Dados
        int sLength = s.length();
        int i = 0; // index para string
        int j = 0; // index para padrao
        // procurar
        while ((sLength - i) >= (pLength - j)) {
            // testar se character eh igual
            if (p.charAt(j) == s.charAt(i)) {
                i++;
                j++;
            }
            // testar se chegou ao fim do padrao
            if (pLength == j) {
                return true;
            } 
            // Testar se proximo teste quebra o padrao
            else if (i < sLength && p.charAt(j) != s.charAt(i)) {
                if (j != 0) {
                    j = lps[j -1];
                } else {
                    i++;
                }
            }  
        }
        return false;
    }

    private void buildLPS() {
        // Dados
        int adress = 0;
        int i = 1; // Iterator para percorrer array pulando posicao 0
        lps[0] = adress; // Gravar o valor 0 no endereco 0
        // Construir lps
        while (i < pLength) {
            // Testar se exite prefixo
            if (p.charAt(i) == p.charAt(adress)) {
                adress++;
                lps[i] = adress;
                i++;
            } else {
                if (adress != 0) {
                    adress = lps[adress - 1]; 
                    // pegar endereco anterio caso adress n volte ao comeco 
                    // para testar prefixo
                } else {
                    lps[i++] = adress; // salvar endereco e incremtar i
                }
            }
        }
    }
}
