package com.kanban.kanbanbff.dto;

import jakarta.validation.constraints.NotBlank;

public class BoardColumnRequest {

    @NotBlank(message = "Le libelle est obligatoire")
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
