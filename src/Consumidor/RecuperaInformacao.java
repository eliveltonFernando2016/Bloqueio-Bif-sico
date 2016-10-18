/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consumidor;

/**
 *
 * @author elivelton
 */
public class RecuperaInformacao {
    Integer idOperacao;
    Integer indiceTransacao;
    String operacao;
    String itemDado;
    String timeStamp;

    public RecuperaInformacao(Integer idOperacao, Integer indiceTransacao, String operacao, String itemDado, String timeStamp) {
        this.idOperacao = idOperacao;
        this.indiceTransacao = indiceTransacao;
        this.operacao = operacao;
        this.itemDado = itemDado;
        this.timeStamp = timeStamp;
    }

    public Integer getIdOperacao() {
        return idOperacao;
    }

    public void setIdOperacao(Integer idOperacao) {
        this.idOperacao = idOperacao;
    }

    public Integer getIndiceTransacao() {
        return indiceTransacao;
    }

    public void setIndiceTransacao(Integer indiceTransacao) {
        this.indiceTransacao = indiceTransacao;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getItemDado() {
        return itemDado;
    }

    public void setItemDado(String itemDado) {
        this.itemDado = itemDado;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    } 
}
