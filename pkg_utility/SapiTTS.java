package pkg_utility;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Classe utilitaire pour la synthèse vocale (Text-to-Speech) utilisant SAPI
 * (Speech API) de Windows. Sélectionne automatiquement la voix appropriée
 * (Anglais ou Allemand) basée sur la langue actuelle. Supporte également la
 * sélection manuelle entre voix homme/femme.
 *
 * @author Alexander KAZAZYAN
 * @version 06/2026
 */
public class SapiTTS {

    /**
     * Converts text to speech using voice appropriate for current language.
     * Automatically selects English or German MS voices based on
     * Lang.getLanguage(). Uses female voice by default.
     *
     * @param text The text to speak.
     */
    public static void speak(String text) {
        speak(text, true); // Default to female voice
    }

    /**
     * Converts text to speech using voice appropriate for current language.
     * Automatically selects English or German MS voices based on
     * Lang.getLanguage().
     *
     * @param text     The text to speak.
     * @param isFemale true for female voice, false for male voice.
     */
    public static void speak(String text, boolean isFemale) {
        if (text == null || text.trim().isEmpty())
            return;

        // Get current language from Lang class
        String currentLanguage = Lang.getInstance().getLanguage();

        // Select appropriate voice based on language and gender
        String voiceLanguageName = getVoiceName(currentLanguage, isFemale);

        // Clean text to avoid breaking command line boundaries
        String safeText = text.replace("\"", "'").replace("\n", " ");

        // Clamp rate between -10 and 10
        int clampedRate = 2;

        // Generate a temporary execution script to run hidden
        File scriptFile = null;
        try {
            scriptFile = File.createTempFile("tts_exec_", ".ps1");
            scriptFile.deleteOnExit();

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(scriptFile),
                    StandardCharsets.UTF_8)) {

                // Write UTF-8 BOM so PowerShell recognizes the encoding
                writer.write('\uFEFF'); // BOM character — must be FIRST
                writer.write("Add-Type -AssemblyName System.Speech;\n");
                writer.write("$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer;\n");
                writer.write("$synth.Rate = " + clampedRate + ";\n");
                writer.write("try {\n");
                writer.write("    $synth.SelectVoice(\"" + voiceLanguageName + "\");\n");
                writer.write("} catch {\n");
                writer.write("    # Fallback: try generic voice selection\n");

                // Fallback logic based on language and gender
                if (currentLanguage.equals("de")) {
                    writer.write("    try { $synth.SelectVoiceByHints([System.Speech.Synthesis.VoiceGender]::");
                    writer.write(isFemale ? "Female" : "Male");
                    writer.write(
                            ", [System.Speech.Synthesis.VoiceAge]::Adult, 0, [System.Globalization.CultureInfo]::GetCultureInfo(\"de-DE\")) } catch {}\n");
                } else if (currentLanguage.equals("en")) {
                    writer.write("    try { $synth.SelectVoiceByHints([System.Speech.Synthesis.VoiceGender]::");
                    writer.write(isFemale ? "Female" : "Male");
                    writer.write(
                            ", [System.Speech.Synthesis.VoiceAge]::Adult, 0, [System.Globalization.CultureInfo]::GetCultureInfo(\"en-US\")) } catch {}\n");
                } else {
                    // Generic fallback for other languages
                    writer.write("    try { $synth.SelectVoiceByHints([System.Speech.Synthesis.VoiceGender]::");
                    writer.write(isFemale ? "Female" : "Male");
                    writer.write(") } catch {}\n");
                }
                writer.write("}\n");
                writer.write("$synth.Speak(\"" + safeText + "\");\n");
            }

            // Execute completely hidden in the background
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-NoProfile", "-ExecutionPolicy", "Bypass",
                    "-WindowStyle", "Hidden", "-InputFormat", "None", "-OutputFormat", "Text", "-File",
                    scriptFile.getAbsolutePath());

            Process process = pb.start();
            process.waitFor(); // Wait for speaking to finish

        } catch (IOException | InterruptedException e) {
            System.err.println("Speech Runtime Execution Error: " + e.getMessage());
        } finally {
            if (scriptFile != null && scriptFile.exists()) {
                scriptFile.delete();
            }
        }
    }

    /**
     * Gets the appropriate Microsoft voice name based on language and gender.
     *
     * @param language The language code ("en", "de", "fr", "zh")
     * @param isFemale true for female voice, false for male voice
     * @return The voice name to use
     */
    private static String getVoiceName(String language, boolean isFemale) {
        // Standard Microsoft voice names for Windows
        switch (language.toLowerCase()) {
        case "en":
            return isFemale ? "Microsoft Zira Desktop" : "Microsoft David Desktop";
        case "de":
            return isFemale ? "Microsoft Hedda Desktop" : "Microsoft Stefan Desktop";
        case "fr":
            return isFemale ? "Microsoft Hortense" : "Microsoft Paul";
        case "zh":
            return isFemale ? "Microsoft Huihui Desktop" : "Microsoft Kangkang Desktop";
        default:
            // Default to English voices
            return isFemale ? "Microsoft Zira Desktop" : "Microsoft David Desktop";
        }
    }

    /**
     * Helper method to check what voice would be used for current language. Useful
     * for debugging and testing.
     *
     * @return A string describing the current voice configuration
     */
    public static String getCurrentVoiceInfo() {
        String currentLanguage = Lang.getInstance().getLanguage();
        String femaleVoice = getVoiceName(currentLanguage, true);
        String maleVoice = getVoiceName(currentLanguage, false);
        return String.format("Current language: %s, Female voice: %s, Male voice: %s", currentLanguage, femaleVoice,
                maleVoice);
    }
}