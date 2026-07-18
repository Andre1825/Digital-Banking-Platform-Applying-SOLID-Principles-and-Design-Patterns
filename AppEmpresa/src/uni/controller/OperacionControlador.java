package uni.controller;

import uni.dao.MovimientoDAO;
import uni.dao.ProcesoBancarioDAO;
import uni.entity.Movimiento;

import java.math.BigDecimal;
import java.util.List;

/**
 * Fachada de operaciones transaccionales (procesos) y consulta de movimientos.
 * Delega en ProcesoBancarioDAO (transacciones) y MovimientoDAO (lectura).
 */
public class OperacionControlador {

    private final ProcesoBancarioDAO procesoDAO = new ProcesoBancarioDAO();
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();

    public void depositar(String numero, BigDecimal monto, int idEmpleado) throws Exception {
        procesoDAO.depositar(numero, monto, idEmpleado);
    }

    public void retirar(String numero, BigDecimal monto, int idEmpleado) throws Exception {
        procesoDAO.retirar(numero, monto, idEmpleado);
    }

    public void transferir(String origen, String destino, BigDecimal monto, int idEmpleado) throws Exception {
        procesoDAO.transferir(origen, destino, monto, idEmpleado);
    }

    /** Estado de cuenta: movimientos de una cuenta entre dos fechas. */
    public List<Movimiento> estadoCuenta(String numeroCuenta, String desde, String hasta) throws Exception {
        return movimientoDAO.listarPorCuentaYFechas(numeroCuenta, desde, hasta);
    }
}
