package uni.service;

import uni.util.Log;

/**
 * Servicio EXTERNO (simulado) de la Camara de Compensacion Electronica (CCE).
 * Expone una API propietaria basada en XML, incompatible con la interfaz
 * {@link RedInterbancaria} que usa la plataforma. Por eso se adapta.
 */
public class WsCamaraCompensacion {

    public String submitOrder(String xml) {
        Log.info("WS-CCE", "submitOrder() recibido: " + xml);
        return "<resp><code>ACCEPTED</code></resp>";
    }

    public String checkStatus(String id) {
        return "<resp><status>SETTLED</status></resp>";
    }
}
