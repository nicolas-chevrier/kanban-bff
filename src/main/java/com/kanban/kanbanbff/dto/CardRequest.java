package com.kanban.kanbanbff.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public class CardRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String label;

    private String description;

    /**
     * Requis a la creation ; absent lors d'une mise a jour depuis l'editeur
     * (le deplacement de colonne passe par l'endpoint /move).
     */
    private String column;

    private Integer priority;

    private Double progress;

    private Instant deadline;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public Instant getDeadline() {
        return deadline;
    }

    public void setDeadline(Instant deadline) {
        this.deadline = deadline;
    }
}
