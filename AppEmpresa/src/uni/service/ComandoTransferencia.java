package uni.service;

import uni.dao.ProcesoBancarioDAO;
import uni.entity.Movimiento;

import java.math.BigDecimal;

/**
 * Patron COMMAND - comando concreto: TRANSFERENCIA.
 *
 * Si la transferencia es interna, delega en el procedimiento almacenado atomico
 * {@code sp_transferencia}. Si es interbancaria (a otra entidad), envia la orden
 * por la {@link RedInterbancaria} (patron Adapter -> Camara de Compensacion) y,
 * si es aceptada, debita localmente la cuenta origen.
 */
public class ComandoTransferencia implements Comando {

    private final ProcesoBancarioDAO dao;
    private final RedInterbancaria red;
    private final String origen;
    private final String destino;
    private final BigDecimal monto;
    private final int idEmpleado;
    private final boolean externa;

    public ComandoTransferencia(ProcesoBancarioDAO dao, RedInterbancaria red,
                                String origen, String destino, BigDecimal monto,
                                int idEmpleado, boolean externa) {
        this.dao = dao;
        this.red = red;
        this.origen = origen;
        this.destino = destino;
        this.monto = monto;
        this.idEmpleado = idEmpleado;
        this.externa = externa;
    }

    @Override
    public void ejecutar() throws Exception {
        if (externa) {
            // Adapter: envia la orden a la camara de compensacion externa.
            if (!red.transferir(destino, monto)) {
                throw new Exception("La camara de compensacion rechazo la transferencia.");
            }
            // Debita localmente la cuenta origen (retiro transaccional).
            dao.retirar(origen, monto, idEmpleado);
        } else {
            dao.transferir(origen, destino, monto, idEmpleado);
        }
    }

    @Override
    public Movimiento getMovimiento() {
        Movimiento m = new Movimiento();
        m.setTipo("TRANSFERENCIA");
        m.setNumeroCuenta(origen);
        m.setCuentaRelacionada(destino);
        m.setMonto(monto);
        m.setIdEmpleado(idEmpleado);
        return m;
    }
}
