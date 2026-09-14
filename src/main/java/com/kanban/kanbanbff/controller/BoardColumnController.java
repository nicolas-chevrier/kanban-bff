package com.kanban.kanbanbff.controller;

import com.kanban.kanbanbff.dto.BoardColumnRequest;
import com.kanban.kanbanbff.dto.BoardColumnResponse;
import com.kanban.kanbanbff.service.BoardColumnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/columns")
@Tag(name = "Columns", description = "Colonnes du kanban, persistees pour pouvoir en ajouter dynamiquement")
public class BoardColumnController {

    private final BoardColumnService service;

    public BoardColumnController(BoardColumnService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister les colonnes, dans l'ordre d'affichage")
    public List<BoardColumnResponse> findAll() {
        return service.findAll();
    }

    @PostMapping
    @Operation(summary = "Creer une colonne")
    public ResponseEntity<BoardColumnResponse> create(@Valid @RequestBody BoardColumnRequest request) {
        BoardColumnResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
