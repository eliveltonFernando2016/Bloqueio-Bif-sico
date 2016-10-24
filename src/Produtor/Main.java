package Produtor;

import Consumidor.Escalonador;
import DAO.ConsumidorDao;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static int numeroItens = 3;
    private static int numeroTransacoes = 4;
    private static int numeroAcessos = 9;
    private static Scanner scanner;

    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws SQLException {
        scanner = new Scanner(System.in);
        System.out.println( "Criando transacoes e gravando no banco..." );
        
        Produtor produtor = new Produtor(numeroItens, numeroTransacoes, numeroAcessos);
        produtor.start();
        
        System.out.println("Pressione Enter para encerrar a producao!");
        if(scanner.hasNextLine()) {
            System.out.println("Producao encerrada");
            produtor.setFlag(false);
        }

        ConsumidorDao infoBD = new ConsumidorDao();
        Escalonador escalonador = new Escalonador();
        escalonador.start();

        escalonador.setFlag(false);
    }
}
