package src;

/**
 * @author Daniel Valadares, Gustavo Silvestre e Larissa Valadares
 */

import java.io.*;
import java.util.PriorityQueue;
import java.util.BitSet;

public class Huffman {
    // Atributos
    private long numBytesDescomp;
    private long numBytesComp;
    private long numBytesOriginal;
    private int[] arrayFrequencia;
    private String arquivoOriginal;
    // Arvore necessária para descomprimir, por isso a não pode ter métodos estáticos
    private HuffmanArv arvore; 

    // Métodos
    // Faz a compressao e salva o arquivo
    void compressao(){
        // O arquivo será comprimido como uma String de chars
        String arquivoString = "", arquivoStringComp = "";
        try {
            byte auxB;
            int auxI;
            RandomAccessFile raf = new RandomAccessFile(arquivoOriginal, "rw");
            for(int i = 0; i < raf.length(); i++){
                auxB = raf.readByte();
                // Caso o byte for negativo transforma ele no valor correto entre 0 - 255
                if(auxB < 0){
                    auxI = auxB & 0xFF;
                    arquivoString += (char)auxI;
                } else {
                    arquivoString += (char)auxB;
                }
                
            }
            // Transforma o arquivoString em uma versão codificada com chars '0' e '1'
            arquivoStringComp = codificar(arvore, arquivoString);
            BitSet bitSet = new BitSet(arquivoStringComp.length());
            int index = 0;
            // Tranforma String com chars '0' e '1' em array de bits
            for(char c : arquivoStringComp.toCharArray()){
                if(c == '1'){
                    bitSet.set(index);
                }
                index++;
            }
            // Cria e salva no arquivo a compressão
            File del = new File("data/contasHuffmanCompressao.huffman");
            //File del = new File("../data/contasHuffmanCompressao.huffman"); // Path para compilação manual
            if(del.exists()){
                del.delete();
            }
            RandomAccessFile rafComp = new RandomAccessFile("data/contasHuffmanCompressao.huffman", "rw");
            // RandomAccessFile rafComp = new RandomAccessFile("../data/contasHuffmanCompressao.huffman", "rw");// Path para compilação manual
            rafComp.write(bitSet.toByteArray());
            numBytesComp = rafComp.length();
            rafComp.close();
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    // Faz a descompactação
    void descompactar(){
        assert arvore != null;
        String arquivoStringDescomp = "", arquivoStringComp = "";
        // Transforma o array de bits de volta em uma String com chars '0' e '1' para a descompressão
        try {
            RandomAccessFile raf = new RandomAccessFile("data/contasHuffmanCompressao.huffman", "rw");
            //RandomAccessFile raf = new RandomAccessFile("../data/contasHuffmanCompressao.huffman", "rw");// path para compilacao manual
            byte[] arrB = new byte[(int)raf.length()];
            raf.read(arrB);
            BitSet bs = BitSet.valueOf(arrB);
            for(int i = 0; i < bs.length(); i++){
                if(bs.get(i)){
                    arquivoStringComp += '1';
                } else{
                    arquivoStringComp += '0';
                }
            }
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        HuffmanNo no = (HuffmanNo)arvore;
    	for (char code : arquivoStringComp.toCharArray()){
    		if (code == '0'){ // Quando for igual a 0 é o Lado Esquerdo
    		    if (no.left instanceof HuffmanFolha) { 
    		    	arquivoStringDescomp += ((HuffmanFolha)no.left).value; // Retorna o valor do nó folha, pelo lado Esquerdo  
	                no = (HuffmanNo)arvore; // Retorna para a Raíz da árvore
	    		}else{
	    			no = (HuffmanNo) no.left; // Continua percorrendo a árvore pelo lado Esquerdo 
	    		}
    		}else if (code == '1'){ // Quando for igual a 1 é o Lado Direito
    		    if (no.right instanceof HuffmanFolha) {
    		    	arquivoStringDescomp += ((HuffmanFolha)no.right).value; //Retorna o valor do nó folha, pelo lado Direito
	                no = (HuffmanNo)arvore; // Retorna para a Raíz da árvore
	    		}else{
	    			no = (HuffmanNo) no.right; // Continua percorrendo a árvore pelo lado Direito
	    		}
    		}
    	} // End for
    	// Cria o arquivo descomprimido
        try {
            // Exclui o arquivo original para colocar o descomprimido
            File del = new File(arquivoOriginal);
            del.delete();
            // Cria o arquivo descomprimido e salva todos os bytes
            RandomAccessFile raf = new RandomAccessFile("data/contas.dbhuffman", "rw");
            //RandomAccessFile raf = new RandomAccessFile("../data/contas.dbhuffman", "rw");// path par compilacao manual
            for(char c : arquivoStringDescomp.toCharArray()){
                raf.writeByte(c);
            }
            numBytesDescomp = raf.length();
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }  
    
    // Preenche array de frequências com a frequência dos bytes no arquivo
    // Trata cada byte como se fosse um char para facilitar na identificação no array
    void preencherFreq(){
        try {
            byte auxB;
            int auxI;
            RandomAccessFile raf = new RandomAccessFile(arquivoOriginal, "rw");
            for(int i = 0; i < raf.length(); i++){
                auxB = raf.readByte();
                // Caso o byte for negativo transforma ele no valor correto entre 0 - 255
                if(auxB < 0){
                    auxI = auxB & 0xFF;
                    arrayFrequencia[auxI]++;
                } else {
                    arrayFrequencia[auxB]++;
                }
            }
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    // Cria a árvore de baixo para cima
    HuffmanArv criarArv(){
        // Cria uma fila de pioridade pela ordem de frequência de cada byte
        PriorityQueue<HuffmanArv> arvores = new PriorityQueue<>();
        // Cria as folhas da árvore para cada byte no array
        for(int i = 0; i < arrayFrequencia.length; i++){
            if(arrayFrequencia[i] > 0){
                // Insere os elementos na fila de prioridade
                arvores.offer(new HuffmanFolha(arrayFrequencia[i], (char)i));
            }
        }
        // Monta a árvore binária
        while(arvores.size() > 1){
            // Retira os nós com menor frequência
            HuffmanArv a = arvores.poll();
            HuffmanArv b = arvores.poll();

            arvores.offer(new HuffmanNo(a, b));
        }
        // Retorna a raíz da árvore
        return arvores.poll();
    }

    // Codifica o array de bytes
    public String codificar(HuffmanArv arvore, String codif){
        // Garante que a arvore não esteja vazia
        assert arvore != null;

        String codifText = "";
        for (char c : codif.toCharArray()){
            codifText += getCodes(arvore, new StringBuffer(), c);
        }
        return codifText;
    }

    public static String getCodes(HuffmanArv tree, StringBuffer prefix, char w) {
        assert tree != null;
        
        if (tree instanceof HuffmanFolha) {
            HuffmanFolha leaf = (HuffmanFolha)tree;
            
            // Retorna o texto compactado da letra
            if (leaf.value == w ){
            	return prefix.toString();
            }
            
        } else if (tree instanceof HuffmanNo) {
            HuffmanNo no = (HuffmanNo)tree;
 
            // Percorre a esquerda
            prefix.append('0');
            String left = getCodes(no.left, prefix, w);
            prefix.deleteCharAt(prefix.length()-1);
 
            // Percorre a direita
            prefix.append('1');
            String right = getCodes(no.right, prefix,w);
            prefix.deleteCharAt(prefix.length()-1);
            
            if (left==null) return right; else return left;
        }
		return null;
    }


    //Construtor
    Huffman(String arquivoDados){
        this.arquivoOriginal = arquivoDados;
        // Array com 256 posições para representar cada byte do arquivo
        int[] arrayFrequencias = new int[256];
        this.arrayFrequencia = arrayFrequencias;
        try {
            RandomAccessFile raf = new RandomAccessFile(arquivoDados, "rw");
            // Número total de bytes do arquivo original
            this.numBytesOriginal = raf.length();
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Preenche o arrayFrequencia
        preencherFreq();
        // Cria árvore de codificação
        arvore = criarArv();
    }

    // Getters
    public long getNumBytesDescomp() {
        return numBytesDescomp;
    }
    public long getNumBytesComp() {
        return numBytesComp;
    }
    public long getNumBytesOriginal() {
        return numBytesOriginal;
    }
    
}
