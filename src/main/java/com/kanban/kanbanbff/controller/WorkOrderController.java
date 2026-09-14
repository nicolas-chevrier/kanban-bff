package com.kanban.kanbanbff.controller;

import com.kanban.kanbanbff.dto.LinkCardRequest;
import com.kanban.kanbanbff.dto.WorkOrderRequest;
import com.kanban.kanbanbff.dto.WorkOrderResponse;
import com.kanban.kanbanbff.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/work-orders")
@Tag(name = "Work Orders", description = "Gestion des ordres de travail liés aux cartes du kanban")
public class WorkOrderController {

    private final WorkOrderService service;

    public WorkOrderController(WorkOrderService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister les ordres de travail", description = "Filtrable par identifiant de carte kanban")
    public List<WorkOrderResponse> findAll(@RequestParam(required = false) Long kanbanCardId) {
        return service.findAll(kanbanCardId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un ordre de travail par id")
    public WorkOrderResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @Operation(summary = "Créer un ordre de travail")
    public ResponseEntity<WorkOrderResponse> create(@Valid @RequestBody WorkOrderRequest request) {
        WorkOrderResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un ordre de travail")
    public WorkOrderResponse update(@PathVariable Long id, @Valid @RequestBody WorkOrderRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/link")
    @Operation(summary = "Lier un ordre de travail à une carte kanban")
    public WorkOrderResponse linkToCard(@PathVariable Long id, @RequestBody LinkCardRequest request) {
        return service.linkToCard(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un ordre de travail")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
