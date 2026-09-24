# COOPUES

Sistema web de punto de venta, pedidos anticipados y fila digital para la cooperativa escolar. La plataforma centraliza el catalogo, los pedidos, el saldo de estudiantes, los pagos, la preparacion y el retiro en ventanilla.

El objetivo del MVP es que el estudiante pueda elegir productos, pagar con saldo o tarjeta, recibir un turno y recoger su pedido; mientras que las encargadas puedan ver que preparar, que entregar y que productos se estan agotando.

## Problema que resuelve

Durante los recreos, las filas largas, los pedidos verbales o en papel y el manejo de efectivo dificultan atender a todos a tiempo. Tambien existe poca visibilidad sobre el inventario, la demanda y los tiempos de espera.

COOP UES propone pedidos anticipados y preparacion organizada por franjas de retiro para:

- Reducir el tiempo de espera.
- Disminuir errores y pedidos olvidados.
- Trazar los pagos, recargas, ajustes y devoluciones.
- Controlar inventario y capacidad de preparacion.
- Ofrecer una experiencia simple desde computadora, tableta o celular mediante enlace o QR.

## Alcance del MVP

### Catalogo y pedidos

- Categorias de comidas, bebidas, snacks y combos.
- Productos con foto, precio, disponibilidad y alergenos.
- Carrito con cantidades, subtotal y notas controladas como “sin salsa”.
- Franjas de retiro configurables, por ejemplo, `10:00–10:20` y `12:00–12:30`.
- Limite por pedido y aviso cuando un producto se agota.
- Comprobante con numero de orden y codigo QR o codigo corto para retirar.

### Saldo y pagos

- Billetera interna con saldo en moneda local para cada estudiante.
- Recargas mediante Stripe Checkout o Payment Element.
- Acreditacion de saldo solo despues de confirmar el webhook firmado de Stripe.
- Pago de pedidos con saldo, tarjeta o modalidad presencial si la escuela la autoriza.
- Libro mayor inmutable para recargas, compras, devoluciones, ajustes aprobados y vencimientos.
- No se almacenan numeros de tarjeta ni datos sensibles de pago en la base de datos.

### Cola de preparacion y retiro

La pantalla operativa organiza los pedidos en estas columnas:

1. Nuevos.
2. En preparacion.
3. Listos.
4. Entregados o cancelados.

La prioridad se calcula por franja de retiro y hora prometida, no solamente por orden de llegada. La capacidad debe controlarse por cantidad de pedidos y, cuando sea necesario, por cantidad de unidades. Si una franja esta llena, se ofrece la siguiente disponible.

Tambien puede existir una vista publica para un monitor, mostrando turnos o pedidos listos sin exponer el nombre completo del estudiante.

## Roles y permisos

| Rol | Responsabilidades | Restricciones principales |
| --- | --- | --- |
| Estudiante | Registrarse, iniciar sesion, recargar credito, consultar catalogo, ordenar, pagar, ver turno e historial. | No puede cambiar precios, inventario ni estados de otros pedidos. |
| Encargada de cooperativa | Ver la cola, aceptar pedidos, preparar, marcar como listo, entregar, registrar ventas presenciales y consultar inventario. | No puede modificar saldos sin motivo y autorizacion. |
| Administracion | Gestionar catalogo, precios, horarios, usuarios, inventario, reportes y devoluciones. | No accede a datos de tarjeta; Stripe los custodia. |
| Supervisor/a escolar | Auditar movimientos, aprobar ajustes de saldo o devoluciones y consultar reportes. | No opera como estudiante. |

## Flujo principal

1. El estudiante inicia sesion con cuenta escolar, idealmente mediante SSO, o con correo institucional verificado.
2. Selecciona productos y una franja de retiro.
3. El sistema reserva capacidad durante unos minutos mientras se completa el pago.
4. El estudiante paga con saldo o mediante Stripe Checkout.
5. El backend confirma el pago a traves del webhook de Stripe.
6. Se crea el pedido como pagado/recibido y se incorpora a la cola.
7. La encargada lo pasa a `EN_PREPARACION` y despues a `LISTO`.
8. El estudiante recibe el aviso, muestra su QR o codigo corto y retira el pedido.
9. La encargada valida el codigo y marca el pedido como `ENTREGADO`.
10. El inventario se descuenta y la operacion queda auditada.

### Estados de pedido

Flujo normal:

`BORRADOR` → `PENDIENTE_PAGO` → `PAGADO/RECIBIDO` → `EN_PREPARACION` → `LISTO` → `ENTREGADO`

Ramas posibles: `CANCELADO`, `REEMBOLSADO` y `NO_RETIRADO`. Cada transicion debe registrar fecha, usuario responsable y regla de autorizacion.

## Pantallas previstas

