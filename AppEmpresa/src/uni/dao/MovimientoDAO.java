package uni.dao;

import uni.database.ConexionBD;
import uni.entity.Movimiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de solo lectura sobre la tabla "movimiento".
 * Las inserciones se realizan dentro de los procedimientos de proceso
 * (deposito/retiro/transferencia) -> ver {@link ProcesoBancarioDAO}.
 */
public class MovimientoDAO {

    /**
     * Devuelve los movimientos de una cuenta entre dos fechas (inclusive).
     * Consulta parametrizada con rango de fechas para el estado de cuenta.
     *
     * @param numeroCuenta numero de cuenta
     * @param desde        fecha inicial yyyy-MM-dd
     * @param hasta        fecha final   yyyy-MM-dd
     */
    public List<Movimiento> listarPorCuentaYFechas(String numeroCuenta,
                                                   String desde, String hasta) throws Exception {
        List<Movimiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimiento "
                   + "WHERE numero_cuenta = ? "
                   + "AND DATE(fecha) BETWEEN ? AND ? "
                   + "ORDER BY fecha, id_movimiento";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numeroCuenta);
            ps.setString(2, desde);
            ps.setString(3, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Movimiento mapear(ResultSet rs) throws Exception {
        Movimiento m = new Movimiento();
        m.setIdMovimiento(rs.getInt("id_movimiento"));
        m.setCodigo(rs.getString("codigo"));
        m.setNumeroCuenta(rs.getString("numero_cuenta"));
        m.setTipo(rs.getString("tipo"));
        m.setMonto(rs.getBigDecimal("monto"));
        m.setFecha(rs.getString("fecha"));
        m.setIdEmpleado(rs.getInt("id_empleado"));
        m.setCuentaRelacionada(rs.getString("cuenta_relacionada"));
        m.setSaldoResultante(rs.getBigDecimal("saldo_resultante"));
        return m;
    }
}
