package com.dss.practica1.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dss.practica1.model.Product;
import com.dss.practica1.service.DatabaseService;
import com.dss.practica1.service.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private DatabaseService databaseService;

    @GetMapping
    public String getAllProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products";
    }

    @PostMapping("/add")
    public String addProduct(@RequestParam String name, @RequestParam double price) {
        productService.saveProduct(new Product(name, price));
        return "redirect:/administration";
    }

    @PostMapping("/edit")
	public String editProduct(@RequestParam Long productId, @RequestParam String name, @RequestParam double price) {
		Product product = productService.getProductById(productId);
		if (product != null) {
			product.setName(name);
			product.setPrice(price);
			productService.saveProduct(product);
		}
		return "redirect:/administration";
	}

    @PostMapping("/delete")
    public String deleteProduct(@RequestParam Long productId) {
        productService.deleteProduct(productId);
        return "redirect:/administration";
    }

    @GetMapping("/databaseExport")
    public ResponseEntity<FileSystemResource> exportDatabase() {
        try {
            File file = new File(databaseService.exportDatabase());
            FileSystemResource resource = new FileSystemResource(file);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=database.sql");

            return ResponseEntity.ok()
                                 .headers(headers)
                                 .body(resource);
        } catch (SQLException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}