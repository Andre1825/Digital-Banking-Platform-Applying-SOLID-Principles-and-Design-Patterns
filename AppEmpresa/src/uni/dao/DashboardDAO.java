package uni.dao;

import uni.database.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Consultas agregadas para el panel de inicio (Dashboard).
 * Devuelve totales del sistema: clientes, cuentas y saldo total depositado.
 */
public class DashboardDAO {

    public int contarClientes() throws Exception {
        return contar("SELECT COUNT(*) FROM cliente");
    }

    public int contarCuentas() throws Exception {
        return contar("SELECT COUNT(*) FROM cuenta");
    }

    public BigDecimal totalDepositado() throws Exception {
        String sql = "SELECT COALESCE(SUM(saldo),0) FROM cuenta WHERE estado = 'ACTIVA'";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal(1);
        }
        return BigDecimal.ZERO;
    }

    private int contar(String sql) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}
