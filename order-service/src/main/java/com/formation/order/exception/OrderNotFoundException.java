package com.formation.order.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Commande introuvable avec l'identifiant " + id);
    }
}
