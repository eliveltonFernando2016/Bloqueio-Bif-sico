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
    private String transacao;
    private String dado;

    public ItemFila() {

    }

    public ItemFila(int estado, String transacao, String dado) {
        this.estado = estado;
        this.transacao = transacao;
        this.dado = dado;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getTransacao() {
        return transacao;
    }

    public void setTransacao(String transacao) {
        this.transacao = transacao;
    }

    public String getDado() {
        return dado;
    }

    public void setDado(String dado) {
        this.dado = dado;
    }
}
