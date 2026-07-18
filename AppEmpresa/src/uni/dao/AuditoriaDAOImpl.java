package uni.dao;

import uni.database.ConexionBD;
import uni.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AuditoriaDAOImpl implements AuditoriaDAO {

    @Override
    public void registrar(String usuario, String operacion, String detalle) {
        // Traza siempre en consola (auditoria de demostracion)
        Log.info("AUDITORIA", usuario + " | " + operacion + " | " + detalle);

        String sql = "INSERT INTO auditoria(usuario, operacion, detalle, fecha) "
                   + "VALUES(?,?,?, NOW())";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, operacion);
            ps.setString(3, detalle);
            ps.executeUpdate();
        } catch (Exception e) {
            // Sin BD se conserva la traza de consola; la auditoria no debe
            // romper la operacion principal.
            System.err.println("Error al auditar: " + e.getMessage());
        }
    }
}
