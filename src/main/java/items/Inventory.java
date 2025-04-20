package game.items;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente l'inventaire d'un dresseur.
 */
public class Inventory {
    private Map<Item, Integer> items;
    private int maxCapacity;

    /**
     * Crée un nouvel inventaire avec une capacité standard.
     */
    public Inventory() {
        this(50); // Capacité par défaut
    }

    /**
     * Crée un nouvel inventaire avec une capacité spécifiée.
     *
     * @param maxCapacity Capacité maximale
     */
    public Inventory(int maxCapacity) {
        this.items = new HashMap<>();
        this.maxCapacity = maxCapacity;
    }

    /**
     * Ajoute un objet à l'inventaire.
     *
     * @param item Objet à ajouter
     * @param quantity Quantité à ajouter
     * @return true si l'ajout a réussi
     */
    public boolean addItem(Item item, int quantity) {
        if (quantity <= 0) {
            return false;
        }

        int totalItems = getTotalItems();
        int currentQuantity = items.getOrDefault(item, 0);

        // Vérifier si l'ajout dépasserait la capacité
        if (totalItems - currentQuantity + quantity > maxCapacity) {
            return false;
        }

        // Ajouter ou mettre à jour la quantité
        items.put(item, currentQuantity + quantity);
        return true;
    }

    /**
     * Retire un objet de l'inventaire.
     *
     * @param item Objet à retirer
     * @param quantity Quantité à retirer
     * @return true si le retrait a réussi
     */
    public boolean removeItem(Item item, int quantity) {
        if (quantity <= 0) {
            return false;
        }

        int currentQuantity = items.getOrDefault(item, 0);

        if (currentQuantity < quantity) {
            return false;
        }

        if (currentQuantity == quantity) {
            items.remove(item);
        } else {
            items.put(item, currentQuantity - quantity);
        }

        return true;
    }

    /**
     * Vérifie si l'inventaire contient un objet spécifique.
     *
     * @param item Objet à vérifier
     * @return true si l'objet est présent
     */
    public boolean hasItem(Item item) {
        return items.containsKey(item) && items.get(item) > 0;
    }

    /**
     * Obtient la quantité d'un objet dans l'inventaire.
     *
     * @param item Objet à vérifier
     * @return Quantité de l'objet
     */
    public int getItemQuantity(Item item) {
        return items.getOrDefault(item, 0);
    }

    /**
     * Obtient la liste des objets dans l'inventaire.
     *
     * @return Liste des objets
     */
    public List<Item> getItems() {
        return new ArrayList<>(items.keySet());
    }

    /**
     * Obtient la liste des objets avec leurs quantités.
     *
     * @return Map des objets et quantités
     */
    public Map<Item, Integer> getItemsWithQuantities() {
        return new HashMap<>(items);
    }

    /**
     * Obtient le nombre total d'objets dans l'inventaire.
     *
     * @return Nombre total d'objets
     */
    public int getTotalItems() {
        int total = 0;
        for (int quantity : items.values()) {
            total += quantity;
        }
        return total;
    }

    /**
     * Vérifie si l'inventaire est plein.
     *
     * @return true si l'inventaire est plein
     */
    public boolean isFull() {
        return getTotalItems() >= maxCapacity;
    }

    /**
     * Obtient l'espace disponible dans l'inventaire.
     *
     * @return Nombre d'emplacements disponibles
     */
    public int getAvailableSpace() {
        return maxCapacity - getTotalItems();
    }

    /**
     * Trie les objets par catégorie.
     *
     * @return Map des objets triés par catégorie
     */
    public Map<ItemCategory, List<Item>> getItemsByCategory() {
        Map<ItemCategory, List<Item>> categorizedItems = new HashMap<>();

        for (Item item : items.keySet()) {
            ItemCategory category = item.getCategory();
            if (!categorizedItems.containsKey(category)) {
                categorizedItems.put(category, new ArrayList<>());
            }
            categorizedItems.get(category).add(item);
        }

        return categorizedItems;
    }

    /**
     * Augmente la capacité maximale de l'inventaire.
     *
     * @param additionalCapacity Capacité supplémentaire
     */
    public void increaseCapacity(int additionalCapacity) {
        if (additionalCapacity > 0) {
            this.maxCapacity += additionalCapacity;
        }
    }

    /**
     * Obtient la capacité maximale de l'inventaire.
     *
     * @return Capacité maximale
     */
    public int get