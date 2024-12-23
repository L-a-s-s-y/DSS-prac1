package com.dss.practica1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dss.practica1.service.ProductService;

@Controller
@RequestMapping("/administration")
public class AdminController {
	@Autowired
	private ProductService productService;

	@GetMapping
	public String getAllProducts(Model model) {
		model.addAttribute("products", productService.getAllProducts());
		return "administration";
	}
}