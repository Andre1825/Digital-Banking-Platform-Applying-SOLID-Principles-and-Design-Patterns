# AppEmpresa — Banking System + Design Patterns Demo

This is **one integrated project** (single `uni.*` source tree, no duplicated
layers). The same layered packages hold **both**:

1. **`SistemaBancario`** — a complete desktop **banking system** with real,
   end-to-end CRUD, transactional operations, role-based login and a Dashboard.
   Launch it from **`uni.AppBanco`**. **This is the main deliverable.**
2. **Patterns demo** — the original console program (`uni.Main`) that showcases
   the GoF/GRASP design patterns. Still works, fully preserved.

The two parts share entities (e.g. one unified `Cliente`) and the security
controller, so nothing is duplicated.

---

## SistemaBancario (English)

### Layered architecture (one package per layer, under `src/uni`)
| Layer | Package | Responsibility |
|-------|---------|----------------|
| entity      | `uni.entity`      | Transfer Objects (one field per column): `Sucursal`, `Empleado`, `Cliente`, `TipoCuenta`, `Cuenta`, `Movimiento`, `Control` (plus the pattern-demo entities). |
| service     | `uni.service`     | Generic interfaces `ICrudDao<T,ID>` (crear/actualizar/eliminar/buscar/listarTodos) and `IProceso<T>` (procesar), alongside the pattern classes. |
| dao         | `uni.dao`         | One DAO per entity. Insert/Update/Delete via **stored procedures** (`CallableStatement`); reads via parameterized `PreparedStatement`. `ProcesoBancarioDAO` runs deposit/withdrawal/transfer inside explicit transactions. `ControlDAO` generates codes. |
| controller  | `uni.controller`  | Business facades with intention-revealing methods; **all SQL stays out of the views**. `SeguridadControlador` does login **and** the Memento demo. |
| database    | `uni.database`    | `AccesoDB` — central JDBC connection for the CRUD (one connection per op). `ConexionBD` — the **Singleton** pattern, pointed at the same `banco` DB. |
| view        | `uni.formularios` | Swing forms. **No SQL in any view.** |

**Flow is always:** `View → Controller → DAO → AccesoDB → MySQL`.

### Design patterns preserved
All GoF/GRASP patterns from the original project remain in `uni.service` /
`uni.entity` (Singleton, Factory Method, Abstract Factory, Builder, Prototype,
Adapter, Facade, Decorator, Composite, Proxy, Bridge, Observer, State, Command,
Memento) and are still exercised by `uni.Main`.

### Features
- **Full CRUD** maintenance form for every entity: Branches, Employees,
  Customers, Account Types and Accounts. Each form has Add, Update, Delete,
  Clear, a `JTable` listing, a search/filter field, and the table refreshes
  after every operation.
- **Transactional operations** as separate process forms: **Deposit**,
  **Withdrawal**, **Transfer**. They call stored procedures with
  `autocommit=off`, `commit` on success and `rollback` on error. Withdrawal and
  transfer validate sufficient balance atomically.
- **Login + role-based access**: the main menu enables/disables modules
  depending on the employee role (`ADMIN` sees everything; `CAJERO` sees
  customers, accounts, operations and the statement).
- **Auto-generated human-readable codes** via the `control` table
  (`C0001` customers, `E0001` employees, `S0001` branches, `T0001` account
  types, 10-digit account numbers, `M000001` movements).
- **Robust data handling**: parameterized statements (no string concatenation
  → no SQL injection), try-with-resources, and input validation with clear
  error dialogs (no negative amounts, required fields, valid DNI/email).
- **Account statement** form: filter a customer's account movements by date
  range and list them in a table.
- **Dashboard** home window: totals for customers, accounts and total deposits.

### 1) Start the database with Docker
From the project root (this folder):

```bash
docker compose up -d        # first run also loads db/init/01_esquema.sql
```

This creates the `banco` database, all tables, all stored procedures and the
seed data. The credentials/port in `docker-compose.yml` match
`uni.database.AccesoDB` exactly, so **no extra configuration is needed**.

- DB name: `banco` · host `localhost` · port `3306` · user `root` · pass `root`
- Stop (keep data): `docker compose down` · Reset everything: `docker compose down -v`

### 2) Run the application
1. Put the **MySQL Connector/J** jar in `lib/` (e.g. `mysql-connector-j-9.7.0.jar`,
   already referenced in `nbproject/project.properties`).
2. Open the project in NetBeans (or compile with Ant), then run the class
   **`uni.AppBanco`** to open the login window.
   *(Running `uni.Main` instead launches the console patterns demo.)*

### 3) Login credentials
```
user: admin    password: admin    role: ADMIN
```

### Seed/sample data
1 branch, 2 account types (Ahorros, Corriente), the `admin` employee, 2 sample
customers and 2 active accounts (balances 3000.00 and 850.00) — every module is
demonstrable right after `docker compose up`.

---

# AppEmpresa - Plataforma de Banca Digital (PBD)
**Banco Andino del Sur** — Proyecto del curso *Diseño de Patrones*

