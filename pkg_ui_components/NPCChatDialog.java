package pkg_ui_components;

import pkg_characters.Character;
import pkg_utility.SapiTTS;
import pkg_utility.ImageUtils;
import pkg_utility.Lang;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Dialogue de chat avec un PNJ spécifique via une API IA (FastAPI). Deux
 * colonnes : chat à gauche, image du personnage à droite. Bulles stylées
 * (joueur à droite, PNJ à gauche) et hauteur automatique.
 *
 * @author Alexander KAZAZYAN
 * @version 05/2026
 */
public class NPCChatDialog extends JDialog {

    // URLs du serveur
    // private static final String CHAT_URL = "http://192.168.1.131:8000/chat";
    private static final String CHAT_URL = "http://localhost:8000/chat";
    private static final String PLAYER_ID = "java_client";

    // Composants graphiques
    private JPanel messagePanel; // contient les bulles de messages
    private JTextField inputField;
    private AppButton sendButton;
    private AppButton speakerButton; // Bouton pour activer/désactiver la synthèse vocale
    private JLabel npcImageLabel; // affiche l'image du PNJ

    // Informations du PNJ
    private final String npcNameKey;
    private final String npcDisplayName;
    private final boolean isFemale;

    // Constantes d'apparence
    private static final Color POPUP_BG = new Color(30, 30, 40, 240);
    private static final Color PANEL_BG = new Color(40, 40, 50);
    private static final Color ACCENT_COLOR = new Color(100, 200, 255);
    private static final Color INPUT_BG = new Color(20, 22, 30);
    private static final Color USER_BUBBLE_BG = new Color(70, 90, 110);
    private static final Color NPC_BUBBLE_BG = new Color(50, 70, 90);
    private static final Color SYSTEM_BUBBLE_BG = new Color(80, 80, 100);
    private static final Color TEXT_COLOR = new Color(220, 240, 255);
    private static final int BUBBLE_PADDING = 8;
    private static final int MAX_BUBBLE_WIDTH = 400;
    private static final int CHARACTER_HEIGHT = 256;

    // Délai avant la parole (en millisecondes) - permet à l'interface de se mettre
    // à jour d'abord
    private static final int SPEECH_DELAY_MS = 100;

    // Drapeau pour suivre si la synthèse vocale est activée
    private boolean speechEnabled = true;

    // Panneau de l'indicateur de chargement (typing indicator)
    private JPanel typingIndicatorPanel;

