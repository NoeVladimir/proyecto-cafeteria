# Project Proposal

## Nombre

COOPUES

## Problema

Durante los recreos, las filas largas, los pedidos verbales o en papel y el manejo de efectivo dificultan atender a todos a tiempo. Tambien existe poca visibilidad sobre el inventario, la demanda y los tiempos de espera.

## Solucion en 30 segundos

COOPUES permite que estudiantes consulten el catalogo, hagan pedidos anticipados y seleccionen una franja de retiro. El personal recibe una cola ordenada para preparar y entregar cada pedido, con control de saldo, pagos e inventario.

## Entidad principal: Pedido

- `id`
- `student_id`
- `slot_id`
- `status`
- `total`
- `payment_method`
- `pickup_code`
- `created_at`
- `updated_at`

## Entidad relacionada: Items del pedido

Un pedido tiene muchos items. Cada item pertenece a un solo pedido y registra:

- `id`
- `order_id`
- `product_id`
- `quantity`
- `unit_price`
- `note`

## Alcance inicial

- Catalogo de productos disponibles.
- Carrito y creacion de pedidos.
- Seleccion de franja de retiro.
- Cola operativa de preparacion y entrega.
- Vista responsive para celular y computadora.
