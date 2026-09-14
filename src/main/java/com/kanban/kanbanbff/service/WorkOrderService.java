package com.kanban.kanbanbff.service;

import com.kanban.kanbanbff.dto.LinkCardRequest;
import com.kanban.kanbanbff.dto.WorkOrderRequest;
import com.kanban.kanbanbff.dto.WorkOrderResponse;
import com.kanban.kanbanbff.exception.WorkOrderNotFoundException;
import com.kanban.kanbanbff.model.WorkOrder;
import com.kanban.kanbanbff.model.WorkOrderStatus;
import com.kanban.kanbanbff.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WorkOrderService {

    private final WorkOrderRepository repository;

    public WorkOrderService(WorkOrderRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> findAll(Long kanbanCardId) {
        List<WorkOrder> workOrders = kanbanCardId != null
                ? repository.findByKanbanCardId(kanbanCardId)
                : repository.findAll();
        return workOrders.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public WorkOrderResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public WorkOrderResponse create(WorkOrderRequest request) {
        WorkOrder workOrder = new WorkOrder();
        applyRequest(workOrder, request);
        return toResponse(repository.save(workOrder));
    }

    public WorkOrderResponse update(Long id, WorkOrderRequest request) {
        WorkOrder workOrder = getOrThrow(id);
        applyRequest(workOrder, request);
        return toResponse(repository.save(workOrder));
    }

    public WorkOrderResponse linkToCard(Long id, LinkCardRequest request) {
        WorkOrder workOrder = getOrThrow(id);
        workOrder.setKanbanCardId(request.getKanbanCardId());
        return toResponse(repository.save(workOrder));
    }

    public void delete(Long id) {
        WorkOrder workOrder = getOrThrow(id);
        repository.delete(workOrder);
    }

    private WorkOrder getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new WorkOrderNotFoundException(id));
    }

    private void applyRequest(WorkOrder workOrder, WorkOrderRequest request) {
        workOrder.setTitle(request.getTitle());
        workOrder.setDescription(request.getDescription());
        workOrder.setStatus(request.getStatus() != null ? request.getStatus() : WorkOrderStatus.BACKLOG);
        workOrder.setPriority(request.getPriority());
        workOrder.setAssignee(request.getAssignee());
        workOrder.setDueDate(request.getDueDate());
        workOrder.setKanbanCardId(request.getKanbanCardId());
    }

    private WorkOrderResponse toResponse(WorkOrder workOrder) {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(workOrder.getId());
        response.setTitle(workOrder.getTitle());
        response.setDescription(workOrder.getDescription());
        response.setStatus(workOrder.getStatus());
        response.setPriority(workOrder.getPriority());
        response.setAssignee(workOrder.getAssignee());
        response.setDueDate(workOrder.getDueDate());
        response.setKanbanCardId(workOrder.getKanbanCardId());
        response.setCreatedAt(workOrder.getCreatedAt());
        response.setUpdatedAt(workOrder.getUpdatedAt());
        return response;
    }
}
