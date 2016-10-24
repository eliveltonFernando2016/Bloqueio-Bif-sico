/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author elivelton
 */
public class ItemFila {
    private int estado;
    private int transacao;
    private String dado;
    private int idOperacao;

    public ItemFila() {

    }

    public ItemFila(int estado, int transacao, String dado, int idOperacao) {
        this.estado = estado;
        this.transacao = transacao;
        this.dado = dado;
        this.idOperacao = idOperacao;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getTransacao() {
        return transacao;
    }

    public void setTransacao(int transacao) {
        this.transacao = transacao;
    }

    public String getDado() {
        return dado;
    }

    public void setDado(String dado) {
        this.dado = dado;
    }

    public int getIdOperacao() {
        return idOperacao;
    }

    public void setIdOperacao(int idOperacao) {
        this.idOperacao = idOperacao;
    }
}