> ### ⭐ Nueva interfaz gráfica: `uni.formularios.AppBancaDigital`
> Ejecute la clase **`uni.formularios.AppBancaDigital`** para abrir el tablero
> moderno de Banca Digital. Es **autocontenido** (funciona en modo simulado, sin
> necesidad de MySQL) y demuestra visualmente, módulo por módulo, los **10
> requisitos funcionales (RF01–RF10)** y los **10 casos de prueba (CP01–CP10)**,
> con una **consola de patrones** integrada que muestra las trazas en vivo.
>
> | Módulo (menú lateral) | Cubre | Patrones |
> |---|---|---|
> | Inicio (tablero gerencial) | RF05, RF10 | Composite, indicadores |
> | Acceso & Sesión | RF01 | Singleton, 2FA/OTP real, sesiones |
> | Productos | RF02, CP07 | Factory Method (5 productos) |
> | Transferencias | RF03, CP01–CP03 | Facade, Adapter, Observer |
> | Pago de servicios | RF04, RF09 | Command, Pasarela (Adapter) |
> | Posición consolidada | RF05 | Composite, Proxy (permisos+caché) |
> | Seguridad & Perfil | RF06, CP09 | Memento |
> | Créditos | RF07, CP08 | Builder + score |
> | Monitoreo & Tarjetas | RF08, CP04 | Observer + State (auto-bloqueo 3 intentos) |
> | Operaciones (Cola) | CP05, CP06 | Command (reintento + extorno) |
> | Integraciones | RF09 | Adapter, Singleton, DIP |
> | Innovación (OCP) | CP10 | OCP (InteresPlazoEscalonado), Abstract Factory, Prototype |
>
> El **diagrama de clases** completo está en [`docs/DiagramaClases.puml`](docs/DiagramaClases.puml).


Aplicación Java (NetBeans + Swing + MySQL) en arquitectura por capas que
implementa los principios **SOLID** y los patrones **GOF** y **GRASP**
descritos en el informe del proyecto.

## Estructura de paquetes (`src/uni`)
| Paquete        | Contenido |
|----------------|-----------|
| `entity`       | Modelo de dominio: Cliente, ProductoFinanciero (LSP), CuentaAhorro, TarjetaCredito (State), PerfilSeguridad (Memento), etc. |
| `database`     | `ConexionBD` — **Singleton** de conexión a MySQL. |
| `dao`          | Repositorios (interfaces = **DIP**) e implementaciones JDBC. |
| `service`      | Lógica de negocio y patrones: Factory Method, Abstract Factory, Builder, Prototype, Adapter, Facade, Decorator, Composite, Proxy, Bridge, Observer, Command, estrategias OCP, etc. |
| `controller`   | Controladores **GRASP** (un punto de entrada por caso de uso). |
| `formularios`  | Interfaz Swing (`LoginForm` con doble factor). |
| `reportes`     | Reporte de posición consolidada (usa Composite). |
| `util`         | Enums y excepciones de negocio. |

## Patrones por ubicación
- **Singleton** → `database/ConexionBD`
- **Factory Method** → `service/ProductoFactory` (+ subclases)
- **Abstract Factory** → `service/CanalFactory` (web/móvil)
- **Builder** → `service/SolicitudBuilder` + `SolicitudDirector`
- **Prototype** → `service/PlantillaProducto`
- **Adapter** → `service/AdaptadorCCE` (red interbancaria)
- **Facade** → `service/TransferenciaFacade`
- **Decorator** → `service/SeguridadDecorator` (2FA, antifraude, auditoría)
- **Composite** → `service/GrupoProductos` / `ProductoSimple`
- **Proxy** → `service/ConsultaCoreProxy` (permisos + caché)
- **Bridge** → `service/Alerta` × `service/CanalEnvio`
- **Observer** → `service/RegistradorTransacciones` + observadores
- **State** → `entity/EstadoTarjeta` (+ estados)
- **Command** → `service/Comando` + `ColaOperaciones`
- **Memento** → `entity/PerfilMemento` + `service/HistorialPerfil`

## Cómo ejecutar
1. **Importar a NetBeans:** *File → Open Project* y seleccionar la carpeta `AppEmpresa`.
2. **Librerías:** colocar en `lib/` los JAR `mysql-connector-j-9.7.0.jar` y `ojdbc6.jar`
   (ya referenciados en `nbproject/project.properties`).
3. **Base de datos (opcional):** ejecutar el script `SP.txt` en MySQL para crear el
   esquema `banca_digital`. Ajustar usuario/clave en `database/ConexionBD.java`.
4. **Demostración por consola:** ejecutar la clase `uni.Main` — recorre los 15 patrones
   reproduciendo los casos de prueba del Capítulo X del informe.
5. **Interfaz gráfica:** ejecutar `uni.formularios.LoginForm`.

> El sistema funciona aunque MySQL no esté disponible: las operaciones se ejecutan
> en modo simulado y la auditoría se registra en consola.
