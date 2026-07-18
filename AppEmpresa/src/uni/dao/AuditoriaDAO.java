package uni.dao;

/** Registro inmutable de actividad exigido por la normativa SBS. */
public interface AuditoriaDAO {
    void registrar(String usuario, String operacion, String detalle);
}
