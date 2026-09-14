package com.kanban.kanbanbff.repository;

import com.kanban.kanbanbff.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByColumnOrderByPositionAsc(String column);

    List<Card> findAllByOrderByPositionAsc();
}
