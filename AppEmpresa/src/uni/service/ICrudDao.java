package uni.service;

import java.util.List;

/**
 * Contrato generico de mantenimiento (CRUD) para cualquier entidad.
 *
 * <T>  tipo de la entidad (Sucursal, Cliente, etc.)
 * <ID> tipo de la clave primaria (Integer, String, ...)
 *
 * Las implementaciones (capa dao) ejecutan los procedimientos almacenados
 * y mapean el ResultSet a la entidad. Las vistas NUNCA implementan esto.
 */
public interface ICrudDao<T, ID> {

    /** Inserta un nuevo registro. */
    void crear(T entidad) throws Exception;

    /** Actualiza un registro existente. */
    void actualizar(T entidad) throws Exception;

    /** Elimina un registro por su clave primaria. */
    void eliminar(ID id) throws Exception;

    /** Busca un registro por su clave primaria; null si no existe. */
    T buscar(ID id) throws Exception;

    /** Devuelve todos los registros. */
    List<T> listarTodos() throws Exception;
}
