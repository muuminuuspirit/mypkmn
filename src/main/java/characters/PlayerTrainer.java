package game.characters;

import game.core.creature.Creature;
import game.items.Item;

/**
 * Représente le dresseur contrôlé par le joueur.
 */
public class PlayerTrainer extends Trainer {
    private int badges;
    private int playerRank;
    private int reputation;

    /**
     * Crée un nouveau dresseur joueur.
     *
     * @param id Identifiant unique
     * @param name Nom du joueur
     */
    public PlayerTrainer(String id, String name) {
        super(id, name);
        this.badges = 0;
        this.playerRank = 1;
        this.reputation = 50; // Valeur neutre
    }

    /**
     * Gagne un badge.
     */
    public void earnBadge() {
        badges++;
        // Débloquer des avantages selon le nombre de badges
        if (badges % 2 == 0) {
            playerRank++;
            setMaxCreatures(getMaxCreatures() + 1); // Augmenter la capacité d'équipe
        }
    }

    /**
     * Gagne de la réputation.
     *
     * @param amount Montant de réputation à gagner
     */
    public void gainReputation(int amount) {
        reputation += amount;
        if (reputation > 100) {
            reputation = 100;
        }
    }

    /**
     * Perd de la réputation.
     *
     * @param amount Montant de réputation à perdre
     */
    public void loseReputation(int amount) {
        reputation -= amount;
        if (reputation < 0) {
            reputation = 0;
        }
    }

    /**
     * Capture une créature sauvage.
     *
     * @param creature Créature à capturer
     * @param captureChance Chance de capture (0-100)
     * @return true si la capture a réussi
     */
    public boolean captureCreature(Creature creature, int captureChance) {
        // Ajuster la chance de capture en fonction du rang du joueur
        captureChance += playerRank * 2;

        // Limiter la chance entre 5% et 95%