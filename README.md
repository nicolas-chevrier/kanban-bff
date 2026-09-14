# kanban-bff

BFF Spring Boot exposant des API REST pour dialoguer avec le projet `react-kanban`. Gère les ordres de travail (work orders) et leur lien avec les cartes du kanban, avec une base H2 embarquée.

## Stack

- Java 21
- Spring Boot 4.1.1 (Web, Data JPA, Validation)
- H2 (base embarquée en mémoire)
- springdoc-openapi (Swagger UI)

## Lancer l'application

```bash
mvn spring-boot:run
```

L'application démarre sur le port `8080` (configurable dans `src/main/resources/application.yml`).

## Documentation API (Swagger)

- Swagger UI : http://localhost:8080/swagger-ui.html
- Spec OpenAPI (JSON) : http://localhost:8080/v3/api-docs

## Console H2

- URL : http://localhost:8082 (serveur web H2 autonome, port dédié — voir `H2ConsoleConfig`)
- JDBC URL : `jdbc:h2:mem:workorderdb`
- User : `sa` / Password : *(vide)*

## API

### Ordres de travail

Base path : `/api/work-orders`

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/work-orders` | Lister les ordres de travail (filtrable par `kanbanCardId`) |
| GET | `/api/work-orders/{id}` | Récupérer un ordre de travail |
| POST | `/api/work-orders` | Créer un ordre de travail |
| PUT | `/api/work-orders/{id}` | Mettre à jour un ordre de travail |
| PATCH | `/api/work-orders/{id}/link` | Lier un ordre de travail à une carte kanban |
| DELETE | `/api/work-orders/{id}` | Supprimer un ordre de travail |

### Colonnes du kanban

Base path : `/api/columns`

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/columns` | Lister les colonnes, dans l'ordre d'affichage |
| POST | `/api/columns` | Créer une colonne (id genere depuis le libelle) |

### Cartes kanban

Base path : `/api/cards` — respecte le contrat attendu par le `RestDataProvider` de `@svar-ui/react-kanban`.

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/api/cards` | Lister les cartes |
| POST | `/api/cards` | Créer une carte |
| PUT | `/api/cards/{id}` | Mettre à jour une carte |
| PUT | `/api/cards/{id}/move` | Déplacer une carte (colonne et/ou position) |
| POST | `/api/cards/{id}/duplicate` | Dupliquer une carte |
| DELETE | `/api/cards/{id}` | Supprimer une carte |
