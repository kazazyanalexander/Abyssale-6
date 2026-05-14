package pkg_items;

/**
 * ItemType - Répertorie les types d'objets disponibles dans le jeu.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public enum ItemType {
    /** Combinaison de plongée - Permet d'accéder aux zones inondées. */
    DIVING_SUIT("item_diving_suit", "item_diving_suit.png", 19000),

    /** Torche - Éclaire les zones sombres. */
    TORCH("item_torch", "item_torch.png", 1300),

    /** Carte bleue - Permet d'accéder aux zones de niveau 2. */
    BLUE_CARD("item_blue_card", "item_blue_card.png", 100),

    /** Carte rouge - Permet d'accéder aux zones de niveau 3. */
    RED_CARD("item_red_card", "item_red_card.png", 100),

    /** Clé anglaise - Outil de réparation pour le réacteur. */
    WRENCH("item_wrench", "item_wrench.png", 1200),

    /** Échantillon génétique - Spécimen d'organisme abyssal. */
    GENEETIC("item_genetic", "item_genetic.png", 300, true, false),

    /** Bouteille d'oxygène - Permet de respirer dans les zones sans air. */
    OXYGEN("item_oxygen", "item_oxygen.png", 3000, true, false),

    /** Trousse de premiers soins - Soigne les blessures du joueur. */
    FIRSTAID("item_firstaid", "item_firstaid.png", 2100, true, false),

    /** Élixir magique - Double la capacité de transport du joueur. */
    MAGIC_COOKIE("item_magic_cookie", "item_magic_cookie.png", 1100, true, false),

    /** Téléporteur - Permet de mémoriser une position et d'y retourner. */
    BEAMER("item_beamer", "item_beamer.png", 5200, true, false);

    /** Clé pour la localisation du nom de l'objet */
    private final String aNameKey;
    /** Nom du fichier image de l'objet */
    private final String aImageName;
    /** Poids de l'objet */
    private final int aWeight;
    /** État de transportabilité */
    private final boolean aCanBePickedUp;
    /** Utilisable ou non (non utilisé pour l'instant) */
    private final boolean aIsUsable;

    /**
     * Constructeur complet.
     * 
     * @param pNameKey       Clé pour la localisation du nom de l'objet
     * @param pImageName     Nom du fichier image de l'objet
     * @param pWeight        Poids de l'objet
     * @param pCanBePickedUp État de transportabilité
     * @param pIsUsable      Utilisable ou non
     */
    private ItemType(final String pNameKey, final String pImageName, final int pWeight, final boolean pCanBePickedUp,
            final boolean pIsUsable) {
        this.aNameKey = pNameKey;
        this.aImageName = pImageName;
        this.aWeight = pWeight;
        this.aCanBePickedUp = pCanBePickedUp;
        this.aIsUsable = pIsUsable;
    }

    /**
     * Constructeur simplifié (objet transportable par défaut).
     * 
     * @param pNameKey   Clé pour la localisation du nom de l'objet
     * @param pImageName Nom du fichier image de l'objet
     * @param pWeight    Poids de l'objet
     */
    private ItemType(final String pNameKey, final String pImageName, final int pWeight) {
        this(pNameKey, pImageName, pWeight, true, true);
    }

    /**
     * Returne La clé pour la localisation du nom de l'objet
     * 
     * @return true si l'objet est utilisable, false sinon
     */
    public boolean isUsable() {
        return this.aIsUsable;
    }

    /**
     * Returne La clé pour la localisation du nom de l'objet
     * 
     * @return La clé du nom de l'objet
     */
    public String getNameKey() {
        return this.aNameKey;
    }

    /**
     * Returne Le nom de l'objet
     * 
     * @return La image de l'objet
     */
    public String getImageName() {
        return this.aImageName;
    }

    /**
     * Returne Le poids de l'objet
     * 
     * @return Le poids de l'objet
     */
    public int getWeight() {
        return this.aWeight;
    }

    /**
     * Returne Si l'objet peut être transporté ou non
     * 
     * @return true si l'objet peut être transporté, false sinon
     */
    public boolean canBePickedUp() {
        return aCanBePickedUp;
    }

    /**
     * Crée une instance d'Item basée sur le type actuel.
     * 
     * @return Une instance de la sous-classe appropriée.
     */
    public Item createItem() {
        return switch (this) {
            case TORCH -> new Torch();
            case DIVING_SUIT -> new DivingSuit();
            case MAGIC_COOKIE -> new MagicCookie();
            case BEAMER -> new Beamer();
            default -> new GenericItem(this);
        };
    }
}
