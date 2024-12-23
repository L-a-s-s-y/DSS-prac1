package com.dss.practica1.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import com.dss.practica1.model.Cart;
import com.dss.practica1.model.Product;
import com.dss.practica1.model.Users;
import com.dss.practica1.repo.CartRepo;
import com.dss.practica1.repo.ProductRepo;
import com.dss.practica1.repo.UserRepo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

@Service
public class CartService {
    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    public Map<Product, Integer> getCart(String username) {
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuario desconocido"));
        Cart cart = cartRepo.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepo.save(newCart);
        });
        return cart.getProducts();
    }

    public void saveProduct(String username, Long id) {
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuario desconocido"));
        Cart cart = cartRepo.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Carro no hallado"));
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto Desconocido"));

        cart.saveProduct(product);
        cartRepo.save(cart);
    }

    public void deleteProduct(String username, Long id) {
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuario desconocido"));
        Cart cart = cartRepo.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Carro no hallado"));
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto Desconocido"));

        cart.deleteProduct(product);
        cartRepo.save(cart);
    }

    public double getTotal(String username) {
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuario desconocido"));
        Cart cart = cartRepo.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Carro no hallado"));

        return cart.getTotal();
    }

    public void clearCart(String username) {
        Users user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Usuario desconocido"));
        Cart cart = cartRepo.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Carro no hallado"));

        cart.clearCart();
        cartRepo.save(cart);
    }

    public FileSystemResource downloadBill(String username) throws IOException {
        File file = new File("bill.pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            writeHeader(contentStream, "Factura de compra");
            writeUserInfo(contentStream, username, java.time.LocalDate.now().toString());
            drawTableHeader(contentStream);

            Map<Product, Integer> products = getCart(username);
            int yOffset = 650;
            for (Map.Entry<Product, Integer> entry : products.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                yOffset = addProductRow(contentStream, product, quantity, yOffset);
            }

            double total = getTotal(username);
            drawTotalRow(contentStream, total, yOffset - 30);

            contentStream.close();
            document.save(file);
        }

        return new FileSystemResource(file);
    }

    private void writeHeader(PDPageContentStream contentStream, String storeName) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 30);
        contentStream.setNonStrokingColor(50, 70, 100);
        contentStream.newLineAtOffset(180, 770);
        contentStream.showText(storeName);
        contentStream.endText();

        contentStream.setNonStrokingColor(100, 149, 237);
        contentStream.addRect(50, 760, 495, 2);
        contentStream.fill();

        contentStream.setNonStrokingColor(220, 220, 220);
        contentStream.addRect(50, 758, 495, 2);
        contentStream.fill();
    }

    private void writeUserInfo(PDPageContentStream contentStream, String username, String date) throws IOException {
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ITALIC, 14);
        contentStream.setNonStrokingColor(80, 80, 80);
        contentStream.newLineAtOffset(50, 740);
        contentStream.showText("Cliente: " + username);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Fecha: " + date);
        contentStream.endText();
    }

    private void drawTableHeader(PDPageContentStream contentStream) throws IOException {
        contentStream.setNonStrokingColor(0, 102, 204);
        contentStream.addRect(50, 670, 495, 30);
        contentStream.fill();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.COURIER_BOLD, 14);
        contentStream.setNonStrokingColor(255, 255, 255);
        contentStream.newLineAtOffset(60, 677);
        contentStream.showText("Producto");
        contentStream.newLineAtOffset(200, 0);
        contentStream.showText("Cantidad");
        contentStream.newLineAtOffset(100, 0);
        contentStream.showText("Precio");
        contentStream.newLineAtOffset(100, 0);
        contentStream.showText("Total");
        contentStream.endText();

        contentStream.setNonStrokingColor(220, 220, 220);
        contentStream.addRect(50, 667, 495, 1);
        contentStream.fill();
    }

    private int addProductRow(PDPageContentStream contentStream, Product product, int quantity, int yOffset) throws IOException {
        double totalPrice = product.getPrice() * quantity;

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
        contentStream.setNonStrokingColor(50, 50, 50);
        contentStream.newLineAtOffset(60, yOffset);
        contentStream.showText(product.getName());
        contentStream.newLineAtOffset(200, 0);
        contentStream.showText(String.valueOf(quantity));
        contentStream.newLineAtOffset(100, 0);
        contentStream.showText(String.format("%.2f€", product.getPrice()));
        contentStream.newLineAtOffset(100, 0);
        contentStream.showText(String.format("%.2f€", totalPrice));
        contentStream.endText();

        contentStream.setNonStrokingColor(220, 220, 220);
        contentStream.addRect(50, yOffset - 5, 495, 1);
        contentStream.fill();

        return yOffset - 25;
    }

    private void drawTotalRow(PDPageContentStream contentStream, double total, int yOffset) throws IOException {
        contentStream.setNonStrokingColor(34, 139, 34);
        contentStream.addRect(50, yOffset, 495, 30);
        contentStream.fill();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_BOLD_ITALIC, 16);
        contentStream.setNonStrokingColor(255, 255, 255);
        contentStream.newLineAtOffset(400, yOffset + 10);
        contentStream.showText("Total: " + String.format("%.2f", total) + "€");
        contentStream.endText();

        contentStream.setNonStrokingColor(0, 100, 0);
        contentStream.addRect(50, yOffset - 2, 495, 2);
        contentStream.fill();
    }
}
