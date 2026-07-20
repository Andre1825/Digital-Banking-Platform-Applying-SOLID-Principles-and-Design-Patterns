package uni.controller;

import uni.dao.ClienteDAO;
import uni.dao.ControlDAO;
import uni.entity.Cliente;
import uni.util.Validaciones;

import java.util.List;

public class ClienteControlador {

    private final ClienteDAO dao = new ClienteDAO();
    private final ControlDAO controlDAO = new ControlDAO();

    public void registrar(Cliente c) throws Exception {
        validar(c);
        c.setCodigo(controlDAO.siguienteCodigo("CLIENTE", "C", 4));
        dao.crear(c);
    }

    public void modificar(Cliente c) throws Exception {
        validar(c);
        dao.actualizar(c);
    }

    public void eliminar(int idCliente) throws Exception {
        dao.eliminar(idCliente);
    }

    public List<Cliente> listar() throws Exception {
        return dao.listarTodos();
    }

    /** Busqueda/filtro por dni, nombres o apellidos. */
    public List<Cliente> buscar(String texto) throws Exception {
        if (texto == null || texto.trim().isEmpty()) return dao.listarTodos();
        return dao.buscarPorTexto(texto.trim());
    }

    private void validar(Cliente c) {
        Validaciones.validarDni(c.getDni());
        Validaciones.requerido(c.getNombres(), "nombres");
        Validaciones.requerido(c.getApellidos(), "apellidos");
        Validaciones.validarEmail(c.getEmail());
    }
}
