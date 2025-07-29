package src;

/**
 * @author Daniel Valadares, Gustavo Silvestre e Larissa Valadares
 */

import java.util.HashMap;
import java.io.IOException;

import java.io.EOFException;
import java.io.RandomAccessFile;

public class LZW {
    private long numBytesDescomp;
    private long numBytesComp;
    private long numBytesOriginal;
    private String path;

    public LZW(String path){
        this.path = path;
    }

    public void compress(String fileName) throws IOException {
        // Dados
        String str = "";
        boolean onLeft = true;
        byte[] buffer = new byte[3];
        int dSize = 256;
        // Criar HashMap para utilizar como dicionario
        HashMap<String, Integer> dicionario = new HashMap<>();
        // Preencher Hash com dicionario inicial
        for (int i = 0; i < dSize; i++) {
            dicionario.put(Character.toString((char) i), i);
        }
        // Raf para leitura e saida
        RandomAccessFile input = new RandomAccessFile(path, "r");
        RandomAccessFile output = new RandomAccessFile("data/" + fileName + "LZWCompressao.lzw", "rw");
        // RandomAccessFile output = new RandomAccessFile("../data" + fileName + "LZWCompressao.lzw", "rw");
        // Iniciar compressão
        try {
            byte b = input.readByte();
            int i = b;
            if(i < 0){
                i += 256;
            } 
            char c = (char) i;
            str = "" + c;
            // While que so finaliza quando estourar o limite do Raf 
            while (true) {
                b = input.readByte();
                i = b;
                if(i < 0){
                    i += 256;
                } 
                c = (char) i;
                // Testar se existe no dicionario
                if (dicionario.containsKey(str + c)) {
                    str += c;
                } else {
                    String b12 = to12bit(dicionario.get(str)); // Converter para 12 bits
                    // Escrever bits faltantes
                    if (onLeft) {
                        // Escrever bits da esquerda na base 2
                        buffer[0] = (byte) Integer.parseInt(b12.substring(0, 8), 2);
                        // Escrever bits da direita na base 2
                        buffer[1] = (byte) Integer.parseInt(b12.substring(8, 12) + "0000", 2);
                    } else {
                        // Escrever bits da esquerda na base 2
                        buffer[1] = (byte) Integer.parseInt(b12.substring(0, 4), 2);
                        // Escrever bits da direita na base 2
                        buffer[2] = (byte) Integer.parseInt(b12.substring(4, 12) + "0000", 2);
                        for (int j = 0; j < buffer.length; j++) {
                            output.writeByte(buffer[j]);
                            buffer[j] = 0;
                        }
                    }
                    onLeft = !onLeft;
                    // Adicionar nova palavra no dicionario
                    if (dSize < 4096) {
                        dicionario.put(str + c, dSize++);
                    }
                    str = "" + c;
                }
            }
        } catch (EOFException e) {
            // Gravar ultimo byte
            String b12 = to12bit(dicionario.get(str));
            if (onLeft) {
                buffer[0] = (byte) Integer.parseInt(b12.substring(0, 8), 2);
                buffer[1] = (byte) Integer.parseInt(b12.substring(8, 12) + "0000", 2);
                output.writeByte(buffer[0]);
                output.writeByte(buffer[1]);
            } else {
                buffer[1] += (byte) Integer.parseInt(b12.substring(0, 4), 2);
                buffer[2] = (byte) Integer.parseInt(b12.substring(4, 12), 2);
                for (int b = 0; b < buffer.length; b++) {
                    output.writeByte(buffer[b]);
                    buffer[b] = 0;
                }
            }
            // Salvar arquivo
            numBytesOriginal = input.length();
            numBytesComp = output.length();
            input.close();
            output.close();
        }
    }

    public void decompress(String fileName) throws IOException {
        // Dados
        String[] array = new String[4096];
        boolean onLeft = true;
        byte[] buffer = new byte[3];
        int cWord = 0;
        int pWord = 0;
        int dSize = 256;
        // Criar HashMap para utilizar como dicionario
        HashMap<String, Integer> dicionario = new HashMap<>();
        // Preencher Hash com dicionario inicial
        for (int i = 0; i < dSize; i++) {
            dicionario.put(Character.toString((char) i), i);
            array[i] = Character.toString((char) i); // Salvar valor no array
        }
        // Raf para leitura e saida
        RandomAccessFile input = new RandomAccessFile("data/" + fileName + "LZWCompressao.lzw", "rw");
        // RandomAccessFile input = new RandomAccessFile("../data/" + fileName + "LZWCompressao.lzw", "rw");
        RandomAccessFile output = new RandomAccessFile(path + "LZW", "rw");
        try {
            buffer[0] = input.readByte();
            buffer[1] = input.readByte();
            pWord = wordToInt(buffer[0], buffer[1], onLeft);
            onLeft = !onLeft;
            output.writeBytes(array[pWord]);
            // Ler input no buffer e gerar palavra
            while (true) {
                if (onLeft) {
                    buffer[0] = input.readByte();
                    buffer[1] = input.readByte();
                    cWord = wordToInt(buffer[0], buffer[1], onLeft);
                } else {
                    buffer[2] = input.readByte();
                    cWord = wordToInt(buffer[1], buffer[2], onLeft);
                }
                onLeft = !onLeft;
                if (cWord >= dSize) {
                    if (dSize < 4096) {
                        array[dSize] = array[pWord] + array[pWord].charAt(0);
                    }
                    dSize++;
                    output.writeBytes(array[pWord] + array[pWord].charAt(0));
                } else {
                    if (dSize < 4096) {
                        array[dSize] = array[pWord] + array[cWord].charAt(0);
                    }
                    dSize++;
                    output.writeBytes(array[cWord]);
                }
                pWord = cWord;
                /*
                if (dicionario.containsKey(cWord)) {
                    output.writeBytes(array[cWord]);
                    dicionario.put(array[pWord] + array[cWord].charAt(0), dSize++);
                    array[dSize] = array[pWord] + array[cWord].charAt(0); 
                } else {
                    dicionario.put(array[pWord] + array[cWord].charAt(0), dSize++);
                    array[dSize] = array[pWord] + array[cWord].charAt(0); 
                    output.writeBytes(array[pWord] + array[cWord].charAt(0));
                }
                */
            }
        } catch (EOFException e) {
            numBytesDescomp = output.length();
            input.close();
            output.close();
        }
    }

    // Converte 8 bits para 12 bits, por meio de uma string
    public String to12bit(int i) {
        String str = Integer.toBinaryString(i);
        while (str.length() < 12) {
            str = "0" + str;
        }
        return str;
    } 

    // Converter palavra para inteiro
    public int wordToInt(byte b1, byte b2, boolean onleft) {
        String tmp1 = Integer.toBinaryString(b1);
        String tmp2 = Integer.toBinaryString(b2);
        while (tmp1.length() < 8) {
            tmp1 = "0" + tmp1;
        }
        if (tmp1.length() == 32) {
            tmp1 = tmp1.substring(24, 32);
        }
        while (tmp2.length() < 8) {
            tmp2 = "0" + tmp2;
        }
        if (tmp2.length() == 32) {
            tmp2 = tmp2.substring(24, 32);
        }
        if (onleft) {
            return Integer.parseInt(tmp1 + tmp2.substring(0, 4), 2);
        } else {
            return Integer.parseInt(tmp1.substring(4, 8) + tmp2, 2);
        }
    }

    // Gets
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
