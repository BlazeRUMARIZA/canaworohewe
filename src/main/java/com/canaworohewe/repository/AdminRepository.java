package com.canaworohewe.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.canaworohewe.model.Admin;
import java.util.Optional;


public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
    boolean existsByPhone(String phone);
}
