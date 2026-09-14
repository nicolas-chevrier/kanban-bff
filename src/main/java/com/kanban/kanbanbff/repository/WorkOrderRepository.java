package com.kanban.kanbanbff.repository;

import com.kanban.kanbanbff.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    List<WorkOrder> findByKanbanCardId(Long kanbanCardId);
}
