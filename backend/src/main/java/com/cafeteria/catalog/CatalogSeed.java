package com.cafeteria.catalog;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration class CatalogSeed {
 @Bean CommandLineRunner seedCatalog(ProductRepository products) { return args -> { if (products.count()==0) { products.save(new Product("Café con leche", "Bebidas", new BigDecimal("1.50"), "Leche")); products.save(new Product("Sándwich de pollo", "Comidas", new BigDecimal("3.25"), "Gluten")); products.save(new Product("Galleta de avena", "Snacks", new BigDecimal("1.00"), "Gluten")); } }; }
}
