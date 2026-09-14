package com.kanban.kanbanbff.controller;

import com.kanban.kanbanbff.dto.CardRequest;
import com.kanban.kanbanbff.dto.CardResponse;
import com.kanban.kanbanbff.dto.MoveCardRequest;
import com.kanban.kanbanbff.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Cards", description = "Cartes du kanban, persistees pour le RestDataProvider du frontend")
public class CardController {

    private final CardService service;

    public CardController(CardService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lister les cartes")
    public List<CardResponse> findAll() {
        return service.findAll();
    }

    @PostMapping
    @Operation(summary = "Creer une carte")
    public ResponseEntity<CardResponse> create(@Valid @RequestBody CardRequest request) {
        CardResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre a jour une carte")
    public CardResponse update(@PathVariable Long id, @Valid @RequestBody CardRequest request) {
        return service.update(id, request);
    }

    @PutMapping("/{id}/move")
    @Operation(summary = "Deplacer une carte (colonne et/ou position)")
    public CardResponse move(@PathVariable Long id, @RequestBody MoveCardRequest request) {
        return service.move(id, request);
    }

    @PostMapping("/{id}/duplicate")
    @Operation(summary = "Dupliquer une carte")
    public ResponseEntity<CardResponse> duplicate(@PathVariable Long id, @RequestBody CardRequest request) {
        CardResponse duplicated = service.duplicate(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(duplicated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une carte")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
