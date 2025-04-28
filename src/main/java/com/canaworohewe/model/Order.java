package com.canaworohewe.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
    
    @Column(nullable = false)
    private Double totalPrice;
    
    @Column(nullable = false)
    private String deliveryAddress;
    
    @Column(nullable = false)
    private LocalDateTime orderDate;
    
    @Column(nullable = false)
    private String status; // "PENDING", "PROCESSING", "DELIVERED", "CANCELLED"
    
    @ManyToOne
    @JoinColumn(name = "delivery_person_id")
    private User deliveryPerson;
}
