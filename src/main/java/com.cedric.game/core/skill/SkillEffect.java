package com.cedric.game.core.skill;

import com.cedric.game.core.creature.Creature;

/**
 * Représente un effet appliqué par une compétence.
 */
public class SkillEffect {
    /**
     * Types d'effets possibles.
     */
    public enum EffectType {
        DAMAGE("Dégâts", "Inflige des dégâts à la cible"),
        HEALING("Soin", "Restaure des points de vie"),
        STAT_BOOST("Amélioration", "Augmente temporairement une statistique"),
        STAT_REDUCE("Réduction", "Réduit temporairement une statistique"),
        STATUS_EFFECT("Statut", "Applique un effet de statut spécial"),
        FIELD_EFFECT("Terrain", "Modifie le terrain de combat");

        private final String name;
        private final String description;

        EffectType(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Statistiques pouvant être affectées.
     */
    public enum StatisticType {
        HEALTH("Santé"),
        STRENGTH("Force"),
        CONSTITUTION("Constitution"),
        SPIRIT("Esprit"),
        MENTAL("Mental"),
        SPEED("Vitesse"),
        ACTION_POINTS("Points d'Action");

        private final String name;

        StatisticType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private String id;
    private String name;
    private EffectType type;
    private int duration; // En nombre de tours
    private int intensity; // Force de l'effet
    private StatisticType affectedStat; // Quelle stat est affectée (si applicable)
    private double chance; // Chance d'appliquer (0.0 - 1.0)

    /**
     * Crée un nouvel effet.
     *
     * @param id Identifiant unique
     * @param name Nom de l'effet
     * @param type Type d'effet
     * @param duration Durée en tours (0 pour instantané)
     * @param intensity Intensité de l'effet
     * @param affectedStat Statistique affectée (peut être null)
     * @param chance Chance d'application (0.0 - 1.0)
     */
    public SkillEffect(String id, String name, EffectType type, int duration, int intensity, StatisticType affectedStat, double chance) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.duration = duration;
        this.intensity = intensity;
        this.affectedStat = affectedStat;
        this.chance = chance;
    }

    /**
     * Applique l'effet à une créature.
     *
     * @param target Créature cible
     */
    public void apply(Creature target) {
        // Vérifier la chance d'application
        if (Math.random() > chance) {
            return; // Échec d'application
        }

        switch (type) {
            case DAMAGE:
                applyDamage(target);
                break;
            case HEALING:
                applyHealing(target);
                break;
            case STAT_BOOST:
                applyStatModifier(target, true);
                break;
            case STAT_REDUCE:
                applyStatModifier(target, false);
                break;
            case STATUS_EFFECT:
                applyStatusEffect(target);
                break;
            case FIELD_EFFECT:
                // Implémentation plus complexe nécessaire
                break;
        }
    }

    /**
     * Applique des dégâts à la cible.
     *
     * @param target Créature cible
     */
    private void applyDamage(Creature target) {
        target.takeDamage(intensity);
    }

    /**
     * Soigne la cible.
     *
     * @param target Créature cible
     */
    private void applyHealing(Creature target) {
        target.getStats().heal(intensity);
    }

    /**
     * Applique un modificateur de statistique.
     *
     * @param target Créature cible
     * @param isPositive true pour un boost, false pour une réduction
     */
    private void applyStatModifier(Creature target, boolean isPositive) {
        int value = isPositive ? intensity : -intensity;

        if (affectedStat == null) {
            return;
        }

        switch (affectedStat) {
            case STRENGTH:
                target.getStats().modifyStrength(value, duration);
                break;
            case SPEED:
                target.getStats().modifySpeed(value, duration);
                break;
            case ACTION_POINTS:
                if (isPositive) {
                    target.restoreActionPoints(value);
                } else {
                    // Réduire les PA actuels si possible
                    target.reduceActionPoints(value);
                }
                break;
            // Autres stats...
        }
    }

    /**
     * Applique un effet de statut spécial.
     *
     * @param target Créature cible
     */
    private void applyStatusEffect(Creature target) {
        // Les effets de statut seraient implémentés séparément
        // Par exemple: Poison, Paralysie, Sommeil, etc.
    }

    /**
     * Met à jour la durée de l'effet.
     */
    public void updateDuration() {
        if (duration > 0) {
            duration--;
        }
    }

    /**
     * Vérifie si l'effet est expiré.
     *
     * @return true si l'effet est terminé
     */
    public boolean isExpired() {
        return duration == 0;
    }

    /**
     * Crée une copie de cet effet.
     *
     * @return Copie de l'effet
     */
    public SkillEffect clone() {
        return new SkillEffect(id, name, type, duration, intensity, affectedStat, chance);
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EffectType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public int getIntensity() {
        return intensity;
    }

    public StatisticType getAffectedStat() {
        return affectedStat;
    }

    public double getChance() {
        return chance;
    }
}