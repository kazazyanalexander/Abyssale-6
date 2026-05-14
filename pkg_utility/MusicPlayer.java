package pkg_utility;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * MusicPlayer - Gestionnaire audio pour le jeu.
 * 
 * Cette classe gère la lecture de la musique de fond en boucle et des effets
 * sonores ponctuels.
 * Elle utilise l'API Java Sound pour charger et contrôler les fichiers audio.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class MusicPlayer {
    /**
     * Référence vers le clip audio principal (musique de fond)
     * Ce clip est conservé pour pouvoir contrôler la lecture (pause, stop, etc.)
     */
    private Clip aClip;

    /**
     * Constructeur par défaut de la classe MusicPlayer.
     */
    public MusicPlayer() {
        
    }

    /**
     * Joue une musique de fond en boucle continue.
     * La musique est chargée depuis le dossier de ressources "/audio/".
     * Le volume est réduit pour être utilisé comme musique d'ambiance.
     * 
     * @param pMusicFile Le nom du fichier audio à jouer (ex:
     *                   "background_music.wav")
     */
    public void playBackgroundMusic(final String pMusicFile) {
        try {
            
            final URL vMusicUrl = getClass().getResource("/audio/" + pMusicFile);

            
            if (vMusicUrl == null) {
                System.err.println("Fichier audio non trouvé : /audio/" + pMusicFile);
                return;
            }

            
            final AudioInputStream vAudioInput = AudioSystem.getAudioInputStream(vMusicUrl);

            
            this.aClip = AudioSystem.getClip();
            this.aClip.open(vAudioInput);

            
            if (this.aClip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                final FloatControl vGainControl = (FloatControl) this.aClip.getControl(FloatControl.Type.MASTER_GAIN);

                
                vGainControl.setValue(-20.0f);
            }

           
            this.aClip.loop(Clip.LOOP_CONTINUOUSLY);

            
            this.aClip.start();

            
        } catch (final Exception vE) {
            
            System.err.println("Erreur lors de la lecture de la musique : " + vE.getMessage());
            vE.printStackTrace();
        }
    }

    /**
     * Joue un effet sonore ponctuel (Sound Effect).
     * Contrairement à la musique de fond, les SFX ne sont pas conservés en mémoire
     * après leur lecture et ne tournent pas en boucle.
     * 
     * @param pSfxName Le nom du fichier d'effet sonore (ex: "door_open.wav")
     */
    public void playSFX(final String pSfxName) {
        try {
            // Construction du chemin vers le fichier SFX
            final URL vMusicUrl = getClass().getResource("/audio/" + pSfxName);

            // Vérification que le fichier a été trouvé
            if (vMusicUrl == null) {
                System.err.println("Fichier SFX non trouvé : /audio/" + pSfxName);
                return;
            }

            // Chargement du flux audio
            final AudioInputStream vStream = AudioSystem.getAudioInputStream(vMusicUrl);

            // Création d'un nouveau clip pour cet effet sonore
            final Clip vSfxClip = AudioSystem.getClip();
            vSfxClip.open(vStream);

            // Démarrage immédiat de la lecture (pas de boucle)
            vSfxClip.start();

            // Note : Le clip sera libéré par le garbage collector après lecture
            // System.out.println("Effet sonore '" + pSfxName + "' joué.");

        } catch (final Exception vE) {
            // Gestion des erreurs de lecture SFX
            System.err.println("Erreur lors de la lecture de l'effet sonore : " + vE.getMessage());
            vE.printStackTrace();
        }
    }

    /**
     * Arrête la musique de fond en cours de lecture.
     * Cette méthode n'affecte pas les effets sonores (SFX).
     */
    public void stopMusic() {
        if (this.aClip != null && this.aClip.isRunning()) {
            this.aClip.stop();
            // System.out.println("Musique de fond arrêtée.");
        }
    }

}
