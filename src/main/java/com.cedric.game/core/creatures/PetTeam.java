package game.core.creature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Représente une équipe de créatures.
 */
public class PetTeam {
    private List<Creature> creatures;
    private int maxSize;
    private String name;
    private Creature activeCreature;

    /**
     * Crée une nouvelle équipe avec une taille maximale standard.
     */
    public PetTeam() {
        this("Équipe standard", 6);
    }

    /**
     * Crée une nouvelle équipe avec un nom et une taille maximale spécifiés.
     *
     * @param name Nom de l'équipe
     * @param maxSize Taille maximale
     */
    public PetTeam(String name, int maxSize) {
        this.name = name;
        this.maxSize = maxSize;
        this.creatures = new ArrayList<>();
        this.activeCreature = null;
    }

    /**
     * Ajoute une créature à l'équipe.
     *
     * @param creature Créature à ajouter
     * @return true si l'ajout a réussi
     */
    public boolean addCreature(Creature creature) {
        if (creature == null || creatures.size() >= maxSize) {
            return false;
        }

        if (creatures.contains(creature)) {
            return false;  // Éviter les doublons
        }

        creatures.add(creature);

        // Si c'est la première créature, la définir comme active
        if (creatures.size() == 1) {
            activeCreature = creature;
        }

        return true;
    }

    /**
     * Retire une créature de l'équipe.
     *
     * @param creature Créature à retirer
     * @return true si le retrait a réussi
     */
    public boolean removeCreature(Creature creature) {
        if (creature == null || !creatures.contains(creature)) {
            return false;
        }

        boolean wasActive = (creature == activeCreature);
        creatures.remove(creature);

        // Si la créature active a été retirée, en choisir une nouvelle
        if (wasActive && !creatures.isEmpty()) {
            activeCreature = creatures.get(0);
        } else if (wasActive) {
            activeCreature = null;
        }

        return true;
    }

    /**
     * Définit la créature active.
     *
     * @param creature Créature à définir comme active
     * @return true si le changement a réussi
     */
    public boolean setActiveCreature(Creature creature) {
        if (creature == null || !creatures.contains(creature)) {
            return false;
        }

        activeCreature = creature;
        return true;
    }

    /**
     * Définit la créature active par son index.
     *
     * @param index Index de la créature
     * @return true si le changement a réussi
     */
    public boolean setActiveCreature(int index) {
        if (index < 0 || index >= creatures.size()) {
            return false;
        }

        activeCreature = creatures.get(index);
        return true;
    }

    /**
     * Trie les créatures selon un critère.
     *
     * @param criteria Critère de tri (name, level, health, etc.)
     * @param ascending true pour tri ascendant, false pour descendant
     */
    public void sortCreatures(String criteria, boolean ascending) {
        Comparator<Creature> comparator;

        switch (criteria.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(Creature::getName);
                break;
            case "level":
                comparator = Comparator.comparing(c -> c.getStats().getLevel());
                break;
            case "health":
                comparator = Comparator.comparing(c -> c.getStats().getHealth());
                break;
            case "type":
                comparator = Comparator.comparing(c -> c.getStats().getTypes().get(0).getName());
                break;
            default:
                return; // Critère non reconnu
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        Collections.sort(creatures, comparator);
    }

    /**
     * Soigne toutes les créatures de l'équipe.
     */
    public void healAllCreatures() {
        for (Creature creature : creatures) {
            creature.getStats().heal(creature.getStats().getMaxHealth());
            creature.restoreAllActionPoints();
        }
    }

    /**
     * Vérifie si toutes les créatures sont K.O.
     *
     * @return true si toutes les créatures sont K.O.
     */
    public boolean isAllDefeated() {
        for (Creature creature : creatures) {
            if (!creature.isDead()) {
                return false;
            }
        }
        return !creatures.isEmpty(); // Vrai seulement si l'équipe n'est pas vide
    }

    /**
     * Obtient le niveau moyen de l'équipe.
     *
     * @return Niveau moyen ou 0 si l'équipe est vide
     */
    public double getAverageLevel() {
        if (creatures.isEmpty()) {
            return 0;
        }

        int totalLevel = 0;
        for (Creature creature : creatures) {
            totalLevel += creature.getStats().getLevel();
        }

        return (double) totalLevel / creatures.size();
    }

    /**
     * Obtient les créatures par type.
     *
     * @param typeName Nom du type recherché
     * @return Liste des créatures du type spécifié
     */
    public List<Creature> getCreaturesByType(String typeName) {
        List<Creature> result = new ArrayList<>();

        for (Creature creature : creatures) {
            for (game.core.type.Type type : creature.getStats().getTypes()) {
                if (type.getName().equalsIgnoreCase(typeName)) {
                    result.add(creature);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Trouve la meilleure créature contre un type spécifique.
     *
     * @param typeName Nom du type adverse
     * @return Meilleure créature ou null si aucune trouvée
     */
    public Creature getBestCreatureAgainstType(String typeName) {
        if (creatures.isEmpty()) {
            return null;
        }

        Creature bestCreature = null;
        double bestEffectiveness = 0;

        for (Creature creature : creatures) {
            if (creature.isDead()) {
                continue;
            }

            double effectiveness = 1.0;
            for (game.core.type.Type type : creature.getStats().getTypes()) {
                for (game.core.type.Type availableType : game.core.type.Type.createDefaultTypes()) {
                    if (availableType.getName().equalsIgnoreCase(typeName)) {
                        effectiveness *= type.getEffectivenessAgainst(availableType);
                    }
                }
            }

            if (bestCreature == null || effectiveness > bestEffectiveness) {
                bestCreature = creature;
                bestEffectiveness = effectiveness;
            }
        }

        return bestCreature;
    }

    // Getters et setters

    public List<Creature> getCreatures() {
        return new ArrayList<>(creatures);
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Creature getActiveCreature() {
        return activeCreature;
    }

    public int getCurrentSize() {
        return creatures.size();
    }

    public boolean isFull() {
        return creatures.size() >= maxSize;
    }

    public List<Creature> getActiveCreatures() {
        List<Creature> activeCreatures = new ArrayList<>();
        for (Creature creature : creatures) {
            if (!creature.isDead()) {
                activeCreatures.add(creature);
            }
        }
        return activeCreatures;
    }
}