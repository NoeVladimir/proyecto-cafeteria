package com.cafeteria.orders;
import com.cafeteria.auth.AuthService; import com.cafeteria.auth.User; import com.cafeteria.catalog.Product; import com.cafeteria.catalog.ProductRepository;
import java.math.BigDecimal; import java.util.*;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api") public class OrderController {
 private final AuthService auth; private final ProductRepository products; private final OrderRepository orders;
 public OrderController(AuthService auth, ProductRepository products, OrderRepository orders){this.auth=auth;this.products=products;this.orders=orders;}
 @GetMapping("/pickup-slots") public List<Slot> slots(){return List.of(new Slot("10:00–10:20", 20),new Slot("12:00–12:30", 20));}
 @GetMapping("/my/orders") public List<OrderResponse> mine(@RequestHeader("X-Auth-Token") String token){User user=auth.requireUser(token);return orders.findByStudentIdOrderByCreatedAtDesc(user.getId()).stream().map(this::response).toList();}
 @PostMapping("/orders") @ResponseStatus(HttpStatus.CREATED) public OrderResponse create(@RequestHeader("X-Auth-Token") String token,@RequestBody CreateOrder request){
  User user=auth.requireUser(token); if(request == null || request.items()==null || request.items().isEmpty() || request.pickupSlot()==null || request.pickupSlot().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Selecciona productos y una franja de retiro");
  List<Product> chosen = new ArrayList<>(); BigDecimal total=BigDecimal.ZERO;
  for(Line line:request.items()){if(line.quantity()<1 || line.quantity()>10) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"La cantidad debe estar entre 1 y 10"); Product product=products.findById(line.productId()).filter(Product::isAvailable).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Producto no disponible")); chosen.add(product); total=total.add(product.getPrice().multiply(BigDecimal.valueOf(line.quantity())));}
  Order order=new Order(user,request.pickupSlot().trim(),total,UUID.randomUUID().toString().substring(0,6).toUpperCase()); for(int i=0;i<request.items().size();i++) order.addItem(new OrderItem(chosen.get(i),request.items().get(i).quantity())); return response(orders.save(order));
 }
 private OrderResponse response(Order order){return new OrderResponse(order.getId(),order.getPickupSlot(),order.getStatus(),order.getTotal(),order.getPickupCode(),order.getCreatedAt(),order.getItems().stream().map(i->new Item(i.getProduct().getName(),i.getQuantity(),i.getUnitPrice())).toList());}
 public record Slot(String label,int availableOrders){} public record Line(Long productId,int quantity){} public record CreateOrder(String pickupSlot,List<Line> items){} public record Item(String productName,int quantity,BigDecimal unitPrice){} public record OrderResponse(Long id,String pickupSlot,String status,BigDecimal total,String pickupCode,java.time.Instant createdAt,List<Item> items){}
}