| Pantalla | Contenido esencial |
| --- | --- |
| Inicio del estudiante | Saldo disponible, productos destacados, proxima franja y pedidos activos. |
| Catalogo | Filtros, disponibilidad, alergenos y carrito persistente. |
| Checkout | Resumen, franja de retiro, metodo de pago y confirmacion. |
| Pedido | Turno, estado, tiempo estimado, QR o codigo. |
| Cola de encargadas | Tarjetas por pedido, hora prometida, articulos, notas y acciones de estado. |
| Cobro rapido | Busqueda o escaneo de estudiante, carrito y pago con saldo o modalidad presencial futura. |
| Administracion | Inventario, productos, horarios, reportes y ajustes sujetos a aprobacion. |

## Arquitectura propuesta

Se recomienda comenzar como un monolito modular: es mas sencillo de desplegar, probar y mantener para una cooperativa escolar. Las fronteras de los modulos permiten separar servicios en el futuro si el volumen lo exige.

| Capa | Tecnologia propuesta | Responsabilidad |
| --- | --- | --- |
| Frontend | Vue 3, TypeScript, Vite, Pinia y Vue Router | PWA responsive, experiencia del estudiante y panel operativo. |
| API | Java 25 y Spring Boot 3 | REST, reglas de negocio, seguridad, pedidos, saldo e inventario. |
| Datos | PostgreSQL y Flyway | Persistencia transaccional, migraciones, auditoria y reportes. |
| Pagos | Stripe Payments y webhooks | Recargas y pagos con tarjeta, verificados server-to-server. |
| Tiempo real | Spring WebSocket/STOMP o SSE | Actualizacion de la cola y estados sin recargar. |
| Infraestructura | Docker, Nginx, HTTPS y gestor de secretos | Despliegue reproducible y proteccion de configuracion. |

### Modulos de backend

- `identity`: usuarios, roles, sesiones e integracion con identidad escolar.
- `catalog`: categorias, productos, precios, alergenos y disponibilidad.
- `inventory`: existencias, movimientos, minimos y agotados.
- `orders`: carrito, reservas, pedidos, estados y entrega.
- `wallet`: cuentas de saldo y libro mayor.
- `payments`: Stripe, idempotencia, webhooks, reembolsos y conciliacion.
- `queue`: capacidad por franja, estimaciones y tablero de preparacion.
- `notifications`: avisos dentro de la aplicacion y correo opcional.
- `reporting/audit`: cierres, ventas, movimientos y trazabilidad.

## Modelo de datos inicial

| Entidad | Campos clave |
| --- | --- |
| `users` | id, correo institucional, nombre, rol, estado, `created_at` |
| `wallet_accounts` | id, `user_id`, `balance_cache`, moneda, estado |
| `wallet_ledger` | id, `wallet_id`, tipo, monto, referencia, `idempotency_key`, `created_at`, `actor_id` |
| `products` | id, categoria, nombre, precio, activo, alergenos, `image_url` |
| `inventory_items` | id, `product_id`, stock actual, stock minimo, version |
| `pickup_slots` | id, fecha, inicio, fin, capacidad de pedidos, capacidad de unidades, estado |
| `orders` | id, `student_id`, `slot_id`, estado, total, metodo de pago, codigo de retiro, timestamps |
| `order_items` | id, `order_id`, `product_id`, cantidad, precio unitario, nota |
| `payment_transactions` | id, `order_id` o `ledger_id`, `stripe_payment_intent`, estado, monto |
| `order_events` | id, `order_id`, estado anterior, estado nuevo, `actor_id`, `created_at` |
| `audit_log` | id, actor, accion, entidad, `entity_id`, metadata, `created_at` |

El saldo visible puede mantenerse como cache para consultas rapidas, pero la fuente de verdad debe ser la suma auditable de `wallet_ledger`. Nunca debe depender unicamente de un campo editable.

## Reglas criticas de negocio

- **Idempotencia:** una recarga o webhook repetido no puede acreditar saldo dos veces.
- **Confirmacion de pagos:** Stripe solo es valido cuando el webhook firmado confirma el evento esperado; no se debe confiar unicamente en el retorno del navegador.
- **Stock:** reservar o descontar dentro de una transaccion e impedir vender mas unidades de las disponibles.
- **Capacidad:** controlar cupos de pedidos y unidades, y liberar reservas de carritos expirados.
- **Transiciones:** validar el estado de origen y el rol autorizado para cada cambio.
- **Reembolsos:** registrar una operacion compensatoria en el libro mayor y solicitar el reembolso a Stripe cuando corresponda.
- **Privacidad:** la pantalla publica muestra turno o codigo, nunca el nombre completo.
- **Menores:** acordar con la escuela consentimiento, retencion de datos y responsable de cada cuenta.

## Seguridad y cumplimiento

