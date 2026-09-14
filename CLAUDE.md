# kanban-bff — Bonnes pratiques de code

Backend Spring Boot (BFF) pour l'application Kanban. Architecture en couches :
`controller` → `service` → `repository`, avec `model` (entités JPA), `dto` (requêtes/réponses),
`exception` (erreurs métier + handler global) et `config`.

## Principes SOLID appliqués à ce projet

- **S — Single Responsibility** : un service par agrégat métier (`CardService`, `BoardColumnService`,
  `WorkOrderService`). Un service ne doit gérer qu'une entité. Si une méthode grossit et mélange
  plusieurs responsabilités (validation, calcul de position, mapping DTO), extraire une méthode privée
  dédiée (voir `computePosition`/`toResponse` dans `CardService`) plutôt que tout empiler dans la méthode publique.
- **O — Open/Closed** : préférer ajouter un nouveau type/statut (ex. `WorkOrderStatus`, une colonne de
  board) plutôt que modifier des `if/switch` existants dispersés dans plusieurs classes. Les enums et
  les colonnes dynamiques (`BoardColumn`) existent justement pour éviter de coder les statuts en dur.
- **L — Liskov Substitution** : peu d'héritage dans ce projet — respecter ce choix. Ne pas introduire de
  hiérarchie de classes juste pour factoriser un peu de code ; préférer la composition.
- **I — Interface Segregation** : les DTO (`CardRequest`/`CardResponse`, `WorkOrderRequest`/`WorkOrderResponse`)
  sont volontairement séparés par usage (entrée vs sortie) plutôt qu'un DTO unique partagé. Garder cette
  séparation pour toute nouvelle entité.
- **D — Dependency Inversion** : les services dépendent des interfaces `Repository` (Spring Data), jamais
  de JPA/Hibernate directement. Les controllers dépendent des services via injection par constructeur
  (pas de `@Autowired` sur champ). Continuer ce style pour tout nouveau composant.

## Clean Architecture / séparation des couches

- **Controller** : validation d'entrée (`@Valid`), mapping HTTP (status codes), délégation immédiate au
  service. Aucune logique métier dans un controller.
- **Service** : logique métier, transactions (`@Transactional`), orchestration des repositories, mapping
  entité ↔ DTO. C'est la seule couche qui doit connaître les règles métier (ex. calcul de `position`,
  valeurs par défaut).
- **Repository** : uniquement des interfaces Spring Data (`JpaRepository`), pas de logique métier.
- **Model** : entités JPA pures. Pas de logique métier complexe dans les entités (comportement anémique
  assumé ici, cohérent avec le style existant).
- **DTO** : jamais exposer une entité JPA directement dans une réponse API. Toujours passer par un DTO
  de réponse (`*Response`), même pour des champs identiques.
- **Exception** : les erreurs métier (ex. `CardNotFoundException`, `WorkOrderNotFoundException`) sont des
  exceptions dédiées, traduites en réponse HTTP par `GlobalExceptionHandler`. Ne pas gérer les erreurs
  HTTP directement dans les services ou controllers.

## Règles pratiques

- Injection de dépendances par constructeur uniquement (pas de champ `@Autowired`, pas de setter injection).
- Un service ne doit pas appeler directement le repository d'un autre agrégat sans passer par son service
  (éviter le couplage direct entre `CardService` et `BoardColumnRepository`, par exemple).
- Pas de logique métier dupliquée entre `create`/`update`/`duplicate` : factoriser dans des méthodes privées
  quand la duplication dépasse 2-3 lignes.
- Toute nouvelle méthode publique de service exposée par un controller doit avoir une annotation `@Operation`
  côté controller (cohérence avec l'existant, utile pour Swagger/OpenAPI).
- Éviter d'ajouter des abstractions (interfaces de service, couches supplémentaires) tant que le besoin
  concret ne s'est pas manifesté — ce projet reste volontairement simple (pas de use-cases/ports séparés
  à la Clean Architecture "stricte", le style Spring Boot pragmatique en couches est le standard ici).
