package uni.service;

import uni.entity.Movimiento;

/**
 * Patron COMMAND - encapsula una operacion bancaria (deposito, retiro o
 * transferencia) como un objeto. Desacopla al invocador ({@link OperacionesFacade})
 * de los detalles de ejecucion en el DAO transaccional.
 */
public interface Comando {

    /** Ejecuta la operacion (transaccional: commit/rollback en el DAO). */
    void ejecutar() throws Exception;

    /** Describe la operacion realizada, para notificar a los observadores. */
    Movimiento getMovimiento();
}
