package src;

/**
 * @author Daniel Valadares e Larissa Valadares
 */

import java.io.*;

public class Sort {
    private static long pointer; // long para armazenar ponteiro de raf
    private static String path = "data/";
    private static int len = 30;
    private static int files = 2;
    private static Conta[] bloco;

    private static void create2nTmpFiles() {
        for (int i = 0; i < files * 2; i++) {
            try {
                RandomAccessFile raf = new RandomAccessFile(path + "tmp" + (i + 1) + ".db", "rw");
                raf.close();
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void delete2nTmpFiles() {
        for (int i = 0; i < files * 2; i++) {
            File file = new File(path + "tmp" + (i + 1) + ".db");
            file.delete();
        }
    }

    private static void swap(int i, int j) {
        Conta tmp = bloco[i].clone();
        bloco[i] = bloco[j].clone();
        bloco[j] = tmp.clone();
    }

    private static void quicksort(int esq, int dir) {
        int i = esq, j = dir;
        int pivo = bloco[(dir + esq) / 2].getIdConta();
        while (i <= j) {
            while (bloco[i].getIdConta() < pivo)
                i++;
            while (bloco[j].getIdConta() > pivo)
                j--;
            if (i <= j) {
                swap(i, j);
                i++;
                j--;
            }
        }
        if (esq < j)
            quicksort(esq, j);
        if (i < dir)
            quicksort(i, dir);
    }

    private static void preencherTmp() {
        // dados
        byte[] ba;
        len = ContaDAO.getNumber();
        bloco = new Conta[len];
        pointer = 4;
        for (int i = 0; pointer > 0; i = (i + 1) % (files * 2)) {
            pointer = ContaDAO.getBlock(pointer, bloco);
            quicksort(0, len - 1);
            try {
                RandomAccessFile raf = new RandomAccessFile(path + "tmp" + (i + 1) + ".db", "rw");
                for (int j = 0; j < len; j++) {
                    if (bloco[j] != null) {
                        ba = bloco[j].toByteArray();
                        raf.seek(raf.length());
                        raf.write(ba);
                    }
                }
                raf.close();
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            bloco = null;
            bloco = new Conta[len];
        }
    }

    private static void intercalarComum() {
        // dados
        int registros = ContaDAO.getNumber();
        int size = len;
        int n = registros / size;
        int j = 0;

        ContaDAO.remakeFile("tmp" + 1 + ".db");

        for (int i = 0; j < n; i = (i + 1) % 2, j++) {
            try {
                RandomAccessFile raf = new RandomAccessFile(path + "tmp" + (i + 1) + ".db", "rw");
                raf.close();
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void intercalacaoComum() {
        // criar arquivos temporarios
        create2nTmpFiles();
        // preencher arquivos
        preencherTmp();
        // intercalacao
        intercalarComum();
        // deletar arquivos temporarios
        delete2nTmpFiles();
    }
}