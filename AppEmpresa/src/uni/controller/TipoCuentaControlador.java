package uni.controller;

import uni.dao.ControlDAO;
import uni.dao.TipoCuentaDAO;
import uni.entity.TipoCuenta;

import java.util.List;

/** Fachada de negocio para TipoCuenta. */
public class TipoCuentaControlador {

    private final TipoCuentaDAO dao = new TipoCuentaDAO();
    private final ControlDAO controlDAO = new ControlDAO();

    public void registrar(TipoCuenta t) throws Exception {
        validar(t);
        t.setCodigo(controlDAO.siguienteCodigo("TIPOCUENTA", "T", 4));
        dao.crear(t);
    }

    public void modificar(TipoCuenta t) throws Exception {
        validar(t);
        dao.actualizar(t);
    }

    public void eliminar(int idTipoCuenta) throws Exception {
        dao.eliminar(idTipoCuenta);
    }

    public List<TipoCuenta> listar() throws Exception {
        return dao.listarTodos();
    }

    private void validar(TipoCuenta t) {
        if (t.getNombre() == null || t.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del tipo de cuenta es obligatorio.");
        }
        if (t.getTasaInteres() < 0) {
            throw new IllegalArgumentException("La tasa de interes no puede ser negativa.");
        }
    }
}
