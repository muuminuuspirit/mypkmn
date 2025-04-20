package game.core;

import java.util.ArrayList;
import java.util.List;

public class Creature {
    private String id;
    private String name;
    private Statistics stats;
    private List<Skill> skills;
    private int currentActionPoints;
    private int maxActionPoints;

    public Creature(String id, String name, Type primaryType) {
        this.id = id;
        this.name = name;
        this.stats = new Statistics();
        this.stats.setName(name);
        this.stats.addType(primaryType);
        this.skills = new ArrayList<>();
        this.maxActionPoints = 10; // Valeur de base, peut être calculée à partir des stats
        this.currentActionPoints = this.maxActionPoints;
    }

    public void unlearnSkill(Skill skill) {
        skills.remove(skill);
    }

    public boolean learnSkill(Skill skill) {
        if (skills.size() < 4) {
            skills.add(skill);
            return true;
        }
        return false;
    }

    // Méthode pour attaquer une autre créature
    public int attack(Creature target, Skill skill) {
        if (!skills.contains(skill)) {
            return 0; // La créature ne connaît pas cette compétence
        }

        if (currentActionPoints < skill.getActionPointCost()) {
            return 0; // Pas assez de PA pour cette compétence
        }

        // Consommer les PA et utiliser la compétence
        if (skill.useSkill(currentActionPoints)) {
            currentActionPoints -= skill.getActionPointCost();

            // Calculer les dégâts
            double typeEffectiveness = 1.0;
            for (Type attackerType : stats.getTypes()) {
                for (Type defenderType : target.getStats().getTypes()) {
                    typeEffectiveness *= attackerType.getEffectivenessAgainst(defenderType);
                }
            }

            // Formule basique de dégâts, intégrant le niveau de compétence
            int baseDamage = skill.getPower() * stats.getStrength() / target.getStats().getConstitution();
            int skillLevelBonus = (int)(baseDamage * (skill.getLevel() * 0.05)); // +5% par niveau de compétence
            int finalDamage = (int)((baseDamage + skillLevelBonus) * typeEffectiveness);

            // Appliquer les dégâts
            target.takeDamage(finalDamage);

            return finalDamage;
        }

        return 0;
    }

    public void takeDamage(int damage) {
        stats.setHealth(Math.max(0, stats.getHealth() - damage));
    }

    // Méthode pour restaurer les PA (début de tour par exemple)
    public void restoreActionPoints(int amount) {
        currentActionPoints = Math.min(maxActionPoints, currentActionPoints + amount);
    }

    // Méthode pour restaurer tous les PA
    public void restoreAllActionPoints() {
        currentActionPoints = maxActionPoints;
    }

    // Calcul des PA max basé sur les stats
    public void recalculateMaxActionPoints() {
        // Exemple: les PA sont influencés par la vitesse et l'esprit
        this.maxActionPoints = 10 + (stats.getSpeed() / 10) + (stats.getSpirit() / 15);
        if (currentActionPoints > maxActionPoints) {
            currentActionPoints = maxActionPoints;
        }
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.stats.setName(name);
    }

    public Statistics getStats() {
        return stats;
    }

    public List<Skill> getSkills() {
        return new ArrayList<>(skills);
    }

    public int getCurrentActionPoints() {
        return currentActionPoints;
    }

    public int getMaxActionPoints() {
        return maxActionPoints;
    }
}
