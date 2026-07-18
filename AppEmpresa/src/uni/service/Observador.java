package uni.service;

import uni.entity.Movimiento;

/**
 * Patron OBSERVER - interfaz del observador. Cada observador reacciona a una
 * operacion bancaria ya confirmada (deposito, retiro o transferencia).
 */
public interface Observador {
    void actualizar(Movimiento movimiento);
}
