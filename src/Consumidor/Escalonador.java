/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consumidor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author elivelton
 */
public class Escalonador {
    PrintWriter writer;

    ArrayList<String> listaTransacao;
    ArrayList<String> dados;
    
    HashMap<String, Integer> componFnteDaLista;
    HashMap<String, EstadoDado> estadoDadoCorrente;
    
    LinkedList<ItemFila> filaTransacao;
    
    public static final String statusDadoDesbloqueado = "U";
    public static final String statusDadoBloqueadoExclusivo = "X";
    public static final String statusDadoBloqueadoCompartilhado = "S";
    
    public List<RecuperaInformacao> informacoes = new ArrayList();
    ConsumidorDao infoBD = new ConsumidorDao();

    public Escalonador() {
        filaTransacao = new LinkedList<>();
        listaTransacao = new ArrayList<>();
        dados = new ArrayList<>();
        estadoDadoCorrente = new HashMap<>();
    }

    public void despertarFila(String dado) {
        if (dado.equals("")) {
            for (int i = 0; i < filaTransacao.size(); i++) {
                ItemFila j = filaTransacao.get(i);
                if (j.getEstado() == 1) {
                    filaTransacao.remove(i);
                    solicitacaoBloqueio(statusDadoBloqueadoCompartilhado, j.getTransacao(), j.getDado());
                }
                if (j.getEstado() == 2) {
                    filaTransacao.remove(i);
                    solicitacaoBloqueio(statusDadoBloqueadoExclusivo, j.getTransacao(), j.getDado());
                }
            }

        } else {
            for (ItemFila i : filaTransacao) {
                if (i.getDado().equals(dado)) {
                    System.out.println("Despertando " + dado);
                    if (i.getEstado() == 1) {
                        filaTransacao.remove(i);
                        solicitacaoBloqueio(statusDadoBloqueadoCompartilhado, i.getTransacao(), i.getDado());
                    }
                    if (i.getEstado() == 2) {
                        filaTransacao.remove(i);
                        solicitacaoBloqueio(statusDadoBloqueadoExclusivo, i.getTransacao(), i.getDado());
                    }
                }
            }
            //procurar por toda a fila quem esta primeiro e atender o chamado, executa;
        }
    }

    public void solicitacaoDesbloqueio(String transacao, String dado) {
        if (!dado.equals("infinito")) {
            System.out.println("Desbloqueando " + transacao + " / " + dado);
            if (estadoDadoCorrente.get(dado).getEstado() == 2) {
                estadoDadoCorrente.get(dado).setEstado(0);//desbloqueia
                despertarFila(dado);
                //desperta a fila-wait(dado);
                estadoDadoCorrente.get(dado).setEstado(1);
            } else if (estadoDadoCorrente.get(dado).getEstado() == 1) {
                listaTransacao.remove(dado);
                if (listaTransacao.isEmpty()) {
                    estadoDadoCorrente.get(dado).setEstado(0);
                    //desperta a fila-wait(dado);
                    despertarFila(dado);
                }
            }
        }
    }

    public void solicitacaoBloqueioCompartilhado(String transacao, String dado) {
        System.out.println("Bloquando compartilhado " + transacao + " / " + dado);
        if (estadoDadoCorrente.get(dado).getEstado() == 0) {
            if (estadoDadoCorrente.get(dado).getTransacao().equals(transacao)) {
                writer.write("R" + transacao + "(" + dado + ")");
                System.out.println("Conseguiu o bloqueio, esta desbloqueado" + "\n");
                listaTransacao.add(transacao);
                estadoDadoCorrente.get(dado).setEstado(1);
            }
            writer.write("R" + transacao + "(" + dado + ")");
            System.out.println("Conseguiu o bloqueio, esta desbloqueado" + "\n");
            listaTransacao.add(transacao);
            estadoDadoCorrente.get(dado).setEstado(1);

        } else if (estadoDadoCorrente.get(dado).getEstado() == 1) {
            if (estadoDadoCorrente.get(dado).getTransacao().equals(transacao)) {
                writer.write("R" + transacao + "(" + dado + ")");
                System.out.println("Conseguiu o bloqueio, esta compartilhado" + "\n");
                listaTransacao.add(transacao);
            }
            writer.write("R" + transacao + "(" + dado + ")");
            System.out.println("Conseguiu o bloqueio, esta desbloqueado" + "\n");
            listaTransacao.add(transacao);
            estadoDadoCorrente.get(dado).setEstado(1);
        } else {
            System.out.println("Nao Conseguiu, entrando pra fila");
            ItemFila novoItemDaFila = new ItemFila(1, transacao, dado);
            filaTransacao.add(novoItemDaFila);

        }
    }

