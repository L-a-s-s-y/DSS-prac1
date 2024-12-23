package com.dss.practica1.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dss.practica1.model.Product;
import com.dss.practica1.service.DatabaseService;
import com.dss.practica1.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {
    @Autowired
    private ProductService productService;

    @Autowired
    private DatabaseService databaseService;

    /**
     * Retrieves all products and populates the model with the product list.
     * @param model the model to populate
     * @return the name of the products view
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * Adds a new product to the database.
     * @param name the name of the product
     * @param price the price of the product
     * @return the new product
     */
    @PostMapping("/add")
    public Product addProduct(@RequestParam String name, @RequestParam double price) {
        Product product = new Product();
        product.setName(name);
		product.setPrice(price);
    	productService.saveProduct(product);
        return product;
    }

    /**
     * Edits an existing product in the database.
     * @param productId the ID of the product to edit
     * @param name the new name of the product
     * @param price the new price of the product
     * @return the edited product
     */
    @PostMapping("/edit")
	public Product editProduct(@RequestParam Long productId, @RequestParam String name, @RequestParam double price) {
		Product product = productService.getProductById(productId);
		if (product != null) {
			product.setName(name);
			product.setPrice(price);
			productService.saveProduct(product);
		}
		return product;
	}

    /**
     * Deletes a product from the database by its ID.
     * @param productId the ID of the product to delete
     */
    @PostMapping("/delete")
    public void deleteProduct(@RequestParam Long productId) {
        productService.deleteProduct(productId);
    }
    
    /**
     * Exports the database schema and data to an SQL file and provides it as a downloadable resource.
     * @return a ResponseEntity containing the SQL file as a resource
     */
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
