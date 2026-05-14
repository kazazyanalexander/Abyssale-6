package pkg_ui_components;

import pkg_core.GameEngine;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import pkg_characters.Character;
import pkg_items.Beamer;
import pkg_items.Item;
import pkg_items.MagicCookie;
import pkg_utility.Lang;
import javax.swing.BorderFactory;

/**
 * Affiche des images de fond avec des transitions de fondu fluides et des
 * overlays multiples (personnages en bas à droite, items en haut à gauche,
 * inventaire en bas à gauche).
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class TransitionPanel extends JPanel {

    // ── Images pour la transition ───────────────────────────────────────────────

    /** Image de fond actuellement affichée. */
    private ImageIcon aCurrentImage;

    /** Image de fond suivante (celle qui apparaît en fondu). */
    private ImageIcon aNextImage;

    // ── Paramètres de l'animation ───────────────────────────────────────────────

    /** Niveau de transparence de l'image suivante pendant la transition (0-1). */
    private float aTransitionAlpha = 0f;

    /** Valeur actuelle du fondu (incrémentée progressivement). */
    private float aAlpha = 0f;

    /** Indique si une transition est actuellement en cours. */
    private boolean aIsTransitioning = false;

    /** Timer responsable de l'animation de transition. */
    private Timer aTransitionTimer;

    /**
     * Gestionnaire d'overlays qui gère les overlays affichés sur le panneau.
     */
    private OverlayManager aOverlayManager;

    // ── Tailles standard pour les overlays ──────────────────────────────────────
    /**
     * Hauteur fixe pour les overlays de personnages (redimensionnement
     * proportionnel).
     */
    private static final int CHARACTER_HEIGHT = 256;

    /** Taille fixe (carré) pour les overlays d'items (48x48 pixels). */
    private static final int ITEM_SIZE = 48;

    /** Marge entre les overlays (en pixels). */
    private static final int MARGIN = 10;

    // ── Références pour les interactions ────────────────────────────────────────

    /** Référence vers le moteur de jeu pour exécuter les commandes. */
    private GameEngine aGameEngine;

    /**
     * Constructeur.
     */
    public TransitionPanel() {
        this.setBackground(Color.BLACK);
        this.setOpaque(true);
        this.setLayout(null);

        this.aOverlayManager = new OverlayManager();

        this.aTransitionTimer = new Timer(30, e -> {
            this.aAlpha += 0.04f;
            if (this.aAlpha >= 1.0f) {
                this.aAlpha = 1.0f;
                this.aIsTransitioning = false;
                this.aTransitionTimer.stop();
                this.completeTransition();
            } else {
                this.aTransitionAlpha = this.aAlpha;
            }
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleOverlayClick(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    handleOverlayClick(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    handleOverlayClick(e);
                }
            }
        });
    }

    /**
     * Définit les références au moteur de jeu et à l'interface graphique.
     * 
     * @param pGameEngine Le moteur de jeu
     */
    public void setGameReferences(GameEngine pGameEngine) {
        this.aGameEngine = pGameEngine;
    }

    // ===== MÉTHODES POUR LES PERSONNAGES =====

    /**
     * Ajoute un overlay de personnage.
     * 
     * @param pIcon    L'icône du personnage
     * @param pNameKey La clé du nom du personnage
     */
    public void addCharacterOverlay(final ImageIcon pIcon, final String pNameKey) {
        ImageIcon vScaledIcon = scaleImageToHeight(pIcon, CHARACTER_HEIGHT);
        CharacterOverlay vOverlay = new CharacterOverlay(vScaledIcon, pNameKey,
                vScaledIcon.getIconWidth(), vScaledIcon.getIconHeight());

        vOverlay.setAlpha(0f);

        // CORRECTION : Pour les personnages, on respecte la transition
        this.aOverlayManager.addCharacterOverlay(vOverlay, this.aIsTransitioning);
        this.startOverlayFadeIn(vOverlay);
        this.updateOverlayPositions();
        this.repaint();
    }

    /**
     * Supprime un overlay de personnage par sa clé.
     * 
     * @param pNameKey La clé du personnage
     */
    public void removeCharacterOverlay(final String pNameKey) {
        this.aOverlayManager.removeCharacterOverlay(pNameKey);
        this.updateOverlayPositions();
        this.repaint();
    }

    /**
     * Supprime tous les overlays de personnages.
     */
    public void clearCharacterOverlays() {
        this.aOverlayManager.clearCharacterOverlays();
        this.repaint();
    }

    /**
     * Vérifie si un personnage est déjà affiché.
     * 
     * @param pNameKey La clé du personnage
     * @return true si présent
     */
    public boolean hasCharacterOverlay(final String pNameKey) {
        return this.aOverlayManager.getCharacterOverlays().stream()
                .anyMatch(overlay -> overlay.getNameKey().equals(pNameKey));
    }

    // ===== MÉTHODES POUR LES ITEMS DE LA PIÈCE =====

    /**
     * Ajoute un overlay d'item de la pièce.
     * CORRECTION : Les items de la pièce sont ajoutés IMMÉDIATEMENT,
     * sans passer par les listes pending.
     * 
     * @param pIcon    L'icône de l'item
     * @param pItemKey La clé de l'item
     */
    public void addRoomItemOverlay(final ImageIcon pIcon, final String pItemKey) {
        ImageIcon vScaledIcon = scaleImage(pIcon, ITEM_SIZE, ITEM_SIZE);
        ItemOverlay vOverlay = new ItemOverlay(vScaledIcon, pItemKey,
                ItemOverlay.ItemOverlayType.ROOM_ITEM, ITEM_SIZE);

        vOverlay.setAlpha(0f);

        // CORRECTION : Ajout IMMÉDIAT, pas de pending même pendant transition
        this.aOverlayManager.addRoomItemOverlay(vOverlay, false);
        this.startOverlayFadeIn(vOverlay);
        this.updateOverlayPositions();
        this.repaint();
    }

    /**
     * Supprime un overlay d'item de la pièce.
     * 
     * @param pItemKey La clé de l'item
     */
    public void removeRoomItemOverlay(final String pItemKey) {
        this.aOverlayManager.removeRoomItemOverlay(pItemKey);
        this.updateOverlayPositions();
        this.repaint();
    }

    /**
     * Supprime tous les overlays d'items de la pièce.
     */
    public void clearRoomItemOverlays() {
        this.aOverlayManager.clearRoomItemOverlays();
        this.repaint();
    }

    // ===== MÉTHODES POUR L'INVENTAIRE =====

    /**
     * Ajoute un overlay d'item de l'inventaire.
     * CORRECTION : Les items de l'inventaire sont ajoutés IMMÉDIATEMENT.
     * 
     * @param pIcon    L'icône de l'item
     * @param pItemKey La clé de l'item
     */
    public void addInventoryOverlay(final ImageIcon pIcon, final String pItemKey) {
        ImageIcon vScaledIcon = scaleImage(pIcon, ITEM_SIZE, ITEM_SIZE);
        ItemOverlay vOverlay = new ItemOverlay(vScaledIcon, pItemKey,
                ItemOverlay.ItemOverlayType.INVENTORY, ITEM_SIZE);

        vOverlay.setAlpha(0f);

        // CORRECTION : Ajout IMMÉDIAT, pas de pending même pendant transition
        this.aOverlayManager.addInventoryOverlay(vOverlay, false);
        this.startOverlayFadeIn(vOverlay);
        this.updateOverlayPositions();
        this.repaint();
    }

    /**
     * Supprime un overlay d'item de l'inventaire.
     * 
     * @param pItemKey La clé de l'item
     */
    public void removeInventoryOverlay(final String pItemKey) {
        this.aOverlayManager.removeInventoryOverlay(pItemKey);
        this.updateOverlayPositions();
        this.repaint();
    }

    /**
     * Supprime tous les overlays d'inventaire.
     */
    public void clearInventoryOverlays() {
        this.aOverlayManager.clearInventoryOverlays();
        this.repaint();
    }

    // ===== MÉTHODES DE NETTOYAGE GÉNÉRAL =====

    /**
     * Supprime tous les overlays.
     */
    public void clearAllOverlays() {
        this.aOverlayManager.clearAll();
        this.repaint();
    }

    // ===== MÉTHODES DE POSITIONNEMENT =====

    /**
     * Met à jour les positions de tous les overlays.
     */
    private void updateOverlayPositions() {
        int vPanelWidth = getWidth();
        int vPanelHeight = getHeight();

        this.positionCharacterOverlays(vPanelWidth, vPanelHeight);
        this.positionRoomItemOverlays(vPanelWidth, vPanelHeight);
        this.positionInventoryOverlays(vPanelWidth, vPanelHeight);
    }

    /**
     * Positionne les overlays de personnages en bas à droite.
     * 
     * @param pPanelWidth  La largeur du panneau
     * @param pPanelHeight La hauteur du panneau
     */
    private void positionCharacterOverlays(int pPanelWidth, int pPanelHeight) {
        List<CharacterOverlay> vOverlays = this.aOverlayManager.getCharacterOverlays();
        if (vOverlays.isEmpty())
            return;

        int vTotalWidth = 0;
        for (CharacterOverlay vOverlay : vOverlays) {
            vTotalWidth += vOverlay.getWidth() + MARGIN;
        }
        vTotalWidth -= MARGIN;

        int vStartX = pPanelWidth - vTotalWidth - 15;
        int vBaseY = pPanelHeight - CHARACTER_HEIGHT - 15;

        int vCurrentX = vStartX;
        for (CharacterOverlay vOverlay : vOverlays) {
            vOverlay.setPosition(vCurrentX, vBaseY);
            vCurrentX += vOverlay.getWidth() + MARGIN;
        }
    }

    /**
     * Positionne les overlays d'items de la pièce en haut à gauche.
     * 
     * @param pPanelWidth  La largeur du panneau
     * @param pPanelHeight La hauteur du panneau
     */
    private void positionRoomItemOverlays(int pPanelWidth, int pPanelHeight) {
        List<ItemOverlay> vOverlays = this.aOverlayManager.getRoomItemOverlays();
        if (vOverlays.isEmpty())
            return;

        int vStartX = 15;
        int vStartY = 5;

        for (int i = 0; i < vOverlays.size(); i++) {
            ItemOverlay vOverlay = vOverlays.get(i);
            vOverlay.setPosition(vStartX + i * (ITEM_SIZE + MARGIN), vStartY);
        }
    }

    /**
     * Positionne les overlays d'inventaire en bas à gauche.
     * 
     * @param pPanelWidth  La largeur du panneau
     * @param pPanelHeight La hauteur du panneau
     */
    private void positionInventoryOverlays(int pPanelWidth, int pPanelHeight) {
        List<ItemOverlay> vOverlays = this.aOverlayManager.getInventoryOverlays();
        if (vOverlays.isEmpty())
            return;

        int vStartX = 15;
        int vStartY = pPanelHeight - ITEM_SIZE - 15;

        for (int i = 0; i < vOverlays.size(); i++) {
            ItemOverlay vOverlay = vOverlays.get(i);
            vOverlay.setPosition(vStartX + i * (ITEM_SIZE + MARGIN), vStartY);
        }
    }

    // ===== MÉTHODES DE REDIMENSIONNEMENT =====

    /**
     * Redimensionne une image à la taille spécifiée.
     * 
     * @param pIcon   L'icône à redimensionner
     * @param pWidth  La largeur souhaitée
     * @param pHeight La hauteur souhaitée
     * @return L'icône redimensionnée
     */
    private ImageIcon scaleImage(final ImageIcon pIcon, final int pWidth, final int pHeight) {
        if (pIcon == null)
            return null;
        Image vOriginalImage = pIcon.getImage();
        Image vScaledImage = vOriginalImage.getScaledInstance(pWidth, pHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(vScaledImage);
    }

    /**
     * Redimensionne une image en conservant les proportions, basé sur la hauteur.
     * 
     * @param pIcon         L'icône à redimensionner
     * @param pTargetHeight La hauteur souhaitée
     * @return L'icône redimensionnée
     */
    private ImageIcon scaleImageToHeight(final ImageIcon pIcon, final int pTargetHeight) {
        if (pIcon == null)
            return null;
        int vOriginalWidth = pIcon.getIconWidth();
        int vOriginalHeight = pIcon.getIconHeight();
        if (vOriginalHeight <= 0)
            return pIcon;
        double vRatio = (double) pTargetHeight / vOriginalHeight;
        int vTargetWidth = (int) (vOriginalWidth * vRatio);
        Image vOriginalImage = pIcon.getImage();
        Image vScaledImage = vOriginalImage.getScaledInstance(vTargetWidth, pTargetHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(vScaledImage);
    }

    /**
     * Démarre une animation de fondu entrant pour un overlay.
     * 
     * @param pOverlay L'overlay à faire apparaître avec un fondu entrant
     */
    private void startOverlayFadeIn(Overlay pOverlay) {
        Timer vFadeTimer = new Timer(30, null);
        vFadeTimer.addActionListener(e -> {
            float vAlpha = pOverlay.getAlpha() + 0.05f;
            if (vAlpha >= 0.9f) {
                vAlpha = 0.9f;
                vFadeTimer.stop();
            }
            pOverlay.setAlpha(vAlpha);
            repaint();
        });
        vFadeTimer.start();
    }

    // ===== GESTION DES CLIQUES =====

    /**
     * Gère les clics sur les overlays.
     * 
     * @param pEvent L'événement de souris à traiter
     */
    private void handleOverlayClick(MouseEvent pEvent) {
        int vX = pEvent.getX();
        int vY = pEvent.getY();

        // Items de la pièce
        for (ItemOverlay vOverlay : this.aOverlayManager.getRoomItemOverlays()) {
            if (isPointInOverlay(vOverlay, vX, vY)) {
                showRoomItemContextMenu(vOverlay, pEvent);
                return;
            }
        }

        // Items de l'inventaire
        for (ItemOverlay vOverlay : this.aOverlayManager.getInventoryOverlays()) {
            if (isPointInOverlay(vOverlay, vX, vY)) {
                showInventoryContextMenu(vOverlay, pEvent);
                return;
            }
        }

        // Personnages
        for (CharacterOverlay vOverlay : this.aOverlayManager.getCharacterOverlays()) {
            if (isPointInOverlay(vOverlay, vX, vY)) {
                showCharacterContextMenu(vOverlay, pEvent);
                return;
            }
        }
    }

    /**
     * Vérifie si un point est à l'intérieur d'un overlay.
     * 
     * @param pOverlay L'overlay à vérifier
     * @param pX       La coordonnée X du point
     * @param pY       La coordonnée Y du point
     * @return true si le point est à l'intérieur de l'overlay, sinon false
     */
    private boolean isPointInOverlay(Overlay pOverlay, int pX, int pY) {
        return pX >= pOverlay.getX() && pX <= pOverlay.getX() + pOverlay.getWidth() &&
                pY >= pOverlay.getY() && pY <= pOverlay.getY() + pOverlay.getHeight();
    }

    /**
     * Affiche le menu contextuel pour un item de la pièce.
     * 
     * @param pOverlay L'overlay de l'item de la pièce cliqué
     * @param pEvent   L'événement de souris déclenchant le menu contextuel
     */
    private void showRoomItemContextMenu(ItemOverlay pOverlay, MouseEvent pEvent) {
        if (this.aGameEngine == null)
            return;

        String vItemKey = pOverlay.getNameKey();
        Item vItem = this.aGameEngine.getPlayer().getCurrentRoom().getItem(vItemKey);
        if (vItem == null)
            return;

        AppPopupMenu vMenu = new AppPopupMenu();
        vMenu.setBackground(new Color(50, 50, 65));
        vMenu.setBorder(BorderFactory.createLineBorder(new Color(100, 200, 255)));

        vMenu.addItem(Lang.localizableString("inspect"), () -> {
            this.aGameEngine.showItemDetails(vItem);
        });

        vMenu.addSeparator();

        if (vItem.canBePickedUp()) {
            vMenu.addItem(Lang.localizableString("take"), () -> {
                this.aGameEngine.interpretCommand("take " + vItemKey);
            });
        }

        vMenu.show(this, pEvent.getX(), pEvent.getY());
    }

    /**
     * Affiche le menu contextuel pour un item de l'inventaire.
     * 
     * @param pOverlay L'overlay de l'item de l'inventaire cliqué
     * @param pEvent   L'événement de souris déclenchant le menu contextuel
     */
    private void showInventoryContextMenu(ItemOverlay pOverlay, MouseEvent pEvent) {
        if (this.aGameEngine == null)
            return;

        String vItemKey = pOverlay.getNameKey();
        Item vItem = this.aGameEngine.getPlayer().getInventory().getItem(vItemKey);
        if (vItem == null)
            return;

        AppPopupMenu vMenu = new AppPopupMenu();
        vMenu.setBackground(new Color(50, 50, 65));
        vMenu.setBorder(BorderFactory.createLineBorder(new Color(100, 200, 255)));

        vMenu.addItem(Lang.localizableString("inspect"), () -> {
            this.aGameEngine.showItemDetails(vItem);
        });

        vMenu.addSeparator();
        vMenu.addItem(Lang.localizableString("drop"), () -> {
            this.aGameEngine.interpretCommand("drop " + vItemKey);
        });

        if (vItem instanceof Beamer) {
            Beamer vBeamer = (Beamer) vItem;
            vMenu.addSeparator();
            if (!vBeamer.isCharged()) {
                vMenu.addItem(Lang.localizableString("charge"), () -> {
                    this.aGameEngine.interpretCommand("charge");
                });
            } else {
                vMenu.addItem(Lang.localizableString("fire"), () -> {
                    this.aGameEngine.interpretCommand("fire");
                });
            }
        }

        if (vItem.isUsable()) {
            vMenu.addSeparator();
            vMenu.addItem(Lang.localizableString("use"), () -> {
                this.aGameEngine.interpretCommand("use " + vItemKey);
            });
        }

        if (vItem instanceof MagicCookie) {
            vMenu.addSeparator();
            vMenu.addItem(Lang.localizableString("eat"), () -> {
                this.aGameEngine.interpretCommand("eat " + vItemKey);
            });
        }

        vMenu.show(this, pEvent.getX(), pEvent.getY());
    }

    /**
     * Affiche le menu contextuel pour un personnage.
     * 
     * @param pOverlay L'overlay du personnage cliqué
     * @param pEvent   L'événement de souris déclenchant le menu contextuel
     */
    private void showCharacterContextMenu(CharacterOverlay pOverlay, MouseEvent pEvent) {
        if (this.aGameEngine == null)
            return;

        String vCharNameKey = pOverlay.getNameKey();
        Character vChar = this.aGameEngine.getPlayer().getCurrentRoom().getCharacter(vCharNameKey);
        if (vChar == null)
            return;

        AppPopupMenu vMenu = new AppPopupMenu();
        vMenu.setBackground(new Color(50, 50, 65));
        vMenu.setBorder(BorderFactory.createLineBorder(new Color(100, 200, 255)));

        vMenu.addItem(Lang.localizableString("talk_button"), () -> {
            this.aGameEngine.interpretCommand("talk " + vCharNameKey);
        });

        if (!vChar.getExchangeItems().isEmpty()) {
            vMenu.addSeparator();
            vMenu.addItem(Lang.localizableString("give_button"), () -> {
                this.aGameEngine.showGivePopup();
            });
        }

        vMenu.show(this, pEvent.getX(), pEvent.getY());
    }

    // ===== MÉTHODES DE TRANSITION D'IMAGE =====

    /**
     * Lance l'effet de fondu vers une nouvelle image.
     * 
     * @param pIcon L'icône de la nouvelle image à afficher
     */
    public void startTransition(final ImageIcon pIcon) {
        if (this.aIsTransitioning) {
            this.aTransitionTimer.stop();
            this.completeTransition();
        }

        this.aNextImage = pIcon;
        this.aAlpha = 0f;
        this.aTransitionAlpha = 0f;
        this.aIsTransitioning = true;

        if (this.aTransitionTimer.isRunning()) {
            this.aTransitionTimer.stop();
        }

        this.aTransitionTimer.start();
        repaint();
    }

    /**
     * Termine la transition et remplace l'image actuelle par la nouvelle.
     */
    private void completeTransition() {
        if (this.aNextImage != null) {
            this.aCurrentImage = this.aNextImage;
        }
        this.aNextImage = null;

        this.aIsTransitioning = false;
        this.aTransitionTimer.stop();

        this.aOverlayManager.commitPending();

        this.aAlpha = 0f;
        this.aTransitionAlpha = 0f;

        this.updateOverlayPositions();
        repaint();
    }

    /**
     * Dessine le composant.
     * 
     * @param pG Le contexte graphique
     */
    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        final Graphics2D vG2d = (Graphics2D) pG.create();
        vG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        vG2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Fond
        vG2d.setColor(Color.BLACK);
        vG2d.fillRect(0, 0, getWidth(), getHeight());

        // Image actuelle
        if (this.aCurrentImage != null) {
            vG2d.drawImage(this.aCurrentImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        }

        // Image suivante (fondu)
        if (this.aIsTransitioning && this.aNextImage != null && this.aTransitionAlpha > 0) {
            vG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.aTransitionAlpha));
            vG2d.drawImage(this.aNextImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            vG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }

        // Items de la pièce
        for (ItemOverlay vOverlay : this.aOverlayManager.getRoomItemOverlays()) {
            vG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, vOverlay.getAlpha()));
            vG2d.drawImage(vOverlay.getIcon().getImage(),
                    vOverlay.getX(), vOverlay.getY(),
                    vOverlay.getWidth(), vOverlay.getHeight(), this);
        }

        // Items de l'inventaire
        for (ItemOverlay vOverlay : this.aOverlayManager.getInventoryOverlays()) {
            vG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, vOverlay.getAlpha()));
            vG2d.drawImage(vOverlay.getIcon().getImage(),
                    vOverlay.getX(), vOverlay.getY(),
                    vOverlay.getWidth(), vOverlay.getHeight(), this);
        }

        // Personnages
        for (CharacterOverlay vOverlay : this.aOverlayManager.getCharacterOverlays()) {
            vG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, vOverlay.getAlpha()));
            vG2d.drawImage(vOverlay.getIcon().getImage(),
                    vOverlay.getX(), vOverlay.getY(),
                    vOverlay.getWidth(), vOverlay.getHeight(), this);
        }

        vG2d.dispose();
    }

    /**
     * Définit les dimensions du composant.
     * 
     * @param width  La largeur du composant
     * @param height La hauteur du composant
     */
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        this.updateOverlayPositions();
    }
}