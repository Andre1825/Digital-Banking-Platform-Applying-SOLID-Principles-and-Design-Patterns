package uni.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Utilitario de consola para trazas y auditoria de demostracion. */
public final class Log {

    private static final DateTimeFormatter F =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Log() { }

    public static void info(String origen, String mensaje) {
        System.out.println("[" + LocalDateTime.now().format(F) + "] [" + origen + "] " + mensaje);
    }

    public static void aviso(String origen, String mensaje) {
        System.out.println("[" + LocalDateTime.now().format(F) + "] [" + origen + "] (!) " + mensaje);
    }
}
