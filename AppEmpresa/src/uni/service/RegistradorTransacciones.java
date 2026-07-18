package uni.service;

import uni.entity.Movimiento;
import uni.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Patron OBSERVER - sujeto observable. Al registrar una operacion confirmada
 * notifica a todos los observadores suscritos (auditoria, notificador al
 * cliente, ...) sin acoplarse a ellos: agregar un nuevo observador no obliga a
 * modificar esta clase (principio OCP).
 */
public class RegistradorTransacciones {

    private final List<Observador> observadores = new ArrayList<>();

    public void suscribir(Observador o) { observadores.add(o); }

    /** Notifica a los observadores que una operacion se confirmo (post-commit). */
    public void registrar(Movimiento m) {
        Log.info("Registrador", "Operacion confirmada: " + m);
        for (Observador o : observadores) o.actualizar(m);
    }
}
