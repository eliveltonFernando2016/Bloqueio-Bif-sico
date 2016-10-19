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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elivelton
 */
public class RecuperaInformacaoBd {
    private static MinhaConexao minhaConexao;

    public List<RecuperaInformacao> selectBD(){
        List<RecuperaInformacao> informacao = new ArrayList();

        minhaConexao = new MinhaConexao();
        minhaConexao.getConnection();

        Connection conn = minhaConexao.getConnection();

        try {
            String sql = "SELECT * FROM schedule";
            PreparedStatement stm = conn.prepareStatement(sql);

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
        }

        return informacao;
    }
}
