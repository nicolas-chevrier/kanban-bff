package com.kanban.kanbanbff.service;

import com.kanban.kanbanbff.dto.CardRequest;
import com.kanban.kanbanbff.dto.CardResponse;
import com.kanban.kanbanbff.dto.MoveCardRequest;
import com.kanban.kanbanbff.exception.CardNotFoundException;
import com.kanban.kanbanbff.model.Card;
import com.kanban.kanbanbff.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CardService {

    private static final String DEFAULT_COLUMN = "backlog";
    private static final double POSITION_GAP = 1024d;

    private final CardRepository repository;

    public CardService(CardRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CardResponse> findAll() {
        return repository.findAllByOrderByPositionAsc().stream().map(this::toResponse).toList();
    }

    public CardResponse create(CardRequest request) {
        Card card = new Card();
        card.setLabel(request.getLabel() != null ? request.getLabel() : "");
        card.setDescription(request.getDescription());
        card.setColumn(request.getColumn() != null ? request.getColumn() : DEFAULT_COLUMN);
        card.setPriority(request.getPriority());
        card.setProgress(request.getProgress());
        card.setDeadline(request.getDeadline());
        card.setPosition(nextPosition(card.getColumn()));
        return toResponse(repository.save(card));
    }

    public CardResponse update(Long id, CardRequest request) {
        Card card = getOrThrow(id);
        if (request.getLabel() != null) {
            card.setLabel(request.getLabel());
        }
        card.setDescription(request.getDescription());
        card.setPriority(request.getPriority());
        card.setProgress(request.getProgress());
        card.setDeadline(request.getDeadline());
        // La colonne se modifie uniquement via /move (glisser-deposer) ;
        // l'editeur de carte n'envoie pas ce champ.
        if (request.getColumn() != null) {
            card.setColumn(request.getColumn());
        }
        return toResponse(repository.save(card));
    }

    public CardResponse move(Long id, MoveCardRequest request) {
        Card card = getOrThrow(id);
        String targetColumn = request.getColumn() != null ? request.getColumn() : card.getColumn();
        card.setColumn(targetColumn);
        card.setPosition(computePosition(targetColumn, request.getBefore(), id));
        return toResponse(repository.save(card));
    }

    public CardResponse duplicate(Long id, CardRequest request) {
        Card source = getOrThrow(id);
        Card duplicate = new Card();
        duplicate.setLabel(request.getLabel() != null ? request.getLabel() : source.getLabel());
        duplicate.setDescription(
                request.getDescription() != null ? request.getDescription() : source.getDescription());
        duplicate.setColumn(request.getColumn() != null ? request.getColumn() : source.getColumn());
        duplicate.setPriority(request.getPriority() != null ? request.getPriority() : source.getPriority());
        duplicate.setProgress(request.getProgress() != null ? request.getProgress() : source.getProgress());
        duplicate.setDeadline(request.getDeadline() != null ? request.getDeadline() : source.getDeadline());
        duplicate.setPosition(nextPosition(duplicate.getColumn()));
        return toResponse(repository.save(duplicate));
    }

    public void delete(Long id) {
        Card card = getOrThrow(id);
        repository.delete(card);
    }

    private double nextPosition(String column) {
        List<Card> inColumn = repository.findByColumnOrderByPositionAsc(column);
        if (inColumn.isEmpty()) return POSITION_GAP;
        return inColumn.get(inColumn.size() - 1).getPosition() + POSITION_GAP;
    }

    private double computePosition(String targetColumn, Long beforeId, Long movedCardId) {
        List<Card> inColumn = repository.findByColumnOrderByPositionAsc(targetColumn).stream()
                .filter(c -> !c.getId().equals(movedCardId))
                .toList();

        if (beforeId == null) {
            return inColumn.isEmpty() ? POSITION_GAP : inColumn.get(inColumn.size() - 1).getPosition() + POSITION_GAP;
        }

        int beforeIndex = -1;
        for (int i = 0; i < inColumn.size(); i++) {
            if (inColumn.get(i).getId().equals(beforeId)) {
                beforeIndex = i;
                break;
            }
        }
        if (beforeIndex == -1) {
            return inColumn.isEmpty() ? POSITION_GAP : inColumn.get(inColumn.size() - 1).getPosition() + POSITION_GAP;
        }

        double beforePosition = inColumn.get(beforeIndex).getPosition();
        double previousPosition = beforeIndex > 0 ? inColumn.get(beforeIndex - 1).getPosition() : 0d;
        return (previousPosition + beforePosition) / 2;
    }

    private Card getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new CardNotFoundException(id));
    }

    private CardResponse toResponse(Card card) {
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setLabel(card.getLabel());
        response.setDescription(card.getDescription());
        response.setColumn(card.getColumn());
        response.setPriority(card.getPriority());
        response.setProgress(card.getProgress());
        response.setDeadline(card.getDeadline());
        response.setCreatedAt(card.getCreatedAt());
        response.setUpdatedAt(card.getUpdatedAt());
        return response;
    }
}
