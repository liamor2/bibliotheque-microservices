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
import com.formation.order.model.Order;
import com.formation.order.model.OrderItem;
import com.formation.order.model.OrderStatus;
import com.formation.order.repository.OrderRepository;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void create_calculeLeTotalEtSauvegardeLesSnapshots() {
        when(productClient.getProductById(1L)).thenReturn(product(1L, "Clavier", "79.90"));
        when(productClient.getProductById(2L)).thenReturn(product(2L, "Souris", "20.00"));
        when(orderRepository.save(org.mockito.ArgumentMatchers.any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        OrderResponse result = orderService.create(new OrderRequest("Alice", List.of(
                new OrderItemRequest(1L, 2), new OrderItemRequest(2L, 1))));

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("179.80");
        assertThat(result.getItems()).extracting(item -> item.getProductName())
                .containsExactly("Clavier", "Souris");
    }

    @Test
    void create_produitInexistant_traduitErreurFeign() {
        when(productClient.getProductById(999L)).thenThrow(notFoundException());

        assertThatThrownBy(() -> orderService.create(request(999L)))
                .isInstanceOf(ProductNotFoundForOrderException.class)
                .hasMessageContaining("999");
    }

    @Test
    void create_serviceProduitIndisponible_traduitErreurFeign() {
        when(productClient.getProductById(1L)).thenThrow(serverErrorException());

        assertThatThrownBy(() -> orderService.create(request(1L)))
                .isInstanceOf(ProductServiceUnavailableException.class);
    }

    @Test
    void findAll_retourneToutesLesCommandes() {
        when(orderRepository.findAll()).thenReturn(List.of(order(1L), order(2L)));

        assertThat(orderService.findAll()).hasSize(2);
    }

    @Test
    void findById_commandeExistante_retourneLaCommande() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order(1L)));

        assertThat(orderService.findById(1L).getId()).isEqualTo(1L);
    }

    @Test
    void findById_commandeInexistante_leveOrderNotFoundException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(99L)).isInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void updateStatus_modifieUniquementLeStatut() {
        Order existing = order(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(existing)).thenReturn(existing);

        OrderResponse result = orderService.updateStatus(1L, new OrderStatusUpdateRequest(OrderStatus.CONFIRMED));

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(result.getCustomerName()).isEqualTo("Alice");
    }

    @Test
    void delete_commandeExistante_supprimeLaCommande() {
        Order existing = order(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));

        orderService.delete(1L);

        verify(orderRepository).delete(existing);
    }

    private OrderRequest request(Long productId) {
        return new OrderRequest("Alice", List.of(new OrderItemRequest(productId, 1)));
    }

    private ProductDto product(Long id, String name, String price) {
        ProductDto product = new ProductDto();
        product.setId(id);
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        product.setQuantity(10);
        return product;
    }

    private Order order(Long id) {
        Order order = new Order("Alice", Instant.parse("2026-01-01T10:00:00Z"),
                OrderStatus.CREATED, new BigDecimal("79.90"));
        order.setId(id);
        order.addItem(new OrderItem(1L, "Clavier", new BigDecimal("79.90"), 1));
        return order;
    }

    private FeignException.NotFound notFoundException() {
        return new FeignException.NotFound("Produit absent", request(), new byte[0], Map.of());
    }

    private FeignException.InternalServerError serverErrorException() {
        return new FeignException.InternalServerError("Service indisponible", request(), new byte[0], Map.of());
    }

    private Request request() {
        return Request.create(Request.HttpMethod.GET, "/api/products/1", Map.of(), new byte[0],
                StandardCharsets.UTF_8);
    }
}
