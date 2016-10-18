package Produtor;

import Consumidor.RecuperaInformacao;
import Consumidor.RecuperaInformacaoBd;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static int numeroItens = 3;
    private static int numeroTransacoes = 4;
    private static int numeroAcessos = 9;
    private static Scanner scanner;
	
    public static void main(String[] args) {
        List<RecuperaInformacao> informacoes = new ArrayList();
        RecuperaInformacaoBd infoBD = new RecuperaInformacaoBd();
        informacoes = infoBD.selectBD();
        
        for(int i=0; i < informacoes.size(); i++){
            System.out.println("idOperacao: "+informacoes.get(i).getIdOperacao());
            System.out.println("indiceTransacao: "+informacoes.get(i).getIndiceTransacao());
            System.out.println("operacao: "+informacoes.get(i).getOperacao());
            System.out.println("itemdado: "+informacoes.get(i).getItemDado());
            System.out.println("timestamp: "+informacoes.get(i).getTimeStamp());
            System.out.println();
        }
        
        /*scanner = new Scanner(System.in);
        System.out.println( "Criando transacoes e gravando no banco..." );
        Produtor produtor = new Produtor(numeroItens, numeroTransacoes, numeroAcessos);
        produtor.start();
        System.out.println("Pressione Enter para encerrar a producao!");

        if(scanner.hasNextLine()) {
            System.out.println("Producao encerrada");
            produtor.setFlag(false);
        }*/
    }
}
