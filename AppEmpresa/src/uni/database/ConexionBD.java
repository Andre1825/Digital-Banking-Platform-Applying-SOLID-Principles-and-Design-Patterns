package uni.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Patron SINGLETON - punto de acceso unico a la base de datos "banco" (MySQL).
 *
 * Toda la aplicacion obtiene sus conexiones a traves de la UNICA instancia de
 * esta clase ({@link #getInstancia()}), centralizando la configuracion JDBC y
 * el registro del driver. Se usa doble verificacion (double-checked locking)
 * para ser seguro en entornos concurrentes.
 *
 * Cada operacion pide una conexion nueva con {@link #getConexion()} y la cierra
 * con try-with-resources en el DAO. Flujo del sistema:
 *   Vista -> Controlador/Facade -> Comando -> DAO -> ConexionBD -> MySQL.
 */
public class ConexionBD {

    private static volatile ConexionBD instancia;

    // Coincide con el servicio MySQL de docker-compose (contenedor banco_mysql).
    private static final String URL =
            "jdbc:mysql://localhost:3306/banco"
          + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String CLAVE   = "root";

    private ConexionBD() {                         // constructor privado (Singleton)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");   // registro del driver
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL no encontrado. "
                    + "Agregue mysql-connector-j a la carpeta lib/. " + e.getMessage());
        }
    }

    /** Devuelve la unica instancia (creada de forma perezosa y segura). */
    public static ConexionBD getInstancia() {
        if (instancia == null) {
            synchronized (ConexionBD.class) {
                if (instancia == null) {
                    instancia = new ConexionBD();
                }
            }
        }
        return instancia;
    }

    /**
     * Entrega una conexion NUEVA lista para usar. El DAO es responsable de
     * cerrarla (try-with-resources).
     */
    public Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    /** Prueba rapida de conectividad (usada por el login y el dashboard). */
    public boolean probarConexion() {
        try (Connection con = getConexion()) {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            System.err.println("No hay conexion con la BD: " + e.getMessage());
            return false;
        }
    }
}
