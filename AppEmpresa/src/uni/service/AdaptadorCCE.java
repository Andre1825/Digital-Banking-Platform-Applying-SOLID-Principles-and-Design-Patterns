package uni.service;

import uni.util.Log;

import java.math.BigDecimal;

/**
 * Patron ADAPTER. Convierte la interfaz externa propietaria
 * ({@link WsCamaraCompensacion}, formato XML) en la interfaz
 * {@link RedInterbancaria} que la plataforma espera. Cambiar de proveedor de
 * compensacion solo afecta a esta clase (bajo acoplamiento).
 */
public class AdaptadorCCE implements RedInterbancaria {

    private final WsCamaraCompensacion ws = new WsCamaraCompensacion();

    @Override
    public boolean transferir(String cuentaDestino, BigDecimal monto) {
        String xml = aXml(cuentaDestino, monto);       // traduce al formato externo
        String resp = ws.submitOrder(xml);
        boolean ok = resp.contains("ACCEPTED");
        Log.info("AdaptadorCCE", "Resultado interbancario: " + (ok ? "ACEPTADA" : "RECHAZADA"));
        return ok;
    }

    private String aXml(String cuentaDestino, BigDecimal monto) {
        return "<order><dest>" + cuentaDestino + "</dest><amount>" + monto + "</amount></order>";
    }
}
