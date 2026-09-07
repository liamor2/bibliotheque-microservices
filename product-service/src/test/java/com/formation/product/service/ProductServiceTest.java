package com.formation.product.service;

import com.formation.product.dto.ProductRequest;
import com.formation.product.dto.ProductResponse;
import com.formation.product.exception.ProductNotFoundException;
import com.formation.product.model.Product;
import com.formation.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void findAll_retourneTousLesProduits() {
        when(productRepository.findAll()).thenReturn(List.of(product(1L, "Écran"), product(2L, "Clavier")));

        List<ProductResponse> result = productService.findAll();

        assertThat(result).extracting(ProductResponse::getName).containsExactly("Écran", "Clavier");
    }

    @Test
    void findById_produitExistant_retourneLeProduit() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, "Écran")));

        ProductResponse result = productService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Écran");
    }

    @Test
    void findById_produitInexistant_leveProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_sauvegardeEtRetourneLeProduitCree() {
        ProductRequest request = request("Souris");
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        ProductResponse result = productService.create(request);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Souris");
    }

    @Test
    void update_produitExistant_modifieTousLesChamps() {
        Product existing = product(1L, "Ancien nom");
        ProductRequest request = new ProductRequest("Nouveau nom", "Nouvelle description",
                new BigDecimal("49.90"), 8);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductResponse result = productService.update(1L, request);

        assertThat(result.getName()).isEqualTo("Nouveau nom");
        assertThat(result.getPrice()).isEqualByComparingTo("49.90");
        assertThat(result.getQuantity()).isEqualTo(8);
    }

    @Test
    void update_produitInexistant_leveProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(99L, request("Souris")))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void delete_produitExistant_supprimeLeProduit() {
        Product existing = product(1L, "Écran");
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        productService.delete(1L);

        verify(productRepository).delete(existing);
    }

    @Test
    void delete_produitInexistant_leveProductNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(99L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    private Product product(Long id, String name) {
        return new Product(id, name, "Description", new BigDecimal("29.90"), 10);
    }

    private ProductRequest request(String name) {
        return new ProductRequest(name, "Souris optique", new BigDecimal("19.90"), 5);
    }
}
