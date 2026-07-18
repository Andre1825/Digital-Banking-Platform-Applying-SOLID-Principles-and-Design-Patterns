package uni.service;

import uni.entity.Movimiento;
import uni.util.Log;

/**
 * Patron OBSERVER - observador concreto. Avisa al titular de la cuenta que se
 * registro una operacion sobre su cuenta.
 */
public class NotificadorCliente implements Observador {

    @Override
    public void actualizar(Movimiento m) {
        Log.info("NotificadorCliente", "Aviso al titular de la cuenta " + m.getNumeroCuenta()
                + ": se registro un " + m.getTipo() + " por S/ " + m.getMonto());
    }
}
