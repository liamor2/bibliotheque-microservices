package com.formation.order.exception;

public class ProductNotFoundForOrderException extends RuntimeException {

    public ProductNotFoundForOrderException(Long productId) {
        super("Le produit " + productId + " demandé dans la commande est introuvable");
    }
}
