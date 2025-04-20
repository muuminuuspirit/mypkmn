package game.core;

public class Skill {
    private String name;
    private String description;
    private Type type;
    private int level;
    private int experience;
    private int experienceToNextLevel;
    private int power;
    private int accuracy;
    private int actionPointCost;  // PA au lieu de PP
    private SkillCategory category;

    public Skill(String name, String description, Type type, int basePower, int baseAccuracy, int actionPointCost, SkillCategory category) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.level = 1;
        this.experience = 0;
        this.experienceToNextLevel = 100;
        this.power = basePower;
        this.accuracy = baseAccuracy;
        this.actionPointCost = actionPointCost;
        this.category = category;
    }

    // Méthode appelée quand une compétence est utilisée
    public boolean useSkill(int creatureActionPoints) {
        if (creatureActionPoints >= actionPointCost) {
            // Gain d'expérience à chaque utilisation
            gainExperience(5);  // 5 points d'XP par utilisation par exemple
            return true;  // Compétence utilisée avec succès
        }
        return false;  // Pas assez de PA
    }

    // Méthode pour gagner de l'expérience et monter la compétence en niveau
    public boolean gainExperience(int amount) {
        this.experience += amount;
        if (this.experience >= this.experienceToNextLevel) {
            levelUp();
            return true;  // Indique qu'il y a eu une montée de niveau
        }
        return false;
    }

    // Méthode pour monter la compétence en niveau
    private void levelUp() {
        this.level++;
        this.experience -= this.experienceToNextLevel;
        this.experienceToNextLevel = calculateNextLevelXP();

        // Amélioration des statistiques de la compétence
        this.power += (int)(this.power * 0.1);  // +10% de puissance par niveau
        this.accuracy += 1;  // +1% de précision par niveau

        // Optionnel: réduire le coût en PA des compétences fortement maîtrisées
        if (level % 5 == 0 && actionPointCost > 1) {
            actionPointCost--;
        }
    }

    // Calcul de l'XP nécessaire pour le prochain niveau
    private int calculateNextLevelXP() {
        return (int)(this.experienceToNextLevel * 1.2);
    }

    // Getters et setters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public int getPower() {
        return power;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getActionPointCost() {
        return actionPointCost;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public int getExperience() {
        return experience;
    }

    public int getExperienceToNextLevel() {
        return experienceToNextLevel;
    }

    public enum SkillCategory {
        PHYSICAL,
        SPECIAL,
        STATUS
    }
}