package src;

/**
 * @author Daniel Valadares e Larissa Valadares
 */

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class Bucket {
    private byte pLocal; // profundidade local do cesto
    private short n; // n de pares presentes no cesto
    private short nMax; // n máxima de pares que o cesto pode conter
    private int[] chaves; // sequência de chaves armazenadas no cesto
    private long[] dados; // sequência de dados correspondentes às chaves
    private short sizePar; // size fixo do par de chave e endereco
    private short sizeBucket; // size fixo do bucket

    public Bucket(int nMax) throws Exception {
        this(nMax, 0);
    }

    public Bucket(int nMax, int pl) throws Exception {
        if (nMax > 32767)
            throw new Exception("Overflow no número máximo dos pares");
        if (pl > 127)
            throw new Exception("Overflow na profundidade local do hash");
        this.pLocal = (byte) pl;
        this.n = 0;
        this.nMax = (short) nMax;
        this.chaves = new int[nMax];
        this.dados = new long[nMax];
        this.sizePar = 12; // int (id - 4) + long (endereço raf - 8)
        this.sizeBucket = (short) (sizePar * nMax + 3);
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        // escrever profundidade local
        dos.writeByte(pLocal);
        // escrever numero de pares
        dos.writeShort(n);
        int i = 0;
        // preencher numero de pares preenchidos
        while (i < n) {
            dos.writeInt(chaves[i]);
            dos.writeLong(dados[i]);
            i++;
        }
        // preencher numero de pares vazios
        while (i < nMax) {
            dos.writeInt(-1);
            dos.writeLong(-1);
            i++;
        }
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        // pegar profundidade local
        pLocal = dis.readByte();
        // pegar numero de pares
        n = dis.readShort();
        int i = 0;
        // pregar numero de pares preenchidos
        while (i < nMax) {
            chaves[i] = dis.readInt();
            dados[i] = dis.readLong();
            i++;
        }
    }

    public boolean create(int c, long d) {
        if (full())
            return false;
        int i = n - 1;
        // empurrar pares
        while (i >= 0 && c < chaves[i]) {
            chaves[i + 1] = chaves[i];
            dados[i + 1] = dados[i];
            i--;
        }
        // colocar par
        i++;
        chaves[i] = c;
        dados[i] = d;
        n++;
        return true;
    }

    public long read(int c) {
        if (empty())
            return -1;
        int i = 0;
        // pesquisa linear no bucket
        while (i < n && c > chaves[i])
            i++;
        if (i < n && c == chaves[i])
            return dados[i];
        else
            return -1;
    }

    public boolean update(int c, long d) {
        if (empty())
            return false;
        int i = 0;
        // pesquisa linear no bucket
        while (i < n && c > chaves[i])
            i++;
        if (i < n && c == chaves[i]) {
            // trocar valor de endereco
            dados[i] = d;
            return true;
        } else
            return false;
    }

    public boolean delete(int c) {
        if (empty())
            return false;
        int i = 0;
        // pesquisa linear no bucket
        while (i < n && c > chaves[i])
            i++;
        if (c == chaves[i]) {
            // realocar pares
            while (i < n - 1) {
                chaves[i] = chaves[i + 1];
                dados[i] = dados[i + 1];
                i++;
            }
            n--;
            return true;
        } else
            return false;
    }

    public boolean empty() {
        return n == 0;
    }

    public boolean full() {
        return n == nMax;
    }

    public String toString() {
        String s = "Profundidade Local: " + pLocal +
                "\nNúmero de pares: " + n +
                "\n| ";
        int i = 0;
        while (i < n) {
            s += chaves[i] + "; " + dados[i] + " | ";
            i++;
        }
        while (i < nMax) {
            s += "NULL; NULL | ";
            i++;
        }
        return s;
    }

    public int getSize() {
        return this.sizeBucket;
    }

    public byte getLocalDeep() {
        return this.pLocal;
    }

    public int getN() {
        return this.n;
    }

    public int[] getChaves() {
        return this.chaves;
    }

    public long[] getDados() {
        return this.dados;
    }
}