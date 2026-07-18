package uni.service;

import uni.dao.AuditoriaDAO;
import uni.dao.AuditoriaDAOImpl;
import uni.entity.Movimiento;

/**
 * Patron OBSERVER - observador concreto. Persiste cada operacion confirmada en
 * la tabla de auditoria (trazabilidad exigida por normativa SBS).
 */
public class AuditorObserver implements Observador {

    private final AuditoriaDAO auditoria = new AuditoriaDAOImpl();

    @Override
    public void actualizar(Movimiento m) {
        String detalle = "Cuenta " + m.getNumeroCuenta() + " por S/ " + m.getMonto()
                + (m.getCuentaRelacionada() != null ? " -> " + m.getCuentaRelacionada() : "");
        auditoria.registrar("empleado#" + m.getIdEmpleado(), m.getTipo(), detalle);
    }
}
