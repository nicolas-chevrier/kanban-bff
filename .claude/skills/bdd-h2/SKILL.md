---
name: manipuler-bdd-h2
description: Interroger ou modifier la base H2 locale du BFF kanban-bff (fichier ./data/workorderdb) — lister les tables, inspecter le schéma, exécuter du SQL ad hoc, seed/nettoyage de données. À utiliser si besoin d'explorer, corriger ou peupler les données en base H2 directement.
---

# Manipuler la base H2 (kanban-bff)

## Utilité

Les ressources exposées par API sont stockées dans une table de cette BDD
La modification d'une ressource API doit être répliquée aussi en BDD

## Connexion — infos tirées de `application.yml`

- Fichier de base : `./data/workorderdb` (relatif à la racine du projet, format H2 fichier).
- URL JDBC : `jdbc:h2:file:./data/workorderdb;AUTO_SERVER=TRUE`
- Utilisateur : `sa` — mot de passe : `""` (vide).
- `AUTO_SERVER=TRUE` permet plusieurs connexions simultanées sur le même fichier : pas besoin
  d'arrêter l'application Spring Boot pour se connecter en parallèle avec le shell H2.
- Le profil de test (`src/test/resources/application.yml`) désactive la console H2
  (`app.h2-console.enabled: false`) et ne pointe pas forcément sur le même fichier — ne pas
  confondre les deux environnements.

## Option A — Console web H2 (exploration visuelle)

Quand l'application tourne (`mvn spring-boot:run`), `H2ConsoleConfig` démarre un serveur H2
autonome sur le port **8082** (indépendant de l'auto-configuration Spring Boot, retirée en
Spring Boot 4 — voir commentaire dans `H2ConsoleConfig.java`). Ouvrir `http://localhost:8082`
dans un navigateur et saisir manuellement l'URL JDBC / utilisateur / mot de passe ci-dessus
(pas de pré-remplissage automatique).

## Option B — Shell H2 en ligne de commande (pour requêtes ad hoc / agent)

Ne pas coder en dur un chemin de jar H2 : la version est gérée par `spring-boot-starter-parent`
et peut changer. Résoudre le classpath via Maven avant d'invoquer le `Shell` H2 :

```powershell
mvn -q dependency:build-classpath "-Dmdep.outputFile=h2-classpath.txt"
$cp = Get-Content h2-classpath.txt
java -cp $cp org.h2.tools.Shell `
  -url "jdbc:h2:file:./data/workorderdb;AUTO_SERVER=TRUE" `
  -user sa -password "" `
  -sql "SELECT * FROM CARD;"
```

Le flag `-sql "<requête>"` exécute la ou les requêtes (séparées par `;`) puis quitte
automatiquement — pas de session interactive à fermer, pas de verrou laissé sur le fichier.

Requêtes utiles pour explorer le schéma :

```sql
SHOW TABLES;
SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'CARD';
```

Pour exécuter un script multi-instructions (seed, nettoyage), préférer `RunScript` :

```powershell
java -cp $cp org.h2.tools.RunScript -url "jdbc:h2:file:./data/workorderdb;AUTO_SERVER=TRUE" -user sa -password "" -script seed.sql
```

Régénérer `h2-classpath.txt` uniquement si les dépendances du `pom.xml` changent (pas besoin de
le refaire à chaque requête).

## Règles de prudence

- Toujours commencer par des `SELECT` pour explorer avant toute modification.
- Ne jamais exécuter `DELETE` / `UPDATE` / `DROP` / `TRUNCATE` / `ALTER` sans confirmation
  explicite de l'utilisateur : la base locale peut contenir des données de dev utiles, et ce
  n'est pas une base jetable par défaut.
- Ne pas lancer une modification en ligne de commande pendant que `mvn test` tourne sur le même
  fichier — utiliser le profil test séparé pour les tests, la base `data/workorderdb` pour le
  dev manuel.
- Passer par l'API REST (via les services/controllers existants) plutôt que par SQL direct dès
  que l'opération correspond à une règle métier déjà implémentée (calcul de `position`, valeurs
  par défaut, etc.) — le SQL direct est réservé à l'exploration et aux corrections ponctuelles de
  données, pas à contourner la couche service.
