package com.cedric.game.core.skill;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un nœud dans l'arbre de compétences.
 */
public class SkillNode {
    private String id;
    private game.core.skill.Skill skill;
    private List<SkillNode> children;
    private List<SkillNode> prerequisites;
    private int pointsRequired;
    private boolean unlocked;

    /**
     * Crée un nouveau nœud d'arbre de compétences.
     *
     * @param id Identifiant unique du nœud
     * @param skill Compétence associée à ce nœud
     * @param pointsRequired Points nécessaires pour débloquer
     */
    public SkillNode(String id, Skill skill, int pointsRequired) {
        this.id = id;
        this.skill = skill;
        this.pointsRequired = pointsRequired;
        this.children = new ArrayList<>();
        this.prerequisites = new ArrayList<>();
        this.unlocked = false;
    }

    /**
     * Ajoute un nœud enfant à ce nœud.
     *
     * @param child Nœud enfant à ajouter
     */
    public void addChild(SkillNode child) {
        if (!children.contains(child)) {
            children.add(child);
            child.addPrerequisite(this);
        }
    }

    /**
     * Ajoute un prérequis à ce nœud.
     *
     * @param prerequisite Nœud prérequis
     */
    public void addPrerequisite(SkillNode prerequisite) {
        if (!prerequisites.contains(prerequisite)) {
            prerequisites.add(prerequisite);
        }
    }

    /**
     * Vérifie si tous les prérequis sont débloqués.
     *
     * @return true si tous les prérequis sont débloqués
     */
    public boolean arePrerequisitesMet() {
        for (SkillNode prereq : prerequisites) {
            if (!prereq.isUnlocked()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Crée une représentation visuelle du nœud (pour l'interface).
     *
     * @return Informations sur la position et les connexions du nœud
     */
    public NodeVisualInfo generateVisualInfo(int depth, int position) {
        return new NodeVisualInfo(this, depth, position);
    }

    /**
     * Classe interne pour stocker les informations visuelles d'un nœud.
     */
    public static class NodeVisualInfo {
        private SkillNode node;
        private int depth;  // Profondeur dans l'arbre
        private int position;  // Position horizontale

        public NodeVisualInfo(SkillNode node, int depth, int position) {
            this.node = node;
            this.depth = depth;
            this.position = position;
        }

        public SkillNode getNode() {
            return node;
        }

        public int getDepth() {
            return depth;
        }

        public int getPosition() {
            return position;
        }
    }

    // Getters et setters

    public String getId() {
        return id;
    }

    public Skill getSkill() {
        return skill;
    }

    public List<SkillNode> getChildren() {
        return new ArrayList<>(children);
    }

    public List<SkillNode> getPrerequisites() {
        return new ArrayList<>(prerequisites);
    }

    public int getPointsRequired() {
        return pointsRequired;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SkillNode skillNode = (SkillNode) obj;
        return id.equals(skillNode.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}