package uni.entity;

/**
 * Objeto de Transferencia (TO) de la tabla "empleado".
 * El rol determina el acceso a los modulos (ADMIN, CAJERO).
 */
public class Empleado {

    private int idEmpleado;
    private String codigo;      // codigo legible (ej. E0001)
    private String nombres;
    private String apellidos;
    private String email;
    private String usuario;
    private String clave;
    private String rol;         // ADMIN, CAJERO
    private int idSucursal;     // sucursal a la que pertenece (0 = sin asignar)
    private String nombreSucursal; // solo lectura para mostrar en tablas

    public Empleado() { }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public int getIdSucursal() { return idSucursal; }
    public void setIdSucursal(int idSucursal) { this.idSucursal = idSucursal; }

    public String getNombreSucursal() { return nombreSucursal; }
    public void setNombreSucursal(String nombreSucursal) { this.nombreSucursal = nombreSucursal; }

    public String getNombreCompleto() {
        return (nombres == null ? "" : nombres) + " " + (apellidos == null ? "" : apellidos);
    }

    public boolean esAdmin() { return "ADMIN".equalsIgnoreCase(rol); }

    @Override
    public String toString() {
        return getNombreCompleto().trim() + " (" + rol + ")";
    }
}