    /**
     * Constructeur.
     * 
     * @param parent     Fenêtre parente (GameGUI)
     * @param pCharacter Personnage PNJ
     */
    public NPCChatDialog(Frame parent, Character pCharacter) {
        super(parent, Lang.localizableString("chat_title"), true);
        this.npcNameKey = pCharacter.getName();
        this.npcDisplayName = Lang.localizableString(npcNameKey);
        this.isFemale = pCharacter.getName() == "character_nurse";

        setUndecorated(true);
        getContentPane().setBackground(POPUP_BG);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(createPopupBorder());

        mainPanel.add(createTitlePanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createInputPanel(), BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        pack();
        setMinimumSize(new Dimension(900, 550));

        // Centrage précis par rapport à la fenêtre parente
        if (parent != null) {
            int x = parent.getX() + (parent.getWidth() - getWidth()) / 2;
            int y = parent.getY() + (parent.getHeight() - getHeight()) / 2;
            setLocation(x, y);
        } else {
            setLocationRelativeTo(null);
        }

        appendSystemMessage(Lang.localizableString("chat_connecting"));
        if (pCharacter.getGreetingKey() != null) {
            String vGreeting = String.format(Lang.localizableString("character_greeting_format"),
                    Lang.localizableString(pCharacter.getGreetingKey()));
            appendNPCMessage(vGreeting);
            // Afficher le message d'abord, puis parler après un court délai (si la synthèse
            // vocale est activée)
            speakWithDelay(vGreeting);
        }
        setVisible(true);
    }

    /**
     * Crée le panneau de titre avec le nom du PNJ et un bouton de fermeture.
     * 
     * @return Le JPanel du titre
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PANEL_BG);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel titleLabel = new JLabel(String.format(Lang.localizableString("chat_with_format"), npcDisplayName));
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        titleLabel.setForeground(ACCENT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Bouton fermer
        AppButton closeButton = new AppButton("✕", new Color(200, 80, 80));
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        closeButton.addActionListener(e -> dispose());
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        closePanel.setBackground(PANEL_BG);
        closePanel.add(closeButton);
        titlePanel.add(closePanel, BorderLayout.EAST);

        return titlePanel;
    }

    /**
     * Crée le panneau central contenant le chat et l'image du PNJ.
     * 
     * @return Le JPanel du centre
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(PANEL_BG);

        JPanel chatPanel = createChatPanel();
        chatPanel.setPreferredSize(new Dimension(650, 400));

        JPanel npcPanel = createNPCImagePanel();
        npcPanel.setPreferredSize(new Dimension(250, 400));

        centerPanel.add(chatPanel, BorderLayout.CENTER);
        centerPanel.add(npcPanel, BorderLayout.EAST);
        return centerPanel;
    }

    /**
     * Crée le panneau de chat avec la zone de messages et le scroll.
     * 
     * @return Le JPanel du chat
     */
    private JPanel createChatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(PANEL_BG);

        JScrollPane scrollPane = new JScrollPane(messagePanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));
        scrollPane.getViewport().setBackground(PANEL_BG);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crée le panneau de l'image du PNJ.
     * 
     * @return Le JPanel de l'image
     */
    private JPanel createNPCImagePanel() {
        JPanel imagePanel = new JPanel(new GridBagLayout());
        imagePanel.setBackground(PANEL_BG);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Charger et redimensionner l'image
        ImageIcon npcIcon = loadNPCImage();
        if (npcIcon != null) {
            npcImageLabel = new JLabel(npcIcon);
            // npcImageLabel.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
        } else {
            npcImageLabel = new JLabel(Lang.localizableString("chat_no_image"));
            npcImageLabel.setForeground(TEXT_COLOR);
            npcImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            npcImageLabel.setPreferredSize(new Dimension(CHARACTER_HEIGHT / 2, CHARACTER_HEIGHT));
            npcImageLabel.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        imagePanel.add(npcImageLabel, gbc);

        // Nom du PNJ sous l'image
        JLabel nameLabel = new JLabel(npcDisplayName);
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        nameLabel.setForeground(ACCENT_COLOR);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0);
        imagePanel.add(nameLabel, gbc);

        // Créer le bouton haut-parleur sous le nom du PNJ
        speakerButton = createSpeakerButton();
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 0, 0, 0);
        imagePanel.add(speakerButton, gbc);

