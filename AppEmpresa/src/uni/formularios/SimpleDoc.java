package uni.formularios;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * DocumentListener simple que ejecuta una accion ante cualquier cambio de
 * texto. Se usa para refrescar la tabla mientras se escribe en el filtro.
 */
public class SimpleDoc implements DocumentListener {

    private final Runnable accion;

    public SimpleDoc(Runnable accion) { this.accion = accion; }

    @Override public void insertUpdate(DocumentEvent e) { accion.run(); }
    @Override public void removeUpdate(DocumentEvent e) { accion.run(); }
    @Override public void changedUpdate(DocumentEvent e) { accion.run(); }
}
