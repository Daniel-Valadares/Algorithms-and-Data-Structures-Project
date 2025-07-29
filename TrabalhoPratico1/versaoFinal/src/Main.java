package src;

/**
 * @author Daniel Valadares e Larissa Valadares
 */

public class Main {
    public static void main(String[] args) {
        ContaDAO.setup();
        Menu.start();
        Menu.end();
    }
}