        return imagePanel;
    }

    /**
     * Charge et redimensionne l'image du PNJ.
     * 
     * @return L'ImageIcon de l'image du PNJ, ou null si non trouvée
     */
    private ImageIcon loadNPCImage() {
        // Chemin : /characters/<nameKey>.png
        String vImageName = "characters/" + npcNameKey + ".png";
        URL vImageURL = getClass().getClassLoader().getResource(vImageName);
        if (vImageURL != null) {
            ImageIcon original = new ImageIcon(vImageURL);
            return ImageUtils.scaleImageToHeight(original, CHARACTER_HEIGHT);
        }
        System.err.println("Image du PNJ non trouvée : " + vImageName);
        return null;
    }

    /**
     * Crée le panneau d'entrée avec le champ de texte et le bouton d'envoi.
     *
     * @return Le JPanel d'entrée
     */
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(PANEL_BG);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));

        inputField = new JTextField();
        inputField.setBackground(INPUT_BG);
        inputField.setForeground(TEXT_COLOR);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputField.setCaretColor(new Color(120, 255, 140));
        inputField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ACCENT_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        inputField.addActionListener(e -> sendUserMessage());
        inputPanel.add(inputField, BorderLayout.CENTER);

        sendButton = new AppButton(Lang.localizableString("send"), ACCENT_COLOR);
        sendButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        sendButton.addActionListener(e -> sendUserMessage());
        inputPanel.add(sendButton, BorderLayout.EAST);

        return inputPanel;
    }

    /**
     * Crée le bouton haut-parleur qui active/désactive la synthèse vocale.
     * 
     * @return Le bouton haut-parleur configuré
     */
    private AppButton createSpeakerButton() {
        // Utiliser une icône de haut-parleur ou du texte selon ce qui est disponible
        AppButton button;

        // Utiliser des symboles unicode pour le haut-parleur
        if (speechEnabled) {
            button = new AppButton("🔊", ACCENT_COLOR);
        } else {
            button = new AppButton("🔈", new Color(150, 150, 150));
        }

        button.setFont(new Font("SansSerif", Font.BOLD, 20));
        button.setPreferredSize(new Dimension(50, 50));
        button.addActionListener(e -> toggleSpeech());

        return button;
    }

    /**
     * Active/désactive la fonction de synthèse vocale et met à jour l'apparence du
     * bouton.
     */
    private void toggleSpeech() {
        speechEnabled = !speechEnabled;

        // Mettre à jour l'apparence du bouton
        if (speechEnabled) {
            speakerButton.setText("🔊");
            speakerButton.setBackgroundColor(ACCENT_COLOR);
        } else {
            speakerButton.setText("🔈");
            speakerButton.setBackgroundColor(new Color(150, 150, 150));
        }
    }

    /**
     * Envoie le message de l'utilisateur.
     */
    private void sendUserMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty())
            return;

        appendUserMessage(message);
        inputField.setText("");
        setInputEnabled(false);
        appendTypingIndicator(); // ← afficher l'indicateur

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return callChatAPI(message);
            }

            @Override
            protected void done() {
                removeTypingIndicator(); // ← retirer l'indicateur
                try {
                    String vReply = get();
                    appendNPCMessage(vReply);
                    speakWithDelay(vReply);
                } catch (Exception e) {
                    appendSystemMessage(Lang.localizableString("chat_error") + " " + e.getMessage());
                } finally {
                    setInputEnabled(true);
                    inputField.requestFocus();
                }
            }
        }.execute();
    }

    /**
     * Affiche un indicateur animé "en train d'écrire..." pendant l'attente de la
     * réponse du PNJ.
     */
    private void appendTypingIndicator() {
        SwingUtilities.invokeLater(() -> {
            // Créer les trois points animés
            JPanel dotsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            dotsPanel.setBackground(NPC_BUBBLE_BG);

            JLabel[] dots = new JLabel[3];
            for (int i = 0; i < 3; i++) {
                dots[i] = new JLabel("●");
                dots[i].setForeground(ACCENT_COLOR);
                dots[i].setFont(new Font("SansSerif", Font.BOLD, 10));
                dotsPanel.add(dots[i]);
            }

            // Animation : chaque point pulse à son tour
            Timer animator = new Timer(400, null);
            final int[] step = { 0 };
            animator.addActionListener(e -> {
                for (int i = 0; i < 3; i++) {
                    dots[i].setForeground(i == (step[0] % 3) ? Color.WHITE : ACCENT_COLOR.darker());
                }
                step[0]++;
            });
            animator.start();

            // Stocker le timer dans le panel pour pouvoir l'arrêter
            dotsPanel.putClientProperty("animator", animator);

            // Nom du PNJ au-dessus
            JLabel nameLabel = new JLabel(npcDisplayName);
            nameLabel.setFont(new Font("Monospaced", Font.BOLD, 11));
            nameLabel.setForeground(ACCENT_COLOR);
            nameLabel.setBorder(BorderFactory.createEmptyBorder(2, BUBBLE_PADDING, 0, BUBBLE_PADDING));

            JPanel bubbleContent = new JPanel(new BorderLayout());
            bubbleContent.setBackground(NPC_BUBBLE_BG);
            bubbleContent.setBorder(new LineBorder(ACCENT_COLOR, 1, true));
            bubbleContent.add(nameLabel, BorderLayout.NORTH);
            bubbleContent.add(dotsPanel, BorderLayout.CENTER);
            dotsPanel.setBorder(
                    BorderFactory.createEmptyBorder(BUBBLE_PADDING, BUBBLE_PADDING, BUBBLE_PADDING, BUBBLE_PADDING));

            typingIndicatorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            typingIndicatorPanel.setBackground(PANEL_BG);
            typingIndicatorPanel.add(bubbleContent);

            messagePanel.add(typingIndicatorPanel);
            messagePanel.add(Box.createVerticalStrut(8));
            messagePanel.revalidate();

            SwingUtilities.invokeLater(() -> {
                JScrollPane scroll = (JScrollPane) messagePanel.getParent().getParent();
                scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
            });
        });
    }

    /**
     * Retire l'indicateur de chargement du panneau de messages.
     */
    private void removeTypingIndicator() {
        SwingUtilities.invokeLater(() -> {
            if (typingIndicatorPanel != null) {
                // Arrêter l'animation
                for (Component c : ((JPanel) typingIndicatorPanel.getComponent(0)).getComponents()) {
                    if (c instanceof JPanel) {
                        Object animator = ((JPanel) c).getClientProperty("animator");
                        if (animator instanceof Timer) {
                            ((Timer) animator).stop();
                        }
                    }
                }
                // Retirer aussi le strut vertical juste après
                int idx = -1;
                for (int i = 0; i < messagePanel.getComponentCount(); i++) {
                    if (messagePanel.getComponent(i) == typingIndicatorPanel) {
                        idx = i;
                        break;
                    }
                }
                if (idx >= 0) {
                    messagePanel.remove(typingIndicatorPanel);
                    // Retirer le strut suivant si présent
                    if (idx < messagePanel.getComponentCount()) {
                        messagePanel.remove(idx);
                    }
                }
                typingIndicatorPanel = null;
                messagePanel.revalidate();
                messagePanel.repaint();
            }
        });
    }

    /**
     * Appelle l'API de chat avec le message de l'utilisateur et retourne la réponse
     * du PNJ.
     * 
     * @param userMessage Le message que le joueur a envoyé
     * @return La réponse du PNJ
     * @throws IOException En cas d'erreur de communication avec l'API
     */
    /**
     * Appelle l'API de chat avec le message de l'utilisateur et retourne la réponse
     * du PNJ.
     * 
     * @param userMessage Le message que le joueur a envoyé
     * @return La réponse du PNJ
     * @throws IOException En cas d'erreur de communication avec l'API
     */
    private String callChatAPI(String userMessage) throws IOException {
        JSONObject payload = new JSONObject();
        payload.put("npc_name", npcNameKey);
        payload.put("player_id", PLAYER_ID);
        payload.put("message", userMessage);

        // Add current language to the request
        String currentLanguage = Lang.getInstance().getLanguage();
        payload.put("language", currentLanguage); // "en", "fr", "de", "zh"

        URL url = URI.create(CHAT_URL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            StringBuilder errorMsg = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null)
                    errorMsg.append(line);
            }
            throw new IOException("HTTP " + responseCode + ": " + errorMsg);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null)
                response.append(line);
        }

        JSONObject respObj = new JSONObject(response.toString());
        return respObj.getString("reply");
    }

    // ========== GESTION DES BULLES AVEC HTML (hauteur correcte) ==========

    /**
     * Ajoute une bulle de message de l'utilisateur au panneau de messages.
     * 
     * @param text Le texte du message de l'utilisateur
     */
    private void appendUserMessage(String text) {
        addBubble(text, USER_BUBBLE_BG, SwingConstants.RIGHT, Lang.localizableString("you"));
    }

    /**
     * Ajoute une bulle de message du PNJ au panneau de messages.
     * 
     * @param text Le texte du message du PNJ
     */
    private void appendNPCMessage(String text) {
        addBubble(text, NPC_BUBBLE_BG, SwingConstants.LEFT, npcDisplayName);
    }

    /**
     * Ajoute une bulle de message du système au panneau de messages.
     * 
     * @param text Le texte du message du système
     */
    private void appendSystemMessage(String text) {
        addBubble(text, SYSTEM_BUBBLE_BG, SwingConstants.CENTER, Lang.localizableString("system"));
    }

    /**
     * Ajoute une bulle de message au panneau de messages avec le style spécifié.
     * 
     * @param text       Le texte du message
     * @param bgColor    La couleur de fond de la bulle
     * @param alignment  L'alignement de la bulle
     * @param senderName Le nom de l'expéditeur
     */
    private void addBubble(String text, Color bgColor, int alignment, String senderName) {
        SwingUtilities.invokeLater(() -> {
            JPanel bubble = createHtmlBubble(text, bgColor, alignment, senderName);
            messagePanel.add(bubble);
            messagePanel.add(Box.createVerticalStrut(8));
            messagePanel.revalidate();
            // Défilement automatique
            SwingUtilities.invokeLater(() -> {
                JScrollPane scroll = (JScrollPane) messagePanel.getParent().getParent();
                JScrollBar vertical = scroll.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            });
        });
    }

    /**
     * Crée une bulle de message utilisant HTML pour le wrapping et la hauteur
     * automatique.
     * 
     * @param text       Le texte du message
     * @param bgColor    La couleur de fond de la bulle
     * @param alignment  L'alignement de la bulle
     * @param senderName Le nom de l'expéditeur
     * @return Le panneau contenant la bulle de message
     */
    private JPanel createHtmlBubble(String text, Color bgColor, int alignment, String senderName) {
        // Utilisation de HTML pour le wrapping automatique et la hauteur correcte
        String textAlign = (alignment == SwingConstants.RIGHT) ? "right" : "left";
        String htmlText = "<html><div style='width:" + (MAX_BUBBLE_WIDTH - 20)
                + "px; word-wrap: break-word; text-align: " + textAlign + ";'>" + text.replace("\n", "<br>")
                + "</div></html>";

        JLabel messageLabel = new JLabel(htmlText);
        messageLabel.setBackground(bgColor);
        messageLabel.setForeground(TEXT_COLOR);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        messageLabel.setBorder(
                BorderFactory.createEmptyBorder(BUBBLE_PADDING, BUBBLE_PADDING, BUBBLE_PADDING, BUBBLE_PADDING));
        messageLabel.setOpaque(true);

        // Nom de l'expéditeur en petit au-dessus (sauf pour système)
        JPanel bubbleContent = new JPanel(new BorderLayout());
        if (alignment != SwingConstants.CENTER) {
            JLabel nameLabel = new JLabel(senderName);
            nameLabel.setFont(new Font("Monospaced", Font.BOLD, 11));
            nameLabel.setForeground(ACCENT_COLOR);
            nameLabel.setBorder(BorderFactory.createEmptyBorder(2, BUBBLE_PADDING, 0, BUBBLE_PADDING));
            nameLabel.setHorizontalAlignment(
                    alignment == SwingConstants.RIGHT ? SwingConstants.RIGHT : SwingConstants.LEFT);
            bubbleContent.add(nameLabel, BorderLayout.NORTH);
        }
        bubbleContent.add(messageLabel, BorderLayout.CENTER);
        bubbleContent.setBackground(bgColor);
        bubbleContent.setBorder(new LineBorder(ACCENT_COLOR, 1, true));

        // Wrapper pour l'alignement horizontal
        JPanel wrapper = new JPanel(new FlowLayout(alignment == SwingConstants.LEFT ? FlowLayout.LEFT
                : (alignment == SwingConstants.RIGHT ? FlowLayout.RIGHT : FlowLayout.CENTER)));
        wrapper.setBackground(PANEL_BG);
        wrapper.add(bubbleContent);
        return wrapper;
    }

    /**
     * Active ou désactive le champ de saisie et le bouton d'envoi. Utile pour
     * éviter les envois multiples pendant l'attente de la réponse du PNJ.
     * 
     * @param enabled true pour activer, false pour désactiver
     */
    private void setInputEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            inputField.setEnabled(enabled);
            sendButton.setEnabled(enabled);
        });
    }

    /**
     * Prononce le texte donné après un court délai pour s'assurer que le message de
     * l'interface est affiché en premier. Cette méthode utilise un Timer pour
     * retarder la parole et l'exécute sur l'EDT.
     * 
     * @param pText Le texte à prononcer
     */
    private void speakWithDelay(String pText) {
        // Vérifier si la synthèse vocale est activée avant de continuer
        if (!speechEnabled) {
            return;
        }

        // Utiliser un Timer Swing pour retarder la parole
        Timer timer = new Timer(SPEECH_DELAY_MS, e -> {
            // Exécuter la parole dans un thread séparé pour ne pas bloquer l'interface
            new Thread(() -> {
                SapiTTS.speak(pText, isFemale);
            }).start();
        });
        timer.setRepeats(false); // S'assurer qu'il ne s'exécute qu'une seule fois
        timer.start();
    }

    /**
     * Crée une bordure personnalisée pour la popup de chat, combinant une ligne
     * colorée et un padding interne.
     * 
     * @return La bordure à appliquer au panneau principal de la popup de chat
     */
    private javax.swing.border.Border createPopupBorder() {
        return BorderFactory.createCompoundBorder(new LineBorder(ACCENT_COLOR, 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }
}