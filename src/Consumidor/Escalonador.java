/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consumidor;

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
    ArrayList<String> listaTransacao;
    ArrayList<String> dados;
    
    HashMap<String, Integer> componFnteDaLista;
    HashMap<String, EstadoDado> estadoDadoCorrente;
    
    LinkedList<ItemFila> filaTransacao;
    
    public static final String statusDadoDesbloqueado = "U";
    public static final String statusDadoBloqueadoExclusivo = "X";
    public static final String statusDadoBloqueadoCompartilhado = "S";
    
    public List<RecuperaInformacao> informacoes = new ArrayList();
    private ConsumidorDao infoBD = new ConsumidorDao();
    private RecuperaInformacao recupera = null;

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
        }
        else {
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
        }
    }

    public void solicitacaoDesbloqueio(String transacao, String dado) {
        if (!dado.equals("infinito")) {
            System.out.println("Desbloqueando " + transacao + " / " + dado);

            if (estadoDadoCorrente.get(dado).getEstado() == 2) {
                estadoDadoCorrente.get(dado).setEstado(0);
                despertarFila(dado);
                estadoDadoCorrente.get(dado).setEstado(1);
            }
            else if (estadoDadoCorrente.get(dado).getEstado() == 1) {
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
        if (estadoDadoCorrente.get(dado).getEstado() == 0) {
            if (estadoDadoCorrente.get(dado).getTransacao().equals(transacao)) {
                recupera = new RecuperaInformacao(Integer.parseInt(transacao), 'R', dado);
                infoBD.insereTabela(recupera);
                
                listaTransacao.add(transacao);
                
                estadoDadoCorrente.get(dado).setEstado(1);
            }
            recupera = new RecuperaInformacao(Integer.parseInt(transacao), 'R', dado);
            infoBD.insereTabela(recupera);

            listaTransacao.add(transacao);
            estadoDadoCorrente.get(dado).setEstado(1);
        }
        else if (estadoDadoCorrente.get(dado).getEstado() == 1) {
            if (estadoDadoCorrente.get(dado).getTransacao().equals(transacao)) {
                recupera = new RecuperaInformacao(Integer.parseInt(transacao), 'R', dado);
                infoBD.insereTabela(recupera);
                
                listaTransacao.add(transacao);
            }
            recupera = new RecuperaInformacao(Integer.parseInt(transacao), 'R', dado);
            infoBD.insereTabela(recupera);
            
            listaTransacao.add(transacao);
            estadoDadoCorrente.get(dado).setEstado(1);
        }
        else {
            ItemFila novoItemDaFila = new ItemFila(1, transacao, dado);
            filaTransacao.add(novoItemDaFila);
        }
    }

    public void solicitacaoBloqueioExclusivo(String transacao, String dado) {
        if (estadoDadoCorrente.get(dado).getEstado() == 0) {
            recupera = new RecuperaInformacao(Integer.parseInt(transacao), 'W', dado);
            infoBD.insereTabela(recupera);
            
            listaTransacao.add(transacao);
            
            estadoDadoCorrente.get(dado).setEstado(2);
        }
        else if (estadoDadoCorrente.get(dado).getEstado() == 2 && estadoDadoCorrente.get(dado).getTransacao().equals(transacao)){
            recupera = new RecuperaInformacao(Integer.parseInt(transacao), 'W', dado);
            infoBD.insereTabela(recupera);

            listaTransacao.add(transacao);

            estadoDadoCorrente.get(dado).setEstado(2);
        }
        else if (estadoDadoCorrente.get(dado).getEstado() == 1 && estadoDadoCorrente.get(dado).getTransacao().equals(transacao)){
            ItemFila novoItemDaFila = new ItemFila(2, transacao, dado);
            filaTransacao.add(novoItemDaFila);
        }
        else {
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
        List<String> itemDado = infoBD.ItemDado();

        for(int i=0; i < itemDado.size(); i++){
            dados.add(itemDado.get(i));
        }

        EstadoDado item = new EstadoDado("", 0);
        for (String dado : dados) {
            estadoDadoCorrente.put(dado, item);
        }

        for (int j=0; j < informacao.size(); j++) {
            System.out.println("Entrei!");
            if ("R".equals(informacao.get(j).getOperacao())) {
                solicitacaoBloqueio(statusDadoBloqueadoCompartilhado, String.valueOf(informacao.get(j).getIndiceTransacao()), informacao.get(j).getItemDado());
            }

            if ("W".equals(informacao.get(j).getOperacao())) {
                solicitacaoBloqueio(statusDadoBloqueadoExclusivo, String.valueOf(informacao.get(j).getIndiceTransacao()), informacao.get(j).getItemDado());
            }

            if ("E".equals(informacao.get(j).getOperacao())) {
                solicitacaoDesbloqueio(String.valueOf(informacao.get(j).getIndiceTransacao()), "infinito");
                verificarFila();
            }
        }
    }

    private void verificarFila() {
        ItemFila first;

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
