package uni.controller;

import uni.dao.ControlDAO;
import uni.dao.SucursalDAO;
import uni.entity.Sucursal;

import java.util.List;

/**
 * Fachada de negocio para Sucursal. Mantiene el SQL fuera de las vistas:
 * la vista solo llama a estos metodos de intencion clara.
 * Flujo: Vista -> Controlador -> DAO -> ConexionBD -> MySQL.
 */
public class SucursalControlador {

    private final SucursalDAO dao = new SucursalDAO();
    private final ControlDAO controlDAO = new ControlDAO();

    /** Registra una sucursal generando su codigo legible (S0001, ...). */
    public void registrar(Sucursal s) throws Exception {
        validar(s);
        s.setCodigo(controlDAO.siguienteCodigo("SUCURSAL", "S", 4));
        dao.crear(s);
    }

    public void modificar(Sucursal s) throws Exception {
        validar(s);
        dao.actualizar(s);
    }

    public void eliminar(int idSucursal) throws Exception {
        dao.eliminar(idSucursal);
    }

    public List<Sucursal> listar() throws Exception {
        return dao.listarTodos();
    }

    private void validar(Sucursal s) {
        if (s.getNombre() == null || s.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la sucursal es obligatorio.");
        }
    }
}
