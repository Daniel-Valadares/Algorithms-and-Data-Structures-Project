package src;

/**
 * @author Daniel Valadares e Larissa Valadares
 */

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

public class ArvoreBp {
    private int ordem;
    private RandomAccessFile raf;
    private String path = "data/arvoreBp.db"; // path para teste com extesao de debuger do VScode
    // private String path = "../data/arvoreBp.db"; // path para
    // compilacao via compilador

    // Variáveis usadas para recursao
    private int lastId;
    private long lastAddress;
    private long lastNode;
    private boolean cresceu;
    private boolean diminuiu;

    public ArvoreBp(int ordem) {
        // Inicializa os atributos da árvore
        this.ordem = ordem;
        try {
            // cria raf e escreve nulo para raiz caso vazio
            raf = new RandomAccessFile(path, "rw");
            if (raf.length() == 0)
                raf.writeLong(-1); // raiz empty
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isEmpty() {
        try {
            long raiz;
            raf.seek(0);
            raiz = raf.readLong();
            return raiz == -1;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean create(int id, long address) throws IOException {
        // Validação das chaves
        if (id < 0 || address < 0) {
            return false;
        }
        // Pegar raiz
        raf.seek(0);
        long nodeAddress = raf.readLong();
        lastId = id;
        lastAddress = address;
        lastNode = -1;
        cresceu = false;
        // inserir chaves
        boolean inserido = create(nodeAddress);
        // Testa a necessidade de criação de uma nova raiz.
        if (cresceu) {
            Node newNode = new Node(ordem);
            newNode.n = 1;
            newNode.id[0] = lastId;
            newNode.address[0] = lastAddress;
            newNode.pointers[0] = nodeAddress;
            newNode.pointers[1] = lastNode;
            // escrever nova Node
            raf.seek(raf.length());
            long raiz = raf.getFilePointer();
            raf.write(newNode.toByteArray());
            raf.seek(0);
            raf.writeLong(raiz);
        }
        return inserido;
    }

    private boolean create(long address) throws IOException {
        // retornar caso precise de crescer
        if (address == -1) {
            cresceu = true;
            lastNode = -1;
            return false;
        }
        // Carregar Node
        raf.seek(address);
        Node pa = new Node(ordem);
        byte[] buffer = new byte[pa.sizeRegistro];
        raf.read(buffer);
        pa.fromByteArray(buffer);
        // Buscar proximo ponteiro
        int i = 0;
        while (i < pa.n && (lastId > pa.id[i])) {
            i++;
        }
        // Testa se o registro já existe em uma folha
        if (i < pa.n && pa.pointers[0] == -1 && lastId == pa.id[i]) {
            cresceu = false;
            return false;
        }
        // Buscar local para insercao de forma recursiva
        boolean inserido;
        if (i == pa.n || lastId < pa.id[i] || (lastId == pa.id[i]))
            inserido = create(pa.pointers[i]);
        else
            inserido = create(pa.pointers[i + 1]);
        // Corrigir arvore apos insercao
        if (!cresceu) // caso n tenha crescido encerrar
            return inserido;
        if (pa.n < ordem - 1) {
            // Puxa todos elementos para a direita
            for (int j = pa.n; j > i; j--) {
                pa.id[j] = pa.id[j - 1];
                pa.address[j] = pa.address[j - 1];
                pa.pointers[j + 1] = pa.pointers[j];
            }
            // Inserir um novo elemento
            pa.id[i] = lastId;
            pa.address[i] = lastAddress;
            pa.pointers[i + 1] = lastNode;
            pa.n++;
            // Escreve a página atualizada no raf
            raf.seek(address);
            raf.write(pa.toByteArray());
            // Encerra o processo de crescimento e retorna
            cresceu = false;
            return true;
        }
        // Caso os elementos nao caibam na Node
        Node np = new Node(ordem);
        // Copia a metade superior dos elementos para a nova página,
        int meio = (ordem - 1) / 2;
        for (int j = 0; j < ((ordem - 1) - meio); j++) {
            // copia o elemento
            np.id[j] = pa.id[j + meio];
            np.address[j] = pa.address[j + meio];
            np.pointers[j + 1] = pa.pointers[j + meio + 1];
            // limpa o espaço liberado
            pa.id[j + meio] = 0;
            pa.address[j + meio] = 0;
            pa.pointers[j + meio + 1] = -1;
        }
        np.pointers[0] = pa.pointers[meio];
        np.n = (ordem - 1) - meio;
        pa.n = meio;
        // Testa o lado de inserção
        // Caso 1 - Novo registro deve ficar na página da esquerda
        if (i <= meio) {
            // Puxa todos os elementos para a direita
            for (int j = meio; j > 0 && j > i; j--) {
                pa.id[j] = pa.id[j - 1];
                pa.address[j] = pa.address[j - 1];
                pa.pointers[j + 1] = pa.pointers[j];
            }
            // Insere o novo elemento
            pa.id[i] = lastId;
            pa.address[i] = lastAddress;
            pa.pointers[i + 1] = lastNode;
            pa.n++;
            // Se a address for folha, seleciona o primeiro elemento da página
            if (pa.pointers[0] == -1) {
                lastId = np.id[0];
                lastAddress = np.address[0];
            } else {
                lastId = pa.id[pa.n - 1];
                lastAddress = pa.address[pa.n - 1];
                pa.id[pa.n - 1] = 0;
                pa.address[pa.n - 1] = 0;
                pa.pointers[pa.n] = -1;
                pa.n--;
            }
        } else { // Caso 2 - Novo registro deve ficar na página da direita
            int j;
            for (j = (ordem - 1) - meio; j > 0
                    && (lastId < np.id[j - 1] || (lastId == np.id[j - 1])); j--) {
                np.id[j] = np.id[j - 1];
                np.address[j] = np.address[j - 1];
                np.pointers[j + 1] = np.pointers[j];
            }
            np.id[j] = lastId;
            np.address[j] = lastAddress;
            np.pointers[j + 1] = lastNode;
            np.n++;
            // Seleciona o primeiro elemento da página da direita para ser promovido
            lastId = np.id[0];
            lastAddress = np.address[0];
            // Se não for folha, remove o elemento promovido da página
            if (pa.pointers[0] != -1) {
                for (j = 0; j < np.n - 1; j++) {
                    np.id[j] = np.id[j + 1];
                    np.address[j] = np.address[j + 1];
                    np.pointers[j] = np.pointers[j + 1];
                }
                np.pointers[j] = np.pointers[j + 1];
                // apaga o último elemento
                np.id[j] = 0;
                np.address[j] = 0;
                np.pointers[j + 1] = -1;
                np.n--;
            }

        }
        // Atualizar ponteiros caso necessário
        if (pa.pointers[0] == -1) {
            np.next = pa.next;
            pa.next = raf.length();
        }
        // Grava as páginas no raf
        lastNode = raf.length();
        raf.seek(lastNode);
        raf.write(np.toByteArray());
        raf.seek(address);
        raf.write(pa.toByteArray());
        return true;
    }

    public boolean update(int id, long address) throws IOException {
        // Validação das chaves
        if (id < 0 || address < 0) {
            return false;
        }
        // Pegar raiz
        raf.seek(0);
        long nodeAddress = raf.readLong();
        lastId = id;
        lastAddress = address;
        lastNode = -1;
        // inserir chaves
        boolean atualizado = update(nodeAddress);
        // Testa a necessidade de criação de uma nova raiz.
        return atualizado;
    }

    private boolean update(long address) throws IOException {
        // Carregar Node
        raf.seek(address);
        Node pa = new Node(ordem);
        byte[] buffer = new byte[pa.sizeRegistro];
        raf.read(buffer);
        pa.fromByteArray(buffer);
        // Buscar proximo ponteiro
        int i = 0;
        while (i < pa.n && (lastId > pa.id[i])) {
            i++;
        }
        // Testa se o registro existe em uma folha
        if (i < pa.n && pa.pointers[0] == -1 && lastId == pa.id[i]) {
            return true;
        }
        // Buscar local de forma recursiva
        if (i == pa.n || lastId < pa.id[i] || (lastId == pa.id[i]))
            return update(pa.pointers[i]);
        else
            return update(pa.pointers[i + 1]);
    }

    public long[] read(int id1, int id2) throws IOException {
        // Recupera a raiz da árvore
        long raiz;
        raf.seek(0);
        raiz = raf.readLong();
        // Executa a busca recursiva
        if (raiz != -1)
            return read(id1, id2, raiz);
        else
            return null;
    }

    private long[] read(int id1, int id2, long address) throws IOException {
        // Caso não seja achado
        if (address == -1)
            return null;
        // Reconstrói a address passada como referência a partir
        raf.seek(address);
        Node pa = new Node(ordem);
        byte[] buffer = new byte[pa.sizeNode];
        raf.read(buffer);
        pa.fromByteArray(buffer);
        // Encontra o ponto em que a chave deve estar na página
        int i = 0;
        while (i < pa.n && id1 > pa.id[i]) {
            i++;
        }
        // Testar se eh folha
        if (i < pa.n && pa.pointers[0] == -1 && id1 >= pa.id[i]) {
            // Cria a lista de retorno e insere as chaves secundárias encontradas
            ArrayList<Long> lista = new ArrayList<Long>();
            while (id1 <= pa.id[i]) {
                // Criar um ArrayList para retorno e inserir chaves validas
                if (id1 <= pa.id[i] && pa.id[i] <= id2)
                    lista.add(pa.address[i]);
                i++;
                // Se chegar ao fim da folha, então avança para a folha seguinte
                if (i == pa.n) {
                    if (pa.next == -1)
                        break;
                    raf.seek(pa.next);
                    raf.read(buffer);
                    pa.fromByteArray(buffer);
                    i = 0;
                }
            }
            // Construir o vetor de resposta
            long[] resposta = new long[lista.size()];
            for (int j = 0; j < lista.size(); j++)
                resposta[j] = lista.get(j);
            return resposta;
        }
        // Caso nao tenha sido encontrado, testar nas proximas folhas
        else if (i == pa.n && pa.pointers[0] == -1) {
            // Testa se existe
            if (pa.next == -1)
                return null;
            // Lê a próxima folha
            raf.seek(pa.next);
            raf.read(buffer);
            pa.fromByteArray(buffer);
            i = 0;
            if (id1 <= pa.id[i] && pa.id[i] <= id2) {
                // Cria a lista de retorno
                ArrayList<Long> lista = new ArrayList<Long>();
                while (id1 <= pa.id[i]) {
                    if (id1 <= pa.id[i] && pa.id[i] <= id2)
                        lista.add(pa.address[i]);
                    i++;
                    if (i == pa.n) {
                        if (pa.next == -1)
                            break;
                        raf.seek(pa.next);
                        raf.read(buffer);
                        pa.fromByteArray(buffer);
                        i = 0;
                    }
                }
                // Constrói o vetor de respostas
                long[] resposta = new long[lista.size()];
                for (int j = 0; j < lista.size(); j++)
                    resposta[j] = lista.get(j);
                return resposta;
            } else
                return null;
        }
        // Chamar busca recursiva pela árvore
        if (i == pa.n || id1 <= pa.id[i])
            return read(id1, id2, pa.pointers[i]);
        else
            return read(id1, id2, pa.pointers[i + 1]);
    }

    public boolean delete(int id, long address) throws IOException {
        // Encontra a raiz da árvore
        raf.seek(0);
        long pagina = raf.readLong();
        // Setar variavel global
        diminuiu = false;
        // Chamar recursividade
        boolean excluido = delete(id, address, pagina);
        // Em caso de diminuir a raiz
        if (excluido && diminuiu) {
            raf.seek(pagina);
            Node pa = new Node(ordem);
            byte[] buffer = new byte[pa.sizeNode];
            raf.read(buffer);
            pa.fromByteArray(buffer);
            // Se a página tiver 0 elementos
            if (pa.n == 0) {
                raf.seek(0);
                raf.writeLong(pa.pointers[0]);
            }
        }
        return excluido;
    }

    private boolean delete(int id, long address, long nodeAddress) throws IOException {
        boolean excluido = false;
        int diminuido;
        // Testa se o registro não foi encontrado
        if (nodeAddress == -1) {
            diminuiu = false;
            return false;
        }
        // Carregar registro
        raf.seek(nodeAddress);
        Node pa = new Node(ordem);
        byte[] buffer = new byte[pa.sizeNode];
        raf.read(buffer);
        pa.fromByteArray(buffer);
        // Procurar pagina
        int i = 0;
        while (i < pa.n && (id > pa.id[i] || (id == pa.id[i] && address > pa.address[i]))) {
            i++;
        }
        // Chaves encontradas em uma folha
        if (i < pa.n && pa.pointers[0] == -1 && id == pa.id[i] && address == pa.address[i]) {
            // Puxa todas os elementos seguintes para uma posição anterior
            int j;
            for (j = i; j < pa.n - 1; j++) {
                pa.id[j] = pa.id[j + 1];
                pa.address[j] = pa.address[j + 1];
            }
            pa.n--;
            // limpa o último elemento
            pa.id[pa.n] = 0;
            pa.address[pa.n] = -1;
            // Atualiza o registro da página no raf
            raf.seek(nodeAddress);
            raf.write(pa.toByteArray());
            // Verificar se precisa de fusao
            diminuiu = pa.n < (ordem - 1) / 2;
            return true;
        }
        // Buscar nova pagina se n encontrado
        if (i == pa.n || id < pa.id[i] || (id == pa.id[i] && address < pa.address[i])) {
            excluido = delete(id, address, pa.pointers[i]);
            diminuido = i;
        } else {
            excluido = delete(id, address, pa.pointers[i + 1]);
            diminuido = i + 1;
        }
        // Testa se há necessidade de fusão de páginas
        if (diminuiu) {
            // Carrega a página filho que ficou com menos que o minimo
            long paginaFilho = pa.pointers[diminuido];
            Node pFilho = new Node(ordem);
            raf.seek(paginaFilho);
            raf.read(buffer);
            pFilho.fromByteArray(buffer);
            // Cria uma página para o irmão
            long paginaIrmao;
            Node pIrmao;
            // Tentar fundir
            if (diminuido > 0) {
                // Carregar irmao da esquerda
                paginaIrmao = pa.pointers[diminuido - 1];
                pIrmao = new Node(ordem);
                raf.seek(paginaIrmao);
                raf.read(buffer);
                pIrmao.fromByteArray(buffer);
                // Testa se o irmao pode ceder algum registro
                if (pIrmao.n > (ordem - 1) / 2) {
                    // Move todos os elementos do filho
                    for (int j = pFilho.n; j > 0; j--) {
                        pFilho.id[j] = pFilho.id[j - 1];
                        pFilho.address[j] = pFilho.address[j - 1];
                        pFilho.pointers[j + 1] = pFilho.pointers[j];
                    }
                    pFilho.pointers[1] = pFilho.pointers[0];
                    pFilho.n++;
                    // Se for folha, copia o elemento do irmao
                    if (pFilho.pointers[0] == -1) {
                        pFilho.id[0] = pIrmao.id[pIrmao.n - 1];
                        pFilho.address[0] = pIrmao.address[pIrmao.n - 1];
                    }
                    // Se não for folha, rotaciona os elementos, descendo o elemento do pai
                    else {
                        pFilho.id[0] = pa.id[diminuido - 1];
                        pFilho.address[0] = pa.address[diminuido - 1];
                    }
                    // Copia o elemento do irmao para o pai
                    pa.id[diminuido - 1] = pIrmao.id[pIrmao.n - 1];
                    pa.address[diminuido - 1] = pIrmao.address[pIrmao.n - 1];
                    // Reduz o elemento no irmao
                    pFilho.pointers[0] = pIrmao.pointers[pIrmao.n];
                    pIrmao.n--;
                    diminuiu = false;
                }
                // Se não puder ceder, faz a fusão dos dois irmaos
                else {
                    // Testar se folha
                    if (pFilho.pointers[0] != -1) {
                        pIrmao.id[pIrmao.n] = pa.id[diminuido - 1];
                        pIrmao.address[pIrmao.n] = pa.address[diminuido - 1];
                        pIrmao.pointers[pIrmao.n + 1] = pFilho.pointers[0];
                        pIrmao.n++;
                    }
                    // Copia todos os registros para o irmao da esquerda
                    for (int j = 0; j < pFilho.n; j++) {
                        pIrmao.id[pIrmao.n] = pFilho.id[j];
                        pIrmao.address[pIrmao.n] = pFilho.address[j];
                        pIrmao.pointers[pIrmao.n + 1] = pFilho.pointers[j + 1];
                        pIrmao.n++;
                    }
                    pFilho.n = 0;
                    // Se as páginas forem folhas, copia o ponteiro para a folha seguinte
                    if (pIrmao.pointers[0] == -1)
                        pIrmao.next = pFilho.next;
                    // puxa os registros no pai
                    int j;
                    for (j = diminuido - 1; j < pa.n - 1; j++) {
                        pa.id[j] = pa.id[j + 1];
                        pa.address[j] = pa.address[j + 1];
                        pa.pointers[j + 1] = pa.pointers[j + 2];
                    }
                    pa.id[j] = 0;
                    pa.address[j] = 0;
                    pa.pointers[j + 1] = -1;
                    pa.n--;
                    diminuiu = pa.n < (ordem - 1) / 2; // testa se o pai também ficou sem o número mínimo de elementos
                }
            }
            // Faz a fusão com o irmão direito
            else {
                // Carrega o irmão
                paginaIrmao = pa.pointers[diminuido + 1];
                pIrmao = new Node(ordem);
                raf.seek(paginaIrmao);
                raf.read(buffer);
                pIrmao.fromByteArray(buffer);
                // Testa se o irmao pode ceder algum elemento
                if (pIrmao.n > (ordem - 1) / 2) {
                    // Se for folha
                    if (pFilho.pointers[0] == -1) {
                        // copia o elemento do irmao
                        pFilho.id[pFilho.n] = pIrmao.id[0];
                        pFilho.address[pFilho.n] = pIrmao.address[0];
                        pFilho.pointers[pFilho.n + 1] = pIrmao.pointers[0];
                        pFilho.n++;
                        // sobe o proximo elemento do irmão
                        pa.id[diminuido] = pIrmao.id[1];
                        pa.address[diminuido] = pIrmao.address[1];
                    }
                    // Se não for folha, rotaciona os elementos
                    else {
                        // Copia o elemento do pai, com o ponteiro esquerdo do irmão
                        pFilho.id[pFilho.n] = pa.id[diminuido];
                        pFilho.address[pFilho.n] = pa.address[diminuido];
                        pFilho.pointers[pFilho.n + 1] = pIrmao.pointers[0];
                        pFilho.n++;
                        // Sobe o elemento esquerdo do irmão para o pai
                        pa.id[diminuido] = pIrmao.id[0];
                        pa.address[diminuido] = pIrmao.address[0];
                    }
                    // move todos os registros no irmão para a esquerda
                    int j;
                    for (j = 0; j < pIrmao.n - 1; j++) {
                        pIrmao.id[j] = pIrmao.id[j + 1];
                        pIrmao.address[j] = pIrmao.address[j + 1];
                        pIrmao.pointers[j] = pIrmao.pointers[j + 1];
                    }
                    pIrmao.pointers[j] = pIrmao.pointers[j + 1];
                    pIrmao.n--;
                    diminuiu = false;
                }
                // Se não puder ceder, faz a fusão dos dois irmãos
                else {
                    // Se a página reduzida não for folha
                    if (pFilho.pointers[0] != -1) {
                        pFilho.id[pFilho.n] = pa.id[diminuido];
                        pFilho.address[pFilho.n] = pa.address[diminuido];
                        pFilho.pointers[pFilho.n + 1] = pIrmao.pointers[0];
                        pFilho.n++;
                    }
                    // Copia todos os registros do irmão da direita
                    for (int j = 0; j < pIrmao.n; j++) {
                        pFilho.id[pFilho.n] = pIrmao.id[j];
                        pFilho.address[pFilho.n] = pIrmao.address[j];
                        pFilho.pointers[pFilho.n + 1] = pIrmao.pointers[j + 1];
                        pFilho.n++;
                    }
                    pIrmao.n = 0; // aqui o endereço do irmão poderia ser incluido em uma lista encadeada no
                                  // cabeçalho, indicando os espaços reaproveitáveis
                    // Se a página for folha, copia o ponteiro para a próxima página
                    pFilho.next = pIrmao.next;
                    // puxa os registros no pai
                    for (int j = diminuido; j < pa.n - 1; j++) {
                        pa.id[j] = pa.id[j + 1];
                        pa.address[j] = pa.address[j + 1];
                        pa.pointers[j + 1] = pa.pointers[j + 2];
                    }
                    pa.n--;
                    diminuiu = pa.n < (ordem - 1) / 2; // testa se o pai também ficou sem o número mínimo de elementos
                }
            }
            // Atualiza todos os registros
            raf.seek(nodeAddress);
            raf.write(pa.toByteArray());
            raf.seek(paginaFilho);
            raf.write(pFilho.toByteArray());
            raf.seek(paginaIrmao);
            raf.write(pIrmao.toByteArray());
        }
        return excluido;
    }

    public void print() {
        try {
            System.out.println("=======================================");
            System.out.println("Arvore Hash");
            System.out.println("=======================================");
            System.out.println("");
            raf.seek(0);
            System.out.println("Raiz da Arvore: " + raf.readLong());
            System.out.println("");
            while (raf.getFilePointer() != raf.length()) {
                Node n = new Node(4);
                byte[] ba = new byte[n.sizeNode];
                raf.read(ba);
                n.fromByteArray(ba);
                System.out.println(n.toString());
            }
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
        File file = new File(path);
        return file.delete();
    }
}
