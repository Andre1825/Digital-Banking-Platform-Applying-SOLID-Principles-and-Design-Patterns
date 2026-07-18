package uni.dao;

import uni.database.ConexionBD;
import uni.entity.TipoCuenta;
import uni.service.ICrudDao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** DAO de la entidad TipoCuenta (Ahorros, Corriente, ...). */
public class TipoCuentaDAO implements ICrudDao<TipoCuenta, Integer> {

    @Override
    public void crear(TipoCuenta t) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_tipocuenta_insert(?,?,?) }")) {
            cs.setString(1, t.getCodigo());
            cs.setString(2, t.getNombre());
            cs.setDouble(3, t.getTasaInteres());
            cs.executeUpdate();
        }
    }

    @Override
    public void actualizar(TipoCuenta t) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_tipocuenta_update(?,?,?) }")) {
            cs.setInt(1, t.getIdTipoCuenta());
            cs.setString(2, t.getNombre());
            cs.setDouble(3, t.getTasaInteres());
            cs.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_tipocuenta_delete(?) }")) {
            cs.setInt(1, id);
            cs.executeUpdate();
        }
    }

    @Override
    public TipoCuenta buscar(Integer id) throws Exception {
        String sql = "SELECT * FROM tipocuenta WHERE id_tipocuenta = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<TipoCuenta> listarTodos() throws Exception {
        List<TipoCuenta> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipocuenta ORDER BY id_tipocuenta";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private TipoCuenta mapear(ResultSet rs) throws Exception {
        TipoCuenta t = new TipoCuenta();
        t.setIdTipoCuenta(rs.getInt("id_tipocuenta"));
        t.setCodigo(rs.getString("codigo"));
        t.setNombre(rs.getString("nombre"));
        t.setTasaInteres(rs.getDouble("tasa_interes"));
        return t;
    }
}
