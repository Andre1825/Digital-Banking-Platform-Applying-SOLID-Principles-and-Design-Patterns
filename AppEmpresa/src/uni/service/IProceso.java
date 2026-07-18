package uni.service;

/**
 * Contrato generico para operaciones transaccionales (procesos de negocio).
 *
 * A diferencia de {@link ICrudDao}, "procesar" implica una transaccion
 * explicita: validar reglas, modificar varias filas/tablas y confirmar
 * (commit) o revertir (rollback) de forma atomica.
 */
public interface IProceso<T> {

    /** Ejecuta el proceso de negocio de forma atomica. */
    void procesar(T datos) throws Exception;
}
