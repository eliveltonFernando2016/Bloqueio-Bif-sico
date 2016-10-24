/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consumidor;

import Model.RecuperaInformacao;
import Model.ItemFila;
import Model.EstadoDado;
import DAO.ConsumidorDao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author elivelton
 */
public class Escalonador extends Thread{
    Thread th;
    
    ArrayList<String> listaTransacao = new ArrayList();
    ArrayList<String> dados = new ArrayList();
    
    HashMap<String, Integer> componFnteDaLista;
    HashMap<String, EstadoDado> estadoDadoCorrente = new HashMap();
    
    LinkedList<ItemFila> filaTransacao = new LinkedList();
    
    public static final String statusDadoUnlocked = "U";
    public static final String statusDadoLockX = "X";
    public static final String statusDadoLockS = "S";
    
    public List<RecuperaInformacao> informacoes = new ArrayList();
    private final ConsumidorDao infoBD = new ConsumidorDao();
    private RecuperaInformacao recupera = null;
    private boolean flag = true;

    public void lock(String status, int indiceTransacao, String itemDado, int idOperacao){
        if (status.equals(statusDadoLockS)) {
            lockS(itemDado, indiceTransacao, idOperacao);
        } else {
            lockX(itemDado, indiceTransacao, idOperacao);
        }
    }

    public void unlock(String dado){
        if (!dado.equals("infinito")) {
            if (estadoDadoCorrente.get(dado).getEstado() == 2) {
                estadoDadoCorrente.get(dado).setEstado(0);
                wakeup(dado);
                estadoDadoCorrente.get(dado).setEstado(1);
            }
            else if (estadoDadoCorrente.get(dado).getEstado() == 1) {
                listaTransacao.remove(dado);
                if (listaTransacao.isEmpty()) {
                    estadoDadoCorrente.get(dado).setEstado(0);
                    wakeup(dado);
                }
            }
        }
    }

    public void wakeup(String operacao){
        if (operacao.equals("")) {
            for (int i = 0; i < filaTransacao.size(); i++) {
                ItemFila j = filaTransacao.get(i);
                if (j.getEstado() == 1) {
                    filaTransacao.remove(i);
                    lock(statusDadoLockS, j.getTransacao(), j.getDado(), j.getIdOperacao());
                }
                if (j.getEstado() == 2) {
                    filaTransacao.remove(i);
                    lock(statusDadoLockX, j.getTransacao(), j.getDado(), j.getIdOperacao());
                }
            }
        }
        else {
            for (ItemFila i : filaTransacao) {
                if (i.getDado().equals(operacao)) {
                    if (i.getEstado() == 1) {
                        filaTransacao.remove(i);
                        lock(statusDadoLockS, i.getTransacao(), i.getDado(), i.getIdOperacao());
                    }
                    if (i.getEstado() == 2) {
                        filaTransacao.remove(i);
                        lock(statusDadoLockX, i.getTransacao(), i.getDado(), i.getIdOperacao());
                    }
                }
            }
        }
    }

    public void lockS(String itemDado, int indiceTransacao, int idOperacao){
        if (estadoDadoCorrente.get(itemDado).getEstado() == 0) {
            if (estadoDadoCorrente.get(itemDado).getTransacao().equals(indiceTransacao)) {
                recupera = new RecuperaInformacao(indiceTransacao, 'R', itemDado);
                infoBD.insereTabela(recupera);
                infoBD.alteraFlag(idOperacao, 2);

                listaTransacao.add(String.valueOf(indiceTransacao));

                estadoDadoCorrente.get(itemDado).setEstado(1);
            }
            recupera = new RecuperaInformacao(indiceTransacao, 'R', itemDado);
            infoBD.insereTabela(recupera);
            infoBD.alteraFlag(idOperacao, 2);

            listaTransacao.add(String.valueOf(indiceTransacao));
            
            estadoDadoCorrente.get(itemDado).setEstado(1);
        }
        else if (estadoDadoCorrente.get(itemDado).getEstado() == 1) {
            if (estadoDadoCorrente.get(itemDado).getTransacao().equals(indiceTransacao)) {
                recupera = new RecuperaInformacao(indiceTransacao, 'R', itemDado);
                infoBD.insereTabela(recupera);
                infoBD.alteraFlag(idOperacao, 2);
                
                listaTransacao.add(String.valueOf(indiceTransacao));
            }
            recupera = new RecuperaInformacao(indiceTransacao, 'R', itemDado);
            infoBD.insereTabela(recupera);
            infoBD.alteraFlag(idOperacao, 2);
            
            listaTransacao.add(String.valueOf(indiceTransacao));
            estadoDadoCorrente.get(itemDado).setEstado(1);
        }
        else {
            ItemFila novoItemDaFila = new ItemFila(1,indiceTransacao, itemDado, idOperacao);
            filaTransacao.add(novoItemDaFila);
        }
    }

