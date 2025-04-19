package com.canaworohewe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.canaworohewe.model.Admin;
import com.canaworohewe.repository.AdminRepository;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostConstruct  // Cette méthode sera exécutée après l'initialisation du bean
    public void initAdmin() {
        // Vérifie si un admin existe déjà
        if (adminRepository.findByUsername("admin").isEmpty()) {
            Admin Admin = new Admin();
            Admin.setUsername("admin");
            Admin.setName("Rumariza Blaze");
            Admin.setPassword(passwordEncoder.encode("admin123"));
            adminRepository.save(Admin);
            System.out.println("✅ Admin account has beeen created");
        }else {
            System.out.println("The Admin is Already Exist");
        }
    }
}
