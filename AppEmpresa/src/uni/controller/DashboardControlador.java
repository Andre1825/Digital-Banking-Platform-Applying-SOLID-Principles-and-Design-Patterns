package uni.controller;

import uni.dao.DashboardDAO;

import java.math.BigDecimal;

/** Fachada para los indicadores del panel de inicio. */
public class DashboardControlador {

    private final DashboardDAO dao = new DashboardDAO();

    public int totalClientes() throws Exception { return dao.contarClientes(); }

    public int totalCuentas() throws Exception { return dao.contarCuentas(); }

    public BigDecimal totalDepositado() throws Exception { return dao.totalDepositado(); }
}
