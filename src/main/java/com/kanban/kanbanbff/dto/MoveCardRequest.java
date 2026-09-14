package com.kanban.kanbanbff.dto;

public class MoveCardRequest {

    private String column;

    private Long before;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Long getBefore() {
        return before;
    }

    public void setBefore(Long before) {
        this.before = before;
    }
}
