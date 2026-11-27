# Contribuer

Conventions du projet, pour ne pas avoir à les retrouver dans l'historique.

## Branches

`main` reste toujours dans un état qui compile et passe la CI. Tout changement passe par une branche et une pull request.

La branche porte le type du changement en préfixe, suivi d'une description courte en kebab-case :

```
feat/course-list
fix/chapter-order
docs/domain-model
refactor/shared-components
```

Une branche correspond à une étape de la feuille de route, pas à une feature entière. Des PR courtes se relisent mieux et gardent un historique lisible.

## Commits et pull requests

Les messages de commit et les titres de PR suivent [Conventional Commits](https://www.conventionalcommits.org/fr/) :

```
<type>: <description à l'impératif, en minuscules>
```

| Type | Usage |
|---|---|
| `feat` | nouvelle fonctionnalité |
| `fix` | correction de bug |
| `docs` | documentation uniquement |
| `refactor` | réorganisation sans changement de comportement |
| `test` | ajout ou correction de tests |
| `build` | Gradle, dépendances, configuration Android |
| `ci` | workflows GitHub Actions |
| `chore` | le reste (fichiers de config, nettoyage) |

Exemples : `feat: liste des chapitres d'un cours`, `fix: ordre des définitions après suppression`.

Les notes de release sont générées à partir des titres de PR fusionnées. Un titre clair donne directement des notes lisibles.

## Avant de pousser

```powershell
.\gradlew ktlintCheck lintDebug testDebugUnitTest
```

Ce sont les vérifications de la CI. Le projet tourne en zéro avertissement : un warning Android Lint ou du compilateur Kotlin fait échouer le build.

Pour corriger le style automatiquement :

```powershell
.\gradlew ktlintFormat
```

Les règles de style (style `android_studio`, lignes de 120 caractères) sont dans `.editorconfig`.

## Ce qui ne va jamais dans le dépôt

- `key.properties`, `*.jks`, `*.keystore` (signature release)
- `google-services.json` (configuration Firebase, à partir de la beta)
- `local.properties`

Ils sont déjà dans le `.gitignore`. La CI les recrée depuis les secrets GitHub quand elle en a besoin.

## CI

Le workflow `ci.yml` tourne à chaque pull request vers `main` et à chaque push sur `main` :

1. validation du wrapper Gradle
2. ktlint
3. Android Lint
4. tests unitaires
5. build de l'APK release

Les rapports de lint et de tests restent disponibles 7 jours dans les artefacts du workflow.

Dependabot ouvre chaque semaine des PR de mise à jour pour les dépendances Gradle et les actions GitHub.

## Publier une version

Les versions suivent [SemVer](https://semver.org/lang/fr/) avec un suffixe pour les préversions : `0.1.0-alpha.1`, `0.3.0-beta.2`, `1.0.0`.

1. Dans `app/build.gradle.kts`, mettre à jour `versionName` et incrémenter `versionCode`.
2. Fusionner ce changement dans `main` par une PR (`build: version 0.1.0-alpha.1`).
3. Créer et pousser le tag depuis `main` :

```powershell
git switch main
git pull
git tag v0.1.0-alpha.1
git push origin v0.1.0-alpha.1
```

Le workflow `release.yml` vérifie que le tag correspond au `versionName` et s'arrête sinon. Il construit ensuite l'AAB et l'APK signés et crée la release GitHub avec les deux fichiers. Un tag qui contient un tiret donne une préversion.

Secrets nécessaires dans *Settings > Secrets and variables > Actions* :

| Secret | Contenu |
|---|---|
| `ANDROID_KEYSTORE_BASE64` | keystore d'envoi (`.jks`) encodé en base64 |
| `ANDROID_KEYSTORE_PASSWORD` | mot de passe du keystore |
| `ANDROID_KEY_ALIAS` | alias de la clé |
| `ANDROID_KEY_PASSWORD` | mot de passe de la clé |
| `GOOGLE_SERVICES_JSON_BASE64` | `google-services.json` en base64, à partir de la beta |

Pour encoder un fichier en base64 sous PowerShell :

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("upload-keystore.jks")) | Set-Clipboard
```
