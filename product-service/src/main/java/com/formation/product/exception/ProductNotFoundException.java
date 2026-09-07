package com.formation.product.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Produit introuvable avec l'identifiant " + id);
    }
}
