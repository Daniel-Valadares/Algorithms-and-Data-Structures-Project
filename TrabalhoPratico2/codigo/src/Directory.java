package src;

/**
 * @author Daniel Valadares, Gustavo Silvestre e Larissa Valadares
 */

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class Directory {
    private byte pGlobal; // profundidade global
    private long[] enderecos; // endereco dos buckets

    public Directory() {
        
        pGlobal = 0;
        enderecos = new long[1];
        enderecos[0] = 0;
    }

    public boolean atualizarEndereco(int p, long e) {
        // testar se a profundidade existe
        if (p > Math.pow(2, pGlobal))
            return false;
        // atualizar
        enderecos[p] = e;
        return true;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        // escrever profundidade global
        dos.writeByte(pGlobal);
        int n = (int) Math.pow(2, pGlobal);
        int i = 0;
        while (i < n) {
            dos.writeLong(enderecos[i]);
            i++;
        }
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        // pegar profundidade global
        pGlobal = dis.readByte();
        int n = (int) Math.pow(2, pGlobal);
        enderecos = new long[n];
        int i = 0;
        // pegar enderecos
        while (i < n) {
            enderecos[i] = dis.readLong();
            i++;
        }
    }

    public String toString() {
        String s = "Profundidade global: " + pGlobal;
        int i = 0;
        int n = (int) Math.pow(2, pGlobal);
        while (i < n) {
            s += "\n" + i + ": " + enderecos[i];
            i++;
        }
        return s;
    }

    public long endereco(int p) {
        if (p > Math.pow(2, pGlobal) && p != 0)
            return -1;
        return enderecos[p];
    }

    public boolean aumentarGlobal() {
        if (pGlobal >= 127)
            return false;
        pGlobal++;
        int n1 = (int) Math.pow(2, pGlobal - 1); // metade
        int n2 = (int) Math.pow(2, pGlobal); // tamanho total
        // novo tamanho
        long[] newEnderecos = new long[n2];
        int i = 0;
        // colocar primeira metade dos enderecos
        while (i < n1) {
            newEnderecos[i] = enderecos[i];
            i++;
        }
        // colocar segunda metade dos enderecos
        while (i < n2) {
            newEnderecos[i] = enderecos[i - n1];
            i++;
        }
        enderecos = newEnderecos;
        return true;
    }

    public int hash(int chave) {
        return chave % (int) Math.pow(2, pGlobal);
    }

    public int hash2(int chave, int pl) { // cálculo do hash para profundidade local
        return chave % (int) Math.pow(2, pl);
    }

    public byte getGlobalDeep() {
        return this.pGlobal;
    }
}
