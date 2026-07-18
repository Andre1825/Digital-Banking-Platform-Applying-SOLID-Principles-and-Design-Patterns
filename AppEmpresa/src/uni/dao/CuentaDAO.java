package uni.dao;

import uni.database.ConexionBD;
import uni.entity.Cuenta;
import uni.service.ICrudDao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la entidad Cuenta. La clave primaria es el numero de cuenta (String).
 */
public class CuentaDAO implements ICrudDao<Cuenta, String> {

    // SELECT con joins para mostrar cliente y tipo en las tablas.
    private static final String BASE =
            "SELECT c.*, "
          + "       CONCAT(cl.nombres,' ',cl.apellidos) AS cliente_nombre, "
          + "       tc.nombre AS tipo_nombre "
          + "FROM cuenta c "
          + "JOIN cliente cl   ON c.id_cliente   = cl.id_cliente "
          + "JOIN tipocuenta tc ON c.id_tipocuenta = tc.id_tipocuenta ";

    @Override
    public void crear(Cuenta c) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_cuenta_insert(?,?,?,?,?) }")) {
            cs.setString(1, c.getNumero());
            cs.setInt(2, c.getIdCliente());
            cs.setInt(3, c.getIdTipoCuenta());
            cs.setBigDecimal(4, c.getSaldo());
            cs.setString(5, c.getEstado());
            cs.executeUpdate();
        }
    }

    @Override
    public void actualizar(Cuenta c) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_cuenta_update(?,?,?,?) }")) {
            cs.setString(1, c.getNumero());
            cs.setInt(2, c.getIdCliente());
            cs.setInt(3, c.getIdTipoCuenta());
            cs.setString(4, c.getEstado());
            cs.executeUpdate();
        }
    }

    @Override
    public void eliminar(String numero) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_cuenta_delete(?) }")) {
            cs.setString(1, numero);
            cs.executeUpdate();
        }
    }

    @Override
    public Cuenta buscar(String numero) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(BASE + "WHERE c.numero = ?")) {
            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<Cuenta> listarTodos() throws Exception {
        List<Cuenta> lista = new ArrayList<>();
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(BASE + "ORDER BY c.numero");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** Lista las cuentas activas de un cliente (para los formularios de proceso). */
    public List<Cuenta> listarPorCliente(int idCliente) throws Exception {
        List<Cuenta> lista = new ArrayList<>();
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(BASE + "WHERE c.id_cliente = ? ORDER BY c.numero")) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Cuenta mapear(ResultSet rs) throws Exception {
        Cuenta c = new Cuenta();
        c.setNumero(rs.getString("numero"));
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setIdTipoCuenta(rs.getInt("id_tipocuenta"));
        c.setSaldo(rs.getBigDecimal("saldo"));
        c.setFechaApertura(rs.getString("fecha_apertura"));
        c.setEstado(rs.getString("estado"));
        c.setNombreCliente(rs.getString("cliente_nombre"));
        c.setNombreTipoCuenta(rs.getString("tipo_nombre"));
        return c;
    }
}
