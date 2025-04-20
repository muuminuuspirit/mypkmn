package com.cedric.game.characters;

import com.cedric.game.core.creature.Creature;
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
        if (captureChance < 5) {
            captureChance = 5;
        } else if (captureChance > 95) {
            captureChance = 95;
        }

        // Ajuster selon la réputation (une meilleure réputation aide à capturer)
        captureChance += (reputation - 50) / 10;

        // Effectuer le jet de capture
        boolean success = (Math.random() * 100) < captureChance;

        if (success) {
            // Ajouter la créature à l'équipe
            if (!addCreature(creature)) {
                // Si l'équipe est pleine, stocker ailleurs (non implémenté dans ce prototype)
                return false;
            }

            // Gain de réputation pour capture réussie
            gainReputation(2);
        }

        return success;
    }

    /**
     * Récupère le nombre de badges.
     *
     * @return Nombre de badges
     */
    public int getBadges() {
        return badges;
    }

    /**
     * Récupère le rang du joueur.
     *
     * @return Rang du joueur
     */
    public int getPlayerRank() {
        return playerRank;
    }

    /**
     * Récupère la réputation du joueur.
     *
     * @return Réputation du joueur
     */
    public int getReputation() {
        return reputation;
    }

    /**
     * Défie un chef de gym.
     *
     * @param gymLeader Chef de gym à défier
     * @return true si le défi a été accepté
     */
    public boolean challengeGym(Trainer gymLeader) {
        // Vérifier si le joueur a au moins 3 créatures actives
        if (getActiveCreatures().size() < 3) {
            return false; // Pas assez de créatures pour défier un gym
        }

        // Ici, la logique pour démarrer un combat contre le chef de gym
        // Sera généralement gérée par le BattleManager
        return true;
    }

    /**
     * Obtient le nombre maximum de badges possible.
     *
     * @return Nombre max de badges
     */
    public static int getMaxBadges() {
        return 8; // Nombre standard dans la plupart des jeux similaires
    }

    /**
     * Achète un objet à un prix réduit en fonction du rang.
     *
     * @param item Objet à acheter
     * @param quantity Quantité à acheter
     * @param basePrice Prix de base unitaire
     * @return true si l'achat a réussi
     */
    public boolean buyItemWithDiscount(game.items.Item item, int quantity, int basePrice) {
        // Calculer le prix avec réduction selon le rang
        double discount = 0.02 * (playerRank - 1); // 2% de réduction par rang
        if (discount > 0.3) {
            discount = 0.3; // Maximum 30% de réduction
        }

        int discountedPrice = (int)(basePrice * (1 - discount));
        return buyItem(item, quantity, discountedPrice);
    }

    /**
     * Vend un objet avec un bonus de prix selon la réputation.
     *
     * @param item Objet à vendre
     * @param quantity Quantité à vendre
     * @param basePrice Prix de base unitaire
     * @return true si la vente a réussi
     */
    public boolean sellItemWithBonus(game.items.Item item, int quantity, int basePrice) {
        // Calculer le prix avec bonus selon la réputation
        double bonus = (reputation - 50) * 0.005; // +0.5% par point au-dessus de 50
        if (bonus < 0) {
            bonus = 0;
        } else if (bonus > 0.25) {
            bonus = 0.25; // Maximum 25% de bonus
        }

        int bonusPrice = (int)(basePrice * (1 + bonus));
        return sellItem(item, quantity, bonusPrice);
    }

    /**
     * Obtient des informations sur les progrès du joueur.
     *
     * @return Chaîne descriptive des progrès
     */
    public String getProgressInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Dresseur: ").append(getName()).append(" (Rang ").append(playerRank).append(")\n");
        info.append("Badges: ").append(badges).append("/").append(getMaxBadges()).append("\n");
        info.append("Réputation: ").append(getReputationText()).append(" (").append(reputation).append("/100)\n");
        info.append("Créatures: ").append(getCreatures().size()).append("/").append(getMaxCreatures());

        return info.toString();
    }

    /**
     * Obtient une description textuelle de la réputation.
     *
     * @return Description de la réputation
     */
    private String getReputationText() {
        if (reputation >= 90) {
            return "Légendaire";
        } else if (reputation >= 75) {
            return "Excellente";
        } else if (reputation >= 60) {
            return "Bonne";
        } else if (reputation >= 40) {
            return "Neutre";
        } else if (reputation >= 25) {
            return "Douteuse";
        } else if (reputation >= 10) {
            return "Mauvaise";
        } else {
            return "Terrible";
        }
    }
}