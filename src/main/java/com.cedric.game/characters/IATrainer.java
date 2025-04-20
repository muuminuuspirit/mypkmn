package com.cedric.game.characters;

import com.cedric.game.core.creature.Creature;
import com.cedric.game.core.skill.Skill;
import java.util.Random;

/**
 * Représente un dresseur contrôlé par l'IA.
 */
public class IATrainer extends Trainer {
    private int difficulty;
    private String trainerType;
    private String strategyType;
    private boolean isGymLeader;
    private Random random;

    /**
     * Crée un nouveau dresseur IA.
     *
     * @param id Identifiant unique
     * @param name Nom du dresseur
     * @param difficulty Niveau de difficulté (1-10)
     * @param trainerType Type de dresseur (Novice, Scientifique, etc.)
     * @param strategyType Type de stratégie (Aléatoire, Équilibré, Intelligent)
     * @param isGymLeader true si c'est un chef de gym
     */
    public IATrainer(String id, String name, int difficulty, String trainerType,
                     String strategyType, boolean isGymLeader) {
        super(id, name);
        this.difficulty = Math.max(1, Math.min(10, difficulty)); // Limiter entre 1 et 10
        this.trainerType = trainerType;
        this.strategyType = strategyType;
        this.isGymLeader = isGymLeader;
        this.random = new Random();

        // Augmenter la capacité d'équipe pour les chefs de gym
        if (isGymLeader) {
            setMaxCreatures(getMaxCreatures() + 2);
        }
    }

    /**
     * Choisit une action en combat basée sur la stratégie.
     *
     * @param myCreature Créature active de l'IA
     * @param enemyCreature Créature adverse
     * @return Compétence choisie ou null pour changer de créature
     */
    public Skill chooseAction(Creature myCreature, Creature enemyCreature) {
        // Stratégie de changement si PV faibles
        if (myCreature.getStats().getHealth() < myCreature.getStats().getMaxHealth() * 0.2) {
            // 70% de chance de changer si stratégie intelligente, 30% si équilibré, 10% si aléatoire
            double switchChance = 0.1; // Aléatoire par défaut

            if (strategyType.equals("Équilibré")) {
                switchChance = 0.3;
            } else if (strategyType.equals("Intelligent")) {
                switchChance = 0.7;
            }

            if (random.nextDouble() < switchChance) {
                return null; // Indique qu'il faut changer de créature
            }
        }

        // Sélection de compétence basée sur la stratégie
        return chooseSkill(myCreature, enemyCreature);
    }

    /**
     * Choisit une compétence à utiliser selon la stratégie.
     *
     * @param myCreature Créature active de l'IA
     * @param enemyCreature Créature adverse
     * @return Compétence choisie
     */
    private Skill chooseSkill(Creature myCreature, Creature enemyCreature) {
        // Obtenir les compétences disponibles (avec assez de PA)
        java.util.List<Skill> availableSkills = new java.util.ArrayList<>();

        for (Skill skill : myCreature.getActiveSkills()) {
            if (skill.getActionPointCost() <= myCreature.getCurrentActionPoints()) {
                availableSkills.add(skill);
            }
        }

        if (availableSkills.isEmpty()) {
            return null; // Pas de compétence disponible
        }

        switch (strategyType) {
            case "Intelligent":
                return chooseIntelligentSkill(availableSkills, myCreature, enemyCreature);

            case "Équilibré":
                // 50% intelligent, 50% aléatoire
                if (random.nextBoolean()) {
                    return chooseIntelligentSkill(availableSkills, myCreature, enemyCreature);
                } else {
                    return availableSkills.get(random.nextInt(availableSkills.size()));
                }

            case "Aléatoire":
            default:
                // Compétence complètement aléatoire
                return availableSkills.get(random.nextInt(availableSkills.size()));
        }
    }

    /**
     * Choisit une compétence selon une stratégie intelligente.
     *
     * @param availableSkills Compétences disponibles
     * @param myCreature Créature active de l'IA
     * @param enemyCreature Créature adverse
     * @return Meilleure compétence selon la stratégie
     */
    private Skill chooseIntelligentSkill(java.util.List<Skill> availableSkills,
                                         Creature myCreature, Creature enemyCreature) {
        Skill bestSkill = null;
        double bestScore = -1;

        for (Skill skill : availableSkills) {
            double score = evaluateSkill(skill, myCreature, enemyCreature);

            if (score > bestScore) {
                bestScore = score;
                bestSkill = skill;
            }
        }

        return bestSkill != null ? bestSkill : availableSkills.get(0);
    }

