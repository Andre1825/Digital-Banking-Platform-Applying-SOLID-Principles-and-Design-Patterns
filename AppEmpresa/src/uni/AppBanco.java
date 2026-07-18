package uni;

import uni.formularios.LoginBanco;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada UNICO del Sistema Bancario (interfaz grafica, CRUD real).
 * Ejecutar esta clase para abrir el login.
 *
 * Flujo: LoginBanco -> MenuPrincipal -> formularios -> controladores/Facade ->
 * comandos -> DAO (procedimientos almacenados) -> ConexionBD (Singleton) -> MySQL.
 */
public class AppBanco {

    public static void main(String[] args) {
        // Mantenemos el L&F Swing por defecto para que el sistema de diseno
        // (Tema) pinte correctamente esquinas redondeadas, botones y campos.
        SwingUtilities.invokeLater(() -> new LoginBanco().setVisible(true));
    }
}
