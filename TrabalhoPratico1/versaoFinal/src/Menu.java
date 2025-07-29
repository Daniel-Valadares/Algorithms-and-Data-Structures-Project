package src;

/**
 * @author Daniel Valadares e Larissa Valadares
 */

import java.util.Scanner;
import java.io.IOException;

public class Menu {
    private static Scanner sc = new Scanner(System.in, "UTF-8");

    // funções que validam os dados informados
    private static boolean isNomeValid(String s) {
        // confere se tem nome e sobrenome
        if (s.contains(" ")) {
            System.out.println("Registro de nome de pessoa aceito!\n");
            return true;
        } else {
            System.out.println("Favor inserir nome e sobrenome! Insira novamente!\n");
            return false;
        }
    }

    private static boolean isEmailValid(String s) {
        // confere se o dado é do tipo email
        if (s.contains("@")) {
            System.out.println("Registro de email aceito!\n");
            return true;
        } else {
            System.out.println("Insercao invalida! Insira novamente!\n");
            return false;
        }
    }

    private static boolean isNumberEmailValid(short x) {
        // confere se o valor é válido
        if (x > 0) {
            return true;
        } else {
            System.out.println("Numero invalido! Insira novamente!\n");
            return false;
        }
    }

    private static boolean isNomeUsuarioValid(String s) {
        // verifica se o nome já foi utilizado
        if (!ContaDAO.checkNomeUsuario(s)) {
            System.out.println("Registro de nome de usuario aceito!\n");
            return true;
        } else {
            System.out.println("Nome de usuario ja registrado! Insira um novo!\n");
            return false;
        }
    }

    private static boolean isSenhaValid(String s) {
        // confere se o dado tem mais de 3 caracteres
        if (s.length() > 3) {
            System.out.println("Registro de senha aceito!\n");
            return true;
        } else {
            System.out.println("Senha muito fraca! Insira uma senha com mais de 3 caracteres!\n");
            return false;
        }
    }

    private static boolean isCpfValid(String s) {
        // confere se o dado tem 11 caracteres
        if (s.length() == 11) {
            System.out.println("Registro de CPF aceito!\n");
            return true;
        } else {
            System.out.println("Numero de caracteres invalidos em CPF! Insira os 11 dígitos do CPF!\n");
            return false;
        }
    }

    private static boolean isCidadeValid(String s) {
        // verifica se algum dado foi informado
        if (!s.isEmpty()) {
            System.out.println("Registro de cidade aceito!\n");
            return true;
        } else {
            System.out.println("Registro vazio! Insira novamente!\n");
            return false;
        }
    }

    private static boolean isSaldoValid(Float x) {
        // verifica se algum dado foi informado
        if (x != null) {
            System.out.println("Registro de saldo aceito!\n");
            return true;
        } else {
            System.out.println("Registro vazio! Insira novamente!\n");
            return false;
        }
    }

    private static boolean isValidForInvertida(String s) {
        // verifica se algum dado foi informado
        if (s != null && !s.contains(" ")) {
            System.out.println("Registro de pesquisa na lista aceito!\n");
            return true;
        } else {
            System.out.println("Registro vazio ou contendo espaços! Insira novamente apenas uma palavra!");
            System.out.println("Ex: Para pesquisar Belo Horizonte, insira ou \"Belo\", ou \"Horizonte\"");
            return false;
        }
    }

    private static boolean isTestValid(String s) {
        return !ContaDAO.checkNomeUsuario(s);
    }

    // procura o id no banco de dados
    private static boolean isIdFound(int x) {
        if (x > 0) {
            System.out.println("Registro de ID encontrado!\n");
            return true;
        } else {
            System.out.println("Procure por um ID valido! Insira novamente!\n");
            return false;
        }
    }

    // verifica se o saldo é suficiente para abrir a conta
    private static boolean isSaldoEnough(Float x, Float amount) {
        if (amount == 0.0F) {
            System.out.println("Valor nulo inserido encerando operacao!\n");
            return true;
        } else if (x >= amount) {
            System.out.println("Saldo suficiente!\n");
            return true;
        } else {
            System.out.println("Saldo insuficiente! Insira uma valor valido, ou 0 parar encerar a operacao!\n");
            return false;
        }
    }

    // funções que inserem os dados na conta
    private static void insertNome(Conta c) {
        // dados
        String nome;
        // inserir
        do {
            System.out.print("Insira seu nome completo: ");
            nome = sc.nextLine();
        } while (!isNomeValid(nome));
        c.setNomePessoa(nome);
    }

    private static void insertEmails(Conta c) {
        // dados
        short nEmail;
        String[] emails;
        // inserir
        do {
            System.out.print("Insira a quantidade de e-mails, para registro, mínimo 1: ");
            try {
                nEmail = Short.parseShort(sc.nextLine());
            } catch (NumberFormatException e) {
                nEmail = -1;
            }
        } while (!isNumberEmailValid(nEmail));
        emails = new String[nEmail];
        for (Short i = 0; i < nEmail; i++) {
            do {
                System.out.print("Insira um e-mail: ");
                emails[i] = sc.nextLine();
            } while (!isEmailValid(emails[i]));
        }
        c.setEmail(emails);
    }

