package uni.entity;

/**
 * Entidad Cliente del Sistema Bancario (TO de la tabla "cliente").
 *
 * Para evitar duplicar columnas, varios accesores son alias del mismo campo:
 *   documento == dni     |     correo == email     |     id == idCliente
 */
public class Cliente {

    private int id;                 // PK (id / idCliente)
    private String codigo;          // codigo legible (ej. C0001)
    private String documento;       // documento / dni
    private String nombres;
    private String apellidos;
    private String direccion;
    private String telefono;
    private String correo;          // correo / email
    private String usuario;         // credencial de acceso al portal digital (= dni por defecto)

    public Cliente() { }

    // ---- Clave primaria (dos nombres, mismo campo) ----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdCliente() { return id; }
    public void setIdCliente(int id) { this.id = id; }

    // ---- Codigo legible ----
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    // ---- Documento / DNI (mismo campo) ----
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getDni() { return documento; }
    public void setDni(String dni) { this.documento = dni; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    // ---- Correo / Email (mismo campo) ----
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getEmail() { return correo; }
    public void setEmail(String email) { this.correo = email; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getNombreCompleto() {
        return (nombres == null ? "" : nombres) + " " + (apellidos == null ? "" : apellidos);
    }

    @Override
    public String toString() {
        if (codigo != null) {
            return codigo + " - " + getNombreCompleto().trim();
        }
        return nombres + " (" + documento + ")";
    }
}
