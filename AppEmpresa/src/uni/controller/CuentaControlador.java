package uni.controller;

import uni.dao.ControlDAO;
import uni.dao.CuentaDAO;
import uni.entity.Cuenta;
import uni.service.CreadorCuenta;
import uni.service.CuentaFactory;

import java.util.List;

/** Fachada de negocio para Cuenta (apertura y mantenimiento). */
public class CuentaControlador {

    private final CuentaDAO dao = new CuentaDAO();
    private final ControlDAO controlDAO = new ControlDAO();

    /**
     * Apertura de cuenta. Usa el patron FACTORY METHOD ({@link CuentaFactory})
     * para construir y validar la cuenta segun su tipo (Ahorros/Corriente),
     * luego genera el numero (10 digitos) y la registra.
     *
     * @param datos      cuenta con idCliente, idTipoCuenta y saldo inicial
     * @param tipoNombre nombre del tipo de cuenta (para elegir la fabrica)
     */
    public void aperturar(Cuenta datos, String tipoNombre) throws Exception {
        CreadorCuenta creador = CuentaFactory.getCreador(tipoNombre);
        Cuenta c = creador.aperturar(datos.getIdCliente(), datos.getIdTipoCuenta(), datos.getSaldo());
        c.setNumero(controlDAO.siguienteCodigo("CUENTA", "", 10));
        dao.crear(c);
        datos.setNumero(c.getNumero());   // devuelve el numero generado a la vista
    }

    public void modificar(Cuenta c) throws Exception {
        validar(c);
        dao.actualizar(c);
    }

    public void eliminar(String numero) throws Exception {
        dao.eliminar(numero);
    }

    public List<Cuenta> listar() throws Exception {
        return dao.listarTodos();
    }

    public List<Cuenta> listarPorCliente(int idCliente) throws Exception {
        return dao.listarPorCliente(idCliente);
    }

    public Cuenta buscar(String numero) throws Exception {
        return dao.buscar(numero);
    }

    private void validar(Cuenta c) {
        if (c.getIdCliente() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un cliente.");
        }
        if (c.getIdTipoCuenta() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de cuenta.");
        }
    }
}
