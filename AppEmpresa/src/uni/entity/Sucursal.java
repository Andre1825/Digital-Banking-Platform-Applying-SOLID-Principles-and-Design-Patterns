package uni.entity;

/**
 * Objeto de Transferencia (TO) de la tabla "sucursal".
 * Un campo por columna; solo getters/setters (sin logica de negocio).
 */
public class Sucursal {

    private int idSucursal;
    private String codigo;      // codigo legible generado (ej. S0001)
    private String nombre;
    private String direccion;
    private String telefono;

    public Sucursal() { }

    public Sucursal(int idSucursal, String codigo, String nombre,
                    String direccion, String telefono) {
        this.idSucursal = idSucursal;
        this.codigo = codigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public int getIdSucursal() { return idSucursal; }
    public void setIdSucursal(int idSucursal) { this.idSucursal = idSucursal; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
