package com.kanban.kanbanbff.exception;

public class WorkOrderNotFoundException extends RuntimeException {

    public WorkOrderNotFoundException(Long id) {
        super("Ordre de travail introuvable avec l'id " + id);
    }
}
