# AppEmpresa — Digital Banking System

Java desktop application built with **NetBeans + Swing + MySQL**, featuring a full layered architecture, **SOLID** principles, and 15+ **GoF / GRASP** design patterns.

---

## Architecture

Single `uni.*` source tree organized in one package per layer:

| Layer | Package | Responsibility |
|---|---|---|
| Entity | `uni.entity` | Transfer objects (one field per DB column): `Sucursal`, `Empleado`, `Cliente`, `TipoCuenta`, `Cuenta`, `Movimiento`, `Control` |
| Service | `uni.service` | Generic interfaces `ICrudDao<T,ID>` and `IProceso<T>`, plus all pattern implementations |
| DAO | `uni.dao` | One DAO per entity. Writes via **stored procedures** (`CallableStatement`); reads via parameterized `PreparedStatement`. `ProcesoBancarioDAO` handles transactional operations with explicit commit/rollback |
| Controller | `uni.controller` | Business facades with intention-revealing methods — no SQL in views |
| Database | `uni.database` | `AccesoDB` — JDBC connection per operation. `ConexionBD` — **Singleton** pattern |
| View | `uni.formularios` | Swing forms — zero SQL |

**Request flow:** `View → Controller → DAO → AccesoDB → MySQL`

---

## Design Patterns

All 15 GoF/GRASP patterns live in `uni.service` / `uni.entity` and are exercised by `uni.Main`:

| Pattern | Location |
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

## Features

- **Full CRUD** for every entity: Branches, Employees, Customers, Account Types and Accounts — each form includes Add, Update, Delete, search/filter and a live-refreshing `JTable`.
- **Transactional operations**: Deposit, Withdrawal and Transfer — each calls a stored procedure with `autocommit=off`, committing on success and rolling back on error. Balance is validated atomically.
- **Role-based login**: `ADMIN` accesses all modules; `CAJERO` is restricted to customers, accounts, operations and statements.
- **Auto-generated human-readable codes** via the `control` table (`C0001` customers, `E0001` employees, `S0001` branches, `T0001` account types, 10-digit account numbers, `M000001` movements).
- **Secure data handling**: parameterized statements only (no SQL injection), try-with-resources, and input validation with descriptive error dialogs.
- **Account statement**: filter a customer's movements by date range.
- **Dashboard**: real-time totals for customers, accounts and total deposits.

---

## Quick Start

### 1. Start the database

```bash
docker compose up -d
```

On first run, Docker automatically executes `db/init/01_esquema.sql`, which creates the `banco` database, all 8 tables, 19 stored procedures and the seed data. No extra configuration needed — credentials in `docker-compose.yml` match `AccesoDB` exactly.

| Setting | Value |
|---|---|
| Database | `banco` |
| Host | `localhost` |
| Port | `3306` |
| User | `root` |
| Password | `root` |

```bash
docker compose down        # stop, keep data
docker compose down -v     # stop, wipe everything
```

### 2. Run the application

1. Place **MySQL Connector/J** (e.g. `mysql-connector-j-9.7.0.jar`) in the `lib/` folder — already referenced in `nbproject/project.properties`.
2. Open the project in NetBeans (or compile with Ant).
3. Run **`uni.AppBanco`** to open the login window.  
   *(Run `uni.Main` instead to launch the console design-patterns demo.)*

### 3. Login

```
Username: admin    Password: admin    Role: ADMIN
```

### Seed data

1 branch · 2 account types (Savings, Checking) · 1 admin employee · 2 sample customers · 2 active accounts (balances 3 000.00 and 850.00). Every module is demonstrable immediately after `docker compose up`.

---

## Class Diagram

See [`docs/DiagramaClases.puml`](docs/DiagramaClases.puml).
