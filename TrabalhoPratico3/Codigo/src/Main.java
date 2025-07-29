package src;

/**
 * @author Daniel Valadares, Gustavo Silvestre e Larissa Valadares
 */

 import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException{
        ContaDAO.setup();
        Menu.start();
        Menu.end();
        // Teste da classe CifraColuna
        /*
        String senha = "amarelinha";
        System.out.println("Senha: " + senha);
        CifraColuna test = new CifraColuna("ch");
        String print = test.criptografar(senha);
        System.out.println("Senha criptografada:    " + print);
        print = test.descriptografar(print);
        System.out.println("Senha descriptografada: " + print);*/
    }
}