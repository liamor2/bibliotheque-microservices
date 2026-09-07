package com.formation.order.mapper;

import com.formation.order.dto.OrderItemResponse;
import com.formation.order.dto.OrderResponse;
import com.formation.order.model.Order;

import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(item.getId(), item.getProductId(), item.getProductName(),
                        item.getUnitPrice(), item.getQuantity(), item.getSubtotal()))
                .toList();
        return new OrderResponse(order.getId(), order.getCustomerName(), order.getOrderDate(), order.getStatus(),
                order.getTotalAmount(), items);
    }
}
