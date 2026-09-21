package com.cafeteria.catalog;
import java.util.List;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/products")
public class CatalogController {
  private final ProductRepository products;
  public CatalogController(ProductRepository products) { this.products=products; }
  @GetMapping public List<Product> list() { return products.findByAvailableTrueOrderByCategoryAscNameAsc(); }
}
