package uni.controller;

import uni.dao.EmpleadoDAO;
import uni.entity.Empleado;
import uni.util.Log;

/**
 * GRASP - Controlador de seguridad. Autentica al empleado del Sistema Bancario
 * (login contra la BD) y es el unico punto por donde pasa el control de acceso.
 */
public class SeguridadControlador {

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    /**
     * Inicia sesion. Devuelve el Empleado autenticado.
     * @throws SecurityException si las credenciales son invalidas.
     */
    public Empleado iniciarSesion(String usuario, String clave) throws Exception {
        if (usuario == null || usuario.trim().isEmpty()
                || clave == null || clave.isEmpty()) {
            throw new IllegalArgumentException("Ingrese usuario y clave.");
        }
        Empleado e = empleadoDAO.autenticar(usuario.trim(), clave);
        if (e == null) {
            throw new SecurityException("Usuario o clave incorrectos.");
        }
        Log.info("SeguridadControlador", "Acceso concedido a " + e.getUsuario());
        return e;
    }
}
