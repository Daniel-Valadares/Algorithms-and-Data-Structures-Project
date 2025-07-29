package src;

/**
 * @author Daniel Valadares e Larissa Valadares
 */

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.File;

public class ListaIndex {
    private String path = "data/ListaIndex"; // path para teste com extesao de debuger do VScode
    // private String path = "../data/ListaIndex"; // path para teste
    private RandomAccessFile raf; // ponteiro para o arquivo

    public ListaIndex(String s) {
        this.path += s + ".db";
        try {
            raf = new RandomAccessFile(path, "rw");
            raf.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void create(String s, long pos) {
        try {
            raf = new RandomAccessFile(path, "rw");
            raf.seek(raf.length());
            raf.writeUTF(s);
            raf.writeLong(pos);
            raf.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public long read(String s) {
        long pos = -1;
        try {
            raf = new RandomAccessFile(path, "rw");
            while (pos < 0 && raf.getFilePointer() < raf.length()) {
                if (s.equals(raf.readUTF())) {
                    pos = raf.readLong();
                }
                else
                    raf.skipBytes(8);
            }
            raf.close();
            return pos;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return pos;
        }
    }

    public String toString (String s) {
        String result = null;
        try {
            raf = new RandomAccessFile(path, "rw");
            while (result == null && raf.getFilePointer() < raf.length()) {
                String tmp = raf.readUTF();
                if (s.equals(tmp)) {
                    result = "String: " + tmp + "; Endereço na Lista Invertida: " + raf.readLong();
                }
                else
                    raf.skipBytes(8);
            }
            raf.close();
            return result;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return result;
        }
    }

    public void end(){
        try {
            raf.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean deleteFile(){
        File file = new File(path);
        return file.delete();
    }
}