    /**
     * Évalue l'efficacité d'une compétence contre une créature adverse.
     *
     * @param skill Compétence à évaluer
     * @param myCreature Créature active de l'IA
     * @param enemyCreature Créature adverse
     * @return Score d'efficacité
     */
    private double evaluateSkill(Skill skill, Creature myCreature, Creature enemyCreature) {
        // Base: puissance de la compétence
        double score = skill.getPower();

        // Vérifier l'efficacité des types
        double typeEffectiveness = 1.0;
        for (game.core.type.Type defenderType : enemyCreature.getStats().getTypes()) {
            typeEffectiveness *= skill.getType().getEffectivenessAgainst(defenderType);
        }

        // Favoriser les compétences super efficaces
        score *= typeEffectiveness * 2;

        // Bonus pour niveau de compétence
        score += skill.getLevel() * 5;

        // Malus pour coût en PA élevé
        score -= skill.getActionPointCost() * 3;

        // Ajuster selon la difficulté
        score *= (0.5 + (difficulty * 0.05)); // +5% par niveau de difficulté

        return score;
    }

    /**
     * Choisit une créature à envoyer au combat.
     *
     * @param currentCreature Créature actuelle ou null
     * @param enemyCreature Créature adverse
     * @return Index de la créature à utiliser ou -1 pour garder l'actuelle
     */
    public int chooseCreature(Creature currentCreature, Creature enemyCreature) {
        if (getCreatures().isEmpty()) {
            return -1;
        }

        // Si c'est un choix initial ou la créature actuelle est K.O.
        if (currentCreature == null || currentCreature.isDead()) {
            return chooseBestCreature(enemyCreature);
        }

        // Sinon, décider si on change selon la stratégie
        return -1; // Par défaut, garder la créature actuelle
    }

    /**
     * Choisit la meilleure créature contre un adversaire.
     *
     * @param enemyCreature Créature adverse
     * @return Index de la meilleure créature
     */
    private int chooseBestCreature(Creature enemyCreature) {
        if (strategyType.equals("Aléatoire")) {
            // Choix aléatoire parmi les créatures non K.O.
            java.util.List<Integer> validIndices = new java.util.ArrayList<>();

            for (int i = 0; i < getCreatures().size(); i++) {
                if (!getCreatures().get(i).isDead()) {
                    validIndices.add(i);
                }
            }

            if (validIndices.isEmpty()) {
                return 0; // Cas improbable, toutes les créatures sont K.O.
            }

            return validIndices.get(random.nextInt(validIndices.size()));
        } else {
            // Choix stratégique basé sur le type
            Creature bestCreature = null;
            int bestIndex = 0;
            double bestScore = -1;

            for (int i = 0; i < getCreatures().size(); i++) {
                Creature myCreature = getCreatures().get(i);

                if (myCreature.isDead()) {
                    continue;
                }

                double score = evaluateCreatureMatch(myCreature, enemyCreature);

                if (score > bestScore) {
                    bestScore = score;
                    bestCreature = myCreature;
                    bestIndex = i;
                }
            }

            return bestIndex;
        }
    }

    /**
     * Évalue l'efficacité d'une créature contre une autre.
     *
     * @param myCreature Créature à évaluer
     * @param enemyCreature Créature adverse
     * @return Score d'efficacité
     */
    private double evaluateCreatureMatch(Creature myCreature, Creature enemyCreature) {
        // Base: niveau et santé
        double score = myCreature.getStats().getLevel() * 5 +
                (double) myCreature.getStats().getHealth() /
                        myCreature.getStats().getMaxHealth() * 100;

        // Avantage/désavantage de type
        double typeAdvantage = 0;

        for (game.core.type.Type myType : myCreature.getStats().getTypes()) {
            for (game.core.type.Type enemyType : enemyCreature.getStats().getTypes()) {
                double effectiveness = myType.getEffectivenessAgainst(enemyType);

                // Favoriser les types avantageux
                if (effectiveness > 1.0) {
                    typeAdvantage += 50 * effectiveness;
                } else if (effectiveness < 1.0) {
                    typeAdvantage -= 30 * (1.0 - effectiveness);
                }
            }
        }

        score += typeAdvantage;

        // Bonus pour vitesse supérieure
        if (myCreature.getStats().getSpeed() > enemyCreature.getStats().getSpeed()) {
            score += 20;
        }

        return score;
    }

    // Getters et setters

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = Math.max(1, Math.min(10, difficulty));
    }

    public String getTrainerType() {
        return trainerType;
    }

    public void setTrainerType(String trainerType) {
        this.trainerType = trainerType;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public boolean isGymLeader() {
        return isGymLeader;
    }

    public void setGymLeader(boolean isGymLeader) {
        this.isGymLeader = isGymLeader;
    }
}