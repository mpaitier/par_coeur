# Architecture

Clean architecture avec MVVM, et un découpage du code par feature plutôt que par couche technique. Tout ce qui concerne les cours est dans `features/course`, tout ce qui concerne les chapitres dans `features/chapter`, et ainsi de suite.

## Organisation des packages

```
com.mpaitier.parcoeur
├── core/            thème, injection de dépendances, utilitaires communs
├── navigation/      routes et NavHost
├── shared/          composants UI réutilisés par plusieurs features
└── features/
    └── <feature>/
        ├── data/
        │   ├── datasources/    accès aux données (en mémoire en alpha, Firebase en beta)
        │   ├── mappers/        conversions model <-> entity
        │   ├── models/         objets propres à la source de données (DTO)
        │   └── repositories/   implémentations des interfaces du domain
        ├── domain/
        │   ├── entities/       modèles métier, Kotlin pur
        │   ├── repositories/   interfaces
        │   └── usecases/       une action métier par classe
        └── presentation/
            ├── components/     composables propres à la feature
            ├── screens/        écrans
            └── viewmodels/     état de l'écran et actions de l'utilisateur
```

Features : `course`, `chapter`, `definition`, `learning` (niveaux et révision), `deadline` (échéances).

## Règle de dépendance

```
presentation  ──>  domain  <──  data
```

- `domain` ne dépend de rien. Il n'importe ni Android, ni Compose, ni Firebase : uniquement du Kotlin et les coroutines.
- `data` implémente les interfaces de `domain`.
- `presentation` utilise `domain`, jamais `data` directement.
- Un ViewModel appelle des use cases. Il ne touche ni aux repositories concrets ni aux datasources.

Le passage de l'alpha à la beta repose sur cette règle : on remplace les datasources en mémoire par des datasources Firebase, et ni le domain ni les écrans ne bougent.

## Flux d'une donnée

Exemple avec l'affichage de la liste des cours :

```
CourseListScreen
   │  collectAsStateWithLifecycle()
   ▼
CourseListViewModel          StateFlow<CourseListUiState>
   │
   ▼
GetCoursesUseCase            domain
   │
   ▼
CourseRepository             interface (domain)
   │
   ▼
CourseRepositoryImpl         data
   │
   ▼
CourseDataSource             en mémoire (alpha) ou Firebase (beta)
```

Les repositories exposent des `Flow`. Quand une donnée change (ajout d'un cours, par exemple), la mise à jour remonte toute seule jusqu'à l'écran, sans rechargement manuel.

Une action de l'utilisateur fait le chemin inverse : l'écran appelle une fonction du ViewModel, qui appelle un use case (`AddCourseUseCase`), qui passe par le repository.

## Entités et relations

Voir le [diagramme du modèle du domaine](domain-model.drawio.svg).

Un enfant référence son parent par identifiant (`Chapter.courseId`, `Definition.chapterId`, `Deadline.courseId`). Un cours ne contient pas la liste de ses chapitres : on les obtient en demandant au repository des chapitres ceux d'un cours donné. Chaque feature reste indépendante, et cette structure se transpose directement en collections Firestore.

Les identifiants sont des `String` (UUID en alpha, identifiants de documents Firestore en beta).

## Navigation

Navigation Compose avec des routes type-safe déclarées dans `navigation/Route.kt` :

```kotlin
sealed interface Route {
    @Serializable
    data object CourseList : Route

    @Serializable
    data class ChapterList(val courseId: String) : Route
}
```

Un écran reçoit seulement des identifiants, jamais d'objets complets. Le ViewModel récupère ses paramètres avec `savedStateHandle.toRoute<Route.ChapterList>()` et charge lui-même les données.

## Où ranger un nouveau fichier

| Je crée… | Dossier |
|---|---|
| un modèle métier | `features/<feature>/domain/entities/` |
| une action métier (ajouter, supprimer, filtrer) | `features/<feature>/domain/usecases/` |
| une interface d'accès aux données | `features/<feature>/domain/repositories/` |
| son implémentation | `features/<feature>/data/repositories/` |
| un écran | `features/<feature>/presentation/screens/` |
| un composable utilisé par une seule feature | `features/<feature>/presentation/components/` |
| un composable utilisé par plusieurs features | `shared/components/` |
| une couleur, une typo, une forme | `core/theme/` |
