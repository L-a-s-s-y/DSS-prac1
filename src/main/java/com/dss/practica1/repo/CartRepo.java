package com.dss.practica1.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dss.practica1.model.Cart;
import com.dss.practica1.model.Users;

@Repository
public interface CartRepo extends JpaRepository<Cart, Long> {
	Optional<Cart> findByUser(Users user);
}

