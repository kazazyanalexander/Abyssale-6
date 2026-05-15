# Contribuer à Station Abyssale‑6

Merci de l’intérêt que vous portez à **Station Abyssale‑6** !  
Ce document décrit les règles à suivre pour signaler un problème, suggérer une amélioration ou soumettre du code.

## 🐛 Signaler un bug

- Utilisez l’onglet **Issues** du dépôt du projet.
- Décrivez clairement le problème : les étapes pour le reproduire, le comportement attendu et observé, et tout message d’erreur.
- Précisez votre environnement (système d’exploitation, version Java – exécutez `java -version`).
- Si le bug concerne une action précise dans le jeu, joignez le fichier de sauvegarde `.xml` correspondant si possible.

## 💡 Suggérer une amélioration

- Ouvrez une *issue* avec le label `enhancement`.
- Expliquez la fonctionnalité et pourquoi elle améliorerait le jeu.
- Soyez précis : par exemple, nouvelle salle, nouvel objet, nouvelle commande, amélioration de l’interface.

## 📦 Configuration pour le développement

### Prérequis

- **Java 21** ou supérieur (le projet utilise la syntaxe et les API compatibles avec Java 21).
- Un IDE Java (IntelliJ IDEA, Eclipse, VS Code avec extensions Java) ou simplement un éditeur de texte et les commandes `javac`/`jar`.
- Git (optionnel mais recommandé).

### Obtenir le code

```bash
git clone https://github.com/kazazyanalexander/Abyssale-6.git
cd abyssale-6
```

### Compilation et exécution

Le projet utilise Java standard sans outil de build externe. Compilez tous les fichiers `.java` :

```bash
javac -d out $(find . -name "*.java")
```

Créez le JAR exécutable :

```bash
jar cfe game.jar pkg_core.Game -C out .
```

Lancez le jeu :

```bash
java -jar game.jar
```

> **Remarque :** Le JAR s’attend à trouver les répertoires suivants au même niveau : `audio/`, `images/`, `items/`, `characters/`, `messages/`, `saves/` (ce dernier est créé automatiquement).

## 🧹 Règles de codage

Nous suivons un style cohérent pour garder le code lisible :

- **Indentation :** 4 espaces (pas de tabulation).
- **Accolades :** style K&R (accolade ouvrante sur la même ligne).
- **Nommage :**
  - Classes : `PascalCase`
  - Méthodes et variables : `camelCase`
  - Constantes : `UPPER_SNAKE_CASE`
  - Packages : `pkg_something` (convention historique, veuillez respecter les noms de packages existants).
- **JavaDoc :** Ajoutez des balises `@param`, `@return` et `@author` pertinentes pour les méthodes et classes publiques.
- **Localisation :** Ne jamais coder en dur une chaîne visible par l’utilisateur. Utilisez `Lang.localazableString("clé")` et ajoutez la clé dans les fichiers `.properties` du répertoire `messages/`.
- **Sérialisation :** Toutes les classes d’état du jeu (`Room`, `Item`, `Character`, …) doivent implémenter `Serializable`. Lorsque vous ajoutez de nouveaux champs, mettez à jour `GameSaverXML` et `GameLoaderXML` en conséquence.

## 🧪 Tests

- Lancez les tests existants avec la commande `test` dans le jeu :
  ```
  test save_n_restore
  test full_game
  ```
- Écrivez de nouveaux scripts de test (fichiers texte dans `tests/`) quand vous ajoutez de nouvelles commandes ou fonctionnalités.
- Utilisez la commande `alea` pour rendre la `TransporterRoom` déterministe pendant les tests.

## 📥 Soumettre des modifications (Pull Requests)

1. **Forkez** le dépôt (si vous n’avez pas les droits d’écriture).
2. **Créez une branche** pour votre modification :
   ```bash
   git checkout -b feature/ma-fonctionnalite
   ```
3. **Validez** vos modifications avec des messages clairs et concis.
4. **Testez** vos changements localement (compilez, exécutez, jouez aux parties concernées).
5. **Poussez** la branche et ouvrez une **Pull Request** vers la branche principale.
6. Dans la description de la PR, expliquez ce que la modification fait et pourquoi.

### Liste de vérification pour une PR

- [ ] Le code respecte le guide de style du projet.
- [ ] Les nouvelles chaînes visibles par l’utilisateur sont localisées.
- [ ] Il n’y a pas de nouveaux avertissements du compilateur.
- [ ] Le jeu fonctionne sans régression (testé avec au moins une partie complète).
- [ ] La documentation (README, JavaDoc) a été mise à jour si nécessaire.

## 🌐 Traductions

Pour ajouter ou améliorer une traduction :

1. Localisez les fichiers `.properties` dans `messages/` (par exemple `messages_fr.properties`).
2. Ajoutez ou modifiez les paires `clé=valeur`.
3. Conservez les mêmes noms de clés dans tous les fichiers de langue.
4. Lancez le jeu et testez avec la commande `lang`.

## 📄 Licence

En contribuant, vous acceptez que votre contribution soit distribuée sous la même licence que le projet (voir le fichier `LICENSE`). Actuellement, le projet utilise la **licence AFL**.

---

Merci d’aider à améliorer Station Abyssale‑6 !  
Pour les changements importants, veuillez d’abord ouvrir une *issue* pour discuter de ce que vous souhaitez modifier.