    public void solicitacaoBloqueioExclusivo(String transacao, String dado) {
        System.out.println("Bloqueando Exclusivo " + transacao + " / " + dado);
        if (estadoDadoCorrente.get(dado).getEstado() == 0) {
            writer.write("W" + transacao + "(" + dado + ")" + "\n");
            System.out.println("Conseguiu o bloqueio");
            listaTransacao.add(transacao);
            estadoDadoCorrente.get(dado).setEstado(2);
        } else if (estadoDadoCorrente.get(dado).getEstado() == 2
                && estadoDadoCorrente.get(dado).getTransacao().equals(transacao)) {
            writer.write("W" + transacao + "(" + dado + ")" + "\n");
            System.out.println("Conseguiu o bloqueio");
            listaTransacao.add(transacao);
            estadoDadoCorrente.get(dado).setEstado(2);
        } else if (estadoDadoCorrente.get(dado).getEstado() == 1
                && estadoDadoCorrente.get(dado).getTransacao().equals(transacao)) {
            System.out.println("Nao Conseguiu, entrando pra fila");
            ItemFila novoItemDaFila = new ItemFila(2, transacao, dado);
            filaTransacao.add(novoItemDaFila);
//            filaDeTransacao.add(transacao);
        } else {
            System.out.println("Nao Conseguiu, entrando pra fila");
            ItemFila novoItemDaFila = new ItemFila(2, transacao, dado);
            filaTransacao.add(novoItemDaFila);
        }
    }

    public void solicitacaoBloqueio(String status, String transacao, String dado) {
        if (status.equals(statusDadoBloqueadoCompartilhado)) {
            solicitacaoBloqueioCompartilhado(transacao, dado);
        } else {
            solicitacaoBloqueioExclusivo(transacao, dado);
        }
    }

    public void escalonar() {
        List<RecuperaInformacao> informacao = infoBD.ConsumoLote();
        String[] scheduleStr = null;
        String schedule = null;

        for(int i=0; i < informacao.size(); i++){
            if(i==0){
                schedule = String.valueOf(informacao.get(i).getOperacao());
            }
            else{
                try {
                    schedule = schedule.concat(String.valueOf(informacao.get(i).getOperacao()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            try {
                schedule = schedule.concat(String.valueOf(informacao.get(i).getIndiceTransacao()));
            } catch(Exception e){
                e.printStackTrace();
            }

            if(!("E".equals(String.valueOf(informacao.get(i).getOperacao())) || "S".equals(String.valueOf(informacao.get(i).getIdOperacao())))){
                try {
                    schedule = schedule.concat(String.valueOf(informacao.get(i).getItemDado()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            schedule = schedule.concat(", ");
        }

        scheduleStr = schedule.split(", ");

        for (String dado : dados) {
            System.out.println(dado + "NOVO");
            EstadoDado item = new EstadoDado("", 0);
            estadoDadoCorrente.put(dado, item);
        }

        for (String j : scheduleStr) {
            //verifica se comeca uma transacao;
            if (j.substring(0, 1).equals("S")) {
                //comeca transacao
                System.out.println("Comecou A transacao " + j);
            }
            if (j.substring(0, 1).equals("E")) {
                solicitacaoDesbloqueio(j.substring(1, 2), "infinito");
                verificarFila(j.substring(1, 2));
                System.out.println("Commitou " + j);
            }
            if (j.substring(0, 1).equals("R")) {
                solicitacaoBloqueio(statusDadoBloqueadoCompartilhado, j.substring(1, 2), j.substring(3, 4));
            }
            if (j.substring(0, 1).equals("W")) {
                solicitacaoBloqueio(statusDadoBloqueadoExclusivo, j.substring(1, 2), j.substring(3, 4));
            }
        }
    }

    private void verificarFila(String substring) {
        ItemFila first;
        System.out.println(Arrays.toString(filaTransacao.toArray()));
        for (int i = 0; i < filaTransacao.size(); i++) {
            first = filaTransacao.get(i);
            if (estadoDadoCorrente.get(first.getDado()).getEstado() == 0) {
                switch (first.getEstado()) {
                    case 1:
                        solicitacaoBloqueio(statusDadoBloqueadoCompartilhado, first.getTransacao(), first.getDado());
                        break;
                    case 2:
                        solicitacaoBloqueio(statusDadoBloqueadoExclusivo, first.getTransacao(), first.getDado());
                        break;
                }
            }
        }
    }

}
