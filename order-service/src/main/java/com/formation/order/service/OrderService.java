package com.formation.order.service;

import com.formation.order.client.ProductClient;
import com.formation.order.dto.OrderItemRequest;
import com.formation.order.dto.OrderRequest;
import com.formation.order.dto.OrderResponse;
import com.formation.order.dto.OrderStatusUpdateRequest;
import com.formation.order.dto.ProductDto;
import com.formation.order.exception.OrderNotFoundException;
import com.formation.order.exception.ProductNotFoundForOrderException;
import com.formation.order.exception.ProductServiceUnavailableException;
import com.formation.order.mapper.OrderMapper;
import com.formation.order.model.Order;
import com.formation.order.model.OrderItem;
import com.formation.order.model.OrderStatus;
import com.formation.order.repository.OrderRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    public OrderResponse create(OrderRequest request) {
        Order order = new Order(request.getCustomerName(), Instant.now(), OrderStatus.CREATED, BigDecimal.ZERO);
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            ProductDto product = fetchProduct(itemRequest.getProductId());
            OrderItem item = new OrderItem(product.getId(), product.getName(), product.getPrice(),
                    itemRequest.getQuantity());
            order.addItem(item);
            total = total.add(item.getSubtotal());
        }

        order.setTotalAmount(total);
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(OrderMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        return OrderMapper.toResponse(findOrder(id));
    }

    public OrderResponse updateStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = findOrder(id);
        order.setStatus(request.getStatus());
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    public void delete(Long id) {
        orderRepository.delete(findOrder(id));
    }

    private ProductDto fetchProduct(Long productId) {
        try {
            return productClient.getProductById(productId);
        } catch (FeignException.NotFound exception) {
            throw new ProductNotFoundForOrderException(productId);
        } catch (FeignException exception) {
            throw new ProductServiceUnavailableException(exception);
        }
    }

    private Order findOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }
}
