package uni.dao;

import uni.database.ConexionBD;
import uni.entity.Movimiento;
import uni.service.IProceso;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;

/**
 * DAO de operaciones TRANSACCIONALES: deposito, retiro y transferencia.
 *
 * Cada operacion:
 *  1) genera el/los codigo(s) de movimiento con la tabla control,
 *  2) abre la conexion con autocommit DESACTIVADO,
 *  3) invoca el procedimiento almacenado (valida saldo, actualiza
 *     saldo(s) e inserta movimiento(s) de forma atomica),
 *  4) confirma (commit) si todo va bien, o revierte (rollback) ante error.
 *
 * Si el saldo es insuficiente, el procedimiento lanza SIGNAL SQLSTATE '45000'
 * y aqui se revierte la transaccion y se propaga el mensaje.
 */
public class ProcesoBancarioDAO implements IProceso<Movimiento> {

    private final ControlDAO controlDAO = new ControlDAO();

    /**
     * Punto de entrada generico (IProceso): despacha segun el tipo del
     * movimiento. Para transferencias usa numeroCuenta -> cuentaRelacionada.
     */
    @Override
    public void procesar(Movimiento m) throws Exception {
        switch (m.getTipo() == null ? "" : m.getTipo().toUpperCase()) {
            case "DEPOSITO":
                depositar(m.getNumeroCuenta(), m.getMonto(), m.getIdEmpleado());
                break;
            case "RETIRO":
                retirar(m.getNumeroCuenta(), m.getMonto(), m.getIdEmpleado());
                break;
            case "TRANSFERENCIA":
                transferir(m.getNumeroCuenta(), m.getCuentaRelacionada(),
                           m.getMonto(), m.getIdEmpleado());
                break;
            default:
                throw new IllegalArgumentException("Tipo de proceso no valido: " + m.getTipo());
        }
    }

    /** Deposito atomico en una cuenta. */
    public void depositar(String numero, BigDecimal monto, int idEmpleado) throws Exception {
        validarMonto(monto);
        String codigo = controlDAO.siguienteCodigo("MOVIMIENTO", "M", 6);
        ejecutarSimple("{ CALL sp_deposito(?,?,?,?) }", numero, monto, idEmpleado, codigo);
    }

    /** Retiro atomico; el procedimiento valida saldo suficiente. */
    public void retirar(String numero, BigDecimal monto, int idEmpleado) throws Exception {
        validarMonto(monto);
        String codigo = controlDAO.siguienteCodigo("MOVIMIENTO", "M", 6);
        ejecutarSimple("{ CALL sp_retiro(?,?,?,?) }", numero, monto, idEmpleado, codigo);
    }

    /** Transferencia atomica entre dos cuentas (dos movimientos). */
    public void transferir(String origen, String destino, BigDecimal monto, int idEmpleado) throws Exception {
        validarMonto(monto);
        if (origen != null && origen.equals(destino)) {
            throw new IllegalArgumentException("La cuenta origen y destino no pueden ser la misma.");
        }
        String codOrigen  = controlDAO.siguienteCodigo("MOVIMIENTO", "M", 6);
        String codDestino = controlDAO.siguienteCodigo("MOVIMIENTO", "M", 6);

        Connection con = null;
        try {
            con = ConexionBD.getInstancia().getConexion();
            con.setAutoCommit(false);                 // inicia transaccion
            try (CallableStatement cs = con.prepareCall("{ CALL sp_transferencia(?,?,?,?,?,?) }")) {
                cs.setString(1, origen);
                cs.setString(2, destino);
                cs.setBigDecimal(3, monto);
                cs.setInt(4, idEmpleado);
                cs.setString(5, codOrigen);
                cs.setString(6, codDestino);
                cs.execute();
            }
            con.commit();                              // confirma
        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ig) { /* ignore */ }
            throw e;
        } finally {
            cerrar(con);
        }
    }

    // ---- Soporte comun para deposito/retiro (un solo CALL) ----
    private void ejecutarSimple(String call, String numero, BigDecimal monto,
                                int idEmpleado, String codigo) throws Exception {
        Connection con = null;
        try {
            con = ConexionBD.getInstancia().getConexion();
            con.setAutoCommit(false);                 // inicia transaccion
            try (CallableStatement cs = con.prepareCall(call)) {
                cs.setString(1, numero);
                cs.setBigDecimal(2, monto);
                cs.setInt(3, idEmpleado);
                cs.setString(4, codigo);
                cs.execute();
            }
            con.commit();                              // confirma
        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (Exception ig) { /* ignore */ }
            throw e;
        } finally {
            cerrar(con);
        }
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
    }

    private void cerrar(Connection con) {
        if (con != null) {
            try { con.setAutoCommit(true); con.close(); } catch (Exception ig) { /* ignore */ }
        }
    }
}
