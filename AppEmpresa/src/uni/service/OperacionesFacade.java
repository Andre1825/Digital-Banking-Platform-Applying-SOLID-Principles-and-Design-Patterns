package uni.service;

import uni.dao.MovimientoDAO;
import uni.dao.ProcesoBancarioDAO;
import uni.entity.Movimiento;

import java.math.BigDecimal;
import java.util.List;

/**
 * Patron FACADE - punto de entrada UNICO para las operaciones bancarias
 * (deposito, retiro, transferencia). Oculta a las vistas la coordinacion entre:
 *   - COMMAND  : encapsula cada operacion ({@link Comando}).
 *   - DAO      : ejecuta el procedimiento almacenado de forma transaccional.
 *   - OBSERVER : notifica auditoria y aviso al cliente tras confirmar.
 *   - ADAPTER  : enruta las transferencias externas por la camara de compensacion.
 *
 * Las vistas solo llaman a metodos de intencion clara; no conocen DAOs ni SQL.
 */
public class OperacionesFacade {

    private final ProcesoBancarioDAO procesoDAO = new ProcesoBancarioDAO();
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final RedInterbancaria red = new AdaptadorCCE();                 // Adapter
    private final RegistradorTransacciones registrador = new RegistradorTransacciones(); // Observer (sujeto)

    public OperacionesFacade() {
        // Suscripcion de observadores (OCP: agregar uno no cambia el flujo).
        registrador.suscribir(new AuditorObserver());
        registrador.suscribir(new NotificadorCliente());
    }

    public void depositar(String numero, BigDecimal monto, int idEmpleado) throws Exception {
        ejecutar(new ComandoDeposito(procesoDAO, numero, monto, idEmpleado));
    }

    public void retirar(String numero, BigDecimal monto, int idEmpleado) throws Exception {
        ejecutar(new ComandoRetiro(procesoDAO, numero, monto, idEmpleado));
    }

    public void transferir(String origen, String destino, BigDecimal monto,
                           int idEmpleado, boolean externa) throws Exception {
        ejecutar(new ComandoTransferencia(procesoDAO, red, origen, destino, monto, idEmpleado, externa));
    }

    /** Estado de cuenta: movimientos de una cuenta entre dos fechas (lectura). */
    public List<Movimiento> estadoCuenta(String numeroCuenta, String desde, String hasta) throws Exception {
        return movimientoDAO.listarPorCuentaYFechas(numeroCuenta, desde, hasta);
    }

    // Plantilla comun: ejecuta el comando (transaccional) y, si tuvo exito,
    // notifica a los observadores. Si el comando lanza excepcion (p. ej. saldo
    // insuficiente -> rollback), NO se notifica.
    private void ejecutar(Comando comando) throws Exception {
        comando.ejecutar();
        registrador.registrar(comando.getMovimiento());
    }
}
