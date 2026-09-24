# Arquitectura y modelo de datos — COOP UES

## Objetivo del MVP

El MVP permite a un estudiante crear una cuenta, iniciar sesión, consultar el catálogo, armar un carrito, elegir una franja de retiro y crear un pedido. Es un monolito modular: las capas se mantienen juntas para simplificar el desarrollo, pero el código se organiza por responsabilidad.

## Arquitectura

```mermaid
flowchart LR
    Browser["Navegador\nVue 3 + Vite :5173"]
    API["API REST\nSpring Boot :8080"]
    DB[("PostgreSQL 16\nappdb :5432")]

    Browser -->|"HTTP /api · JSON"| API
    API -->|"JPA / Hibernate"| DB
```

El servidor de desarrollo de Vite reenvía las rutas que comienzan con `/api` a Spring Boot. Por eso el frontend nunca necesita conocer host, puerto ni credenciales de la base de datos.

## Contenedores de desarrollo

```mermaid
flowchart TB
    Dev["Dev Container: app\nJava 25, Maven, Node 22"]
    Postgres["Servicio db\nPostgreSQL 16"]
    Volume[("cafeteria-postgres-data")]

    Dev -->|"jdbc:postgresql://db:5432/appdb"| Postgres
    Postgres --> Volume
```

El volumen conserva los datos entre reinicios. El nombre específico del volumen evita reutilizar por accidente una base local antigua con otras credenciales.

## Capas y módulos

| Capa | Ubicación | Responsabilidad |
| --- | --- | --- |
| Vista | `frontend/src/App.vue` | Formularios, catálogo, carrito, mensajes y pedidos reactivos. |
| Cliente API | `frontend/src/composables/useAuth.js` | Registro, login, token de sesión y llamadas autenticadas. |
| Auth | `backend/.../auth` | Usuarios, BCrypt, registro, login, logout y validación del token. |
| Catálogo | `backend/.../catalog` | Productos disponibles y carga de productos de ejemplo. |
| Pedidos | `backend/.../orders` | Validación del carrito, total, código de retiro y persistencia del pedido. |
| Persistencia | PostgreSQL + JPA | Tablas, relaciones y almacenamiento transaccional. |

## API REST

| Método | Ruta | Uso | Autenticación |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Crea una cuenta de estudiante. | No |
| `POST` | `/api/auth/login` | Inicia sesión. | No |
| `POST` | `/api/auth/logout` | Invalida la sesión actual. | `X-Auth-Token` |
| `GET` | `/api/auth/me` | Obtiene la persona de la sesión. | `X-Auth-Token` |
| `GET` | `/api/products` | Lista productos disponibles. | No |
| `GET` | `/api/pickup-slots` | Lista franjas de retiro del MVP. | No |
| `POST` | `/api/orders` | Crea un pedido confirmado. | `X-Auth-Token` |
| `GET` | `/api/my/orders` | Muestra los pedidos del estudiante. | `X-Auth-Token` |

Las rutas son recursos REST, los métodos HTTP expresan la acción y las respuestas son JSON. Un registro correcto devuelve `201 Created`; validaciones incorrectas devuelven `400`, credenciales inválidas `401` y un correo ya registrado `409`.

## Modelo de datos

```mermaid
erDiagram
    USERS ||--o{ CAFETERIA_ORDERS : "realiza"
    CAFETERIA_ORDERS ||--|{ ORDER_ITEMS : "contiene"
    PRODUCTS ||--o{ ORDER_ITEMS : "aparece en"

    USERS {
        bigint id PK
        varchar name
        varchar email UK
        varchar password
        varchar role
    }

    PRODUCTS {
        bigint id PK
        varchar name
        varchar category
        decimal price
        boolean available
        varchar allergens
    }

    CAFETERIA_ORDERS {
        bigint id PK
        bigint student_id FK
        varchar pickup_slot
        varchar status
        decimal total
        varchar pickup_code UK
        timestamp created_at
    }

    ORDER_ITEMS {
        bigint id PK
        bigint order_id FK
        bigint product_id FK
        integer quantity
        decimal unit_price
    }
```

### Decisiones del modelo

- Un usuario puede tener muchos pedidos; cada pedido pertenece a un estudiante.
- Un pedido contiene uno o más ítems. Cada ítem guarda la cantidad y el precio unitario aplicado al crear el pedido, para que futuros cambios de precio no alteren el historial.
- Un producto puede estar presente en muchos ítems de pedido.
- El pedido guarda un `pickup_code` único, que es el código corto para retirar en ventanilla.
- `status` inicia como `RECIBIDO`. El flujo operativo futuro será `RECIBIDO → EN_PREPARACION → LISTO → ENTREGADO`.
- Las contraseñas nunca se guardan en texto plano: se almacenan como hashes BCrypt.

## Flujo de un pedido

```mermaid
sequenceDiagram
    participant E as Estudiante
    participant V as Vue
    participant A as API REST
    participant D as PostgreSQL

    E->>V: Inicia sesión
    V->>A: POST /api/auth/login
    A->>D: Busca usuario y comprueba BCrypt
    D-->>A: Usuario
    A-->>V: Token de sesión
    V->>A: GET /api/products
    A->>D: Consulta productos disponibles
    D-->>A: Catálogo
    A-->>V: JSON de productos
    E->>V: Confirma carrito y franja
    V->>A: POST /api/orders + X-Auth-Token
    A->>D: Guarda pedido e ítems
    D-->>A: Pedido creado
    A-->>V: 201, total y código de retiro
```

## Límites explícitos del MVP

El sistema ya tiene una base adecuada para extenderse, pero todavía no implementa inventario real, pagos con saldo o Stripe, billetera/libro mayor, roles operativos, actualización de estados ni notificaciones. Esos módulos deben incorporarse después con migraciones versionadas —por ejemplo, Flyway— y pruebas de integración para cada regla de negocio.
