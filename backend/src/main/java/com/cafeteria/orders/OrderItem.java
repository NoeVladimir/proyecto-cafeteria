package com.cafeteria.orders;
import com.cafeteria.catalog.Product;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name = "order_items") public class OrderItem {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional = false) private Order order;
 @ManyToOne(optional = false) private Product product;
 @Column(nullable = false) private int quantity;
 @Column(nullable = false, precision = 10, scale = 2) private BigDecimal unitPrice;
 protected OrderItem() {} public OrderItem(Product product, int quantity) {this.product=product;this.quantity=quantity;this.unitPrice=product.getPrice();}
 void setOrder(Order order){this.order=order;} public Product getProduct(){return product;} public int getQuantity(){return quantity;} public BigDecimal getUnitPrice(){return unitPrice;}
}
