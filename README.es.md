# AppEmpresa — Sistema de Banca Digital

Aplicación de escritorio Java construida con **NetBeans + Swing + MySQL**, con arquitectura en capas completa, principios **SOLID** y más de 15 patrones de diseño **GoF / GRASP**.

---

## Arquitectura

Un único árbol de fuentes `uni.*` organizado en un paquete por capa:

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Entity | `uni.entity` | Transfer Objects (un campo por columna de BD): `Sucursal`, `Empleado`, `Cliente`, `TipoCuenta`, `Cuenta`, `Movimiento`, `Control` |
| Service | `uni.service` | Interfaces genéricas `ICrudDao<T,ID>` e `IProceso<T>`, más todas las implementaciones de patrones |
| DAO | `uni.dao` | Un DAO por entidad. Escrituras via **stored procedures** (`CallableStatement`); lecturas via `PreparedStatement` parametrizado. `ProcesoBancarioDAO` gestiona operaciones transaccionales con commit/rollback explícito |
| Controller | `uni.controller` | Fachadas de negocio con métodos de nombre descriptivo — ninguna vista contiene SQL |
| Database | `uni.database` | `AccesoDB` — conexión JDBC por operación. `ConexionBD` — patrón **Singleton** |
| View | `uni.formularios` | Formularios Swing — cero SQL |

**Flujo de petición:** `Vista → Controlador → DAO → AccesoDB → MySQL`

---

## Patrones de Diseño

Los 15 patrones GoF/GRASP viven en `uni.service` / `uni.entity` y son ejecutados por `uni.Main`:

| Patrón | Ubicación |
|---|---|
| Singleton | `database/ConexionBD` |
| Factory Method | `service/ProductoFactory` |
| Abstract Factory | `service/CanalFactory` |
| Builder | `service/SolicitudBuilder` + `SolicitudDirector` |
| Prototype | `service/PlantillaProducto` |
| Adapter | `service/AdaptadorCCE` |
| Facade | `service/TransferenciaFacade` |
| Decorator | `service/SeguridadDecorator` |
| Composite | `service/GrupoProductos` / `ProductoSimple` |
| Proxy | `service/ConsultaCoreProxy` |
| Bridge | `service/Alerta` × `service/CanalEnvio` |
| Observer | `service/RegistradorTransacciones` |
| State | `entity/EstadoTarjeta` |
| Command | `service/Comando` + `ColaOperaciones` |
| Memento | `entity/PerfilMemento` + `service/HistorialPerfil` |

---

## Funcionalidades

- **CRUD completo** para cada entidad: Sucursales, Empleados, Clientes, Tipos de Cuenta y Cuentas — cada formulario incluye Agregar, Actualizar, Eliminar, búsqueda/filtro y una `JTable` que se refresca en tiempo real.
- **Operaciones transaccionales**: Depósito, Retiro y Transferencia — cada una llama un stored procedure con `autocommit=off`, haciendo commit al éxito y rollback ante error. El saldo se valida de forma atómica.
- **Login con roles**: `ADMIN` accede a todos los módulos; `CAJERO` queda restringido a clientes, cuentas, operaciones y estado de cuenta.
- **Códigos legibles autogenerados** via la tabla `control` (`C0001` clientes, `E0001` empleados, `S0001` sucursales, `T0001` tipos de cuenta, números de cuenta de 10 dígitos, `M000001` movimientos).
- **Manejo seguro de datos**: solo sentencias parametrizadas (sin SQL injection), try-with-resources, y validación de entrada con diálogos de error descriptivos.
- **Estado de cuenta**: filtrar los movimientos de un cliente por rango de fechas.
- **Dashboard**: totales en tiempo real de clientes, cuentas y depósitos acumulados.

---

## Inicio Rápido

### 1. Iniciar la base de datos

```bash
docker compose up -d
```

En el primer arranque, Docker ejecuta automáticamente `db/init/01_esquema.sql`, que crea la base de datos `banco`, las 8 tablas, los 19 stored procedures y los datos semilla. No se necesita configuración adicional — las credenciales en `docker-compose.yml` coinciden exactamente con `AccesoDB`.

| Parámetro | Valor |
|---|---|
| Base de datos | `banco` |
| Host | `localhost` |
| Puerto | `3306` |
| Usuario | `root` |
| Contraseña | `root` |

```bash
docker compose down        # detener, conservar datos
docker compose down -v     # detener, borrar todo
```

### 2. Ejecutar la aplicación

1. Colocar **MySQL Connector/J** (ej. `mysql-connector-j-9.7.0.jar`) en la carpeta `lib/` — ya referenciado en `nbproject/project.properties`.
2. Abrir el proyecto en NetBeans (o compilar con Ant).
3. Ejecutar **`uni.AppBanco`** para abrir la ventana de login.  
   *(Ejecutar `uni.Main` lanza la demo de patrones por consola.)*

### 3. Credenciales

```
Usuario: admin    Contraseña: admin    Rol: ADMIN
```

### Datos semilla

1 sucursal · 2 tipos de cuenta (Ahorros, Corriente) · 1 empleado administrador · 2 clientes de ejemplo · 2 cuentas activas (saldos 3 000.00 y 850.00). Todos los módulos son demostrables inmediatamente después de `docker compose up`.

---

## Diagrama de Clases

Ver [`docs/DiagramaClases.puml`](docs/DiagramaClases.puml).
