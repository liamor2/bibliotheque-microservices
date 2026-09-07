package com.formation.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.formation.order.client.ProductClient;
import com.formation.order.dto.OrderItemRequest;
import com.formation.order.dto.OrderRequest;
import com.formation.order.dto.ProductDto;
import com.formation.order.repository.OrderRepository;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private ProductClient productClient;

    @BeforeEach
    void cleanDatabase() {
        orderRepository.deleteAll();
    }

    @Test
    void cycleDeVieComplet_creerListerRecupererChangerStatutSupprimer() throws Exception {
        ProductDto product = new ProductDto();
        product.setId(1L);
        product.setName("Clavier mécanique");
        product.setPrice(new BigDecimal("79.90"));
        product.setQuantity(20);
        when(productClient.getProductById(1L)).thenReturn(product);

        OrderRequest createRequest = new OrderRequest("Alice", List.of(new OrderItemRequest(1L, 2)));
        String response = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalAmount").value(159.80))
                .andExpect(jsonPath("$.items[0].productName").value("Clavier mécanique"))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id));

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"));

        mockMvc.perform(patch("/api/orders/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        mockMvc.perform(delete("/api/orders/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_produitInexistantChezProductService_retourne400() throws Exception {
        when(productClient.getProductById(999L)).thenThrow(notFoundException());
        OrderRequest request = new OrderRequest("Alice", List.of(new OrderItemRequest(999L, 1)));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("999")));
    }

    private FeignException.NotFound notFoundException() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/products/999", Map.of(), new byte[0],
                StandardCharsets.UTF_8);
        return new FeignException.NotFound("Produit absent", request, new byte[0], Map.of());
    }
}
