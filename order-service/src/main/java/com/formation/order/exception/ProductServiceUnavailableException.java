package com.formation.order.exception;

public class ProductServiceUnavailableException extends RuntimeException {

    public ProductServiceUnavailableException(Throwable cause) {
        super("product-service est indisponible, impossible de traiter la commande", cause);
    }
}
