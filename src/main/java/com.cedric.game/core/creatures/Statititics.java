package game.core.creature;

import game.core.type.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gère les statistiques d'une créature.
 */
public class Statistics {
    private String name;
    private String description;
    private int level;
    private int xp;
    private int maxXpNextLevel;
    private int health;
    private int maxHealth;
    private int vitality;
    private int maxVitality;
    private int vitalEnergy;
    private int maxVitalEnergy;
    private int strength;
    private int constitution;
    private int spirit;
    private int mental;
    private int speed;
    private List<Type> types;
    private Map<String, StatModifier> temporaryModifiers; // Modificateurs temporaires

    /**
     * Classe interne pour les modificateurs temporaires de stats.
     */
    private static class StatModifier {
        private int value;
        private int remainingTurns;

        public StatModifier(int value, int duration) {
            this.value = value;
            this.remainingTurns = duration;
        }

        public int getValue() {
            return value;
        }

        public void decreaseDuration() {
            remainingTurns--;
        }

        public boolean isExpired() {
            return remainingTurns <= 0;
        }
    }

    /**
     * Crée un nouvel objet de statistiques avec des valeurs par défaut.
     */
    public Statistics() {
        this.level = 1;
        this.xp = 0;
        this.maxXpNextLevel = 100;
        this.health = 50;
        this.maxHealth = 50;
        this.vitality = 30;
        this.maxVitality = 30;
        this.vitalEnergy = 20;
        this.maxVitalEnergy = 20;
        this.strength = 10;
        this.constitution = 10;
        this.spirit = 10;
        this.mental = 10;
        this.speed = 10;
        this.types = new ArrayList<>();
        this.temporaryModifiers = new HashMap<>();
    }

    /**
     * Fait gagner de l'expérience à la créature.
     *
     * @param amount Quantité d'XP à ajouter
     * @return true si la créature a monté de niveau
     */
    public boolean gainExperience(int amount) {
        this.xp += amount;
        if (this.xp >= this.maxXpNextLevel) {
            levelUp();
            return true;
        }
        return false;
    }

    /**
     * Monte la créature d'un niveau.
     */
    private void levelUp() {
        this.level++;
        this.xp -= this.maxXpNextLevel;
        this.maxXpNextLevel = calculateNextLevelXp();

        // Augmentation des stats
        this.maxHealth += 5 + (constitution / 2);
        this.health = this.maxHealth;
        this.maxVitality += 3 + (spirit / 3);
        this.vitality = this.maxVitality;
        this.maxVitalEnergy += 2 + (mental / 2);
        this.vitalEnergy = this.maxVitalEnergy;

        // Augmentation aléatoire d'autres stats
        this.strength += 1 + (int)(Math.random() * 2);
        this.constitution += 1 + (int)(Math.random() * 2);
        this.spirit += 1 + (int)(Math.random() * 2);
        this.mental += 1 + (int)(Math.random() * 2);
        this.speed += 1 + (int)(Math.random() * 2);
    }

    /**
     * Calcule l'XP nécessaire pour le prochain niveau.
     *
     * @return XP nécessaire
     */
    private int calculateNextLevelXp() {
        return (int)(this.maxXpNextLevel * 1.2);
    }

    /**
     * Ajoute un type à la créature.
     *
     * @param type Type à ajouter
     */
    public void addType(Type type) {
        if (!this.types.contains(type)) {
            this.types.add(type);
        }
    }

    /**
     * Génère aléatoirement un type secondaire.
     *
     * @param availableTypes Liste des types disponibles
     */
    public void generateRandomSecondaryType(List<Type> availableTypes) {
        if (this.types.size() >= 2) {
            return; // Déjà deux types ou plus
        }

        if (this.types.isEmpty()) {
            return; // Pas de type principal défini
        }

        // Filtrer les types disponibles pour exclure le type principal
        List<Type> filteredTypes = new ArrayList<>();
        Type primaryType = this.types.get(0);

        for (Type type : availableTypes) {
            if (!type.equals(primaryType)) {
                filteredTypes.add(type);
            }
        }

        if (!filteredTypes.isEmpty()) {
            // Sélectionner un type aléatoire
            int randomIndex = (int)(Math.random() * filteredTypes.size());
            addType(filteredTypes.get(randomIndex));
        }
    }

    /**
     * Soigne la créature.
     *
     * @param amount Quantité de points de vie à restaurer
     */
    public void heal(int amount) {
        this.health = Math.min(this.maxHealth, this.health + amount);
    }

    /**
     * Ajoute un modificateur temporaire à une statistique.
     *
     * @param statName Nom de la stat ("strength", "speed", etc.)
     * @param value Valeur du modificateur
     * @param duration Durée en tours
     */
    public void addTemporaryModifier(String statName, int value, int duration) {
        temporaryModifiers.put(statName, new StatModifier(value, duration));
    }

    /**
     * Met à jour tous les modificateurs temporaires (à appeler à chaque tour).
     */
    public void updateModifiers() {
        List<String> expiredModifiers = new ArrayList<>();

        for (Map.Entry<String, StatModifier> entry : temporaryModifiers.entrySet()) {
            StatModifier modifier = entry.getValue();
            modifier.decreaseDuration();

            if (modifier.isExpired()) {
                expiredModifiers.add(entry.getKey());
            }
        }

        // Supprimer les modificateurs expirés
        for (String statName : expiredModifiers) {
            temporaryModifiers.remove(statName);
        }
    }

    /**
     * Modifie la force temporairement.
     *
     * @param value Valeur du modificateur
     * @param duration Durée en tours
     */
    public void modifyStrength(int value, int duration) {
        addTemporaryModifier("strength", value, duration);
    }

    /**
     * Modifie la vitesse temporairement.
     *
     * @param value Valeur du modificateur
     * @param duration Durée en tours
     */
    public void modifySpeed(int value, int duration) {
        addTemporaryModifier("speed", value, duration);
    }

    /**
     * Obtient la valeur d'une stat incluant les modificateurs temporaires.
     *
     * @param baseValue Valeur de base
     * @param statName Nom de la stat
     * @return Valeur modifiée
     */
    private int getModifiedStat(int baseValue, String statName) {
        StatModifier modifier = temporaryModifiers.get(statName);
        return modifier != null ? baseValue + modifier.getValue() : baseValue;
    }

    // Getters et setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(maxHealth, health));
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getStrength() {
        return getModifiedStat(strength, "strength");
    }

    public int getConstitution() {
        return getModifiedStat(constitution, "constitution");
    }

    public int getSpirit() {
        return getModifiedStat(spirit, "spirit");
    }

    public int getMental() {
        return getModifiedStat(mental, "mental");
    }

    public int getSpeed() {
        return getModifiedStat(speed, "speed");
    }

    public List<Type> getTypes() {
        return new ArrayList<>(types); // Retourne une copie pour encapsulation
    }

    public int getXp() {
        return xp;
    }

    public int getMaxXpNextLevel() {
        return maxXpNextLevel;
    }
}