package com.dss.practica1.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dss.practica1.service.CartService;
import com.dss.practica1.model.Product;

@RestController
@RequestMapping("/api/cart")
public class CartRestController {

	@Autowired
    private CartService cartService;
	
	/**
     * Displays the user's cart.
     * Adds the list of products and the total price to the model.
     * @param model the model to populate with cart data
     * @param auth the authentication object to retrieve the logged-in user's name
     * @return the cart
     */
	@ResponseBody
    @GetMapping
    public List<Product> getCart(Authentication auth, Model model) {
        String username = auth.getName();
        Map<Product, Integer> cart = cartService.getCart(username);

        // Convierte el mapa en una lista de productos, duplicando según la cantidad
        List<Product> productList = new ArrayList<>();
        cart.forEach((product, quantity) -> {
            for (int i = 0; i < quantity; i++) {
                productList.add(product);
            }
        });

        model.addAttribute("products", productList);
        model.addAttribute("total", cartService.getTotal(username));

        return productList;
    }
	
	/**
     * Adds a product to the user's cart.
     * @param productId the ID of the product to add
     * @param auth the authentication object to retrieve the logged-in user's name
     * @return id of the product
     */
    @ResponseBody
    @PostMapping("/add")
    public void saveProduct(@RequestParam Long productId, Authentication auth) {
        cartService.saveProduct(auth.getName(), productId);
    }
    
    /**
     * Clears all products from the user's cart.
     */
    @ResponseBody
    @PostMapping("/payment")
    public void clearCart(Authentication auth) {
        cartService.clearCart(auth.getName());
    }
	
	/**
     * Removes a product from the user's cart.
     * @param productId the ID of the product to remove
     * @param auth the authentication object to retrieve the logged-in user's name
     */
    @PostMapping("/delete")
    public void deleteProduct(@RequestParam Long productId, Authentication auth) {
        cartService.deleteProduct(auth.getName(), productId);
    }
    
    /**
     * Exports the user's cart as a PDF bill and clears the cart.
     * @param auth the authentication object to retrieve the logged-in user's name
     * @return a ResponseEntity containing the PDF bill as a downloadable resource
     */
    @GetMapping("/bill")
    public ResponseEntity<FileSystemResource> billExport(Authentication auth) {
        String username = auth.getName();
        try {
            FileSystemResource resource = cartService.downloadBill(username);
            cartService.clearCart(username);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bill.pdf")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(resource);
        } catch (IOException e) {
            return handleError(HttpStatus.INTERNAL_SERVER_ERROR, e);
        } catch (IllegalStateException e) {
            return handleError(HttpStatus.NOT_FOUND, e);
        }
    }

    /**
     * Handles errors by logging the exception and returning a ResponseEntity with the specified status.
     * @param status the HTTP status to return
     * @param e the exception to log
     * @return a ResponseEntity with the specified status and no body
     */
    private ResponseEntity<FileSystemResource> handleError(HttpStatus status, Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(status).body(null);
    }
}
