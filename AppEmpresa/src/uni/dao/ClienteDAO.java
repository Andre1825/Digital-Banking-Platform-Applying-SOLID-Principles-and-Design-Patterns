package uni.dao;

import uni.database.ConexionBD;
import uni.entity.Cliente;
import uni.service.ICrudDao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** DAO de la entidad Cliente. */
public class ClienteDAO implements ICrudDao<Cliente, Integer> {

    @Override
    public void crear(Cliente c) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_cliente_insert(?,?,?,?,?,?,?) }")) {
            cs.setString(1, c.getCodigo());
            cs.setString(2, c.getDni());
            cs.setString(3, c.getNombres());
            cs.setString(4, c.getApellidos());
            cs.setString(5, c.getDireccion());
            cs.setString(6, c.getTelefono());
            cs.setString(7, c.getEmail());
            cs.executeUpdate();
        }
    }

    @Override
    public void actualizar(Cliente c) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_cliente_update(?,?,?,?,?,?,?) }")) {
            cs.setInt(1, c.getIdCliente());
            cs.setString(2, c.getDni());
            cs.setString(3, c.getNombres());
            cs.setString(4, c.getApellidos());
            cs.setString(5, c.getDireccion());
            cs.setString(6, c.getTelefono());
            cs.setString(7, c.getEmail());
            cs.executeUpdate();
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_cliente_delete(?) }")) {
            cs.setInt(1, id);
            cs.executeUpdate();
        }
    }

    @Override
    public Cliente buscar(Integer id) throws Exception {
        String sql = "SELECT * FROM cliente WHERE id_cliente = ?";
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
    public List<Cliente> listarTodos() throws Exception {
        return listar("SELECT * FROM cliente ORDER BY id_cliente", null);
    }

    /**
     * Filtra clientes por dni, nombres o apellidos (busqueda parcial).
     * Parametrizado con PreparedStatement para evitar inyeccion SQL.
     */
    public List<Cliente> buscarPorTexto(String texto) throws Exception {
        String sql = "SELECT * FROM cliente "
                   + "WHERE dni LIKE ? OR nombres LIKE ? OR apellidos LIKE ? "
                   + "ORDER BY id_cliente";
        return listar(sql, "%" + texto + "%");
    }

    private List<Cliente> listar(String sql, String filtro) throws Exception {
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (filtro != null) {
                ps.setString(1, filtro);
                ps.setString(2, filtro);
                ps.setString(3, filtro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /**
     * Autentica al cliente por usuario y clave (credenciales del portal digital).
     * Por defecto el usuario y la clave son iguales al DNI del cliente.
     * Devuelve el Cliente autenticado o null si las credenciales son incorrectas.
     */
    public Cliente autenticar(String usuario, String clave) throws Exception {
        String sql = "SELECT * FROM cliente WHERE usuario = ? AND clave = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.trim());
            ps.setString(2, clave);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    private Cliente mapear(ResultSet rs) throws Exception {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setCodigo(rs.getString("codigo"));
        c.setDni(rs.getString("dni"));
        c.setNombres(rs.getString("nombres"));
        c.setApellidos(rs.getString("apellidos"));
        c.setDireccion(rs.getString("direccion"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        try { c.setUsuario(rs.getString("usuario")); } catch (Exception ignore) { }
        return c;
    }
}
