import javax.sound.sampled.*;
import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * Décrivez votre classe MusicPlayer ici.
 *
 * @author Alexander KAZAZYAN
 * @version 1.0 (Janvier 2026)
 */
public class MusicPlayer
{
    private Clip clip;

    public void playBackgroundMusic(final String musicPath) {
        try {
            File musicFile = new File(musicPath);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioInput);
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-20.0f); // volume is a float (e.g., -20.0f for lower volume)
            }
            // Loop the music forever
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch (Exception e) {
            System.out.println("Error playing music: " + e.getMessage());
        }
    }

    // PLAY ONE-SHOT SFX (Opening door, taking item)
    public void playSFX(final String sfxName) {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            File sfxFile = new File(sfxName);
            AudioInputStream stream = AudioSystem.getAudioInputStream(sfxFile);
            Clip sfxClip = AudioSystem.getClip();
            sfxClip.open(stream);
            sfxClip.start(); 
            // This plays ON TOP of the background music

        } catch (Exception e) { e.printStackTrace(); }
    }

    public void stopMusic() {
        if (clip != null) {
            clip.stop();
        }
    }
}