package uni.service;

import uni.entity.Cuenta;

import java.math.BigDecimal;

/**
 * Patron FACTORY METHOD - creador abstracto de cuentas.
 *
 * Define la plantilla comun de apertura ({@link #aperturar}) y delega en el
 * metodo fabrica {@link #crearCuenta()} la construccion del tipo concreto, que
 * cada subclase configura con sus propias reglas (monto minimo, estado inicial).
 * Agregar un nuevo tipo de cuenta es crear una subclase; no se modifica el
 * codigo cliente (principios OCP y DIP).
 */
public abstract class CreadorCuenta {

    /** Metodo fabrica: construye la cuenta concreta con sus valores por defecto. */
    protected abstract Cuenta crearCuenta();

    /** Monto minimo exigido para aperturar este tipo de cuenta. */
    protected abstract BigDecimal montoMinimoApertura();

    /** Nombre legible del tipo (para mensajes). */
    protected abstract String nombreTipo();

    /** Plantilla de apertura reutilizada por todos los tipos de cuenta. */
    public Cuenta aperturar(int idCliente, int idTipoCuenta, BigDecimal saldoInicial) {
        if (idCliente <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un cliente.");
        }
        if (idTipoCuenta <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de cuenta.");
        }
        BigDecimal saldo = (saldoInicial == null) ? BigDecimal.ZERO : saldoInicial;
        if (saldo.compareTo(montoMinimoApertura()) < 0) {
            throw new IllegalArgumentException("El saldo inicial minimo para una cuenta "
                    + nombreTipo() + " es S/ " + montoMinimoApertura() + ".");
        }
        Cuenta c = crearCuenta();          // <-- factory method
        c.setIdCliente(idCliente);
        c.setIdTipoCuenta(idTipoCuenta);
        c.setSaldo(saldo);
        return c;
    }
}
