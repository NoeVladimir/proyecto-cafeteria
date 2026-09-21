package com.cafeteria.orders;

import com.cafeteria.auth.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "cafeteria_orders")
public class Order {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional = false) private User student;
 @Column(nullable = false) private String pickupSlot;
 @Column(nullable = false) private String status = "RECIBIDO";
 @Column(nullable = false, precision = 10, scale = 2) private BigDecimal total;
 @Column(nullable = false, unique = true) private String pickupCode;
 @Column(nullable = false) private Instant createdAt = Instant.now();
 @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) private List<OrderItem> items = new ArrayList<>();
 protected Order() {}
 public Order(User student, String pickupSlot, BigDecimal total, String pickupCode) { this.student=student; this.pickupSlot=pickupSlot; this.total=total; this.pickupCode=pickupCode; }
 public void addItem(OrderItem item) { item.setOrder(this); items.add(item); }
 public Long getId(){return id;} public String getPickupSlot(){return pickupSlot;} public String getStatus(){return status;} public BigDecimal getTotal(){return total;} public String getPickupCode(){return pickupCode;} public Instant getCreatedAt(){return createdAt;} public List<OrderItem> getItems(){return items;}
}
