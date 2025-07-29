package src;

/**
 * @author Daniel Valadares, Gustavo Silvestre e Larissa Valadares
 */

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

public class HashEstendido {
    private String pathDiretorio = "data/diretorioHash.db"; // path para teste com extesao de debuger do VScode
    // private String pathDiretorio = "../data/diretorioHash.db"; // path para
    // compilacao via compilador
    private String pathBucket = "data/bucketHash.db"; // path para teste com extesao de debuger do VScode
    // private String pathBucket = "../data/bucketHash.db"; // path para compilacao
    // via compilador
    private RandomAccessFile rafDiretorio;
    private RandomAccessFile rafBucket;
    private int nBucket;
    private Directory diretorio;

    public HashEstendido(int n) {
        // colcoar tamnho dos buckets
        nBucket = n;
        // setar path dos arquivos
        try {
            rafDiretorio = new RandomAccessFile(pathDiretorio, "rw");
            rafBucket = new RandomAccessFile(pathBucket, "rw");
            // criar se nao existir
            if (rafDiretorio.length() == 0 || rafBucket.length() == 0) {
                // Cria um novo diretorio, com profundidade de 0 bits (1 único elemento)
                diretorio = new Directory();
                byte[] bd = diretorio.toByteArray();
                rafDiretorio.write(bd);
                // Cria bucket
                Bucket b = new Bucket(nBucket);
                bd = b.toByteArray();
                rafBucket.seek(0);
                rafBucket.write(bd);
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean create(int chave, long dado) {
        try {
            // Carrega o diretorio
            byte[] bd = new byte[(int) rafDiretorio.length()];
            rafDiretorio.seek(0);
            rafDiretorio.read(bd);
            diretorio = new Directory();
            diretorio.fromByteArray(bd);
            // Identifica a hash do diretorio,
            int i = diretorio.hash(chave);
            // Recupera o Bucket
            long enderecoBucket = diretorio.endereco(i);
            Bucket b = new Bucket(nBucket);
            byte[] ba = new byte[b.getSize()];
            rafBucket.seek(enderecoBucket);
            rafBucket.read(ba);
            b.fromByteArray(ba);
            // Testa se a chave já não existe no Bucket
            if (b.read(chave) != -1)
                throw new Exception("Erro no Hash, chave já existente");
            // Testa se o Bucket já não está cheio
            if (!b.full()) {
                // Insere a chave no Bucket e o atualiza
                b.create(chave, dado);
                rafBucket.seek(enderecoBucket);
                rafBucket.write(b.toByteArray());
                return true;
            }
            // caso cheio continua o codigo

            // Testar se necessario duplicar diretorio
            byte pl = b.getLocalDeep(); // pLocal bucket
            if (pl >= diretorio.getGlobalDeep())
                diretorio.aumentarGlobal();
            byte pg = diretorio.getGlobalDeep(); // pGlobal diretorio
            // Cria os novos Buckets
            Bucket b1 = new Bucket(nBucket, pl + 1);
            rafBucket.seek(enderecoBucket);
            rafBucket.write(b1.toByteArray());
            Bucket b2 = new Bucket(nBucket, pl + 1);
            long newEndereco = rafBucket.length();
            rafBucket.seek(newEndereco);
            rafBucket.write(b2.toByteArray());
            // Atualizar os dados no diretorio
            int j = diretorio.hash2(chave, b.getLocalDeep());
            int aux = (int) Math.pow(2, pl);
            int max = (int) Math.pow(2, pg);
            boolean troca = false;
            for (int k = j; k < max; k += aux) {
                if (troca)
                    diretorio.atualizarEndereco(k, newEndereco);
                troca = !troca;
            }
            // Atualiza o arquivo do diretorio
            bd = diretorio.toByteArray();
            rafDiretorio.seek(0);
            rafDiretorio.write(bd);
            // Reinserir as chaves
            for (int k = 0; k < b.getN(); k++) {
                create(b.getChaves()[k], b.getDados()[k]);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return create(chave, dado);
    }

    public long read(int chave) {
        try {
            // Carrega o diretorio
            byte[] bd = new byte[(int) rafDiretorio.length()];
            rafDiretorio.seek(0);
            rafDiretorio.read(bd);
            diretorio = new Directory();
            diretorio.fromByteArray(bd);
            // Pegar hash
            int i = diretorio.hash(chave);
            // Recuperar o bucket
            long enderecoBucket = diretorio.endereco(i);
            Bucket b = new Bucket(nBucket);
            byte[] ba = new byte[b.getSize()];
            if (enderecoBucket > 0) {
                rafBucket.seek(enderecoBucket);
                rafBucket.read(ba);
                b.fromByteArray(ba);
                // retornar elemento dentro do bucket, caso nao exista retorna -1
                return b.read(chave);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public boolean update(int chave, long newDado) {
        try {
            // Carrega o diretorio
            byte[] bd = new byte[(int) rafDiretorio.length()];
            rafDiretorio.seek(0);
            rafDiretorio.read(bd);
            diretorio = new Directory();
            diretorio.fromByteArray(bd);
            // Identifica a hash do diretorio,
            int i = diretorio.hash(chave);
            // Recupera o Bucket
            long enderecoBucket = diretorio.endereco(i);
            Bucket b = new Bucket(nBucket);
            byte[] ba = new byte[b.getSize()];
            rafBucket.seek(enderecoBucket);
            rafBucket.read(ba);
            b.fromByteArray(ba);
            // atualizar o dado
            if (!b.update(chave, newDado))
                return false;
            // Atualiza o Bucket no arquivo
            rafBucket.seek(enderecoBucket);
            rafBucket.write(b.toByteArray());
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean delete(int chave) {
        try {
            // Carrega o diretorio
            byte[] bd = new byte[(int) rafDiretorio.length()];
            rafDiretorio.seek(0);
            rafDiretorio.read(bd);
            diretorio = new Directory();
            diretorio.fromByteArray(bd);
            // Achar Hash
            int i = diretorio.hash(chave);
            // Recupera o Bucket
            long enderecoBucket = diretorio.endereco(i);
            Bucket b = new Bucket(nBucket);
            byte[] ba = new byte[b.getSize()];
            rafBucket.seek(enderecoBucket);
            rafBucket.read(ba);
            b.fromByteArray(ba);
            // Deletar chave no bucket
            if (!b.delete(chave))
                return false;
            // Atualizar o Bucket no arquivo
            rafBucket.seek(enderecoBucket);
            rafBucket.write(b.toByteArray());
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public void print() {
        try {
            byte[] bd = new byte[(int) rafDiretorio.length()];
            rafDiretorio.seek(0);
            rafDiretorio.read(bd);
            diretorio = new Directory();
            diretorio.fromByteArray(bd);
            System.out.println("=======================================");
            System.out.println("Diretorio do Hash Estendido");
            System.out.println("=======================================");
            System.out.println("");
            System.out.println(diretorio.toString());
            System.out.println("");
            System.out.println("=======================================");
            System.out.println("Buckets do Hash Estendido");
            System.out.println("=======================================");
            rafBucket.seek(0);
            System.out.println("");
            while (rafBucket.getFilePointer() != rafBucket.length()) {
                Bucket b = new Bucket(nBucket);
                byte[] ba = new byte[b.getSize()];
                rafBucket.read(ba);
                b.fromByteArray(ba);
                System.out.println(b.toString());
                System.out.println("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void end() {
        try {
            rafDiretorio.close();
            rafBucket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean deleteFile() {
        File file1 = new File(pathBucket);
        boolean tmp = file1.delete();
        File file2 = new File(pathDiretorio);
        return tmp && file2.delete();
    }
}