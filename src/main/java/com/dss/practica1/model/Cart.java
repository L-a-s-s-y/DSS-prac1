package com.dss.practica1.model;


import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyJoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart")
public class Cart {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @ElementCollection
    @CollectionTable(name = "cart_products", joinColumns = @JoinColumn(name = "cart_id"))
    @MapKeyJoinColumn(name = "product_id")
    @Column(name = "number")
    private Map<Product, Integer> products = new HashMap<>();

    public Cart() {}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public Users getUser() { return user; }
	public void setUser(Users user) { this.user = user; }
	public Map<Product, Integer> getProducts() { return products; }
	public void setProducts(Map<Product, Integer> products) { this.products = products; }

	public void saveProduct(Product product) {
		if (products.containsKey(product)) products.put(product, products.get(product) + 1);
        else products.put(product, 1);
	}
	public void deleteProduct(Product product) { products.remove(product); }

	public double getTotal() {
		double total = 0.0;
		for(Map.Entry<Product, Integer> product : products.entrySet()) {
			total += (product.getKey().getPrice()*product.getValue());
		}
		total = Math.round(total * 100.0) / 100.0;
	    return total;
	}
	
	public void clearCart() { products.clear(); }
}
