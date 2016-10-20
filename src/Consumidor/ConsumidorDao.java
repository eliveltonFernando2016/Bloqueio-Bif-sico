/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Consumidor;

import Produtor.MinhaConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author elivelton
 */
public class ConsumidorDao {
    private static MinhaConexao minhaConexao;
    public int ultimoIndice = 0;

    public List<RecuperaInformacao> ConsumoLote(){
        List<RecuperaInformacao> informacao = new ArrayList();

        minhaConexao = new MinhaConexao();
        minhaConexao.getConnection();

        int ultimaOp = ultimoIdOperacao();

        Connection conn = minhaConexao.getConnection();
        try {
            String sql = "SELECT * FROM schedule WHERE idoperacao >= ? AND idoperacao <= ?";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, ultimaOp-50);
            stm.setInt(2, ultimaOp);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                RecuperaInformacao info = new RecuperaInformacao(rs.getInt("idoperacao"),
                                                                 rs.getInt("indicetransacao"),
                                                                 rs.getString("operacao").charAt(0),
                                                                 rs.getString("itemdado"),
                                                                 rs.getString("timestampj"),
                                                                 rs.getInt("flag"));
                alteraFlag(info.getIdOperacao());

                informacao.add(info);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            minhaConexao.desconexao(conn);
        }

        return informacao;
    }
    
    public void alteraFlag(int idOperacao){
        minhaConexao = new MinhaConexao();
        minhaConexao.getConnection();

        Connection conn = minhaConexao.getConnection();
        
        try {
            String sql = "UPDATE schedule SET flag = 1 WHERE ?";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, idOperacao);
            stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            minhaConexao.desconexao(conn);
        }
    }

    public List<String> ItemDado(){
        List<String> informacao = new ArrayList();

        minhaConexao = new MinhaConexao();
        minhaConexao.getConnection();
        
        int ultimaOp = ultimoIdOperacao();
        
        Connection conn = minhaConexao.getConnection();

        try {
            String sql = "SELECT distinct itemdado FROM schedule WHERE (idoperacao >= ? AND idoperacao <= ?) AND itemdado IS NOT NULL";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, ultimaOp-50);
            stm.setInt(2, ultimaOp);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                informacao.add(rs.getString("itemdado"));
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            minhaConexao.desconexao(conn);
        }

        return informacao;
    }
    
    public int ultimoIdOperacao(){
        minhaConexao = new MinhaConexao();
        minhaConexao.getConnection();

        Connection conn = minhaConexao.getConnection();

        int ultimoId = 0;

        try {
            String sql = "SELECT MAX(idoperacao) FROM schedule WHERE flag <> 2";
            PreparedStatement stm = conn.prepareStatement(sql);

            ResultSet rs = stm.executeQuery();

            rs.next();
            ultimoId = rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            minhaConexao.desconexao(conn);
        }

        return ultimoId;
    }

    public boolean insereTabela(RecuperaInformacao info){
        boolean inseriu = false;
        
        minhaConexao = new MinhaConexao();
        minhaConexao.getConnection();

        Connection conn = minhaConexao.getConnection();
        
        try {
            String sql = "INSERT INTO scheduleout(indiceTransacao, operacao, itemDado, timestampj) VALUES (?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, info.getIndiceTransacao());
            stm.setString(2, String.valueOf(info.getOperacao()));
            stm.setString(3, String.valueOf(info.getItemDado()));
            stm.setString(4, new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
            stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            minhaConexao.desconexao(conn);
        }

        return inseriu;
    }
}