    public void lockX(String itemDado, int indiceTransacao, int idOperacao){
        if (estadoDadoCorrente.get(itemDado).getEstado() == 0) {
            recupera = new RecuperaInformacao(indiceTransacao, 'W', itemDado);
            infoBD.insereTabela(recupera);
            infoBD.alteraFlag(idOperacao, 2);
            
            listaTransacao.add(String.valueOf(indiceTransacao));
            
            estadoDadoCorrente.get(itemDado).setEstado(2);
        }
        else if (estadoDadoCorrente.get(itemDado).getEstado() == 2 && estadoDadoCorrente.get(itemDado).getTransacao().equals(indiceTransacao)){
            recupera = new RecuperaInformacao(indiceTransacao, 'W', itemDado);
            infoBD.insereTabela(recupera);
            infoBD.alteraFlag(idOperacao, 2);

            listaTransacao.add(String.valueOf(indiceTransacao));

            estadoDadoCorrente.get(itemDado).setEstado(2);
        }
        else if (estadoDadoCorrente.get(itemDado).getEstado() == 1 && estadoDadoCorrente.get(itemDado).getTransacao().equals(indiceTransacao)){
            ItemFila novoItemDaFila = new ItemFila(2, indiceTransacao, itemDado, idOperacao);
            filaTransacao.add(novoItemDaFila);
        }
        else {
            ItemFila novoItemDaFila = new ItemFila(2, indiceTransacao, itemDado, idOperacao);
            filaTransacao.add(novoItemDaFila);
        }
    }

    public void run(){
        do{
            List<RecuperaInformacao> informacao = infoBD.ConsumoLote();

            List<String> itemDado = null;
            itemDado = infoBD.ItemDado();

            for(int i=0; i < itemDado.size(); i++){
                dados.add(itemDado.get(i));
            }

            EstadoDado item = new EstadoDado("", 0);
            for (String dado : dados) {
                estadoDadoCorrente.put(dado, item);
            }

            System.out.println("Meu print: "+informacao.size());
            for (int j=0; j < informacao.size(); j++) {
                infoBD.alteraFlag(informacao.get(j).getIdOperacao(), 1);
                if (informacao.get(j).getOperacao() == 'R'){
                    lock(statusDadoLockS, informacao.get(j).getIndiceTransacao(), informacao.get(j).getItemDado(), informacao.get(j).getIdOperacao());
                }

                if (informacao.get(j).getOperacao() == 'W'){
                    lock(statusDadoLockX, informacao.get(j).getIndiceTransacao(), informacao.get(j).getItemDado(), informacao.get(j).getIdOperacao());
                }
                
                if (informacao.get(j).getOperacao() == 'E'){
                    unlock("infinito");
                    verificarFila();
                }
            }
            try {
                Thread.sleep( 3 * 2000 );
            } catch (InterruptedException ex) {
                Logger.getLogger(Escalonador.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while (flag);
    }

    private void verificarFila() {
        ItemFila first;

        for (int i = 0; i < filaTransacao.size(); i++) {
            first = filaTransacao.get(i);

            if (estadoDadoCorrente.get(first.getDado()).getEstado() == 0) {
                switch (first.getEstado()) {
                    case 1:
                        lock(statusDadoLockS, first.getTransacao(), first.getDado(), first.getIdOperacao());
                        break;
                    case 2:
                        lock(statusDadoLockX, first.getTransacao(), first.getDado(), first.getIdOperacao());
                        break;
                }
            }
        }
    }

    public void setFlag(boolean state) {
        this.flag = state;
    }
    
    public void start() {
	if (th == null) {
            th = new Thread (this);
            th.start ();
        }
    }
}