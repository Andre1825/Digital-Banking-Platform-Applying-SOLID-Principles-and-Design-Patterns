package uni.entity;

/**
 * Objeto de Transferencia (TO) de la tabla "tipocuenta".
 * Ejemplos: Ahorros, Corriente. La tasa de interes es anual.
 */
public class TipoCuenta {

    private int idTipoCuenta;
    private String codigo;          // codigo legible (ej. T0001)
    private String nombre;
    private double tasaInteres;     // tasa anual, ej. 0.0250 = 2.5%

    public TipoCuenta() { }

    public int getIdTipoCuenta() { return idTipoCuenta; }
    public void setIdTipoCuenta(int idTipoCuenta) { this.idTipoCuenta = idTipoCuenta; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getTasaInteres() { return tasaInteres; }
    public void setTasaInteres(double tasaInteres) { this.tasaInteres = tasaInteres; }

    @Override
    public String toString() {
        return nombre;
    }
}
