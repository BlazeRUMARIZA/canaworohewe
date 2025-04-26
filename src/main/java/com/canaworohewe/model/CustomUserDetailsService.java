package com.canaworohewe.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.canaworohewe.repository.AdminRepository;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    @Autowired
    private AdminRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Tentative de chargement de l'utilisateur: {}", username);
        
        try {
            Admin user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

            logger.info("Utilisateur trouvé: {} avec le rôle: {}", username, user.getPhone());
            
            return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("Phone_" + user.getPhone()))
            );
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de l'utilisateur: {} - {}", username, e.getMessage(), e);
            throw e;
        }
    }
}