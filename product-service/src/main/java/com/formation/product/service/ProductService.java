package com.formation.product.service;

import com.formation.product.dto.ProductRequest;
import com.formation.product.dto.ProductResponse;
import com.formation.product.exception.ProductNotFoundException;
import com.formation.product.mapper.ProductMapper;
import com.formation.product.model.Product;
import com.formation.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return repository.findAll().stream().map(ProductMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return ProductMapper.toResponse(findProduct(id));
    }

    public ProductResponse create(ProductRequest request) {
        return ProductMapper.toResponse(repository.save(ProductMapper.toEntity(request)));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findProduct(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        return ProductMapper.toResponse(repository.save(product));
    }

    public void delete(Long id) {
        repository.delete(findProduct(id));
    }

    private Product findProduct(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }
}
