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
import java.sql.SQLException;
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

    public List<RecuperaInformacao> ConsumoLote() throws SQLException{
        List<RecuperaInformacao> informacao = new ArrayList();

        minhaConexao = new MinhaConexao();
        minhaConexao.getConnection();

        int ultimaOp = 0;

        Connection conn = minhaConexao.getConnection();
        try {
            String sqlUltimoId = "SELECT MAX(idoperacao) FROM schedule WHERE flag <> 2";
            PreparedStatement stmt = conn.prepareStatement(sqlUltimoId);

            ResultSet rst = stmt.executeQuery();

            rst.next();
            ultimaOp = rst.getInt(1);

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
                //alteraFlag(info.getIdOperacao());

                informacao.add(info);
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            minhaConexao.release(conn);
        }

        return informacao;
    }
    
    public void alteraFlag(int idOperacao) throws SQLException{
        minhaConexao = new MinhaConexao();
        minhaConexao.getConnection();

        Connection conn = minhaConexao.getConnection();
        
        try {
            String sql = "UPDATE schedule SET flag = 1 WHERE idoperacao = ?";
            PreparedStatement stm = conn.prepareStatement(sql);

            stm.setInt(1, idOperacao);
            stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            minhaConexao.release(conn);
        }
    }

    public List<String> ItemDado() throws SQLException{
        List<String> informacao = new ArrayList();

        minhaConexao = new MinhaConexao();
        minhaConexao.getConnection();
        
        int ultimaOp = 0;
        
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

    public boolean insereTabela(RecuperaInformacao info) throws SQLException{
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
            inseriu = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            minhaConexao.release(conn);
        }

        return inseriu;
    }
}