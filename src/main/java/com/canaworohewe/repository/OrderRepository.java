package com.canaworohewe.repository;

import com.canaworohewe.model.Order;
import com.canaworohewe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByDeliveryPerson(User deliveryPerson);
    List<Order> findByStatus(String status);
}