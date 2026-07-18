package uni.service;

import java.math.BigDecimal;

/**
 * Interfaz ESTABLE que la plataforma espera para enviar transferencias a otra
 * entidad financiera. El resto del sistema depende de esta abstraccion, no del
 * proveedor externo concreto (principio DIP).
 */
public interface RedInterbancaria {
    boolean transferir(String cuentaDestino, BigDecimal monto);
}
