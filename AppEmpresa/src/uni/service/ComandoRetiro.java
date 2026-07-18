package uni.service;

import uni.dao.ProcesoBancarioDAO;
import uni.entity.Movimiento;

import java.math.BigDecimal;

/** Patron COMMAND - comando concreto: RETIRO. */
public class ComandoRetiro implements Comando {

    private final ProcesoBancarioDAO dao;
    private final String numero;
    private final BigDecimal monto;
    private final int idEmpleado;

    public ComandoRetiro(ProcesoBancarioDAO dao, String numero, BigDecimal monto, int idEmpleado) {
        this.dao = dao;
        this.numero = numero;
        this.monto = monto;
        this.idEmpleado = idEmpleado;
    }

    @Override
    public void ejecutar() throws Exception {
        dao.retirar(numero, monto, idEmpleado);
    }

    @Override
    public Movimiento getMovimiento() {
        Movimiento m = new Movimiento();
        m.setTipo("RETIRO");
        m.setNumeroCuenta(numero);
        m.setMonto(monto);
        m.setIdEmpleado(idEmpleado);
        return m;
    }
}
