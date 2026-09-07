# Bibliothèque — module 10

Projet autonome de gestion de livres et d'emprunts, construit sur Eureka, Config Server et API Gateway.

## Démarrage

```bash
mvn clean verify
docker compose up --build
```

Compose démarre les sept services. Le profil `demo` de `book-service` crée trois livres, dont le livre 3 épuisé.

## Accès

| Service | Port | API |
|---|---:|---|
| Gateway | 8080 | `/api/books/**`, `/api/loans/**` |
| Book | 8091 | `/api/books`, `/swagger-ui.html` |
| Loan | 8092 | `/api/loans`, `/swagger-ui.html` |
| Eureka | 8761 | tableau de bord |
| Config Server | 8888 | configuration native |

Swagger Book/Loan est également accessible via `/docs/books/swagger-ui.html` et `/docs/loans/swagger-ui.html` sur la gateway.
Les services Product et Order conservent leur documentation Swagger sur leurs ports directs.

Les fichiers `.http` et la collection Postman du dossier `postman/` couvrent création, emprunt réussi, stock épuisé, retour et double retour.
