package com.dss.practica1.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.dss.practica1.service.CartService;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public String getCart(Model model, Authentication auth) {
        String username = auth.getName();
        model.addAttribute("products", cartService.getCart(username));
        model.addAttribute("total", cartService.getTotal(username));
        return "cart";
    }

    @PostMapping("/add")
    public String saveProduct(@RequestParam Long productId, Authentication auth) {
        cartService.saveProduct(auth.getName(), productId);
        return "redirect:/products";
    }

    @PostMapping("/delete")
    public String deleteProduct(@RequestParam Long productId, Authentication auth) {
        cartService.deleteProduct(auth.getName(), productId);
        return "redirect:/cart";
    }

    //Downloadable bill
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
        } catch (IOException | IllegalStateException e) {
        	e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
