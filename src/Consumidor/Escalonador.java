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
    RecuperaInformacaoBd infoBD = new RecuperaInformacaoBd();

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

    /*void executar(String arquivotxt) throws FileNotFoundException, IOException {
        FileReader file = new FileReader(new File(arquivotxt));
        BufferedReader buffer = new BufferedReader(file);
        String linha;
        int numeroLinhas = 0;
        int numeroItensDados = 0;
        int numeroTransacoes = 0;
        int numeroAcessos = 0;
        String[] s;

        String l;
        String[] schedule = null;
        while ((linha = buffer.readLine()) != null) {
            numeroLinhas++;
            System.out.println(linha);
            if (numeroLinhas == 1) {
                s = linha.split(", ");
                numeroItensDados = s.length;
                numeroTransacoes = Integer.parseInt(s[1]);
                numeroAcessos = Integer.parseInt(s[2]);
            } else if (numeroLinhas == 2) {
                linha = linha.replace("Dados: [", "");
                linha = linha.replace("]", "");
                s = linha.split(", ");
                for (int i = 0; i < s.length; i++) {
                    System.out.println(s[i] + "DADO");
                    dados.add(s[i]);
                }
            } else if (numeroLinhas == 3 && numeroLinhas < 3 + numeroTransacoes) {
                //transacoes
            } else {
                //escalonador;
                l = linha.replace("schedule: [", "");
                l = l.replace("]", "");
                schedule = linha.split(", ");
            }
        }
        escalonar(schedule);
        int verif = 0;
        while (!filaTransacao.isEmpty()) {
            if (verif < 500) {
                despertarFila("");
                verif++;
            } else {
                System.out.println("Deadlock Encontrado");
                break;
            }
        }
    }*/

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
        informacoes = infoBD.selectBD();

        String schedule = null;

        for(int i=0; i < informacoes.size(); i++){
            if(i==0){
                schedule = String.valueOf(informacoes.get(i).getOperacao());
            }
            else{
                schedule = schedule.concat(String.valueOf(informacoes.get(i).getOperacao()));
            }
            
            schedule = schedule.concat(String.valueOf(informacoes.get(i).getIndiceTransacao()));
            
            if(!("E".equals(informacoes.get(i).getOperacao()) || "S".equals(informacoes.get(i).getOperacao()))){
                schedule = schedule.concat("(");
                schedule = schedule.concat(informacoes.get(i).getItemDado());
                schedule = schedule.concat(")");
            }
            
            schedule = schedule.concat(", ");
        }

        System.out.println(schedule);
        /*for (String dado : dados) {
        System.out.println(dado + "NOVO");
        EstadoDado item = new EstadoDado("", 0);
        estadoDadoCorrente.put(dado, item);
        }
        for (String i : schedule) {
        //verifica se comeca uma transacao;
        if (i.substring(0, 1).equals("S")) {
        //comeca transacao
        System.out.println("Comecou A transacao " + i);
        writer.write(i + "\n");
        }
        if (i.substring(0, 1).equals("E")) {
        writer.write(i + "\n");
        //tem q ver se num tem nada na fila;
        //termina transacao
        solicitacaoDesbloqueio(i.substring(1, 2), "infinito");
        verificarFila(i.substring(1, 2));
        System.out.println("Commitou " + i);
        }
        if (i.substring(0, 1).equals("R")) {
        //solicita bloqueio compartilhado
        System.out.println(i.substring(1, 2));
        System.out.println(i.substring(3, 4));
        solicitacaoBloqueio(statusDadoBloqueadoCompartilhado, i.substring(1, 2), i.substring(3, 4));
        }
        if (i.substring(0, 1).equals("W")) {
        //solicita bloqueio exclusivo
        System.out.println(i.substring(1, 2));
        System.out.println(i.substring(3, 4));
        solicitacaoBloqueio(statusDadoBloqueadoExclusivo, i.substring(1, 2), i.substring(3, 4));
        }
        }
        writer.close();*/
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
