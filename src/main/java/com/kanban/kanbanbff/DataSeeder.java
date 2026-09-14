package com.kanban.kanbanbff;

import com.kanban.kanbanbff.model.BoardColumn;
import com.kanban.kanbanbff.model.Card;
import com.kanban.kanbanbff.model.WorkOrder;
import com.kanban.kanbanbff.model.WorkOrderStatus;
import com.kanban.kanbanbff.repository.BoardColumnRepository;
import com.kanban.kanbanbff.repository.CardRepository;
import com.kanban.kanbanbff.repository.WorkOrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Peuple les donnees de demo via les repositories JPA plutot qu'un data.sql :
 * un CommandLineRunner s'execute apres le demarrage complet du contexte
 * Spring, donc apres que Hibernate ait cree le schema (contrairement a
 * data.sql, dont l'ordre par rapport a la creation du schema n'est pas
 * fiable dans cette version de Spring Boot).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final BoardColumnRepository boardColumnRepository;
    private final CardRepository cardRepository;
    private final WorkOrderRepository workOrderRepository;

    public DataSeeder(
            BoardColumnRepository boardColumnRepository,
            CardRepository cardRepository,
            WorkOrderRepository workOrderRepository) {
        this.boardColumnRepository = boardColumnRepository;
        this.cardRepository = cardRepository;
        this.workOrderRepository = workOrderRepository;
    }

    @Override
    public void run(String... args) {
        if (boardColumnRepository.count() == 0) {
            seedColumns();
        }

        if (cardRepository.count() > 0) {
            return;
        }

        Card bug = new Card();
        bug.setLabel("Corriger le bug de connexion");
        bug.setColumn("backlog");
        bug.setPriority(2);
        bug.setPosition(1024d);
        cardRepository.save(bug);

        Card tests = new Card();
        tests.setLabel("Ecrire les tests");
        tests.setColumn("progress");
        tests.setPriority(1);
        tests.setPosition(1024d);
        cardRepository.save(tests);

        WorkOrder diagnose = new WorkOrder();
        diagnose.setTitle("Diagnostiquer le bug de connexion");
        diagnose.setDescription("Analyser les logs et reproduire le bug de login signalé en production");
        diagnose.setStatus(WorkOrderStatus.BACKLOG);
        diagnose.setPriority(2);
        diagnose.setAssignee("Alice Martin");
        diagnose.setDueDate(LocalDate.of(2026, 9, 20));
        diagnose.setKanbanCardId(bug.getId());
        workOrderRepository.save(diagnose);

        WorkOrder unitTests = new WorkOrder();
        unitTests.setTitle("Ecrire les tests unitaires");
        unitTests.setDescription("Couvrir le module d'authentification avec des tests unitaires");
        unitTests.setStatus(WorkOrderStatus.IN_PROGRESS);
        unitTests.setPriority(1);
        unitTests.setAssignee("Bruno Petit");
        unitTests.setDueDate(LocalDate.of(2026, 9, 18));
        unitTests.setKanbanCardId(tests.getId());
        workOrderRepository.save(unitTests);

        WorkOrder docs = new WorkOrder();
        docs.setTitle("Mettre a jour la documentation technique");
        docs.setDescription("Documenter la nouvelle API du BFF pour les developpeurs frontend");
        docs.setStatus(WorkOrderStatus.BACKLOG);
        docs.setPriority(3);
        workOrderRepository.save(docs);
    }

    private void seedColumns() {
        boardColumnRepository.save(newColumn("backlog", "A faire", 0));
        boardColumnRepository.save(newColumn("progress", "En cours", 1));
        boardColumnRepository.save(newColumn("done", "Termine", 2));
    }

    private BoardColumn newColumn(String id, String label, int position) {
        BoardColumn column = new BoardColumn();
        column.setId(id);
        column.setLabel(label);
        column.setPosition(position);
        return column;
    }
}
