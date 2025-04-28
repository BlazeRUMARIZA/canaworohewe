package com.canaworohewe.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.canaworohewe.exception.ResourceNotFoundException;
import com.canaworohewe.model.Order;
import com.canaworohewe.model.Product;
import com.canaworohewe.model.User;
import com.canaworohewe.repository.OrderRepository;
import com.canaworohewe.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUser(user);
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public Order createOrder(Order order) {
        // Set order date
        order.setOrderDate(LocalDateTime.now());
        
        // Calculate total price
        double total = order.getItems().stream()
                .mapToDouble(item -> {
                    Product product = productService.getProductById(item.getProduct().getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                    
                    // Update stock
                    product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
                    productService.saveProduct(product);
                    
                    item.setUnitPrice(product.getPrice());
                    item.setOrder(order);
                    return product.getPrice() * item.getQuantity();
                })
                .sum();
        
        order.setTotalPrice(total);
        order.setStatus("PENDING");
        
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order assignDeliveryPerson(Long orderId, Long deliveryPersonId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        User deliveryPerson = userRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found"));
        
        if (!deliveryPerson.getRole().equals("DELIVERY_PERSON")) {
            throw new IllegalArgumentException("User is not a delivery person");
        }
        
        order.setDeliveryPerson(deliveryPerson);
        order.setStatus("ASSIGNED");
        
        return orderRepository.save(order);
    }
}