package uni.controller;

import uni.dao.ControlDAO;
import uni.dao.EmpleadoDAO;
import uni.entity.Empleado;
import uni.util.Validaciones;

import java.util.List;

/** Fachada de negocio para Empleado (usuarios del sistema). */
public class EmpleadoControlador {

    private final EmpleadoDAO dao = new EmpleadoDAO();
    private final ControlDAO controlDAO = new ControlDAO();

    public void registrar(Empleado e) throws Exception {
        validar(e, true);
        e.setCodigo(controlDAO.siguienteCodigo("EMPLEADO", "E", 4));
        dao.crear(e);
    }

    public void modificar(Empleado e) throws Exception {
        validar(e, false);
        dao.actualizar(e);
    }

    public void eliminar(int idEmpleado) throws Exception {
        dao.eliminar(idEmpleado);
    }

    public List<Empleado> listar() throws Exception {
        return dao.listarTodos();
    }

    private void validar(Empleado e, boolean esNuevo) {
        Validaciones.requerido(e.getNombres(), "nombres");
        Validaciones.requerido(e.getApellidos(), "apellidos");
        Validaciones.requerido(e.getUsuario(), "usuario");
        Validaciones.validarEmail(e.getEmail());
        if (esNuevo || (e.getClave() != null && !e.getClave().isEmpty())) {
            Validaciones.requerido(e.getClave(), "clave");
        }
        if (e.getRol() == null || e.getRol().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un rol (ADMIN o CAJERO).");
        }
    }
}