    private static void insertNomeUsuario(Conta c) {
        // dados
        String nomeUsuario;
        // inserir
        do {
            System.out.print("Insira um nome de usuario: ");
            nomeUsuario = sc.nextLine();
        } while (!isNomeUsuarioValid(nomeUsuario));
        c.setNomeUsuario(nomeUsuario);
    }

    private static void insertSenha(Conta c) {
        // dados
        String senha;
        // inserir
        do {
            System.out.print("Insira uma senha para a conta: ");
            senha = sc.nextLine();
        } while (!isSenhaValid(senha));
        c.setSenha(senha);

    }

    private static void insertCpf(Conta c) {
        // dados
        String cpf;
        // inserir
        do {
            System.out.print("Insira seu CPF: ");
            cpf = sc.nextLine();
        } while (!isCpfValid(cpf));
        c.setCpf(cpf);

    }

    private static void insertCidade(Conta c) {
        // dados
        String cidade;
        // inserir
        do {
            System.out.print("Insira sua cidade: ");
            cidade = sc.nextLine();
        } while (!isCidadeValid(cidade));
        c.setCidade(cidade);

    }

    private static void insertSaldo(Conta c) {
        // dados
        Float saldoConta;
        // inserir
        do {
            System.out.print("Insira seu saldo inicial: ");
            try {
                saldoConta = Float.parseFloat(sc.nextLine());
            } catch (NumberFormatException e) {
                saldoConta = null;
            }
        } while (!isSaldoValid(saldoConta));
        c.setSaldoConta(saldoConta);
    }

    // cria uma conta de teste
    private static void createTestAccount(Conta c) {
        if (isTestValid(c.getNomeUsuario())) {
            System.out.println(c.toString());
            ContaDAO.create(c);
        } else {
            System.out.println(c.toString() + "| Conta de teste ja inserida, excluindo novo registro...");
        }
    }

