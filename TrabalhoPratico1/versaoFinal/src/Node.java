package src;

/**
 * @author Daniel Valadares e Larissa Valadares
 */

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class Node {
    protected int ordem;
    protected int n; // Elementos presentes na pagina
    protected int[] id; // Ids nos elementos
    protected long[] address; // Enderecos dos ids nos elementos
    protected long next; // Ponteiro para folha irma
    protected long[] pointers; // Ponteiros para os filhos
    protected int sizeRegistro = 12;
    protected int sizeNode;

    public Node(int ordem) {
        this.ordem = ordem;
        this.n = 0;
        this.id = new int[ordem - 1];
        this.address = new long[ordem - 1];
        this.pointers = new long[ordem];
        this.next = -1;
        // colocar elementos vazios
        int i = 0;
        while (i < n) {
            this.id[i] = 0;
            this.address[i] = -1;
            this.pointers[i] = -1;
            i++;
        }
        this.pointers[i] = -1;
        this.sizeNode = 4 + (ordem - 1) * sizeRegistro + ordem * 8 + 16;
    }

    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            // escrever quantidade de elementos
            dos.writeInt(n);
            int i = 0;
            while (i < n) {
                dos.writeInt(id[i]);
                dos.writeLong(address[i]);
                dos.writeLong(pointers[i]);
                i++;
            }
            dos.writeLong(pointers[i]);
            // caso precise colocar registros vazios
            while (i < n) {
                dos.writeInt(0);
                dos.writeLong(-1);
                dos.writeLong(-1);
                i++;
            }
            // Proxima pagina
            dos.writeLong(next);
            return baos.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void fromByteArray(byte[] ba) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            DataInputStream dis = new DataInputStream(bais);
            // escrever quantidade de elementos
            n = dis.readInt();
            int i = 0;
            while (i < ordem - 1) {
                id[i] = dis.readInt();
                address[i] = dis.readLong();
                pointers[i] = dis.readLong();
                i++;
            }
            pointers[i] = dis.readLong();
            next = dis.readLong();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String toString() {
        String s = "Ordem: " + ordem + " | Quantidade de elementos: " + n + " | Elementos [";
        int i;
        for (i = 0; i < n; i++) {
            s += "(" + i + ") " + id[i] + "; " + address[i] + " ";
        }
        for (int j = i; j < n; j++) {
            s += "(" + i + ") " + "NULL" + "; " + "NULL" + " ";
        }
        s += "] Proxima folha: " + next;
        return s;
    }
}