package uni.entity;

import java.math.BigDecimal;

/**
 * Objeto de Transferencia (TO) de la tabla "cuenta".
 * La clave primaria es el numero de cuenta (codigo legible generado).
 */
public class Cuenta {

    private String numero;
    private int idCliente;
    private int idTipoCuenta;
    private BigDecimal saldo;
    private String fechaApertura;   // formato yyyy-MM-dd
    private String estado;          // ACTIVA, BLOQUEADA

    // Campos solo lectura para mostrar en tablas (joins)
    private String nombreCliente;
    private String nombreTipoCuenta;

    public Cuenta() {
        this.saldo = BigDecimal.ZERO;
        this.estado = "ACTIVA";
    }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdTipoCuenta() { return idTipoCuenta; }
    public void setIdTipoCuenta(int idTipoCuenta) { this.idTipoCuenta = idTipoCuenta; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public String getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(String fechaApertura) { this.fechaApertura = fechaApertura; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getNombreTipoCuenta() { return nombreTipoCuenta; }
    public void setNombreTipoCuenta(String nombreTipoCuenta) { this.nombreTipoCuenta = nombreTipoCuenta; }

    public boolean estaActiva() { return "ACTIVA".equalsIgnoreCase(estado); }

    @Override
    public String toString() {
        return numero + (nombreCliente != null ? " - " + nombreCliente : "");
    }
}
