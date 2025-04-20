package game;

import game.ui.GameGUI;
import game.core.SaveSystem;

/**
 * Lanceur principal du jeu de créatures.
 */
public class GameLauncher {
    /**
     * Point d'entrée du programme.
     *
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        System.out.println("Démarrage du Jeu de Créatures...");

        // Initialiser le système de sauvegarde
        SaveSystem.initialize();

        // Configurer l'apparence de l'interface
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Impossible de définir le look and feel natif: " + e.getMessage());
            // Continuer avec le look and feel par défaut
        }

        // Lancer l'interface graphique
        javax.swing.SwingUtilities.invokeLater(() -> {
            new GameGUI();
        });
    }
}