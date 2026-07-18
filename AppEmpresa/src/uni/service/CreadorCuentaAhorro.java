package uni.service;

import uni.entity.Cuenta;

import java.math.BigDecimal;

/**
 * Patron FACTORY METHOD - creador concreto para cuentas de AHORRO.
 * Exige un monto minimo de apertura y arranca en estado ACTIVA.
 */
public class CreadorCuentaAhorro extends CreadorCuenta {

    @Override
    protected Cuenta crearCuenta() {
        Cuenta c = new Cuenta();
        c.setEstado("ACTIVA");
        return c;
    }

    @Override
    protected BigDecimal montoMinimoApertura() {
        return new BigDecimal("100");   // apertura minima de una cuenta de ahorro
    }

    @Override
    protected String nombreTipo() {
        return "de Ahorros";
    }
}
