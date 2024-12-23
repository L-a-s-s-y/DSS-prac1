package com.dss.practica1.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dss.practica1.model.Users;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {
	Optional<Users> findByUsername(String username);
}
