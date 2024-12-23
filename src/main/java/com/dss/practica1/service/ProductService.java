package com.dss.practica1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dss.practica1.model.Product;
import com.dss.practica1.repo.ProductRepo;

@Service
public class ProductService {
	@Autowired
	private ProductRepo productRepo;
	
	public List<Product> getAllProducts() { return productRepo.findAll(); }
	public Product getProductById(Long id) { return productRepo.findById(id).orElse(null); }
	public void saveProduct(Product product) { productRepo.save(product); }
	public void deleteProduct(Long id) { productRepo.deleteById(id); }
	
}