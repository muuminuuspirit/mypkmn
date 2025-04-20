package game.items;

import game.core.creature.Creature;
import game.characters.Trainer;

/**
 * Représente un objet du jeu.
 */
public class Item {
    private String id;
    private String name;
    private String description;
    private int value;
    private ItemCategory category;
    private boolean consumable;

    /**
     * Crée un nouvel objet.
     *
     * @param id Identifiant unique
     * @param name Nom de l'objet
     * @param description Description de l'objet
     * @param value Valeur de base (prix)
     * @param category Catégorie de l'objet
     * @param consumable Si l'objet est consommable
     */
    public Item(String id, String name, String description, int value, ItemCategory category, boolean consumable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = value;
        this.category = category;
        this.consumable = consumable;
    }

    /**
     * Utilise l'objet sur une créature.
     *
     * @param target Créature cible
     * @return true si l'utilisation a réussi
     */
    public boolean use(Creature target) {
        if (target == null) {
            return false;
        }

        // Comportement par défaut, à surcharger dans les classes dérivées
        return onUse(target);
    }

    /**
     * Méthode appelée lors de l'utilisation de l'objet.
     * À surcharger dans les classes dérivées.
     *
     * @param target Créature cible
     * @return true si l'effet a été appliqué
     */
    protected boolean onUse(Creature target) {
        // Par défaut, aucun effet
        return false;
    }

    /**
     * Utilise l'objet par un dresseur.
     *
     * @param trainer Dresseur utilisant l'objet
     * @return true si l'utilisation a réussi
     */
    public boolean useByTrainer(Trainer trainer) {
        if (trainer == null) {
            return false;
        }

        // Comportement par défaut, à surcharger dans les classes dérivées
        return onUseByTrainer(trainer);
    }

    /**
     * Méthode appelée lors de l'utilisation de l'objet par un dresseur.
     * À surcharger dans les classes dérivées.
     *
     * @param trainer Dresseur utilisant l'objet
     * @return true si l'effet a été appliqué
     */
    protected boolean onUseByTrainer(Trainer trainer) {
        // Par défaut, aucun effet
        return false;
    }

    /**
     * Vérifie si l'objet peut être utilisé en combat.
     *
     * @return true si utilisable en combat
     */
    public boolean canUseInBattle() {
        return category == ItemCategory.COMBAT || category == ItemCategory.HEALING;
    }

    /**
     * Crée une potion de soin.
     *
     * @param id Identifiant unique
     * @param name Nom de la potion
     * @param healAmount Quantité de PV restaurés
     * @param value Prix de la potion
     * @return Potion créée
     */
    public static Item createHealingPotion(String id, String name, int healAmount, int value) {
        return new Item(id, name, "Restaure " + healAmount + " PV.", value, ItemCategory.HEALING, true) {
            @Override
            protected boolean onUse(Creature target) {
                if (target.isDead()) {
                    return false;
                }

                target.getStats().heal(healAmount);
                return true;
            }
        };
    }

    /**
     * Crée une potion de restauration de PA.
     *
     * @param id Identifiant unique
     * @param name Nom de la potion
     * @param apAmount Quantité de PA restaurés
     * @param value Prix de la potion
     * @return Potion créée
     */
    public static Item createAPPotion(String id, String name, int apAmount, int value) {
        return new Item(id, name, "Restaure " + apAmount + " PA.", value, ItemCategory.HEALING, true) {
            @Override
            protected boolean onUse(Creature target) {
                if (target.isDead()) {
                    return false;
                }

                target.restoreActionPoints(apAmount);
                return true;
            }
        };
    }

    /**
     * Crée une pierre d'évolution.
     *
     * @param id Identifiant unique
     * @param name Nom de la pierre
     * @param typeName Type associé
     * @param value Prix de la pierre
     * @return Pierre créée
     */
    public static Item createEvolutionStone(String id, String name, String typeName, int value) {
        return new Item(id, name, "Pierre permettant l'évolution de certaines créatures.",
                value, ItemCategory.EVOLUTION, false) {
            // L'évolution est un processus complexe qui serait géré ailleurs
        };
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public boolean isConsumable() {
        return consumable;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}