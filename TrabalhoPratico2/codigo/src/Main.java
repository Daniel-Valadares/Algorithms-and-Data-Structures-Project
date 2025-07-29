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
    }
}