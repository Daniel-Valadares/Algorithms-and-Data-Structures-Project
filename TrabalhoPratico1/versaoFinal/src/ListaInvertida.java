package src;

/**
 * @author Daniel Valadares e Larissa Valadares
 */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;

public class ListaInvertida {
    private String pathInvertida = "data/invertida"; // path para teste com extesao de debuger do VScode
    // private String pathInvertida = "../data/invertida"; // path para teste
    // via compilador
    private RandomAccessFile raf;
    private ListaIndex index;
    private String[] ignore = { "da", "do", "de", "das", "dos" };

    public ListaInvertida(String s) {
        this.pathInvertida += s + ".db";
        this.index = new ListaIndex(s);
        try {
            raf = new RandomAccessFile(pathInvertida, "rw");
            raf.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean create(String s, int id, long address) {
        try {
            raf = new RandomAccessFile(pathInvertida, "rw");
            for (String test : s.split(" ")) {
                if (isIgnore(test))
                    continue;
                long pos = index.read(test);
                // caso nao exista criar no arquivo
                if (pos == -1) {
                    // criar no arquivo
                    index.create(test, raf.length());
                    raf.seek(raf.length());
                    raf.writeInt(1); // numero de repeticoes
                    // elementos da lista
                    raf.writeBoolean(false);
                    raf.writeInt(id);
                    raf.writeLong(address);
                    raf.writeLong(-1); // endereco do proximo da lista invertida
                } else {
                    raf.seek(pos);
                    int n = raf.readInt(); // numero de repeticoes da palavra
                    raf.seek(pos);
                    raf.writeInt(n + 1);
                    long tmpPos = pos + 4; // pos pulado int de repeticoes
                    long pointerPos; // posicao previa
                    do {
                        raf.seek(tmpPos);
                        raf.skipBytes(13); // pular bool (lapide) + int (id) + long (address)
                        pointerPos = raf.getFilePointer();
                        tmpPos = raf.readLong();
                    } while (tmpPos != -1);
                    raf.seek(pointerPos);
                    raf.writeLong(raf.length());
                    raf.seek(raf.length());
                    // elementos da lista
                    raf.writeBoolean(false);
                    raf.writeInt(id);
                    raf.writeLong(address);
                    raf.writeLong(-1); // endereco do proximo da lista invertida
                }
            }
            raf.close();
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public long[] read(String s) {
        long[] result;
        try {
            raf = new RandomAccessFile(pathInvertida, "r");
            if (isIgnore(s))
                result = null;
            else {
                long pos = index.read(s);
                if (pos == -1) {
                    result = null;
                } else {
                    raf.seek(pos);
                    int n = raf.readInt(); // numero de repeticoes da palavra
                    result = new long[n];
                    long tmpPos = pos + 4; // pos pulado int de repeticoes
                    int i = 0;
                    do {
                        raf.seek(tmpPos);
                        Boolean lapide = raf.readBoolean();
                        raf.skipBytes(4); // pular id
                        result[i] = lapide ? -1 : raf.readLong();
                        i = lapide ? i : i + 1;
                        tmpPos = raf.readLong();
                        n = lapide ? n : n - 1; // diminui se n lapide
                    } while (n > 0);
                }
            }
            raf.close();
            return result;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean update(int id, String s, long address) {
        long lPos; // posicao da lapide
        boolean result = false;
        try {
            raf = new RandomAccessFile(pathInvertida, "rw");
            for (String test : s.split(" ")) {
                if (isIgnore(test))
                    continue;
                long pos = index.read(test);
                // caso nao exista criar no arquivo
                if (pos == -1) {
                    result = false;
                } else {
                    raf.seek(pos);
                    int n = raf.readInt(); // numero de repeticoes da palavra
                    long tmpPos = pos + 4; // pos pulado int de repeticoes
                    do {
                        raf.seek(tmpPos);
                        lPos = raf.getFilePointer();
                        Boolean lapide = raf.readBoolean();
                        if (id == raf.readInt()) {
                            raf.seek(lPos);
                            raf.writeBoolean(false);
                            raf.writeInt(id);
                            raf.writeLong(address);
                            result = true;
                        }
                        raf.skipBytes(8); // long (address)
                        tmpPos = raf.readLong();
                        n = lapide ? n : n - 1; // diminui se n lapide
                    } while (n > 0);
                }
            }
            raf.close();
            return result;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean delete(int id, String s) {
        long lPos; // posicao da lapide
        boolean result = false;
        try {
            raf = new RandomAccessFile(pathInvertida, "rw");
            for (String test : s.split(" ")) {
                if (isIgnore(test))
                    continue;
                long pos = index.read(test);
                // caso nao exista criar no arquivo
                if (pos == -1) {
                    result = false;
                } else {
                    raf.seek(pos);
                    int n = raf.readInt(); // numero de repeticoes da palavra
                    int nTotal = n;
                    long tmpPos = pos + 4; // pos pulado int de repeticoes
                    do {
                        raf.seek(tmpPos);
                        lPos = raf.getFilePointer();
                        Boolean lapide = raf.readBoolean();
                        if (id == raf.readInt()) {
                            raf.seek(lPos);
                            raf.writeBoolean(true);
                            raf.seek(pos); // voltar para pos do numero total de elementos
                            raf.writeInt(nTotal - 1); // atualizar valor total de elementos
                            result = true;
                            n = 0; // parar loop
                        }
                        raf.skipBytes(8); // long (address)
                        tmpPos = raf.readLong();
                        n = lapide ? n : n - 1; // diminui se n lapide
                    } while (n > 0);
                }
            }
            raf.close();
            return result;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public String[] toString(String s) {
        String[] result;
        try {
            raf = new RandomAccessFile(pathInvertida, "r");
            if (isIgnore(s) || s == null)
                result = null;
            else {
                long pos = index.read(s);
                if (pos == -1) {
                    result = null;
                } else {
                    raf.seek(pos);
                    int n = raf.readInt(); // numero de repeticoes da palavra
                    if (n == 0) {
                        result = null;
                    } else {
                        result = new String[n];
                        long tmpPos = pos + 4; // pos pulado int de repeticoes
                        int i = 0;
                        do {
                            raf.seek(tmpPos);
                            Boolean lapide = raf.readBoolean();
                            result[i] = lapide ? "Lapide: " + raf.readInt() + " " + raf.readLong()
                                    : "ID: " + raf.readInt() + "; Endereço: " + raf.readLong() + "\n";
                            i = lapide ? i : i + 1;
                            tmpPos = raf.readLong();
                            n = lapide ? n : n - 1; // diminui se n lapide
                        } while (n > 0);
                    }
                }
            }
            raf.close();
            return result;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private boolean isIgnore(String str) {
        for (String s : this.ignore)
            if (s.equalsIgnoreCase(str))
                return true;
        return false;
    }

    public void print(String s) {
        try {
            System.out.println("=======================================");
            System.out.println("Index da Lista Invertida");
            System.out.println("=======================================");
            System.out.println("");
            System.out.println(index.toString(s));
            System.out.println("");
            System.out.println("=======================================");
            System.out.println("Resultados da Lista");
            System.out.println("=======================================");
            System.out.println("");
            String[] tmp = toString(s);
            if (tmp == null) {
                System.out.println("Nenhum resultado encontrado...");
            } else {
                int i = 0;
                while (i < tmp.length) {
                    System.out.print(tmp[i++]);
                }
            }
            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
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
        boolean tmp = index.deleteFile();
        File file = new File(pathInvertida);
        return tmp && file.delete();
    }
}