- SSO escolar mediante OIDC cuando este disponible; como alternativa, correo institucional verificado.
- Autorizacion RBAC y auditoria de cambios de precios, saldos, inventario y reembolsos.
- HTTPS obligatorio.
- Secretos de Stripe solo en el backend o en un gestor de secretos.
- Validacion de firma de webhooks y uso de claves de idempotencia.
- Argon2 o BCrypt si se administran contrasenas localmente.
- Sesiones seguras y rate limiting.
- Minimizacion de datos: evitar fecha de nacimiento, direccion y datos no necesarios.
- Respaldos cifrados, politica de retencion y procedimiento para incidentes.

## API REST orientativa

| Area | Endpoints iniciales |
| --- | --- |
| Auth | `GET /me`, `POST /auth/login`, `POST /auth/logout` |
| Catalogo | `GET /products`, `GET /categories`, `GET /pickup-slots` |
| Cartera | `GET /wallet`, `GET /wallet/ledger`, `POST /wallet/top-ups/checkout` |
| Pedidos | `POST /orders`, `GET /orders/{id}`, `GET /my/orders`, `POST /orders/{id}/cancel` |
| Operacion | `GET /operator/queue`, `PATCH /operator/orders/{id}/status`, `POST /operator/orders/{id}/handoff` |
| Administracion | CRUD de `/admin/products`, `/admin/slots` y ajustes de `/admin/inventory` |
| Stripe | `POST /webhooks/stripe`, con firma validada y sin sesion de usuario |

## Desarrollo local

### Requisitos

- Docker Desktop.
- VS Code con la extension Dev Containers.
- Git.

El entorno usa Docker Compose con PostgreSQL 16. Para trabajar con la configuracion prevista, abre el proyecto en VS Code y selecciona **Dev Containers: Reopen in Container**.

### Servicios y comandos

| Servicio | Comando | Puerto |
| --- | --- | --- |
| API Spring Boot | `mvn -f backend/pom.xml spring-boot:run` | `8080` |
| Frontend Vue/Vite | `npm --prefix frontend run dev` | `5173` |
| PostgreSQL | Servicio `db` de Compose | `5432` |

Comandos de verificacion:

```bash
mvn -f backend/pom.xml verify
npm --prefix frontend run build
psql postgresql://dev:dev@db:5432/appdb -c "select 1;"
```

El script `.devcontainer/setup.sh` instala las dependencias Maven y npm al crear el contenedor. Las credenciales de PostgreSQL definidas en Compose son solo para desarrollo local y no deben reutilizarse en produccion.

### Estado actual del repositorio

El repositorio contiene actualmente el esqueleto de una API Spring Boot y un frontend Vue/Vite. La propuesta funcional descrita en este README representa el producto objetivo; los modulos de identidad, catalogo, inventario, pedidos, billetera, pagos, cola y auditoria deben implementarse progresivamente.

La imagen del Dev Container y `backend/pom.xml` usan Java 25 para mantener alineados el entorno local y el backend.

## Plan de implementacion

| Fase | Alcance | Resultado |
| --- | --- | --- |
| 0. Descubrimiento (1–2 semanas) | Horarios, productos, roles, credito, politica de menores, capacidad y procesos actuales. | Backlog priorizado y prototipo validado. |
| 1. MVP (4–6 semanas) | Login, catalogo, carrito, pedidos, saldo, Stripe, cola, entrega y reportes basicos. | Piloto en una franja o recreo. |
| 2. Estabilizacion (2–3 semanas) | Inventario detallado, cancelaciones, reembolsos, alertas, auditoria y pruebas de carga. | Uso diario controlado. |
| 3. Evolucion | QR, notificaciones, combos, preventa semanal, analitica y Stripe Terminal si aplica. | Mayor adopcion y menos espera. |

## Criterios de exito del piloto

- Al menos el 80 % de los pedidos digitales se entrega dentro de su franja.
- El tiempo promedio de retiro disminuye frente a la fila tradicional.
- Pagos, saldo e inventario quedan conciliados al cierre.
- No existen acreditaciones duplicadas de Stripe.
- Los ajustes tienen trazabilidad completa.
- Las encargadas pueden operar la cola con capacitacion breve y sin hojas paralelas.

## Decisiones pendientes con la UES

Antes de construir el MVP, la institucion debe confirmar:

- Si cada estudiante tendra cuenta individual y quien aprobara o recargara el credito.
- Moneda, topes de saldo y metodos de pago presencial.
- Disponibilidad de SSO escolar o uso de correo institucional.
- Franjas reales de recreo y capacidad del equipo de preparacion.
- Tratamiento de alergenos, pedidos no retirados y reembolsos.
- Datos de menores permitidos y periodo de retencion.
- Roles autorizados para ajustes, devoluciones y cierres de caja.

## Direccion recomendada

Construir primero el circuito de mayor valor:

**catalogo → pedido anticipado → pago o saldo → cola → retiro**

Las franjas de retiro, el tablero de preparacion y el saldo recargable atacan directamente el problema de las filas. Stripe debe permanecer aislado en el modulo de pagos y cualquier modificacion de credito debe pasar por un libro mayor auditable.