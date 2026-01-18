# Station Abyssale-6

## PROJET : JEU D'AVENTURE TEXTUEL ENVIRONNEMENTAL

**Station Abyssale-6** est un jeu d'aventure immersif qui combine une interface graphique JavaFX avec une narration textuelle. 
Le joueur incarne un survivant dans une station de recherche sous-marine en perdition à 400 mètres de profondeur, 
avec pour mission de rétablir le courant avant que l'oxygène ne s'épuise.

### OBJECTIF DU PROJET
Créer une expérience de jeu immersive mêlant :
- Exploration d'une station sous-marine 
- Résolution d'énigmes basées sur la collecte d'objets

### VERSION
Version 1.0 - Janvier 2026

### AUTEURS
**Alexander KAZAZYAN** - Développeur principal

### STRUCTURE DU PROJET

#### CLASSES PRINCIPALES

1. **GameEngine** - Contrôleur principal du jeu
   - Gère la logique de jeu et les transitions entre salles
   - Coordonne l'interface graphique, le parsing des commandes et l'audio
   - Gère les conditions de victoire/défaite

2. **Room** - Système de salles
   - 13 salles interconnectées avec des conditions d'accès
   - Système de collecte d'objets (cartes, combinaisons, outils)
   - Sorties directionnelles (nord, sud, est, ouest, haut, bas)

3. **Parser** - Analyseur de commandes
   - Reconnaît les commandes : go, quit, help
   - Vérifie la validité des directions
   - Interface avec le contrôleur de jeu


### INSTRUCTIONS DE DÉMARRAGE

#### PRÉREQUIS
- Java 11 ou supérieur
- Structure de dossiers :
  ```
  projet/

  ```

### INSTRUCTIONS DE JEU

#### DÉMARRAGE
1. Lancer l'application


#### COMMANDES DE JEU


#### STRATÉGIE
1. **Explorer systématiquement** toutes les salles accessibles

### ARCHITECTURE TECHNIQUE


### LICENCE
Projet éducatif - Libre d'utilisation pour l'apprentissage

### CRÉDITS
- Développement : Alexander KAZAZYAN
- Conception : Projet universitaire
- Inspirations : Jeux d'aventure textuels classiques, films de science-fiction

