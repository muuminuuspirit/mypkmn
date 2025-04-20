package com.cedric.game.core.creature;

import com.cedric.game.core.skill.Skill;
import com.cedric.game.core.skill.SkillTree;
import com.cedric.game.core.skill.SkillEffect;
import game.core.type.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une créature dans le jeu.
 */
public class Creature {
    private String id;
    private String name;
    private Statistics stats;
    private SkillTree skillTree;
    private List<Skill> activeSkills; // Compétences actuellement équipées (max 4)
    private int currentActionPoints;
    private int maxActionPoints;
    private int skillPoints; // Points pour débloquer des compétences
    private List<SkillEffect> activeEffects; // Effets actifs sur la créature

    /**
     * Crée une nouvelle créature.
     *
     * @param id Identifiant unique de la créature
     * @param name Nom de la créature
     * @param primaryType Type principal de la créature
     */
    public Creature(String id, String name, Type primaryType) {
        this.id = id;
        this.name = name;
        this.stats = new Statistics();
        this.stats.setName(name);
        this.stats.addType(primaryType);
        this.skillTree = new SkillTree(primaryType);
        this.activeSkills = new ArrayList<>();
        this.maxActionPoints = 10; // Valeur de base, peut être calculée à partir des stats
        this.currentActionPoints = this.maxActionPoints;
        this.skillPoints = 3; // Points de compétence initiaux
        this.activeEffects = new ArrayList<>();
    }

    /**
     * Débloquer un nœud dans l'arbre de compétences.
     *
     * @param nodeId Identifiant du nœud à débloquer
     * @return true si le nœud a été débloqué avec succès
     */
    public boolean unlockSkillNode(String nodeId) {
        if (skillPoints <= 0) {
            return false; // Pas assez de points
        }

        if (skillTree.unlockNode(nodeId, this, skillPoints)) {
            skillPoints--;
            return true;
        }
        return false;
    }

    /**
     * Équiper une compétence débloquée.
     *
     * @param skill Compétence à équiper
     * @return true si la compétence a été équipée avec succès
     */
    public boolean equipSkill(Skill skill) {
        if (!skillTree.isSkillUnlocked(skill)) {
            return false; // Compétence non débloquée
        }

        if (activeSkills.size() >= 4) {
            return false; // Déjà 4 compétences équipées
        }

        activeSkills.add(skill);
        return true;
    }

    /**
     * Déséquiper une compétence.
     *
     * @param skill Compétence à retirer
     */
    public void unequipSkill(Skill skill) {
        activeSkills.remove(skill);
    }

    /**
     * Remplacer une compétence par une autre.
     *
     * @param oldSkill Compétence à remplacer
     * @param newSkill Nouvelle compétence
     * @return true si le remplacement a réussi
     */
    public boolean replaceSkill(Skill oldSkill, Skill newSkill) {
        int index = activeSkills.indexOf(oldSkill);
        if (index == -1) {
            return false;
        }

        if (!skillTree.isSkillUnlocked(newSkill)) {
            return false;
        }

        activeSkills.set(index, newSkill);
        return true;
    }

    /**
     * Attaquer une autre créature avec une compétence.
     *
     * @param target Créature cible
     * @param skill Compétence à utiliser
     * @return Dégâts infligés
     */
    public int attack(Creature target, Skill skill) {
        if (!activeSkills.contains(skill)) {
            return 0; // La créature ne possède pas cette compétence
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

            // Formule de dégâts, intégrant le niveau de compétence
            int baseDamage = skill.getPower() * stats.getStrength() / target.getStats().getConstitution();
            int skillLevelBonus = (int)(baseDamage * (skill.getLevel() * 0.05)); // +5% par niveau de compétence
            int finalDamage = (int)((baseDamage + skillLevelBonus) * typeEffectiveness);

            // Appliquer les dégâts
            target.takeDamage(finalDamage);

            // Appliquer les effets de la compétence
            for (SkillEffect effect : skill.getEffects()) {
                target.applyEffect(effect.clone()); // Clone pour avoir un effet unique par cible
            }

            // Donner de l'expérience à la compétence en fonction des dégâts
            skill.gainExperience(5 + (int)(finalDamage * 0.1), target, finalDamage);

            return finalDamage;
        }

        return 0;
    }

    /**
     * Appliquer un effet à la créature.
     *
     * @param effect Effet à appliquer
     */
    public void applyEffect(SkillEffect effect) {
        // Vérifier si l'effet existe déjà, si oui, le remplacer
        for (int i = 0; i < activeEffects.size(); i++) {
            if (activeEffects.get(i).getName().equals(effect.getName())) {
                activeEffects.set(i, effect);
                return;
            }
        }

        // Sinon, ajouter le nouvel effet
        activeEffects.add(effect);
        effect.apply(this);
    }

    /**
     * Mettre à jour les effets actifs (à appeler à chaque tour).
     */
    public void updateEffects() {
        List<SkillEffect> expiredEffects = new ArrayList<>();

        for (SkillEffect effect : activeEffects) {
            effect.updateDuration();
            if (effect.isExpired()) {
                expiredEffects.add(effect);
            }
        }

        // Retirer les effets expirés
        activeEffects.removeAll(expiredEffects);
    }

    /**
     * Subir des dégâts.
     *
     * @param damage Montant des dégâts
     */
    public void takeDamage(int damage) {
        stats.setHealth(Math.max(0, stats.getHealth() - damage));
    }

    /**
     * Restaurer des points d'action.
     *
     * @param amount Quantité à restaurer
     */
    public void restoreActionPoints(int amount) {
        currentActionPoints = Math.min(maxActionPoints, currentActionPoints + amount);
    }

    /**
     * Restaurer tous les points d'action.
     */
    public void restoreAllActionPoints() {
        currentActionPoints = maxActionPoints;
    }

    /**
     * Recalculer les points d'action maximum en fonction des stats.
     */
    public void recalculateMaxActionPoints() {
        this.maxActionPoints = 10 + (stats.getSpeed() / 10) + (stats.getSpirit() / 15);
        if (currentActionPoints > maxActionPoints) {
            currentActionPoints = maxActionPoints;
        }
    }

    /**
     * Gagner des points de compétence.
     *
     * @param amount Quantité de points à gagner
     */
    public void gainSkillPoints(int amount) {
        this.skillPoints += amount;
    }

    /**
     * Appelé quand la créature monte de niveau.
     */
    public void onLevelUp() {
        // Gagner des points de compétence
        gainSkillPoints(2);

        // Autres bonus de niveau...
    }

    // Getters et setters

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

    public List<Skill> getActiveSkills() {
        return new ArrayList<>(activeSkills);
    }

    public int getCurrentActionPoints() {
        return currentActionPoints;
    }

    public int getMaxActionPoints() {
        return maxActionPoints;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public SkillTree getSkillTree() {
        return skillTree;
    }

    public List<SkillEffect> getActiveEffects() {
        return new ArrayList<>(activeEffects);
    }

    public boolean isDead() {
        return stats.getHealth() <= 0;
    }
}