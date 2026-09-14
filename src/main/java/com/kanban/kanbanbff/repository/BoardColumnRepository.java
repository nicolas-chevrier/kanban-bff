package com.kanban.kanbanbff.repository;

import com.kanban.kanbanbff.model.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardColumnRepository extends JpaRepository<BoardColumn, String> {

    List<BoardColumn> findAllByOrderByPositionAsc();
}
