package game.core.skill;

import game.core.creature.Creature;
import game.core.type.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une compétence pouvant être utilisée par une créature.
 */
public class Skill {
    private String id;
    private String name;
    private String description;
    private Type type;
    private int level;
    private int experience;
    private int experienceToNextLevel;
    private int power;
    private int accuracy;
    private int actionPointCost;
    private SkillCategory category;
    private List<SkillEffect> effects;
    private List<Skill> evolutionOptions;

    /**
     * Catégories de compétences.
     */
    public enum SkillCategory {
        PHYSICAL("Physique", "Utilise la force physique pour attaquer"),
        SPECIAL("Spéciale", "Utilise des pouvoirs élémentaires pour attaquer"),
        STATUS("Statut", "Modifie les statistiques ou applique des effets");

        private final String name;
        private final String description;

        SkillCategory(String name, String description) {
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
     * Crée une nouvelle compétence.
     *
     * @param name Nom de la compétence
     * @param description Description de la compétence
     * @param type Type élémental de la compétence
     * @param basePower Puissance de base
     * @param baseAccuracy Précision de base
     * @param actionPointCost Coût en points d'action
     * @param category Catégorie de la compétence
     */
    public Skill(String id, String name, String description, Type type, int basePower, int baseAccuracy, int actionPointCost, SkillCategory category) {
        this.id = id;
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
        this.effects = new ArrayList<>();
        this.evolutionOptions = new ArrayList<>();
    }

    /**
     * Utilise la compétence, vérifie si le coût en PA peut être payé.
     *
     * @param creatureActionPoints Points d'action actuels de la créature
     * @return true si la compétence a pu être utilisée
     */
    public boolean useSkill(int creatureActionPoints) {
        if (creatureActionPoints >= actionPointCost) {
            // La compétence peut être utilisée
            return true;
        }
        return false;
    }

    /**
     * Fait gagner de l'expérience à la compétence.
     *
     * @param amount Quantité d'XP de base
     * @param target Créature cible
     * @param damageDealt Dégâts infligés
     * @return true si la compétence a monté de niveau
     */
    public boolean gainExperience(int amount, Creature target, double damageDealt) {
        // Calcul des bonus d'XP en fonction de l'efficacité, dégâts, etc.
        int bonusXp = calculateBonusXp(target, damageDealt);
        return gainExperience(amount + bonusXp);
    }

    /**
     * Ajoute de l'expérience à la compétence.
     *
     * @param amount Quantité d'XP à ajouter
     * @return true si la compétence a monté de niveau
     */
    public boolean gainExperience(int amount) {
        this.experience += amount;
        if (this.experience >= this.experienceToNextLevel) {
            levelUp();
            return true;  // Indique qu'il y a eu une montée de niveau
        }
        return false;
    }

    /**
     * Monte la compétence d'un niveau.
     */
    private void levelUp() {
        this.level++;
        this.experience -= this.experienceToNextLevel;
        this.experienceToNextLevel = calculateNextLevelXP();

        // Amélioration des statistiques de la compétence
        this.power += (int)(this.power * 0.1);  // +10% de puissance par niveau
        this.accuracy += 1;  // +1% de précision par niveau

        // Réduction du coût en PA pour les niveaux élevés
        if (level % 5 == 0 && actionPointCost > 1) {
            actionPointCost--;
        }
    }

    /**
     * Calcule l'XP nécessaire pour le prochain niveau.
     *
     * @return XP nécessaire
     */
    private int calculateNextLevelXP() {
        return (int)(this.experienceToNextLevel * 1.2);
    }

    /**
     * Calcule les bonus d'XP en fonction de la situation de combat.
     *
     * @param target Créature cible
     * @param damageDealt Dégâts infligés
     * @return Bonus d'XP à ajouter
     */
    private int calculateBonusXp(Creature target, double damageDealt) {
        int bonus = 0;

        // Bonus pour dégâts élevés
        if (damageDealt > target.getStats().getMaxHealth() * 0.5) {
            bonus += 10; // Bonus si plus de 50% de vie enlevée
        }

        // Bonus pour l'efficacité du type
        double effectiveness = 1.0;
        for (Type targetType : target.getStats().getTypes()) {
            effectiveness *= this.type.getEffectivenessAgainst(targetType);
        }

        if (effectiveness > 1.0) {
            bonus += 5; // Bonus pour super efficace
        }

        // Bonus pour niveau élevé de la cible
        bonus += Math.min(10, target.getStats().getLevel() / 5);

        return bonus;
    }

    /**
     * Ajoute un effet à la compétence.
     *
     * @param effect Effet à ajouter
     */
    public void addEffect(SkillEffect effect) {
        effects.add(effect);
    }

    /**
     * Ajoute une option d'évolution pour cette compétence.
     *
     * @param evolution Compétence évoluée
     */
    public void addEvolutionOption(Skill evolution) {
        evolutionOptions.add(evolution);
    }

    /**
     * Obtient les options d'évolution disponibles si le niveau requis est atteint.
     *
     * @param requiredLevel Niveau requis pour l'évolution
     * @return Liste des évolutions possibles
     */
    public List<Skill> getAvailableEvolutions(int requiredLevel) {
        if (this.level >= requiredLevel) {
            return new ArrayList<>(evolutionOptions);
        }
        return new ArrayList<>();
    }

    /**
     * Évolue la compétence vers une option spécifique.
     *
     * @param optionIndex Index de l'option d'évolution
     * @return Compétence évoluée ou celle-ci si l'évolution a échoué
     */
    public Skill evolve(int optionIndex) {
        if (optionIndex >= 0 && optionIndex < evolutionOptions.size()) {
            Skill evolvedSkill = evolutionOptions.get(optionIndex);
            // Transférer l'expérience ou autres attributs si nécessaire
            evolvedSkill.experience = this.experience;
            evolvedSkill.level = this.level;
            return evolvedSkill;
        }
        return this;
    }

    /**
     * Crée un clone de la compétence.
     *
     * @return Copie de la compétence
     */
    public Skill clone() {
        Skill clone = new Skill(
                this.id,
                this.name,
                this.description,
                this.type,
                this.power,
                this.accuracy,
                this.actionPointCost,
                this.category
        );

        clone.level = this.level;
        clone.experience = this.experience;
        clone.experienceToNextLevel = this.experienceToNextLevel;

        // Cloner les effets
        for (SkillEffect effect : this.effects) {
            clone.addEffect(effect.clone());
        }

        return clone;
    }

    // Getters et setters

    public String getId() {
        return id;
    }

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

    public List<SkillEffect> getEffects() {
        return new ArrayList<>(effects);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Skill skill = (Skill) obj;
        return id.equals(skill.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}