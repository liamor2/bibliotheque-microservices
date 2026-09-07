# Bibliothèque — module 10

Projet autonome de gestion de livres et d'emprunts, construit sur Eureka, Config Server et API Gateway.

## Attendus du projet

- [x] book-service refuse bien de décrémenter le stock d'un livre déjà à 0 exemplaires disponibles (409, pas une exception non gérée).
- [x] loan-service refuse la création d'un emprunt pour un livre inexistant (400), et pour un livre en rupture de stock (409).
- [x] Le retour d'un emprunt déjà RETURNED est refusé proprement (409), pas silencieusement accepté.
- [x] availableCopies ne peut jamais dépasser totalCopies, même après plusieurs retours.
- [x] Les deux services s'enregistrent dans Eureka et sont joignables via la gateway.
- [x] Au moins un test vérifie explicitement le scénario "stock épuisé".

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


## Documentation et Tests

Swagger Book/Loan est également accessible via `/docs/books/swagger-ui.html` et `/docs/loans/swagger-ui.html` sur la gateway.
Les services Product et Order conservent leur documentation Swagger sur leurs ports directs.

Les fichiers `.http` et la collection Postman du dossier `postman/` couvrent création, emprunt réussi, stock épuisé, retour et double retour.

## Contributeurs

- Liam GATTEGNO
- Emmanuelle CURIANT