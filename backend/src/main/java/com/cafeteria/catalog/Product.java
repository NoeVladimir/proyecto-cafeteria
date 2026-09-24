package com.cafeteria.catalog;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String category;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal price;
    @Column(nullable = false) private boolean available = true;
    private String allergens;
    protected Product() {}
    public Product(String name, String category, BigDecimal price, String allergens) { this.name=name; this.category=category; this.price=price; this.allergens=allergens; }
    public Long getId(){ return id; } public String getName(){ return name; } public String getCategory(){ return category; }
    public BigDecimal getPrice(){ return price; } public boolean isAvailable(){ return available; } public String getAllergens(){ return allergens; }
}
