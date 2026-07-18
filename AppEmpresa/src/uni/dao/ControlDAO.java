package uni.dao;

import uni.database.ConexionBD;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

/**
 * DAO de la tabla "control": genera codigos legibles autoincrementales.
 *
 * Usa el procedimiento almacenado sp_siguiente_valor, que incrementa el
 * contador y devuelve el nuevo valor de forma atomica (evita codigos
 * duplicados ante operaciones concurrentes).
 */
public class ControlDAO {

    /**
     * Devuelve el siguiente codigo formateado, ej. ("CLIENTE","C",4) -> "C0001".
     *
     * @param parametro nombre del contador en la tabla control
     * @param prefijo   letra(s) que anteceden al numero
     * @param ancho     cantidad de digitos (con ceros a la izquierda)
     */
    public String siguienteCodigo(String parametro, String prefijo, int ancho) throws Exception {
        try (Connection con = ConexionBD.getInstancia().getConexion();
             CallableStatement cs = con.prepareCall("{ CALL sp_siguiente_valor(?, ?) }")) {
            cs.setString(1, parametro);
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            int valor = cs.getInt(2);
            return prefijo + String.format("%0" + ancho + "d", valor);
        }
    }
}
