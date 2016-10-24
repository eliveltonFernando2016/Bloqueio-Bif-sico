/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Model.RecuperaInformacao;
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
public final class ConsumidorDao {
    private static MinhaConexao minhaConexao;
    public int ultimoIndice = 0;
    private int inicio= retornaId();
    public Connection conn = null;

    public List<RecuperaInformacao> ConsumoLote(){
        List<RecuperaInformacao> informacao = new ArrayList();

        minhaConexao = new MinhaConexao();
        conn = minhaConexao.getConnection();

        try {
            String sql = "SELECT * FROM schedule WHERE flag <> 2 AND (idoperacao >= ? AND idoperacao <= ?)";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, inicio);
            stm.setInt(2, inicio+50);
            inicio = inicio+50;

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                RecuperaInformacao info = new RecuperaInformacao(rs.getInt("idoperacao"),
                                                                 rs.getInt("indicetransacao"),
                                                                 rs.getString("operacao").charAt(0),
                                                                 rs.getString("itemdado"),
                                                                 rs.getString("timestampj"),
                                                                 rs.getInt("flag"));
                informacao.add(info);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            minhaConexao.release(conn);
        }

        return informacao;
    }
    
    public void alteraFlag(int idOperacao, int flag){
        minhaConexao = new MinhaConexao();
        Connection conn = minhaConexao.getConnection();
        
        //System.out.println("Alterei flag do id: "+idOperacao+" alterei para: "+flag);
        
        try {
            String sql = "UPDATE schedule SET flag = ? WHERE idoperacao = ?";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, flag);
            stm.setInt(2, idOperacao);
            stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            minhaConexao.release(conn);
        }
    }

    public List<String> ItemDado(){
        List<String> informacao = new ArrayList();
        int ultimaOp = 0;

        minhaConexao = new MinhaConexao();
        Connection conn = minhaConexao.getConnection();

        try {
            String sqlUltimoId = "SELECT MAX(idoperacao) FROM schedule WHERE flag <> 2";
            PreparedStatement stmt = conn.prepareStatement(sqlUltimoId);

            ResultSet rst = stmt.executeQuery();

            rst.next();
            ultimaOp = rst.getInt(1);

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
            minhaConexao.release(conn);
        }

        return informacao;
    }

    public boolean insereTabela(RecuperaInformacao info){
        boolean inseriu = false;
        
        minhaConexao = new MinhaConexao();
        Connection conn = minhaConexao.getConnection();

        try {
            String sql = "INSERT INTO scheduleout(indiceTransacao, operacao, itemDado, timestampj) VALUES (?, ?, ?, ?)";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, info.getIndiceTransacao());
            stm.setString(2, String.valueOf(info.getOperacao()));
            stm.setString(3, String.valueOf(info.getItemDado()));
            stm.setString(4, new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()));
            
            stm.executeUpdate();
            inseriu = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            minhaConexao.release(conn);
        }

        return inseriu;
    }

    public int retornaId(){
        int retornaid = 0;

        minhaConexao = new MinhaConexao();
        Connection conn = minhaConexao.getConnection();

        try {

            String sql = "SELECT MIN(idoperacao) FROM schedule WHERE flag <> 2";
            PreparedStatement stm = conn.prepareStatement(sql);

            ResultSet rs = stm.executeQuery();

            rs.next();
            retornaid = rs.getInt(1);
        } catch(Exception e){
            e.printStackTrace();
        } finally{
            minhaConexao.release(conn);
        }
        
        return retornaid;
    }
    
    public boolean transacaAConsumir(){
        boolean tem = false;
        
        minhaConexao = new MinhaConexao();
        conn = minhaConexao.getConnection();
        
        try {
            String sql = "SELECT * FROM schedule WHERE flag = 0 AND (operacao LIKE 'R' OR operacao LIKE 'W')";
            PreparedStatement stm = conn.prepareStatement(sql);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                tem = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            minhaConexao.release(conn);
        }

        return tem;
    }
}