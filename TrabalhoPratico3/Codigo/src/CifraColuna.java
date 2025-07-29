package src;

/**
 * @author Daniel Valadares, Gustavo Silvestre e Larissa Valadares
 */

public class CifraColuna {
    // Atributos
    private String chave;
    private char[] vetorPrec;

    String criptografar(String senha) {

        String criptografia = "";
        int numLinhas;
        int tamanhoChave = chave.length();
        int tamanhoSenha = senha.length();
        int mod = tamanhoSenha % tamanhoChave;
        // espacoVazio = true se existir espaço vazio na matriz
        boolean espacoVazio = (mod != 0);

        // Caso exista espaco vazio na matriz, é adicionado 1 no numLinhas
        if (espacoVazio) {
            numLinhas = tamanhoSenha / tamanhoChave;
            numLinhas++;
        } else {
            numLinhas = tamanhoSenha / tamanhoChave;
        }
        // Matriz para criptografar mensagem
        char[][] matriz = new char[numLinhas][tamanhoChave];
        int linha = 0;
        int coluna = 0;
        for (int i = 0; i < tamanhoSenha; i++) {
            if (i % tamanhoChave == 0 && i != 0) {
                linha++;
                coluna = 0;
            }
            matriz[linha][coluna++] = senha.charAt(i);
        }

        // Pegar os caracteres na ordem correta para criptografar
        int[] ordem = pegarOrdem();
        // Caso não exista espaço vazio na matriz
        if (!espacoVazio) {
            for (int i = 0; i < tamanhoChave; i++) {
                for (int j = 0; j < numLinhas; j++) {
                    criptografia += matriz[j][ordem[i]];
                }
            }
        } else {
            for (int i = 0; i < tamanhoChave; i++) {
                if (mod > ordem[i]) {
                    for (int j = 0; j < numLinhas; j++) {
                        criptografia += matriz[j][ordem[i]];
                    }
                } else {
                    for (int j = 0; j < numLinhas - 1; j++) {
                        criptografia += matriz[j][ordem[i]];
                    }
                }

            }
        }
        return criptografia;
    }

    String descriptografar(String criptografia){
        String senha = "";
        int numLinhas;
        int tamanhoChave = chave.length();
        int tamanhoSenha = criptografia.length();
        int mod = tamanhoSenha % tamanhoChave;
        // espacoVazio = true se existir espaço vazio na matriz
        boolean espacoVazio = (mod != 0);

        // Caso exista espaco vazio na matriz, é adicionado 1 no numLinhas
        if (espacoVazio) {
            numLinhas = tamanhoSenha / tamanhoChave;
            numLinhas++;
        } else {
            numLinhas = tamanhoSenha / tamanhoChave;
        }

        // Matriz para descriptografar mensagem
        char[][] matriz = new char[numLinhas][tamanhoChave];

        // Pega os caracteres da chave na ordem correta para descriptografar
        int[] ordem = pegarOrdem();
        
        int cont = 0;
        // Verifica se existe espaço vazio, preenche a matriz e faz a descriptografia
        if(!espacoVazio){
            // Preenche a matriz
            for (int i = 0; i < ordem.length; i++) {
                for (int j = 0; j < numLinhas; j++) {
                    matriz[j][ordem[i]] = criptografia.charAt(cont++);
                }
            }
            // Faz a descriptografa
            for(int i = 0; i < numLinhas; i++){
                for(int j = 0; j < tamanhoChave; j++){
                    senha += matriz[i][j];
                }
            }
        } else{
            // Preenche a matriz
            for(int i = 0; i < ordem.length; i++){
                if(mod > ordem[i]){
                    for (int j = 0; j < numLinhas; j++) {
                        matriz[j][ordem[i]] = criptografia.charAt(cont++);
                    } 
                } else{
                    for (int j = 0; j < numLinhas - 1; j++) {
                        matriz[j][ordem[i]] = criptografia.charAt(cont++);
                    }
                }
            }
            // Faz a descriptografa
            for(int i = 0; i < numLinhas; i++){
                if(mod > i){
                    for(int j = 0; j < tamanhoChave; j++){
                        senha += matriz[i][j];
                    }
                } else {
                    for(int j = 0; j < tamanhoChave - 1; j++){
                        senha += matriz[i][j];
                    }
                }
            }
        }
        
        return senha;
    }

    // Cria o array com a ordem de retirada dos caracteres
    int[] pegarOrdem() {
        int[] ordem = new int[chave.length()];
        char[] aux = vetorPrec.clone();

        int menor = 0;

        for (int i = 0; i < chave.length(); i++) {
            for (int j = 0; j < chave.length(); j++) {
                if (aux[j] != 0xFFFF) {
                    if (aux[j] < aux[menor]) {
                        menor = j;
                    }
                }
            }
            ordem[i] = menor;
            aux[menor] = 0xFFFF; // Um char com valor alto para "retirar" ele da array
        }

        return ordem;
    }

    CifraColuna(String chave) {
        // Inicializa a chave para (des)criptografar
        this.chave = chave;

        // Inicializa um vetor com os números na ordem de precedência das letras
        char[] vetorPrec = new char[chave.length()];
        for (int i = 0; i < chave.length(); i++) {
            char num = 1; // Char utilizado como um inteiro para evitar casting
            for (int j = 0; j < chave.length(); j++) {
                if (j != i) {
                    if(chave.charAt(i) > chave.charAt(j)){
                        num++;
                    } else if(chave.charAt(i) == chave.charAt(j) && i > j){
                        num++;
                    }
                }
            }
            vetorPrec[i] = num;
        }
        this.vetorPrec = vetorPrec;
    }

    
}