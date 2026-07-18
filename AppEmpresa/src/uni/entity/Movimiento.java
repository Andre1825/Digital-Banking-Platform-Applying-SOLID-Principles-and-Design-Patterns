package uni.entity;

import java.math.BigDecimal;

/**
 * Objeto de Transferencia (TO) de la tabla "movimiento".
 * Registra cada operacion transaccional (DEPOSITO, RETIRO, TRANSFERENCIA).
 */
public class Movimiento {

    private int idMovimiento;
    private String codigo;              // codigo legible (ej. M0001)
    private String numeroCuenta;
    private String tipo;               // DEPOSITO, RETIRO, TRANSFERENCIA
    private BigDecimal monto;
    private String fecha;              // yyyy-MM-dd HH:mm:ss
    private int idEmpleado;
    private String cuentaRelacionada;   // cuenta destino/origen en transferencias
    private BigDecimal saldoResultante;

    public Movimiento() { }

    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getCuentaRelacionada() { return cuentaRelacionada; }
    public void setCuentaRelacionada(String cuentaRelacionada) { this.cuentaRelacionada = cuentaRelacionada; }

    public BigDecimal getSaldoResultante() { return saldoResultante; }
    public void setSaldoResultante(BigDecimal saldoResultante) { this.saldoResultante = saldoResultante; }

    @Override
    public String toString() {
        return codigo + " " + tipo + " " + monto;
    }
}
