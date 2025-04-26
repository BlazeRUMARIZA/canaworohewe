package com.canaworohewe.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import com.canaworohewe.model.Admin;
import com.canaworohewe.model.LoginRequest;
import com.canaworohewe.repository.AdminRepository;
import com.canaworohewe.service.JwtService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthenticationProvider authenticationProvider;
    private final JwtService jwtService;
    private final AdminRepository adminRepository; // Ajout du repository

    public AuthController(AuthenticationProvider authenticationProvider, 
                         JwtService jwtService,
                         AdminRepository adminRepository) {
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
        this.adminRepository = adminRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Tentative de connexion pour l'utilisateur: {}", loginRequest.getUsername());
            
            // Vérifier si l'utilisateur existe
            var userOpt = adminRepository.findByUsername(loginRequest.getUsername());
            if (userOpt.isEmpty()) {
                logger.error("Utilisateur non trouvé: {}", loginRequest.getUsername());
                return ResponseEntity.badRequest().body(Map.of("error", "Utilisateur non trouvé"));
            }

            // Tentative d'authentification
            Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            Admin admin = userOpt.get();
            String token = jwtService.generateToken(admin);
            
            logger.info("Connexion réussie pour l'utilisateur: {}", loginRequest.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", Map.of(
                "username", admin.getUsername(),
                // "role", admin.getRole().name(),
                "authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList())
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            logger.error("Échec d'authentification pour l'utilisateur: {} - Mauvaises credentials", 
                        loginRequest.getUsername());
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", "Identifiants invalides"));
                
        } catch (AuthenticationException e) {
            logger.error("Erreur d'authentification pour l'utilisateur: {} - {}", 
                        loginRequest.getUsername(), e.getMessage());
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", "Erreur d'authentification"));
                
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la connexion pour l'utilisateur: {} - {}", 
                        loginRequest.getUsername(), e.getMessage(), e);
            return ResponseEntity
                .internalServerError()
                .body(Map.of("error", "Une erreur interne s'est produite"));
        }
    }
}
