package uni.service;

/**
 * Selector del creador ({@link CreadorCuenta}) segun el tipo de cuenta elegido.
 * Encapsula la decision de que subclase de fabrica usar, dejando a los clientes
 * (controlador/vista) desacoplados de las clases concretas.
 */
public class CuentaFactory {

    public static CreadorCuenta getCreador(String nombreTipo) {
        if (nombreTipo != null && nombreTipo.toLowerCase().contains("horro")) {
            return new CreadorCuentaAhorro();
        }
        return new CreadorCuentaCorriente();
    }
}
