package com.kanban.kanbanbff.service;

import com.kanban.kanbanbff.dto.BoardColumnRequest;
import com.kanban.kanbanbff.dto.BoardColumnResponse;
import com.kanban.kanbanbff.model.BoardColumn;
import com.kanban.kanbanbff.repository.BoardColumnRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class BoardColumnService {

    private final BoardColumnRepository repository;

    public BoardColumnService(BoardColumnRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<BoardColumnResponse> findAll() {
        return repository.findAllByOrderByPositionAsc().stream().map(this::toResponse).toList();
    }

    public BoardColumnResponse create(BoardColumnRequest request) {
        BoardColumn column = new BoardColumn();
        column.setId(uniqueSlug(request.getLabel()));
        column.setLabel(request.getLabel());
        column.setPosition(nextPosition());
        return toResponse(repository.save(column));
    }

    private int nextPosition() {
        return repository.findAllByOrderByPositionAsc().stream()
                .map(BoardColumn::getPosition)
                .max(Integer::compareTo)
                .map(p -> p + 1)
                .orElse(0);
    }

    private String uniqueSlug(String label) {
        String base = slugify(label);
        String candidate = base;
        int suffix = 2;
        while (repository.existsById(candidate)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }

    private String slugify(String label) {
        String withoutAccents = Normalizer.normalize(label, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        String slug = withoutAccents.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        return slug.isEmpty() ? "colonne" : slug;
    }

    private BoardColumnResponse toResponse(BoardColumn column) {
        BoardColumnResponse response = new BoardColumnResponse();
        response.setId(column.getId());
        response.setLabel(column.getLabel());
        response.setPosition(column.getPosition());
        return response;
    }
}
