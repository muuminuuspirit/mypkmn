package game.characters;

import game.core.creature.Creature;
import game.items.Inventory;
import game.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un dresseur de créatures.
 */
public class Trainer {
    private String id;
    private String name;
    private List<Creature> creatures;
    private Inventory inventory;
    private int money;
    private int maxCreatures;

    /**
     * Crée un nouveau dresseur.
     *
     * @param id Identifiant unique
     * @param name Nom du dresseur
     */
    public Trainer(String id, String name) {
        this.id = id;
        this.name = name;
        this.creatures = new ArrayList<>();
        this.inventory = new Inventory();
        this.money = 1000; // Montant de départ
        this.maxCreatures = 6; // Maximum standard
    }

    /**
     * Ajoute une créature à l'équipe du dresseur.
     *
     * @param creature Créature à ajouter
     * @return true si l'ajout a réussi
     */
    public boolean addCreature(Creature creature) {
        if (creatures.size() < maxCreatures) {
            creatures.add(creature);
            return true;
        }
        return false;
    }

    /**
     * Retire une créature de l'équipe du dresseur.
     *
     * @param creature Créature à retirer
     * @return true si le retrait a réussi
     */
    public boolean removeCreature(Creature creature) {
        return creatures.remove(creature);
    }

    /**
     * Retire une créature de l'équipe par son index.
     *
     * @param index Index de la créature
     * @return Créature retirée ou null si échec
     */
    public Creature removeCreature(int index) {
        if (index >= 0 && index < creatures.size()) {
            return creatures.remove(index);
        }
        return null;
    }

    /**
     * Utilise un objet sur une créature.
     *
     * @param item Objet à utiliser
     * @param target Créature ciblée
     * @return true si l'utilisation a réussi
     */
    public boolean useItem(Item item, Creature target) {
        if (inventory.hasItem(item) && target != null) {
            boolean result = item.use(target);
            if (result) {
                inventory.removeItem(item, 1);
            }
            return result;
        }
        return false;
    }

    /**
     * Achète un objet pour de l'argent.
     *
     * @param item Objet à acheter
     * @param quantity Quantité à acheter
     * @param price Prix unitaire
     * @return true si l'achat a réussi
     */
    public boolean buyItem(Item item, int quantity, int price) {
        int totalPrice = price * quantity;
        if (money >= totalPrice) {
            money -= totalPrice;
            inventory.addItem(item, quantity);
            return true;
        }
        return false;
    }

    /**
     * Vend un objet pour de l'argent.
     *
     * @param item Objet à vendre
     * @param quantity Quantité à vendre
     * @param price Prix unitaire
     * @return true si la vente a réussi
     */
    public boolean sellItem(Item item, int quantity, int price) {
        if (inventory.getItemQuantity(item) >= quantity) {
            inventory.removeItem(item, quantity);
            money += price * quantity;
            return true;
        }
        return false;
    }

    /**
     * Guérit toutes les créatures du dresseur.
     */
    public void healAllCreatures() {
        for (Creature creature : creatures) {
            creature.getStats().heal(creature.getStats().getMaxHealth());
            creature.restoreAllActionPoints();
        }
    }

    /**
     * Obtient les créatures actives (non K.O.) du dresseur.
     *
     * @return Liste des créatures actives
     */
    public List<Creature> getActiveCreatures() {
        List<Creature> activeCreatures = new ArrayList<>();
        for (Creature creature : creatures) {
            if (!creature.isDead()) {
                activeCreatures.add(creature);
            }
        }
        return activeCreatures;
    }

    /**
     * Vérifie si toutes les créatures du dresseur sont K.O.
     *
     * @return true si toutes les créatures sont K.O.
     */
    public boolean isDefeated() {
        return getActiveCreatures().isEmpty();
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
    }

    public List<Creature> getCreatures() {
        return new ArrayList<>(creatures);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMaxCreatures() {
        return maxCreatures;
    }

    public void setMaxCreatures(int maxCreatures) {
        this.maxCreatures = maxCreatures;
    }
}