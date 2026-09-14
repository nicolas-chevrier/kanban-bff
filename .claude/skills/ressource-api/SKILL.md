---
name: creer-ressource-api
description: Scaffolde une nouvelle ressource REST complete (entite JPA, repository, DTOs, service, controller, exception) dans le BFF kanban-bff, en suivant exactement les conventions et l'architecture en couches deja utilisees (Card, WorkOrder, BoardColumn). A utiliser quand l'utilisateur demande d'ajouter/exposer une nouvelle ressource ou un nouvel endpoint API dans ce projet.
---

# Créer une nouvelle ressource API (kanban-bff)

Ce skill scaffolde une ressource REST complète en respectant l'architecture en couches et les
principes SOLID décrits dans `CLAUDE.md` à la racine du projet. Relire `CLAUDE.md` avant de générer
le code si ce n'est pas déjà en contexte.

## 1. Collecter les informations nécessaires

Avant de générer quoi que ce soit, demander à l'utilisateur (si non fourni) :

- **Nom de l'entité** au singulier, PascalCase (ex. `Comment`, `Attachment`).
- **Champs** : liste `nom: type` (ex. `content: String`, `authorName: String`, `pinned: Boolean`).
- **Champ(s) obligatoire(s)** parmi ces champs (pour la validation `@NotBlank`/`@NotNull`).
- **Base path** de l'API, kebab-case pluriel (ex. `/api/comments`). Proposer une valeur par défaut
  dérivée du nom de l'entité si l'utilisateur ne précise pas.
- Si l'entité est liée à une autre (ex. `cardId` vers `Card`) : le nom du champ de clé étrangère
  (simple `Long xxxId`, pas de relation JPA `@ManyToOne` sauf demande explicite — cohérent avec le
  style actuel du projet qui reste volontairement simple).

Ne pas avancer sur des hypothèses non confirmées si l'un de ces points est ambigu : poser la question.

## 2. Fichiers à générer (package `com.kanban.kanbanbff`)

Toujours respecter l'ordre des couches et les conventions ci-dessous, calquées sur `Card`/`CardService`/`CardController`.

### `model/<Entity>.java`
- `@Entity` + `@Table(name = "<nom_table_snake_case>")`.
- `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)` sur `Long id`.
- Champs métier en `@Column` (préciser `nullable = false` pour les champs obligatoires).
- `createdAt`/`updatedAt` en `Instant`, remplis via `@PrePersist`/`@PreUpdate` (copier le pattern de `Card`).
- Entité anémique : getters/setters classiques, pas de logique métier dans le modèle.

### `repository/<Entity>Repository.java`
- Interface `extends JpaRepository<<Entity>, Long>`.
- Ajouter des méthodes dérivées (`findByXxx`) uniquement si un besoin de tri/filtre est exprimé —
  ne pas ajouter de méthode spéculative.

### `dto/<Entity>Request.java`
- POJO simple avec getters/setters, annotations `jakarta.validation` (`@NotBlank`, `@NotNull`) sur
  les champs obligatoires uniquement.

### `dto/<Entity>Response.java`
- POJO simple avec tous les champs exposés côté API, y compris `id`, `createdAt`, `updatedAt`.
- Ne jamais réutiliser l'entité JPA comme corps de réponse.

### `exception/<Entity>NotFoundException.java`
- `extends RuntimeException`, constructeur `(Long id)` avec message `"<Entité> introuvable avec l'id " + id`
  (copier `CardNotFoundException`).
- Ajouter le handler correspondant dans `exception/GlobalExceptionHandler.java` (même pattern que les
  handlers `WorkOrderNotFoundException`/`CardNotFoundException`, réponse 404 avec `ApiError`).

### `service/<Entity>Service.java`
- `@Service @Transactional` sur la classe ; `@Transactional(readOnly = true)` sur les méthodes de lecture.
- Injection du repository par **constructeur uniquement** (pas de `@Autowired` sur champ).
- Méthodes CRUD minimales selon le besoin exprimé (`findAll`, `findById`, `create`, `update`, `delete`) —
  ne pas ajouter `duplicate`/`move` sauf si l'utilisateur le demande.
- Méthodes privées `toResponse(entity)` pour le mapping entité → DTO (jamais dans le controller).
- Méthode privée `getOrThrow(id)` qui lève `<Entity>NotFoundException`.

### `controller/<Entity>Controller.java`
- `@RestController @RequestMapping("<base-path>")`.
- `@Tag(name = "...", description = "...")` sur la classe (cohérence Swagger).
- Injection du service par constructeur.
- Une méthode par endpoint, chacune annotée `@Operation(summary = "...")`.
- `@Valid @RequestBody` sur les endpoints de création/mise à jour.
- `ResponseEntity.status(HttpStatus.CREATED)` pour la création, `ResponseEntity.noContent()` pour la
  suppression — aucune logique métier dans le controller, délégation immédiate au service.

## 3. Après génération

- Vérifier que le projet compile (`mvn -q compile`).
- Rappeler à l'utilisateur d'ajouter la nouvelle section dans `README.md` (tableau des endpoints, même
  format que les sections existantes) — le faire soi-même si l'utilisateur ne s'y oppose pas.
- Ne pas créer de couche supplémentaire (interface de service, mapper dédié, use-case) : ce projet reste
  volontairement en couches simples (voir `CLAUDE.md`, section "Règles pratiques").