    private static void enter() {
        try {
            System.out.print("Aperte ENTER para continuar...");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clear() {
        try {
            String operatingSystem = System.getProperty("os.name");

            if (operatingSystem.contains("Windows")) {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                Process startProcess = pb.inheritIO().start();
                startProcess.waitFor();
            } else {
                ProcessBuilder pb = new ProcessBuilder("clear");
                Process startProcess = pb.inheritIO().start();

                startProcess.waitFor();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // cria a conta, setando os dados coletados
    private static void criarConta() {
        // dados
        Conta c = new Conta();
        String tmp;
        boolean isOk;
        // inserir dados em objeto c
        c.setIdConta(ContaDAO.newId());
        c.setTransferenciasRealizadas(0);
        System.out.println("");
        do {
            insertNome(c);
            insertEmails(c);
            insertNomeUsuario(c);
            insertSenha(c);
            insertCpf(c);
            insertCidade(c);
            insertSaldo(c);
            System.out.println("Registro gerado: ");
            System.out.println("");
            System.out.println(
                    "ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
            System.out.println("");
            System.out.println(c.toString() + "\n");
            System.out.print("Seus dados estão de acordo com o registro? [Y/n]: ");
            tmp = sc.nextLine();
            System.out.println("");
            if (tmp.contains("n") || tmp.contains("N")) {
                isOk = false;
                System.out.println("Reiniciando processo de criacao de conta...");
            } else {
                isOk = true;
            }
        } while (!isOk);
        ContaDAO.create(c);
        enter();
    }

    /*
     * faz a transferência do valor, validando os IDs inseridos, verificando se o
     * saldo
     * da conta onde se deseja retirar o valor é suficiente para fazer a alteração e
     * aumentando o saldo da conta onde se deseja aumentar tal valor
     */
    private static void realizarTransferencia() {
        // dados
        Conta transfer;
        Conta receiver;
        Float amount;
        int idT;
        int idR;
        do {
            System.out.print("Insira o ID da conta transferidora: ");
            try {
                idT = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                idT = -1;
            }
            transfer = ContaDAO.read(idT);
        } while (!isIdFound(transfer.getIdConta()));

        do {
            System.out.print("Insira o ID da conta recebedora: ");
            try {
                idR = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                idR = -1;
            }
            idR = idR != idT ? idR : -1;
            receiver = ContaDAO.read(idR);
        } while (!isIdFound(receiver.getIdConta()));

        System.out.println("Ambas as contas encontradas:");
        System.out.println("");
        System.out.println("ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
        System.out.println("");
        System.out.println(transfer.toString());
        System.out.println(receiver.toString());

        do {
            System.out.print("Insira o valor da transferência: ");
            amount = Float.parseFloat(sc.nextLine());
        } while (!isSaldoEnough(transfer.getSaldoConta(), amount));

        if (amount != 0.0F) {
            transfer.transfer(amount, receiver);
            System.out.println("Transferência realizada com sucesso! ");
            System.out.println("Atualizando registros...");
            System.out.println("");
            System.out.println(
                    "ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
            System.out.println("");
            System.out.println(transfer.toString());
            System.out.println(receiver.toString());
            System.out.println("");
        }
        enter();
    }

    private static void menuLeitura() {
        int x;
        do {
            System.out.println("Escolha uma das seguintes opções para atualizar: ");
            System.out.println("");
            System.out.println(" [1] Ler um registro de forma sequencial");
            System.out.println(" [2] Ler B+");
            System.out.println(" [3] Ler um registro pela Hash Estendida");
            System.out.println(" [4] Ler todos os registros que contem determinada palavra em nome");
            System.out.println(" [5] Ler todos os registros que contem determinada palavra em cidade");
            System.out.println(" [0] Voltar ao menu principal");
            System.out.println("");
            System.out.print("Insira a seguir o valor da opção preferida: ");
            try {
                x = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                x = -1;
            }
            System.out.println("");
            switch (x) {
                case 0:
                    break;
                case 1:
                    lerRegistro();
                    clear();
                    break;
                case 2:
                    lerBMais();
                    clear();
                    break;
                case 3:
                    lerHash();
                    clear();
                    break;
                case 4:
                    lerNome();
                    clear();
                    break;
                case 5:
                    lerCidade();
                    clear();
                    break;
                default:
                    System.out.println("Valor invalido, favor inserir um valor valido!");
                    break;
            }
        } while (x != 0);
        System.out.println("");
        enter();
    }

    // procura a conta, através do id informado, e mostra os dados na tela
    private static void lerRegistro() {
        // dados
        Conta c;
        int id;
        do {
            System.out.print("Insira o ID da conta para ler registro: ");
            try {
                id = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                id = -1;
            }
            c = ContaDAO.read(id);
        } while (!isIdFound(c.getIdConta()));
        System.out.println("Registro encontrado: ");
        System.out.println("");
        System.out.println("ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
        System.out.println("");
        System.out.println(c.toString());
        System.out.println("");
        enter();
    }

    private static void lerBMais() {
        // dados
        /*
        Conta c;
        int id;
        do {
            System.out.print("Insira o menor ID da conta para ler registro: ");
            try {
                id = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                id = -1;
            }
            c = ContaDAO.readHash(id);
        } while (!isIdFound(c.getIdConta()));
        System.out.println("Registro encontrado: ");
        System.out.println("");
        System.out.println("ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
        System.out.println("");
        System.out.println(c.toString());
        System.out.println("");
        */
        enter();
    }

    private static void lerHash() {
        // dados
        Conta c;
        int id;
        do {
            System.out.print("Insira o ID da conta para ler registro: ");
            try {
                id = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                id = -1;
            }
            c = ContaDAO.readHash(id);
        } while (!isIdFound(c.getIdConta()));
        /*/
        long[] tmp = ContaDAO.invertidaNomeaddressArray(nome);
        if (tmp == null) {
            System.out.println("Nenhum registro com \"" + nome + "\" encontrado...");
        } else {
            System.out.println("Registro encontrado: ");
            System.out.println("");
            System.out.println(
                    "ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
            System.out.println("");
            for (int i = 0; i < tmp.length; i++) {
                System.out.println(ContaDAO.readaddress(tmp[i]).toString());
            }
        }
        System.out.println("");
        enter();
        */
    }

    private static void lerNome() {
        // dados
        String nome;
        do {
            System.out.print("Insira uma palavra para pesquisa: ");
            nome = sc.nextLine();
        } while (!isValidForInvertida(nome));
        long[] tmp = ContaDAO.invertidaNomeaddressArray(nome);
        if (tmp == null) {
            System.out.println("Nenhum registro com \"" + nome + "\" encontrado...");
        } else {
            System.out.println("Registro encontrado: ");
            System.out.println("");
            System.out.println(
                    "ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
            System.out.println("");
            for (int i = 0; i < tmp.length; i++) {
                System.out.println(ContaDAO.readAddress(tmp[i]).toString());
            }
        }
        System.out.println("");
        enter();
    }

    private static void lerCidade() {
        // dados
        String cidade;
        do {
            System.out.print("Insira uma palavra para pesquisa: ");
            cidade = sc.nextLine();
        } while (!isValidForInvertida(cidade));
        long[] tmp = ContaDAO.invertidaCidadeaddressArray(cidade);
        if (tmp == null) {
            System.out.println("Nenhum registro com \"" + cidade + "\" encontrado...");
        } else {
            System.out.println("Registro encontrado: ");
            System.out.println("");
            System.out.println("ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
            System.out.println("");
            for (int i = 0; i < tmp.length; i++) {
                System.out.println(ContaDAO.readAddress(tmp[i]).toString());
            }
        }
        System.out.println("");
        enter();
    }

    // atualiza o dado desejado, verificando o id informado, e reinserindo o novo
    // valor
    /**
     * 
     */
    private static void atualizarRegistro() {
        // dados
        Conta oldC;
        Conta newC;
        int id;
        int x;

        do {
            System.out.print("Insira o ID da conta para atualizar registro: ");
            try {
                id = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                id = -1;
            }
            oldC = ContaDAO.read(id);
        } while (!isIdFound(oldC.getIdConta()));
        // crir nova conta para atualizacoes
        newC = oldC.clone();
        // menu de opcoes de atualizacao
        do {
            System.out.println("Escolha uma das seguintes opções para atualizar: ");
            System.out.println("");
            System.out.println(" [1] Atualizar nome da pessoa");
            System.out.println(" [2] Atualizar e-mails");
            System.out.println(" [3] Atualizar nome de usuário");
            System.out.println(" [4] Atualizar senha");
            System.out.println(" [5] Atualizar cpf");
            System.out.println(" [6] Atualizar cidade");
            System.out.println(" [7] Atualizar saldo da conta");
            System.out.println(" [0] Confirmar atualizações");
            System.out.println("");
            System.out.println("Conta:");
            System.out.println("");
            System.out.println(
                    "ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
            System.out.println("");
            System.out.println(newC.toString());
            System.out.println("");
            System.out.print("Insira a seguir o valor da opção preferida: ");
            try {
                x = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                x = -1;
            }
            System.out.println("");
            switch (x) {
                case 0:
                    break;
                case 1:
                    insertNome(newC);
                    clear();
                    break;
                case 2:
                    insertEmails(newC);
                    clear();
                    break;
                case 3:
                    insertNomeUsuario(newC);
                    clear();
                    break;
                case 4:
                    insertSenha(newC);
                    clear();
                    break;
                case 5:
                    insertCpf(newC);
                    clear();
                    break;
                case 6:
                    insertCidade(newC);
                    clear();
                    break;
                case 7:
                    insertSaldo(newC);
                    clear();
                    break;
                default:
                    System.out.println("Valor invalido, favor inserir um valor valido!");
                    break;
            }
        } while (x != 0);
        System.out.println(ContaDAO.update(oldC, newC) ? "Registro atualizado com sucesso!\n"
                : "Houve um erro na atualizacao, favor tentar novamente...\n");
        enter();
    }

    /*
     * verifica o id informado e, após confirmar a "exclusão", marca o registro com
     * um
     * apontador lápide
     */
    private static void deletarRegistro() {
        // dados
        Conta c;
        String tmp;
        int id;
        do {
            System.out.print("Insira o ID da conta para deletar registro: ");
            try {
                id = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                id = -1;
            }
            c = ContaDAO.read(id);
        } while (!isIdFound(c.getIdConta()));
        System.out.print("Registro encontrado: ");
        System.out.println("");
        System.out.println("ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
        System.out.println("");
        System.out.println(c.toString() + "\n");
        System.out.print("Deletar conta? [Y/n]: ");
        tmp = sc.nextLine();
        System.out.println("");
        if (tmp.contains("n") || tmp.contains("N")) {
            System.out.println("Voltando ao menu...");
        } else {
            System.out.println(ContaDAO.delete(id) ? "Conta deletada com sucesso\n"
                    : "Erro ao deletar conta, voltando ao menu...\n");
        }
        enter();
    }

    /**
     * private static void ordernarArquivo() {
     * // dados
     * int x;
     * String tmp;
     * do {
     * System.out.println("Escolha o método de ordenacao: ");
     * System.out.println("");
     * System.out.println(" [1] Intercalacao balanceada comum");
     * System.out.println(" [2] Intercalacao balanceada com blocos de tamanho
     * variavel");
     * System.out.println(" [3] Intercalacao balanceada com selecao por
     * substituicao");
     * System.out.println(" [4] Intercalacao balanceada usando n+1 arquivos");
     * System.out.println(" [5] Intercalacao Polifasica");
     * System.out.println(" [0] Encerrar ordenacao");
     * System.out.println("");
     * System.out.println("Obs: Os elementos serão ordenados por ID, usando 2
     * caminhos e blocos de tamanho 10");
     * System.out.println("");
     * System.out.print("Insira a seguir o valor da opcao preferida: ");
     * try {
     * x = Integer.parseInt(sc.nextLine());
     * } catch (NumberFormatException e) {
     * x = -1;
     * }
     * System.out.println("");
     * switch (x) {
     * case 0:
     * clear();
     * //start();
     * break;
     * case 1:
     * Sort.intercalacaoComum();
     * clear();
     * break;
     * case 2:
     * Sort.intercalacaoVariavel();
     * clear();
     * break;
     * case 3:
     * Sort.intercalacaoSubstituicao();
     * clear();
     * break;
     * case 4:
     * Sort.intercalacaoArquivos();
     * clear();
     * break;
     * case 5:
     * Sort.intercalacaoPolifasica();
     * clear();
     * break;
     * default:
     * System.out.println("Valor invalido, favor inserir um valor valido!");
     * System.out.println("");
     * enter();
     * clear();
     * break;
     * }
     * } while (!(1 <= x && x <= 5));
     * System.out.println("");
     * System.out.print("Arquivo ordenado, gostaria de ver os registros? [Y/n] ");
     * tmp = sc.nextLine();
     * System.out.println("");
     * if (tmp.contains("n") || tmp.contains("N")) {
     * return;
     * } else {
     * mostrarRegistro();
     * }
     * }
     */

    private static void menuMostrar() {
        int x;
        do {
            System.out.println("Escolha uma das seguintes opções para atualizar: ");
            System.out.println("");
            System.out.println(" [1] Mostrar arquivo de registros");
            System.out.println(" [2] Mostrar arquivo de Arvore B+");
            System.out.println(" [3] Mostrar arquivo de Hash Estendida");
            System.out.println(" [4] Mostrar arquivo de Lista Invertida de Nome Pessoa");
            System.out.println(" [5] Mostrar arquivo de Lista Invertida de Cidade");
            System.out.println(" [0] Voltar ao menu principal");
            System.out.println("");
            System.out.print("Insira a seguir o valor da opção preferida: ");
            try {
                x = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                x = -1;
            }
            System.out.println("");
            switch (x) {
                case 0:
                    break;
                case 1:
                    mostrarRegistro();
                    clear();
                    break;
                case 2:
                    mostrarBMais();
                    clear();
                    break;
                case 3:
                    mostrarHash();
                    clear();
                    break;
                case 4:
                    mostrarListaN();
                    clear();
                    break;
                case 5:
                    mostrarListaC();
                    clear();
                    break;
                default:
                    System.out.println("Valor invalido, favor inserir um valor valido!");
                    break;
            }
        } while (x != 0);
        System.out.println("");
        enter();
    }

    private static void mostrarRegistro() {
        System.out.println("Mostrando todos os elementos:");
        System.out.println("");
        System.out.println("ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
        System.out.println("");
        ContaDAO.showAll();
        System.out.println("");
        enter();
    }

    private static void mostrarBMais(){
        System.out.println("Mostrando todos os elementos:");
        System.out.println("");
        //ContaDAO.printBMais();
        System.out.println("");
        enter();
    }

    private static void mostrarHash() {
        System.out.println("Mostrando todos os elementos:");
        System.out.println("");
        ContaDAO.printHash();
        System.out.println("");
        enter();
    }

    private static void mostrarListaN() {
        String nome;
        do {
            System.out.print("Insira uma palavra para pesquisa: ");
            nome = sc.nextLine();
        } while (!isValidForInvertida(nome));
        System.out.println("");
        System.out.println("Mostrando todos os elementos:");
        System.out.println("");
        ContaDAO.printInvertidaNome(nome);
        System.out.println("");
        enter();
    }

    private static void mostrarListaC() {
        String cidade;
        do {
            System.out.print("Insira uma palavra para pesquisa: ");
            cidade = sc.nextLine();
        } while (!isValidForInvertida(cidade));
        System.out.println("");
        System.out.println("Mostrando todos os elementos:");
        System.out.println("");
        ContaDAO.printInvertidaCidade(cidade);
        System.out.println("");
        enter();
    }

    // registros de teste
    private static void inserirSmallTest() {
        // preparar emails
        String[] emails1 = { "joao@gmail.com" };
        String[] emails2 = { "clara@gmail.com", "clara@hotmail.com" };
        String[] emails3 = { "bia@gmail.com", "beatriz@hotmail.com", "bia.financas@gmail.com" };
        String[] emails4 = { "alcantra@gmail.com", "alcantra@yahoo.com", "pedro@gmail.com" };
        String[] emails5 = { "pablo@gmail.com", "pablo@hotmail.com" };
        String[] emails6 = { "murilo.costa@gmail.com" };
        String[] emails7 = { "yara@gmail.com", "yara.financas@yahoo.com" };
        String[] emails8 = { "ana@gmail.com" };
        String[] emails9 = { "amanda@gmail.com" };
        String[] emails10 = { "giovanna@gmail.com" };
        // criar contas em memoria primaria
        Conta c1 = new Conta(ContaDAO.newId(), "João Carlos", emails1, "Jozin", "1234", "12345678901", "Belo Horizonte",
                0, 12000.87F);
        Conta c2 = new Conta(ContaDAO.newId(), "Clara Clarice", emails2, "Clarinha", "4321", "12345678900", "São Paulo",
                0, 30.21F);
        Conta c3 = new Conta(ContaDAO.newId(), "Beatriz Nogueira", emails3, "Bia", "!@#$", "11223344556",
                "Rio de Janeiro", 0, 9202400.99F);
        Conta c4 = new Conta(ContaDAO.newId(), "Pedro Alcantra", emails4, "PedraR", "0987", "11223344558",
                "Distrito Federal",
                0, 10000.0F);
        Conta c5 = new Conta(ContaDAO.newId(), "Pablo Almeida", emails5, "Pablo", "12/11/1978", "01234567890",
                "São Paulo",
                0, 437.23F);
        Conta c6 = new Conta(ContaDAO.newId(), "Murilo Rocha", emails6, "Mu", "Toto", "98979695949",
                "Curitiba", 0, 24400.87F);
        Conta c7 = new Conta(ContaDAO.newId(), "Yara Mara", emails7, "Ym", "Ym123", "32313456779", "Belo Horizonte",
                0, 0.30F);
        Conta c8 = new Conta(ContaDAO.newId(), "Ana Julia", emails8, "Aninha", "7777", "76731122445", "Betim",
                0, 78455.32F);
        Conta c9 = new Conta(ContaDAO.newId(), "Amanda Ribeiro", emails9, "Manda", "!$#@", "1234565321",
                "São Paulo", 0, 9200.99F);
        Conta c10 = new Conta(ContaDAO.newId(), "Giovanna Machado", emails10, "Gi", "1223", "9876452311",
                "Rio de Janeiro",
                0, 12000.87F);
        // retorno visual
        System.out.println("Os seguintes elementos de teste foram inseridos: ");
        System.out.println("");
        System.out.println("ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
        System.out.println("");
        // passar registros para memoria secundaria, se n existentes
        createTestAccount(c1);
        createTestAccount(c2);
        createTestAccount(c3);
        createTestAccount(c4);
        createTestAccount(c5);
        createTestAccount(c6);
        createTestAccount(c7);
        createTestAccount(c8);
        createTestAccount(c9);
        createTestAccount(c10);
        System.out.println("");
        enter();
    }

    private static void inserirLongTest() {
        // preparar emails
        String[] emails1 = { "joao@gmail.com" };
        String[] emails2 = { "clara@gmail.com", "clara@hotmail.com" };
        String[] emails3 = { "bia@gmail.com", "beatriz@hotmail.com", "bia.financas@gmail.com" };
        String[] emails4 = { "alcantra@gmail.com", "alcantra@yahoo.com", "pedro@gmail.com" };
        String[] emails5 = { "pablo@gmail.com", "pablo@hotmail.com" };
        String[] emails6 = { "murilo.costa@gmail.com" };
        String[] emails7 = { "yara@gmail.com", "yara.financas@yahoo.com" };
        String[] emails8 = { "ana@gmail.com" };
        String[] emails9 = { "amanda@gmail.com" };
        String[] emails10 = { "giovanna@gmail.com" };
        String[] emails11 = { "robertoCarlos@gmail.com" };
        String[] emails12 = { "lazaros@gmail.com" };
        String[] emails13 = { "silvo_rico@gmail.com" };
        String[] emails14 = { "anitta@gmail.com" };
        String[] emails15 = { "castrado@gmail.com" };
        String[] emails16 = { "bbb_grazi@gmail.com" };
        String[] emails17 = { "loganlindo@gmail.com" };
        String[] emails18 = { "homiranha@gmail.com" };
        String[] emails19 = { "cantora_disney@gmail.com" };
        String[] emails20 = { "austin.ally@gmail.com" };
        String[] emails21 = { "retrato@gmail.com" };
        String[] emails22 = { "run.forest@gmail.com" };
        String[] emails23 = { "facada@gmail.com" };
        String[] emails24 = { "azul.mar@gmail.com" };
        String[] emails25 = { "mago.magico@gmail.com" };
        String[] emails26 = { "vampirinho@gmail.com" };
        String[] emails27 = { "homem.aranha@gmail.com" };
        String[] emails28 = { "iron.man@gmail.com" };
        String[] emails29 = { "thora@gmail.com" };
        String[] emails30 = { "rei.marvel@gmail.com" };
        // criar contas em memoria primaria
        Conta c1 = new Conta(ContaDAO.newId(), "João Carlos", emails1, "Jozin", "1234", "12345678901", "Belo Horizonte",
                0, 12000.87F);
        Conta c2 = new Conta(ContaDAO.newId(), "Clara Clarice", emails2, "Clarinha", "4321", "12345678900", "São Paulo",
                0, 30.21F);
        Conta c3 = new Conta(ContaDAO.newId(), "Beatriz Nogueira", emails3, "Bia", "!@#$", "11223344556",
                "Rio de Janeiro", 0, 9202400.99F);
        Conta c4 = new Conta(ContaDAO.newId(), "Pedro Alcantra", emails4, "PedraR", "0987", "11223344558",
                "Distrito Federal", 0, 10000.0F);
        Conta c5 = new Conta(ContaDAO.newId(), "Pablo Almeida", emails5, "Pablo", "12/11/1978", "01234567890",
                "São Paulo", 0, 437.23F);
        Conta c6 = new Conta(ContaDAO.newId(), "Murilo Rocha", emails6, "Mu", "Toto", "98979695949", "Curitiba", 0,
                24400.87F);
        Conta c7 = new Conta(ContaDAO.newId(), "Yara Mara", emails7, "Ym", "Ym123", "32313456779", "Belo Horizonte", 0,
                0.30F);
        Conta c8 = new Conta(ContaDAO.newId(), "Ana Julia", emails8, "Aninha", "7777", "76731122445", "Betim", 0,
                78455.32F);
        Conta c9 = new Conta(ContaDAO.newId(), "Amanda Ribeiro", emails9, "Manda", "!$#@", "1234565321", "São Paulo", 0,
                9200.99F);
        Conta c10 = new Conta(ContaDAO.newId(), "Giovanna Machado", emails10, "Gi", "1223", "9876452311",
                "Rio de Janeiro", 0, 12000.87F);
        Conta c11 = new Conta(ContaDAO.newId(), "Roberto Carlos", emails11, "Robertin", "1234", "20345678901",
                "Belo Horizonte", 0, 12000.87F);
        Conta c12 = new Conta(ContaDAO.newId(), "Lazaro Ramos", emails12, "LazRamos", "4321", "20345678900",
                "São Paulo", 0, 30.21F);
        Conta c13 = new Conta(ContaDAO.newId(), "Silvio Santos", emails13, "Dindin", "!@#$", "20223344556",
                "Rio de Janeiro", 0, 9202400.99F);
        Conta c14 = new Conta(ContaDAO.newId(), "Larissa Machado", emails14, "Anitta", "0987", "20223344558",
                "Distrito Federal", 0, 10000.0F);
        Conta c15 = new Conta(ContaDAO.newId(), "Caio Castro", emails15, "PaoDuro", "12/11/1978", "20234567890",
                "São Paulo", 0, 437.23F);
        Conta c16 = new Conta(ContaDAO.newId(), "Grazi Massafera", emails16, "MassaFera", "Toto", "20979695949",
                "Curitiba", 0, 24400.87F);
        Conta c17 = new Conta(ContaDAO.newId(), "Logan Lerman", emails17, "Poseidon", "Ym123", "20313456779",
                "Belo Horizonte", 0, 0.30F);
        Conta c18 = new Conta(ContaDAO.newId(), "Tom Holland", emails18, "Miranha", "7777", "20731122445", "Betim", 0,
                78455.32F);
        Conta c19 = new Conta(ContaDAO.newId(), "Sabrina Capenter", emails19, "Disney", "!$#@", "2034565321",
                "São Paulo", 0, 9200.99F);
        Conta c20 = new Conta(ContaDAO.newId(), "Ross Linch", emails20, "Austin", "1223", "2076452311",
                "Rio de Janeiro", 0, 12000.87F);
        Conta c21 = new Conta(ContaDAO.newId(), "Dorian Gray", emails21, "Velhin", "1234", "30345678901",
                "Belo Horizonte", 0, 12000.87F);
        Conta c22 = new Conta(ContaDAO.newId(), "Forest Gump", emails22, "Corredor", "4321", "30345678900", "São Paulo",
                0, 30.21F);
        Conta c23 = new Conta(ContaDAO.newId(), "Wandinha Addams", emails23, "Wanddams", "!@#$", "30223344556",
                "Rio de Janeiro", 0, 9202400.99F);
        Conta c24 = new Conta(ContaDAO.newId(), "Percy Jackson", emails24, "GuineaPig", "0987", "30223344558",
                "Distrito Federal", 0, 10000.0F);
        Conta c25 = new Conta(ContaDAO.newId(), "Harry Potter", emails25, "Hp", "12/11/1978", "30234567890",
                "São Paulo", 0, 437.23F);
        Conta c26 = new Conta(ContaDAO.newId(), "Edwart Cullen", emails26, "Vamp", "Toto", "30979695949", "Curitiba", 0,
                24400.87F);
        Conta c27 = new Conta(ContaDAO.newId(), "Peter Paker", emails27, "Pp", "Ym123", "30313456779", "Belo Horizonte",
                0, 0.30F);
        Conta c28 = new Conta(ContaDAO.newId(), "Tony Stark", emails28, "Tonyn", "7777", "30731122445", "Betim", 0,
                78455.32F);
        Conta c29 = new Conta(ContaDAO.newId(), "Thor Odinson", emails29, "Trovao", "!$#@", "3034565321", "São Paulo",
                0, 9200.99F);
        Conta c30 = new Conta(ContaDAO.newId(), "Stan Lee", emails30, "Marvel", "1223", "3076452311", "Rio de Janeiro",
                0, 12000.87F);
        // retorno visual
        System.out.println("Os seguintes elementos de teste foram inseridos: ");
        System.out.println("");
        System.out.println("ID | Nome | [Emails] | Nome de Usuario | Senha | CPF | Cidade | N. Transferências | Saldo");
        System.out.println("");
        // passar registros para memoria secundaria, se n existentes
        createTestAccount(c1);
        createTestAccount(c2);
        createTestAccount(c3);
        createTestAccount(c4);
        createTestAccount(c5);
        createTestAccount(c6);
        createTestAccount(c7);
        createTestAccount(c8);
        createTestAccount(c9);
        createTestAccount(c10);
        createTestAccount(c11);
        createTestAccount(c12);
        createTestAccount(c13);
        createTestAccount(c14);
        createTestAccount(c15);
        createTestAccount(c16);
        createTestAccount(c17);
        createTestAccount(c18);
        createTestAccount(c19);
        createTestAccount(c20);
        createTestAccount(c21);
        createTestAccount(c22);
        createTestAccount(c23);
        createTestAccount(c24);
        createTestAccount(c25);
        createTestAccount(c26);
        createTestAccount(c27);
        createTestAccount(c28);
        createTestAccount(c29);
        createTestAccount(c30);
        System.out.println("");
        enter();
    }

    // menu
    public static void start() {
        // dados
        int x;
        // retorno de inicializacao do programa
        System.out.println("========================================================================================");
        System.out.println("Iniciando o sistema de gerenciamento de contas bancarias...");
        System.out.println("========================================================================================");
        System.out.println("");
        do {
            System.out.println("======================================================================");
            System.out.println("Iniciando o menu de testes...");
            System.out.println("======================================================================");
            System.out.println("");
            System.out.println("Escolha uma das seguintes opções abaixo: ");
            System.out.println("");
            System.out.println(" [1] Criar uma nova conta");
            System.out.println(" [2] Realizar uma transferências");
            System.out.println(" [3] Ler um ou vários registro");
            System.out.println(" [4] Atualizar um registro");
            System.out.println(" [5] Deletar um registro");
            System.out.println(" [6] Ordenar arquivo (Intercalação balanceada comum)");
            System.out.println(" [7] Mostrar arquivos gerados");
            System.out.println(" [8] Inserir algumas contas de teste");
            System.out.println(" [9] Inserir varias contas de teste");
            System.out.println(" [0] Encerrar o programa");
            System.out.println("");
            System.out.print("Insira a seguir o valor da opção preferida: ");
            try {
                x = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                x = -1;
            }
            System.out.println("");
            switch (x) {
                case 0:
                    clear();
                    break;
                case 1:
                    criarConta();
                    clear();
                    break;
                case 2:
                    realizarTransferencia();
                    clear();
                    break;
                case 3:
                    menuLeitura();
                    clear();
                    break;
                case 4:
                    atualizarRegistro();
                    clear();
                    break;
                case 5:
                    deletarRegistro();
                    clear();
                    break;
                case 6:
                    // ordernarArquivo();
                    Sort.intercalacaoComum();
                    System.out.println("");
                    System.out.print("Arquivo ordenado, gostaria de ver os registros? [Y/n] ");
                    String tmp = sc.nextLine();
                    System.out.println("");
                    if (tmp.contains("n") || tmp.contains("N")) {
                        return;
                    } else {
                        mostrarRegistro();
                    }
                    clear();
                    break;
                case 7:
                    menuMostrar();
                    clear();
                    break;
                case 8:
                    inserirSmallTest();
                    clear();
                    break;
                case 9:
                    inserirLongTest();
                    clear();
                    break;
                default:
                    System.out.println("Valor invalido, favor inserir um valor valido!");
                    System.out.println("");
                    enter();
                    clear();
                    break;
            }
        } while (x != 0);
    }

    public static void end() {
        // dados
        String tmp;
        System.out.println("======================================================================");
        System.out.println("Preparando para encerrar...");
        System.out.println("======================================================================");
        System.out.println("");
        System.out.print("Manter arquivo gerado? [Y/n]: ");
        tmp = sc.nextLine();
        System.out.println("");
        if (tmp.contains("n") || tmp.contains("N")) {
            System.out.println(ContaDAO.deleteFiles() ? "Arquivos excluídos com sucesso"
                    : "Houve um erro na exclusão, favor excluir arquivos manualmente");
            System.out.println("");
            enter();
            System.out.println("");
            System.out.println("======================================================================");
            System.out.println("Programa encerado!");
            System.out.println("======================================================================");
        } else {
            System.out.println("");
            System.out.println("======================================================================");
            System.out.println("Programa encerado!");
            System.out.println("======================================================================");
        }
        ContaDAO.endAll();
        sc.close();
    }
}