package com.kanban.kanbanbff.exception;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException(Long id) {
        super("Carte introuvable avec l'id " + id);
    }
}
