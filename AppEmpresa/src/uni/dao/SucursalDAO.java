package uni.dao;

import uni.database.ConexionBD;
import uni.entity.Sucursal;
import uni.service.ICrudDao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la entidad Sucursal. Las altas/bajas/modificaciones se hacen por
 * procedimientos almacenados; las consultas con SELECT parametrizado.
 */
public class SucursalDAO implements ICrudDao<Sucursal, Integer> {

    @Override
    public void crear(Sucursal s) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_sucursal_insert(?,?,?,?) }")) {
            cs.setString(1, s.getCodigo());
            cs.setString(2, s.getNombre());
            cs.setString(3, s.getDireccion());
            cs.setString(4, s.getTelefono());
            cs.executeUpdate();
        }
    }

    @Override
    public void actualizar(Sucursal s) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_sucursal_update(?,?,?,?) }")) {
            cs.setInt(1, s.getIdSucursal());
            cs.setString(2, s.getNombre());
            cs.setString(3, s.getDireccion());
            cs.setString(4, s.getTelefono());
            cs.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_sucursal_delete(?) }")) {
            cs.setInt(1, id);
            cs.executeUpdate();
        }
    }

    @Override
    public Sucursal buscar(Integer id) throws Exception {
        String sql = "SELECT * FROM sucursal WHERE id_sucursal = ?";
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
    public List<Sucursal> listarTodos() throws Exception {
        List<Sucursal> lista = new ArrayList<>();
        String sql = "SELECT * FROM sucursal ORDER BY id_sucursal";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    private Sucursal mapear(ResultSet rs) throws Exception {
        Sucursal s = new Sucursal();
        s.setIdSucursal(rs.getInt("id_sucursal"));
        s.setCodigo(rs.getString("codigo"));
        s.setNombre(rs.getString("nombre"));
        s.setDireccion(rs.getString("direccion"));
        s.setTelefono(rs.getString("telefono"));
        return s;
    }
}
