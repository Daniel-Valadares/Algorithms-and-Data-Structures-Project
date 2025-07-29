package src;

/**
 * @author Daniel Valadares, Gustavo Silvestre e Larissa Valadares
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.EOFException;
import java.io.File;

public class ContaDAO {
    private static String path = "data/contas.db"; // path para teste com extesao de debuger do VScode
    // private static String path = "../data/contas.db"; // path para compilacao via
    // compilador
    private static ArvoreBp b;
    private static HashEstendido he;
    private static ListaInvertida lNome;
    private static ListaInvertida lCidade;
    private static CifraColuna cifra = new CifraColuna("chave");

    public static void setup() {
        b = new ArvoreBp(4);
        he = new HashEstendido(4);
        lNome = new ListaInvertida("NomePessoa");
        lCidade = new ListaInvertida("Cidade");
        try {
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            if (raf.length() == 0) {
                raf.writeInt(1);
                raf.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean deleteFiles() {
        boolean tmp = b.deleteFile();
        tmp = tmp && he.deleteFile();
        tmp = tmp && lNome.deleteFile();
        tmp = tmp && lCidade.deleteFile();
        File file = new File(path);
        return tmp && file.delete();
    }

    public static void endAll() {
        b.end();
        he.end();
        lNome.end();
        lCidade.end();
    }

    public static void remakeFile(String tmpPath) {
        // dados
        Conta c;
        short nEmails; // numero de emails
        String[] emails; // guardar emails
        // deletar arquivo antigo e manter cabecario
        int id = newId();
        deleteFiles();
        try {
            // RandomAccessFile
            RandomAccessFile tmp = new RandomAccessFile("data/" + tmpPath, "rw");
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            raf.writeInt(id);
            raf.close();
            while (true) {
                try {
                    c = new Conta();
                    if (!tmp.readBoolean()) {
                        c.setIdConta(tmp.readInt());
                        c.setNomePessoa(tmp.readUTF());
                        // pegar emails
                        nEmails = tmp.readShort();
                        emails = new String[nEmails];
                        for (int i = 0; i < nEmails; i++) {
                            emails[i] = tmp.readUTF();
                        }
                        c.setEmail(emails); // colocar emails em c
                        c.setNomeUsuario(tmp.readUTF());
                        c.setSenhaDAO(tmp.readUTF());
                        c.setCpf(tmp.readUTF());
                        c.setCidade(tmp.readUTF());
                        c.setTransferenciasRealizadas(tmp.readInt());
                        c.setSaldoConta(tmp.readFloat());
                        create(c);
                    }
                } catch (EOFException e) {
                    break;
                }
            }
            tmp.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static int newId() {
        int id = -1;
        try {
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            id = raf.readInt();
            raf.seek(0);
            raf.writeInt(id + 1);
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return id;
    }

    public static void create(Conta c) {
        try {
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            // Passar conta para array de Byte
            byte[] ba = c.toByteArray();
            long endereco = raf.length();
            raf.seek(endereco);
            raf.writeInt(ba.length);
            raf.write(ba);
            raf.close();
            // Adicionar na B+
            //b.create(c.getIdConta(), endereco);
            // Adicionar na Hash Estendido
            he.create(c.getIdConta(), endereco);
            // Adcionar na Lista Invertida de Nome
            lNome.create(c.getNomePessoa(), c.getIdConta(), endereco);
            // Adcionar na Lista Invertida de Cidade
            lCidade.create(c.getCidade(), c.getIdConta(), endereco);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Conta read(int id) {
        // dados
        Conta c = new Conta();
        boolean lapide;
        boolean found = false;
        int len;
        long lPos; // pos lapide
        short nEmails; // numero de emails
        String[] emails; // guardar emails
        int idRaf;
        try {
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            raf.seek(4);
            while (!found) {
                try {
                    len = raf.readInt();
                    lPos = raf.getFilePointer();
                    lapide = raf.readBoolean();
                    idRaf = raf.readInt();
                    if (!lapide && idRaf == id) {
                        c.setIdConta(idRaf);
                        c.setNomePessoa(raf.readUTF());
                        // pegar emails
                        nEmails = raf.readShort();
                        emails = new String[nEmails];
                        for (int i = 0; i < nEmails; i++) {
                            emails[i] = raf.readUTF();
                        }
                        c.setEmail(emails); // colocar emails em c
                        c.setNomeUsuario(raf.readUTF());
                        c.setSenhaDAO(raf.readUTF());
                        c.setCpf(raf.readUTF());
                        c.setCidade(raf.readUTF());
                        c.setTransferenciasRealizadas(raf.readInt());
                        c.setSaldoConta(raf.readFloat());
                        found = true;
                    } else {
                        raf.seek(lPos + len);
                    }
                } catch (EOFException e) {
                    break;
                }
            }
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return c;
    }

    public static Conta readDescriptografado(int id) {
        // dados
        Conta c = new Conta();
        boolean lapide;
        boolean found = false;
        int len;
        long lPos; // pos lapide
        short nEmails; // numero de emails
        String[] emails; // guardar emails
        int idRaf;
        try {
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            raf.seek(4);
            while (!found) {
                try {
                    len = raf.readInt();
                    lPos = raf.getFilePointer();
                    lapide = raf.readBoolean();
                    idRaf = raf.readInt();
                    if (!lapide && idRaf == id) {
                        c.setIdConta(idRaf);
                        c.setNomePessoa(raf.readUTF());
                        // pegar emails
                        nEmails = raf.readShort();
                        emails = new String[nEmails];
                        for (int i = 0; i < nEmails; i++) {
                            emails[i] = raf.readUTF();
                        }
                        c.setEmail(emails); // colocar emails em c
                        c.setNomeUsuario(raf.readUTF());
                        c.setSenhaDAO(cifra.descriptografar(raf.readUTF()));
                        c.setCpf(raf.readUTF());
                        c.setCidade(raf.readUTF());
                        c.setTransferenciasRealizadas(raf.readInt());
                        c.setSaldoConta(raf.readFloat());
                        found = true;
                    } else {
                        raf.seek(lPos + len);
                    }
                } catch (EOFException e) {
                    break;
                }
            }
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return c;
    }

    public static Conta readAddress(long pos) {
        // dados
        Conta c = new Conta();
        short nEmails; // numero de emails
        String[] emails; // guardar emails
        try {
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            raf.seek(pos + 5); // pular lapide e length
            c.setIdConta(raf.readInt());
            c.setNomePessoa(raf.readUTF());
            // pegar emails
            nEmails = raf.readShort();
            emails = new String[nEmails];
            for (int i = 0; i < nEmails; i++) {
                emails[i] = raf.readUTF();
            }
            c.setEmail(emails); // colocar emails em c
            c.setNomeUsuario(raf.readUTF());
            c.setSenhaDAO(raf.readUTF());
            c.setCpf(raf.readUTF());
            c.setCidade(raf.readUTF());
            c.setTransferenciasRealizadas(raf.readInt());
            c.setSaldoConta(raf.readFloat());
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return c;
    }

    public static Conta readHash(int id) {
        // dados
        long pos = he.read(id); // se nao encontrar retorna -1
        return readAddress(pos);
    }

    public static long[] invertidaNomeaddressArray(String s) {
        return lNome.read(s);
    }

    public static long[] invertidaCidadeaddressArray(String s) {
        return lCidade.read(s);
    }

    private static boolean updateIfLarger(int id, Conta newC) {
        delete(id);
        create(newC);
        return true;
    }

    private static boolean updateIfSameSize(Conta newC) {
        // dados
        boolean found = false;
        int id = newC.getIdConta();
        long pos = he.read(newC.getIdConta()); // se nao encontrar retorna -1
        try {
            // retorna falso se menor que 0
            if (pos < 0) {
                return false;
            }
            // passar para byte array
            byte[] ba = newC.toByteArray();
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            raf.seek(pos);
            raf.writeInt(ba.length);
            raf.write(ba);
            // atualizar b+
            //b.update(id, pos);
            // atualizar hash
            he.update(id, pos);
            // atualizar listas invertidas
            lNome.update(newC.getIdConta(), newC.getNomePessoa(), pos);
            lCidade.update(newC.getIdConta(), newC.getCidade(), pos);
            raf.close();
            found = true;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return found;
    }

    public static boolean update(Conta oldC, Conta newC) {
        // testar de algum elemento de lista invertida atualizou
        if (!oldC.getNomePessoa().equals(newC.getNomePessoa())) {
            lNome.delete(newC.getIdConta(), newC.getNomePessoa());
            lNome.create(newC.getNomePessoa(), newC.getIdConta(), he.read(newC.getIdConta()));
        }
        if (!oldC.getCidade().equals(newC.getCidade())) {
            lCidade.delete(newC.getIdConta(), newC.getCidade());
            lCidade.create(newC.getCidade(), newC.getIdConta(), he.read(newC.getIdConta()));
        }
        boolean updated = false;
        try {
            byte[] baOld = oldC.toByteArray();
            byte[] baNew = newC.toByteArray();
            if (baOld.length == baNew.length) {
                updated = updateIfSameSize(newC);
            } else {
                updated = updateIfLarger(oldC.getIdConta(), newC);
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return updated;
    }

    public static boolean delete(int id) {
        // dados
        boolean found = false;
        long pos = he.read(id); // se nao encontrar retorna -1
        try {
            // retorna falso se menor que 0
            if (pos < 0) {
                return false;
            }
            Conta c = read(id);
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            raf.seek(pos);
            raf.readInt();
            raf.writeBoolean(true);
            // deltar na arvore b+
            // b.delete();
            // deletar na hash
            he.delete(id);
            // deletar na lista
            lNome.delete(id, c.getNomePessoa());
            lCidade.delete(id, c.getCidade());
            // achado
            found = true;
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return found;
    }

    public boolean ordernarArquivo() {
        try {
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            Sort.intercalacaoComum();
            raf.close();
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void showAll() {
        // dados
        Conta c;
        short nEmails; // numero de emails
        String[] emails; // guardar emails
        int len;
        int pos = 0;
        try {
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            raf.seek(4);
            while (true) {
                try {
                    c = new Conta();
                    len = raf.readInt(); // skip nos 4 bytes de tamanho
                    if (!raf.readBoolean()) {
                        c.setIdConta(raf.readInt());
                        c.setNomePessoa(raf.readUTF());
                        // pegar emails
                        nEmails = raf.readShort();
                        emails = new String[nEmails];
                        for (int i = 0; i < nEmails; i++) {
                            emails[i] = raf.readUTF();
                        }
                        c.setEmail(emails); // colocar emails em c
                        c.setNomeUsuario(raf.readUTF());
                        c.setSenhaDAO(raf.readUTF());
                        c.setCpf(raf.readUTF());
                        c.setCidade(raf.readUTF());
                        c.setTransferenciasRealizadas(raf.readInt());
                        c.setSaldoConta(raf.readFloat());
                        System.out.println("[" + pos++ + "] " + c.toString());
                    } else {
                        raf.skipBytes(len - 1);
                    }
                } catch (EOFException e) {
                    break;
                }
            }
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void readPatternNome(String p) {
        // dados
        KMP kmp = new KMP(p.toUpperCase());
        Conta c;
        short nEmails; // numero de emails
        String[] emails; // guardar emails
        int len;
        int pos = 0;
        try {
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            raf.seek(4);
            while (true) {
                try {
                    c = new Conta();
                    len = raf.readInt(); // skip nos 4 bytes de tamanho
                    if (!raf.readBoolean()) {
                        c.setIdConta(raf.readInt());
                        c.setNomePessoa(raf.readUTF());
                        // pegar emails
                        nEmails = raf.readShort();
                        emails = new String[nEmails];
                        for (int i = 0; i < nEmails; i++) {
                            emails[i] = raf.readUTF();
                        }
                        c.setEmail(emails); // colocar emails em c
                        c.setNomeUsuario(raf.readUTF());
                        c.setSenhaDAO(raf.readUTF());
                        c.setCpf(raf.readUTF());
                        c.setCidade(raf.readUTF());
                        c.setTransferenciasRealizadas(raf.readInt());
                        c.setSaldoConta(raf.readFloat());
                        if (kmp.search(c.getNomePessoa().toUpperCase())) 
                            System.out.println("[" + pos++ + "] " + c.toString());
                        else
                            pos++; 
                    } else {
                        raf.skipBytes(len - 1);
                    }
                } catch (EOFException e) {
                    break;
                }
            }
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void readPatternCidade(String p) {
        // dados
        KMP kmp = new KMP(p.toUpperCase());
        Conta c;
        short nEmails; // numero de emails
        String[] emails; // guardar emails
        int len;
        int pos = 0;
        try {
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            raf.seek(4);
            while (true) {
                try {
                    c = new Conta();
                    len = raf.readInt(); // skip nos 4 bytes de tamanho
                    if (!raf.readBoolean()) {
                        c.setIdConta(raf.readInt());
                        c.setNomePessoa(raf.readUTF());
                        // pegar emails
                        nEmails = raf.readShort();
                        emails = new String[nEmails];
                        for (int i = 0; i < nEmails; i++) {
                            emails[i] = raf.readUTF();
                        }
                        c.setEmail(emails); // colocar emails em c
                        c.setNomeUsuario(raf.readUTF());
                        c.setSenhaDAO(raf.readUTF());
                        c.setCpf(raf.readUTF());
                        c.setCidade(raf.readUTF());
                        c.setTransferenciasRealizadas(raf.readInt());
                        c.setSaldoConta(raf.readFloat());
                        if (kmp.search(c.getCidade().toUpperCase())) 
                            System.out.println("[" + pos++ + "] " + c.toString());
                        else
                            pos++; 
                    } else {
                        raf.skipBytes(len - 1);
                    }
                } catch (EOFException e) {
                    break;
                }
            }
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void showAllDescriptografado() {
        // dados
        Conta c;
        short nEmails; // numero de emails
        String[] emails; // guardar emails
        int len;
        int pos = 0;
        try {
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            raf.seek(4);
            while (true) {
                try {
                    c = new Conta();
                    len = raf.readInt(); // skip nos 4 bytes de tamanho
                    if (!raf.readBoolean()) {
                        c.setIdConta(raf.readInt());
                        c.setNomePessoa(raf.readUTF());
                        // pegar emails
                        nEmails = raf.readShort();
                        emails = new String[nEmails];
                        for (int i = 0; i < nEmails; i++) {
                            emails[i] = raf.readUTF();
                        }
                        c.setEmail(emails); // colocar emails em c
                        c.setNomeUsuario(raf.readUTF());
                        c.setSenhaDAO(cifra.descriptografar(raf.readUTF()));
                        c.setCpf(raf.readUTF());
                        c.setCidade(raf.readUTF());
                        c.setTransferenciasRealizadas(raf.readInt());
                        c.setSaldoConta(raf.readFloat());
                        System.out.println("[" + pos++ + "] " + c.toString());
                    } else {
                        raf.skipBytes(len - 1);
                    }
                } catch (EOFException e) {
                    break;
                }
            }
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean checkNomeUsuario(String s) {
        // dados
        short nEmails; // numero de emails
        int len;
        boolean found = false;
        boolean lapide;
        long lPos;
        try {
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            raf.seek(4);
            while (!found) {
                try {
                    len = raf.readInt();
                    lPos = raf.getFilePointer();
                    lapide = raf.readBoolean();
                    if (!lapide) {
                        raf.readInt(); // pular id conta
                        raf.readUTF(); // pular nome da pessoa
                        nEmails = raf.readShort();
                        for (int i = 0; i < nEmails; i++) {
                            raf.readUTF();
                        }
                        if (s.equals(raf.readUTF())) {
                            found = true;
                        }
                    }
                    raf.seek(lPos + len);
                } catch (EOFException e) {
                    break;
                }
            }
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return found;
    }

    public static long getBlock(long pointer, Conta[] b) {
        // dados
        Conta c;
        short nEmails; // numero de emails
        String[] emails; // guardar emails
        int len;
        long pos = 0;
        int i = 0;
        try {
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            if (pointer < raf.length() - 1) {
                raf.seek(pointer);
                while (i < b.length) {
                    try {
                        c = new Conta();
                        len = raf.readInt(); // skip nos 4 bytes de tamanho
                        if (!raf.readBoolean()) {
                            c.setIdConta(raf.readInt());
                            c.setNomePessoa(raf.readUTF());
                            // pegar emails
                            nEmails = raf.readShort();
                            emails = new String[nEmails];
                            for (int j = 0; j < nEmails; j++) {
                                emails[j] = raf.readUTF();
                            }
                            c.setEmail(emails); // colocar emails em c
                            c.setNomeUsuario(raf.readUTF());
                            c.setSenhaDAO(raf.readUTF());
                            c.setCpf(raf.readUTF());
                            c.setCidade(raf.readUTF());
                            c.setTransferenciasRealizadas(raf.readInt());
                            c.setSaldoConta(raf.readFloat());
                            b[i++] = c.clone();
                        } else {
                            raf.skipBytes(len - 1);
                        }
                    } catch (EOFException e) {
                        break;
                    }
                }
                if (raf.getFilePointer() < raf.length() - 1) {
                    pos = raf.getFilePointer();
                } else {
                    pos = -1;
                }
            } else {
                pos = -1;
            }
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return pos;
    }

    public static int getNumber() {
        // dados
        int len;
        int n = 0;
        try {
            // RandomAccessFile
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            while (true) {
                try {
                    len = raf.readInt(); // skip nos 4 bytes de tamanho
                    if (!raf.readBoolean()) {
                        n++;
                        raf.skipBytes(len - 1);
                    } else {
                        raf.skipBytes(len - 1);
                    }
                } catch (EOFException e) {
                    break;
                }
            }
            raf.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return n;
    }

    public static void printBMais() {
        b.print();
    }

    public static void printHash() {
        he.print();
    }

    public static void printInvertidaNome(String s) {
        lNome.print(s);
    }

    public static void printInvertidaCidade(String s) {
        lCidade.print(s);
    }

    public static long readaddress(int id) {
        return he.read(id);
    }
}