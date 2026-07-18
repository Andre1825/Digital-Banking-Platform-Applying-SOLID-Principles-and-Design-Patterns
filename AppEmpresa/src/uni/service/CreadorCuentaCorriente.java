package uni.service;

import uni.entity.Cuenta;

import java.math.BigDecimal;

/**
 * Patron FACTORY METHOD - creador concreto para cuentas CORRIENTES.
 * Permite apertura sin saldo inicial (orientada a movimiento empresarial).
 */
public class CreadorCuentaCorriente extends CreadorCuenta {

    @Override
    protected Cuenta crearCuenta() {
        Cuenta c = new Cuenta();
        c.setEstado("ACTIVA");
        return c;
    }

    @Override
    protected BigDecimal montoMinimoApertura() {
        return BigDecimal.ZERO;
    }

    @Override
    protected String nombreTipo() {
        return "Corriente";
    }
}
