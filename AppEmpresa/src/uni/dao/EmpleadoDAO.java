package uni.dao;

import uni.database.ConexionBD;
import uni.entity.Empleado;
import uni.service.ICrudDao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** DAO de la entidad Empleado, mas la autenticacion para el login. */
public class EmpleadoDAO implements ICrudDao<Empleado, Integer> {

    // SELECT con join a sucursal para mostrar el nombre en las tablas.
    private static final String BASE =
            "SELECT e.*, s.nombre AS sucursal_nombre "
          + "FROM empleado e LEFT JOIN sucursal s ON e.id_sucursal = s.id_sucursal ";

    @Override
    public void crear(Empleado e) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_empleado_insert(?,?,?,?,?,?,?,?) }")) {
            cs.setString(1, e.getCodigo());
            cs.setString(2, e.getNombres());
            cs.setString(3, e.getApellidos());
            cs.setString(4, e.getEmail());
            cs.setString(5, e.getUsuario());
            cs.setString(6, e.getClave());
            cs.setString(7, e.getRol());
            setSucursal(cs, 8, e.getIdSucursal());
            cs.executeUpdate();
        }
    }

    @Override
    public void actualizar(Empleado e) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_empleado_update(?,?,?,?,?,?,?,?) }")) {
            cs.setInt(1, e.getIdEmpleado());
            cs.setString(2, e.getNombres());
            cs.setString(3, e.getApellidos());
            cs.setString(4, e.getEmail());
            cs.setString(5, e.getUsuario());
            cs.setString(6, e.getClave());
            cs.setString(7, e.getRol());
            setSucursal(cs, 8, e.getIdSucursal());
            cs.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_empleado_delete(?) }")) {
            cs.setInt(1, id);
            cs.executeUpdate();
        }
    }

    @Override
    public Empleado buscar(Integer id) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(BASE + "WHERE e.id_empleado = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<Empleado> listarTodos() throws Exception {
        List<Empleado> lista = new ArrayList<>();
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(BASE + "ORDER BY e.id_empleado");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /**
     * Valida usuario y clave. Devuelve el Empleado autenticado o null.
     * Consulta parametrizada -> sin riesgo de inyeccion SQL.
     */
    public Empleado autenticar(String usuario, String clave) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(BASE + "WHERE e.usuario = ? AND e.clave = ?")) {
            ps.setString(1, usuario);
            ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    private void setSucursal(CallableStatement cs, int idx, int idSucursal) throws Exception {
        if (idSucursal > 0) cs.setInt(idx, idSucursal);
        else cs.setNull(idx, java.sql.Types.INTEGER);
    }

    private Empleado mapear(ResultSet rs) throws Exception {
        Empleado e = new Empleado();
        e.setIdEmpleado(rs.getInt("id_empleado"));
        e.setCodigo(rs.getString("codigo"));
        e.setNombres(rs.getString("nombres"));
        e.setApellidos(rs.getString("apellidos"));
        e.setEmail(rs.getString("email"));
        e.setUsuario(rs.getString("usuario"));
        e.setClave(rs.getString("clave"));
        e.setRol(rs.getString("rol"));
        e.setIdSucursal(rs.getInt("id_sucursal"));
        e.setNombreSucursal(rs.getString("sucursal_nombre"));
        return e;
    }
}
