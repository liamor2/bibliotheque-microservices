package com.formation.order.dto;

import com.formation.order.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderResponse {

    private Long id;
    private String customerName;
    private Instant orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;

    public OrderResponse(Long id, String customerName, Instant orderDate, OrderStatus status,
                         BigDecimal totalAmount, List<OrderItemResponse> items) {
        this.id = id;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public Instant getOrderDate() { return orderDate; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public List<OrderItemResponse> getItems() { return items; }
}
