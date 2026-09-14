package com.kanban.kanbanbff.dto;

public class LinkCardRequest {

    /**
     * Null pour dissocier l'ordre de travail de toute carte kanban.
     */
    private Long kanbanCardId;

    public Long getKanbanCardId() {
        return kanbanCardId;
    }

    public void setKanbanCardId(Long kanbanCardId) {
        this.kanbanCardId = kanbanCardId;
    }
}
