# Station Abyssale‑6

**Station Abyssale‑6** est un jeu d’aventure textuel immersif, mêlant exploration, collecte d’objets, interactions avec des personnages non‑joueurs (PNJ) et résolution d’énigmes.  
Vous incarnez un survivant dans une station de recherche sous‑marine en perdition à 1 000 mètres de profondeur. Votre objectif : rétablir le courant avant que l’oxygène ne s’épuise.

![Java](https://img.shields.io/badge/Java-21-blue)
![Version](https://img.shields.io/badge/version-2.1.15-green)
![Licence](https://img.shields.io/badge/licence-Éducatif-lightgrey)
![Langues](https://img.shields.io/badge/langues-EN/FR/DE/ZH-red)
![TTS](https://img.shields.io/badge/TTS-Multilingue-purple)
![LLM](https://img.shields.io/badge/LLM-Local-yellow)

---

## ✨ Fonctionnalités

- **Carte étendue** : 13 salles interconnectées avec sorties directionnelles (Nord, Sud, Est, Ouest, Haut, Bas).
- **Système d’inventaire** : poids maximal, objets à collecter, échanges avec PNJ.
- **Personnages dynamiques** :
  - PNJ fixes et mobiles (déplacements aléatoires, suivis de chemin ou poursuite du joueur).
  - Dialogues et échanges d'objets (donner/recevoir).
- **Objets spéciaux** :
  - **Téléporteur (Beamer)** : mémorise une pièce et y retourne.
  - **Cookie magique** : augmente la capacité de transport.
  - **Torche** : éclaire et déclenche un easter egg.
  - **Combinaison de plongée, cartes d’accès, clé anglaise…**
- **Portes verrouillables** : nécessitent des objets spécifiques pour être ouvertes/fermées.
- **Salle Transporteur** : téléportation aléatoire dans une autre pièce (mode test déterministe disponible).
- **Puzzle final** : panneau de commande du réacteur avec voltmètre analogique – trouver la bonne combinaison d’interrupteurs.
- **Sauvegarde XML** : exportez et reprenez votre partie à tout moment.
- **Musique et effets sonores** : ambiance sous‑marine, bruitages d’objets, alarme de fin de temps.
- **Minuteur** : 10 minutes pour réussir – Game Over si le temps expire ou si le puzzle échoue.
- **Internationalisation avancée** : support complet de l'anglais, français, allemand, chinois (traduction dynamique de l'interface et des dialogues).
- **Interface graphique moderne** : images en fondu, overlays d’objets/personnages, popups interactives.
- **Synthèse vocale multilingue (Text-to-Speech)** : les dialogues des PNJ sont prononcés à voix haute automatiquement dans la langue du jeu.
- **LLM local** : dialogues dynamiques et adaptatifs avec les PNJ grâce à l'intelligence artificielle (LM Studio + LangGraph).

---

## 🖼️ Captures d’écran

<div align="center">
  <img src="docs/images/sas.gif" width="45%" alt="SAS d'entrée" />
  <img src="docs/images/win.gif" width="45%" alt="Condition de victoire" />
</div>

---

## 🚀 Installation et lancement

### Prérequis

- Java 21 ou supérieur
- Python 3.8 ou supérieur (pour le serveur NPC)
- LM Studio (pour exécuter le modèle LLM localement)
- Un fichier JAR exécutable (`game.jar`)

### Lancement du jeu

```bash
java -jar game.jar
```

> **Note** : Si vous lancez depuis un IDE, assurez‑vous que le répertoire `audio/`, `images/`, `items/`, `characters/` et `messages/` est dans le classpath.

---

## 🤖 Installation du serveur NPC (LLM local)

Le jeu utilise un serveur FastAPI avec LangGraph pour gérer les dialogues des PNJ via un modèle de langage local **multilingue**.

### 1. Installation de LM Studio

1. Téléchargez LM Studio depuis [https://lmstudio.ai/](https://lmstudio.ai/)
2. Installez LM Studio sur votre système
3. Lancez LM Studio

### 2. Téléchargement du modèle qwen3-4b-instruct-2507

Dans LM Studio :
1. Cliquez sur l'onglet **Search** (loupe)
2. Recherchez `qwen3-4b-instruct-2507`
3. Téléchargez le modèle (IBM Granite 3.2 2B Instruct)
4. Allez dans l'onglet **Local Server** (icône `</>`)
5. Sélectionnez `qwen3-4b-instruct-2507` dans le dropdown
6. Cliquez sur **Start Server**
7. Vérifiez que le serveur tourne sur `http://localhost:1234` (ou notez l'adresse)

### 3. Installation de Python et dépendances

#### Windows
```bash
# Téléchargez Python depuis https://python.org (version 3.8+)
# Vérifiez l'installation
python --version

# Installez pip (normalement inclus)
python -m ensurepip --upgrade
```

### 4. Installation des dépendances Python

```bash
# Installez les packages requis
pip install langgraph langchain_openai fastapi uvicorn
```

### 5. Configuration du serveur NPC

Créez un fichier `npc_server.py` (fourni avec le jeu) et un fichier `npc_config.json` contenant la configuration des PNJ.

### 6. Lancement du serveur NPC

```bash
python npc_server.py
```

Le serveur démarre sur `http://localhost:8000` :
```
INFO:     Uvicorn running on http://0.0.0.0:8000
INFO:     Started server process [12345]
INFO:     Waiting for application startup.
INFO:     Application startup complete.
✅ Loaded NPC: character_guard for language: en
✅ Loaded NPC: character_guard for language: fr
✅ Loaded NPC: character_guard for language: de
✅ Loaded NPC: character_guard for language: zh
✅ Loaded NPC: dr_chen for language: en
...
```

### 7. Vérification du bon fonctionnement

Testez le serveur NPC avec curl :
```bash
curl http://localhost:8000/npcs
```

Devrait retourner :
```json
{
  "npcs":["character_guard","character_scientist","character_doctor","character_engineer","character_nurse","character_geneticist","character_wandering_tech","character_researcher","character_stalker"],"supported_languages":["en","fr","de","zh"]
}
```

Testez un dialogue multilingue :
```bash
# Dialogue en français
curl.exe --% -X POST http://localhost:8000/chat -H "Content-Type: application/json" -d "{\"npc_name\":\"character_guard\",\"player_id\":\"player1\",\"message\":\"Bonjour, qui êtes-vous?\",\"language\":\"fr\"}"
```

---

## 🌍 Support Multilingue Complet

### Langues disponibles

| Code | Langue | Interface | Dialogues PNJ | TTS | LLM |
|------|--------|-----------|---------------|-----|-----|
| `en` | Anglais | ✅ | ✅ | ✅ | ✅ |
| `fr` | Français | ✅ | ✅ | ✅ | ✅ |
| `de` | Allemand | ✅ | ✅ | ✅ | ✅ |
| `zh` | Chinois | ✅ | ✅ | ✅ | ✅ |

### Changement de langue en jeu

```bash
lang fr     # Passe en français
lang en     # Passe en anglais
lang de     # Passe en allemand
lang zh     # Passe en chinois
```

**Effets du changement de langue** :
- ✅ Interface graphique immédiatement traduite
- ✅ Dialogues des PNJ générés dynamiquement dans la nouvelle langue
- ✅ Synthèse vocale bascule automatiquement vers les voix appropriées
- ✅ LLM reçoit l'indicateur de langue pour répondre correctement

---

## 🎤 Synthèse Vocale Multilingue (TTS)

Le jeu intègre une fonctionnalité de **synthèse vocale multilingue** qui permet aux PNJ de **parler à voix haute** dans la langue actuelle du jeu.

### 🌐 Support linguistique TTS

| Langue | Voix Féminine | Voix Masculine | Disponibilité |
|--------|---------------|----------------|----------------|
| **Anglais** | Microsoft Zira Desktop | Microsoft David Desktop | Windows 10/11 |
| **Français** | Microsoft Hortense | Microsoft Paul | Pack linguistique FR |
| **Allemand** | Microsoft Hedda Desktop | Microsoft Stefan Desktop | Pack linguistique DE |
| **Chinois** | Microsoft Huihui Desktop | Microsoft Kangkang Desktop | Pack linguistique ZH |

### Comment ça fonctionne

- **Détection automatique** : La langue actuelle est lue via `Lang.getInstance().getLanguage()`
- **Sélection dynamique** : Les voix appropriées sont chargées automatiquement selon la langue
- **Support UTF-8 complet** : Tous les accents et caractères spéciaux sont correctement prononcés
- **Fallback intelligent** : Si une voix spécifique n'est pas disponible, le système utilise une méthode générique par genre/culture

### Contrôle de la synthèse vocale

Dans la fenêtre de dialogue d'un PNJ, vous trouverez un **bouton haut-parleur** (🔊/🔈) situé sous l'image du personnage :

| État | Icône | Couleur | Description |
|------|-------|---------|-------------|
| **Activée** | 🔊 | Bleu (couleur d'accent) | Les dialogues sont prononcés automatiquement dans la langue actuelle |
| **Désactivée** | 🔈 | Gris | Seul le texte s'affiche, pas de synthèse vocale |

**Comment utiliser** :
1. Cliquez sur le bouton haut-parleur pour **activer/désactiver** la synthèse vocale
2. L'icône change pour indiquer l'état actuel
3. La préférence persiste pendant toute la durée de la conversation
4. Le TTS s'adapte automatiquement lors du changement de langue

### Configuration technique

La synthèse vocale utilise l'**API SAPI (Speech API) de Windows** via des scripts PowerShell :

```java
// Exemple d'utilisation - détection automatique
SapiTTS.speak("Hello, I am Guard Thompson");  // Utilise la voix anglaise
SapiTTS.speak("Bonjour, je suis le garde");   // Utilise la voix française
SapiTTS.speak("Guten Tag, ich bin der Wächter"); // Utilise la voix allemande

// Contrôle manuel du genre
SapiTTS.speak("Text", true);  // Voix féminine
SapiTTS.speak("Text", false); // Voix masculine
```

**Technologies utilisées** :
- **PowerShell** : Exécution en arrière-plan des commandes TTS
- **SAPI 5** : Moteur de synthèse vocale natif de Windows
- **Lang class** : Fournit la langue actuelle pour la sélection des voix
- **UTF-8 BOM** : Encodage garantissant le support des caractères internationaux

### Installation des voix TTS

#### Windows 10/11

Pour installer une nouvelle langue et ses voix TTS :

1. **Paramètres** → **Heure et langue** → **Langue et région**
2. Cliquez sur **Ajouter une langue**
3. Sélectionnez la langue souhaitée (Allemand, Chinois, etc.)
4. Installez le pack linguistique complet
5. Les voix TTS seront automatiquement disponibles

#### Vérification des voix installées

```powershell
# PowerShell commande pour lister les voix disponibles
[System.Speech.Synthesis.SpeechSynthesizer]::new().GetInstalledVoices() | 
    Select-Object -ExpandProperty VoiceInfo | 
    Select Name, Culture
```

### Personnages et leurs voix multilingues

| PNJ | Genre | Voix EN | Voix FR | Voix DE | Voix ZH |
|-----|-------|---------|---------|---------|---------|
| Garde Thompson | M | David | Paul | Stefan | Kangkang |
| Dr. Chen | M | David | Paul | Stefan | Kangkang |
| Docteur Williams | M | David | Paul | Stefan | Kangkang |
| Ingénieur Martinez | M | David | Paul | Stefan | Kangkang |
| Technicien Errant | M | David | Paul | Stefan | Kangkang |
| **Infirmière** | **F** | **Zira** | **Hortense** | **Hedda** | **Huihui** |

---

## 🧠 Intelligence Artificielle Multilingue (LLM)

### Architecture du système LLM

Le jeu utilise un système de dialogue avancé basé sur **LangGraph** et **FastAPI** :

```
Jeu Java → API REST → Serveur FastAPI → LangGraph Agent → LLM Local → Réponse
```

### Caractéristiques multilingues

- **Détection langue** : La classe `Lang` transmet la langue actuelle à chaque requête API
- **Agents par langue** : Chaque PNJ a 4 agents distincts (1 par langue) pour une mémoire séparée
- **Prompt systèmes localisés** : Instructions spécifiques à chaque langue pour guider le LLM
- **Contexte maintenu** : Historique des conversations conservé séparément par langue

### Configuration des PNJ (npc_config.json)

```json
[
    {
        "name": "character_guard",
        "model": "qwen3-4b-instruct-2507",
        "temperature": 0.7,
        "system_prompt": "You are Guard Thompson, a stern but fair security officer..."
    },
    {
        "name": "dr_chen", 
        "model": "qwen3-4b-instruct-2507",
        "temperature": 0.8,
        "system_prompt": "You are Dr. Chen, a brilliant but eccentric scientist..."
    }
]
```

### Comment le LLM gère les langues

1. **Requête du jeu** :
   ```json
   {
     "npc_name": "character_guard",
     "player_id": "player123",
     "message": "Bonjour, qui êtes-vous?",
     "language": "fr"
   }
   ```

2. **Sélection de l'agent** : Le serveur utilise `agents[character_guard][fr]`

3. **Prompt système amélioré** :
   ```
   You are Guard Thompson, a stern but fair security officer...
   
   You must respond ONLY in French language. Use 'tu' for informal address.
   
   IMPORTANT: The player is currently speaking in FR. You must respond in the SAME language.
   ```

4. **Génération de réponse** : Le LLM produit une réponse cohérente en français

5. **Conservation mémoire** : Historique sauvegardé par `thread_id = character_guard_player123_fr`

### API Endpoints

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/chat` | POST | Envoie un message au PNJ (avec paramètre language) |
| `/npcs` | GET | Liste tous les PNJ et langues supportées |
| `/npc/{name}/languages` | GET | Langues disponibles pour un PNJ spécifique |

### Performance et mémoire

- **Mémoire séparée** : Chaque combinaison (PNJ, Joueur, Langue) a son propre historique
- **Checkpointing** : Utilisation de `MemorySaver` de LangGraph pour persistance
- **Scalabilité** : Supporte des dizaines de joueurs simultanés
- **Latence** : ~200-500ms par réponse (dépend du modèle)

---

## 🎮 Comment jouer

### Objectif

1. Explorez les 13 salles de la station.
2. Collectez des objets (cartes, combinaison, clé anglaise, torche…).
3. Échangez avec les PNJ pour obtenir des objets manquants.
4. Déverrouillez les portes et progressez jusqu’à la salle du réacteur.
5. Résolvez le puzzle électrique pour rétablir le courant.

### Commandes

| Commande            | Description                                                     |
| ------------------- | --------------------------------------------------------------- |
| `go <direction>`    | Se déplacer (nord, sud, est, ouest, haut, bas).                 |
| `look`              | Réafficher la description de la pièce.                          |
| `take <objet>`      | Ramasser un objet dans la pièce.                                |
| `drop <objet>`      | Déposer un objet de l’inventaire dans la pièce.                 |
| `inventory` / `inv` | Afficher l’inventaire détaillé (poids, objets).                 |
| `eat <objet>`       | Consommer un objet comestible (ex. cookie magique).             |
| `use <objet>`       | Utiliser un objet (clé sur une porte, torche, etc.).            |
| `charge`            | Charger le téléporteur (Beamer) avec la pièce actuelle.         |
| `fire`              | Se téléporter vers la pièce mémorisée par le Beamer.            |
| `talk [nom]`        | Parler à un PNJ (sans nom : popup de sélection).                |
| `give <objet>`      | Donner un objet à un PNJ (popup si seul `give`).                |
| `back`              | Revenir à la pièce précédente (si accessible).                  |
| `help`              | Afficher l’aide.                                                |
| `save <nom>`        | Sauvegarder la partie en XML (dans `saves/`).                   |
| `load <nom>`        | Charger une partie XML.                                         |
| `test <fichier>`    | Exécuter un script de test (fonctionnalité développeur).        |
| `lang <code>`       | Changer la langue (`en`, `fr`, `de`, `zh`).                     |
| `alea <salle>`      | (Mode test) Force une destination pour les salles Transporteur. |
| `tts on/off`        | Activer/désactiver globalement la synthèse vocale.              |
| `quit`              | Quitter le jeu (avec confirmation).                             |

### Commandes multilingues

Les commandes sont disponibles dans toutes les langues supportées :

| Français | English | Deutsch | 中文 |
|----------|---------|---------|-----|
| `aller nord` | `go north` | `gehe norden` | `去北边` |
| `regarder` | `look` | `schauen` | `看` |
| `prendre` | `take` | `nehmen` | `拿` |
| `inventaire` | `inventory` | `inventar` | `背包` |

---

## 📦 Objets

| Image                                                          | Objet                  | Utilité                                                     |
| -------------------------------------------------------------- | ---------------------- | ----------------------------------------------------------- |
| <img src="items/item_blue_card.png" width="32" height="32">    | Carte bleue            | Déverrouille certaines portes (ex. serre).                  |
| <img src="items/item_red_card.png" width="32" height="32">     | Carte rouge            | Déverrouille la porte des machines.                         |
| <img src="items/item_wrench.png" width="32" height="32">       | Clé anglaise           | Déverrouille l’accès au réacteur.                           |
| <img src="items/item_diving_suit.png" width="32" height="32">  | Combinaison de plongée | Permet d’accéder au laboratoire (porte étanche).            |
| <img src="items/item_genetic.png" width="32" height="32">      | Échantillon génétique  | Échangeable contre la combinaison de plongée.               |
| <img src="items/item_oxygen.png" width="32" height="32">       | Bouteille d’oxygène    | Échangeable contre le cookie magique.                       |
| <img src="items/item_firstaid.png" width="32" height="32">     | Trousse de secours     | Donnée par l’infirmière, échangeable contre la carte rouge. |
| <img src="items/item_torch.png" width="32" height="32">        | Torche                 | Éclaire (easter egg dans la salle d’observation).           |
| <img src="items/item_magic_cookie.png" width="32" height="32"> | Cookie magique         | Augmente la capacité de transport de +13 kg.                |
| <img src="items/item_beamer.png" width="32" height="32">       | Téléporteur (Beamer)   | Mémorise une pièce (`charge`) et y retourne (`fire`).       |

---

## 👥 Personnages (PNJ)

| Image                                                                      | PNJ               | Localisation            | Échange / Rôle                                 | Voix        | LLM |
| -------------------------------------------------------------------------- | ----------------- | ----------------------- | ---------------------------------------------- | ----------- |-----|
| <img src="characters/character_scientist.png" width="32" height="64">      | Scientifique      | Laboratoire             | Beamer ↔ Bouteille d’oxygène                   | Masculine   | ✅  |
| <img src="characters/character_doctor.png" width="32" height="64">         | Médecin           | Infirmerie              | Oxygène ↔ Cookie magique                       | Masculine   | ✅  |
| <img src="characters/character_guard.png" width="32" height="64">          | Garde             | Poste de garde          | Torche ↔ Carte bleue                           | Masculine   | ✅  |
| <img src="characters/character_engineer.png" width="32" height="64">       | Ingénieur         | Serre                   | Échantillon génétique ↔ Combinaison de plongée | Masculine   | ✅  |
| <img src="characters/character_nurse.png" width="32" height="64">          | Infirmière        | Infirmerie              | Carte bleue ↔ Trousse de secours               | **Féminine**| ✅  |
| <img src="characters/character_geneticist.png" width="32" height="64">     | Génétiste         | Hydroponics             | Trousse de secours ↔ Carte rouge               | Masculine   | ✅  |
| <img src="characters/character_wandering_tech.png" width="32" height="64"> | Technicien errant | Mobile (aléatoire)      | Réagit à la clé anglaise                       | Masculine   | ✅  |
| <img src="characters/character_researcher.png" width="32" height="64">     | Chercheur         | Mobile (chemin fixe)    | Réagit à la clé anglaise                       | Masculine   | ✅  |
| <img src="characters/character_stalker.png" width="32" height="64">        | Stalker           | Mobile (suit le joueur) | Dialogue, ne donne rien (mais suit activement) | Masculine   | ✅  |

---

## 🧩 Puzzle du réacteur

Avant de gagner, vous devez résoudre un **puzzle électrique** dans la salle du réacteur :

- Un panneau avec 8 interrupteurs.
- Chaque interrupteur active une résistance et une source de tension.
- L’aiguille du voltmètre indique la tension résultante.
- **Solution** : obtenir une tension comprise entre **3,2 V et 3,4 V** (configuration unique).
- Si la tension est incorrecte, vous échouez → **Game Over**.

> 💡 Indice : la bonne configuration correspond à la résolution “manuelle” du circuit (loi des nœuds / Millman).

---

## 🧪 Mode test et sauvegarde XML

### Sauvegarde

La commande `save <nom>` génère un fichier `saves/<nom>.xml` contenant :

- Toutes les pièces (avec leur image associée).
- Les sorties, portes, trappes.
- L’état du joueur (inventaire, historique, poids, pièce courante).
- L’état des personnages mobiles (position, stratégie, chemin).
- Le temps restant.
- **La langue active** (pour restaurer correctement l'interface).

### Chargement

`load <nom>` restaure intégralement la partie, y compris la langue.

### Commandes de test

- `test <fichier>` : exécute une série de commandes depuis un fichier texte.
- `alea <salle>` : force la prochaine téléportation (mode test – désactivable avec `alea` seul).
- `roomis`, `roomhas`, `playerhas`… sont utilisées dans les scripts de validation.

---

## 🌐 Internationalisation

### Architecture i18n

Le jeu supporte quatre langues avec des fichiers de ressources externes :

- Français (`messages_fr_FR.properties`)
- Anglais (`messages_en_US.properties`)
- Allemand (`messages_de_DE.properties`)
- Chinois (`messages_zh_CN.properties`)

### Format des fichiers de propriétés

```properties
# messages_fr_FR.properties
welcome=Station Abyssale-6
look_description=Vous êtes dans {0}. Vous voyez : {1}
take_success=Vous avez pris {0}
```

### Changement dynamique

```bash
lang fr     # Change immédiatement toute l'interface
```

Les textes mis à jour :
- ✅ Interface graphique
- ✅ Messages système
- ✅ Commandes
- ✅ Aide
- ✅ Dialogues LLM (via API)
- ✅ Synthèse vocale

---

## 🎵 Musique et sons

- Musique d’ambiance en boucle : `theme.wav`
- Effets sonores : porte, téléportation, charge, clé acceptée/refusée, compte à rebours, explosion (game over), félicitations (victoire).
- La musique est gérée par la classe `MusicPlayer` (API Java Sound).
- **Synthèse vocale multilingue** : Parole des PNJ via PowerShell + SAPI Windows avec support EN/FR/DE/ZH.

---

## 🥚 Easter Egg

Dans la **salle d’observation** (`room_obs`), utilisez la **torche** (`use torch`) :

- Si la batterie de la torche est **≥ 15 %**, une image secrète apparaît (fondu entrant) et la batterie se décharge progressivement.
- Si la batterie est trop faible, un message vous invite à recharger la torche.

---

## 🛠️ Architecture technique (résumé)

- **Modèle MVC** : `GameEngine` (contrôleur), `Room`/`Player`/`Item` (modèle), `GameGUI` (vue).
- **Gestion des commandes** : `Parser` + `CommandWord` enum + classes dédiées (`GoCommand`, `TakeCommand`, …).
- **PNJ** : hiérarchie `Entity` → `Character` → `MovingCharacter`. Stratégies de déplacement par `enum MovementStrategy`.
- **Objets** : base `Item`, sous‑classes `Beamer`, `MagicCookie`, `DivingSuit`, `Torch`, `GenericItem`.
- **Sauvegarde XML** : `GameSaverXML` / `GameLoaderXML` avec gestion des attributs (`image`, `charged`, `room`, `language`, etc.).
- **Internationalisation** : classe `Lang` (singleton) + `ResourceBundle` + support dynamique.
- **Interface graphique** : `TransitionPanel` (images en fondu), `OverlayManager` (icônes d’objets/personnages), `InventoryPopup`, `CharacterInteractionPopup`, `ReactorPuzzleDialog`.
- **Audio** : `MusicPlayer` (clip boucle + SFX).
- **Serveur NPC** : FastAPI + LangGraph + LangChain, modèle local via LM Studio, **support multilingue complet**.
- **Synthèse vocale multilingue** : `SapiTTS` avec sélection automatique des voix EN/FR/DE/ZH via SAPI Windows, support UTF-8, voix commutables.

### Flux de données multilingue

```
Jeu Java
  └─ Lang.getInstance().getLanguage() → "fr"
      ├─ Interface UI → messages_fr_FR.properties
      ├─ API Call → {"language": "fr"}
      │   └─ Serveur Python → Agent FR → Réponse FR
      └─ TTS → Microsoft Hortense/Paul (voix FR)
```

---

## 🐛 Dépannage

### Problèmes TTS multilingue

| Problème | Solution |
|----------|----------|
| La voix anglaise fonctionne, pas l'allemande | Installer le pack linguistique allemand dans Windows |
| Accents français mal prononcés | Utiliser la version récente avec support UTF-8 |
| Voix chinoise ne fonctionne pas | Installer le pack linguistique chinois (sinon, fallback anglais) |
| "Microsoft Hedda" introuvable | Le système utilise fallback par genre/culture |

### Problèmes LLM

| Problème | Solution |
|----------|----------|
| Le PNJ répond en anglais alors que jeu est en français | Vérifier que le paramètre "language" est bien envoyé |
| Réponses incohérentes | Réduire `temperature` dans npc_config.json |
| Mémoire perdue entre sessions | Vérifier le `MemorySaver` - normalement conservé en RAM |
| Temps de réponse lent | Utiliser un modèle plus petit (ex: 1B au lieu de 2B) |

---

## 🙏 Crédits et licence

- **Développement** : Alexander KAZAZYAN
- **Conception** : Projet universitaire – IUT, département Informatique
- **Inspirations** : jeux d’aventure textuels classiques (Zork, Colossal Cave), films de science‑fiction
- **Technologies** : Java 21, Python 3.8+, LangChain, LangGraph, FastAPI, LM Studio
- **Licence** : Projet éducatif – libre d’utilisation pour l’apprentissage

---

<div align="center">
  ⏥ STATION ABYSSALE‑6 · version 3.1.1 · juin 2026 ⏥<br>
  🌍 Multilingual Adventure · 🤖 AI-Powered NPCs · 🎤 Text-to-Speech
</div>
