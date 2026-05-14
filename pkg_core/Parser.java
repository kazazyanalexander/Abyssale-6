package pkg_core;

import pkg_command.Command;
import pkg_command.CommandWord;

import java.util.StringTokenizer;

/**
 * Parser - Analyseur syntaxique pour les commandes du jeu.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class Parser {

    /**
     * Crée un nouvel analyseur syntaxique.
     */
    public Parser() {
    }

    /**
     * Analyse une ligne de commande et retourne un objet Command.
     * 
     * @param pInputLine La ligne à analyser
     * @return Un objet Command initialisé (jamais null)
     */
    public Command getCommand(final String pInputLine) {
        String vWord1 = null;
        String vWord2 = null;

        // Gestion des lignes vides
        if (pInputLine == null || pInputLine.trim().isEmpty()) {
            // Retourner une commande UNKNOWN
            Command vUnknown = CommandWord.UNKNOWN.getCommand();
            vUnknown.setSecondWord(null);
            return vUnknown;
        }

        StringTokenizer vTokenizer = new StringTokenizer(pInputLine);

        if (vTokenizer.hasMoreTokens()) {
            vWord1 = vTokenizer.nextToken();
        }
        if (vTokenizer.hasMoreTokens()) {
            vWord2 = vTokenizer.nextToken();
        }

        CommandWord vCommandWord = CommandWord.fromString(vWord1);
        Command vCommand = vCommandWord.getCommand();

        // Normalement, vCommand ne devrait jamais être null car toutes les commandes
        // ont une classe associée, mais par sécurité on vérifie quand même
        if (vCommand == null) {
            vCommand = CommandWord.UNKNOWN.getCommand();
        }

        vCommand.setSecondWord(vWord2);
        return vCommand;
    }

    /**
     * Affiche la liste de toutes les commandes disponibles.
     * 
     * @return La liste de toutes les commandes
     */
    public String showCommands() {
        return CommandWord.getAllCommands();
    }
}
