package uni.util;

/**
 * Utilidades de validacion de entrada reutilizadas por los controladores.
 * Lanzan IllegalArgumentException con un mensaje claro para mostrar en dialogos.
 */
public final class Validaciones {

    private Validaciones() { }

    public static void requerido(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + campo + "' es obligatorio.");
        }
    }

    /** Documento numerico de 8 a 15 digitos. */
    public static void validarDni(String dni) {
        requerido(dni, "documento");
        if (!dni.matches("\\d{8,15}")) {
            throw new IllegalArgumentException("El documento debe tener entre 8 y 15 digitos numericos.");
        }
    }

    /** Formato basico de correo (permite vacio si no es obligatorio). */
    public static void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) return;
        if (!email.matches("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("El correo electronico no tiene un formato valido.");
        }
    }
}
