package com.canaworohewe.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.canaworohewe.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByType(String type);
}
