package com.cedric.game.items;

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
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Transfère un objet vers un autre inventaire.
     *
     * @param item Objet à transférer
     * @param quantity Quantité à transférer
     * @param targetInventory Inventaire cible
     * @return true si le transfert a réussi
     */
    public boolean transferItem(Item item, int quantity, Inventory targetInventory) {
        if (quantity <= 0 || !hasItem(item) || getItemQuantity(item) < quantity) {
            return false;
        }

        // Vérifier si l'inventaire cible peut accepter les objets
        if (targetInventory.getAvailableSpace() < quantity) {
            return false;
        }

        // Effectuer le transfert
        if (targetInventory.addItem(item, quantity)) {
            removeItem(item, quantity);
            return true;
        }

        return false;
    }

    /**
     * Fusionne deux objets similaires.
     *
     * @param itemA Premier objet
     * @param itemB Second objet
     * @return true si la fusion a réussi
     */
    public boolean mergeItems(Item itemA, Item itemB) {
        // Pour cette implémentation simplifiée, on suppose que
        // les objets peuvent être fusionnés si ils ont la même catégorie
        if (itemA.getCategory() != itemB.getCategory()) {
            return false;
        }

        // Logique de fusion (exemple basique)
        if (hasItem(itemA) && hasItem(itemB)) {
            int quantityA = getItemQuantity(itemA);
            int quantityB = getItemQuantity(itemB);

            // Supprimer les deux objets
            removeItem(itemA, quantityA);
            removeItem(itemB, quantityB);

            // Ajouter un nouvel objet combiné (pour cet exemple, on ajoute simplement A)
            addItem(itemA, quantityA + quantityB);
            return true;
        }

        return false;
    }

    /**
     * Vérifie si l'inventaire contient tous les objets d'une liste.
     *
     * @param requiredItems Map des objets requis et leurs quantités
     * @return true si tous les objets sont présents en quantité suffisante
     */
    public boolean hasAllItems(Map<Item, Integer> requiredItems) {
        for (Map.Entry<Item, Integer> entry : requiredItems.entrySet()) {
            Item item = entry.getKey();
            int requiredQuantity = entry.getValue();

            if (getItemQuantity(item) < requiredQuantity) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retire plusieurs objets différents en une fois.
     *
     * @param itemsToRemove Map des objets à retirer et leurs quantités
     * @return true si tous les objets ont été retirés
     */
    public boolean removeItems(Map<Item, Integer> itemsToRemove) {
        // Vérifier d'abord si tous les objets sont disponibles
        if (!hasAllItems(itemsToRemove)) {
            return false;
        }

        // Retirer tous les objets
        for (Map.Entry<Item, Integer> entry : itemsToRemove.entrySet()) {
            removeItem(entry.getKey(), entry.getValue());
        }

        return true;
    }

    /**
     * Calcule la valeur totale des objets dans l'inventaire.
     *
     * @return Valeur totale
     */
    public int calculateTotalValue() {
        int totalValue = 0;
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            totalValue += entry.getKey().getValue() * entry.getValue();
        }
        return totalValue;
    }

    /**
     * Recherche des objets par nom ou partie de nom.
     *
     * @param searchTerm Terme de recherche
     * @return Liste des objets correspondants
     */
    public List<Item> searchItems(String searchTerm) {
        List<Item> foundItems = new ArrayList<>();
        String lowerSearchTerm = searchTerm.toLowerCase();

        for (Item item : items.keySet()) {
            if (item.getName().toLowerCase().contains(lowerSearchTerm) ||
                    item.getDescription().toLowerCase().contains(lowerSearchTerm)) {
                foundItems.add(item);
            }
        }

        return foundItems;
    }

    /**
     * Obtient les objets d'une catégorie spécifique.
     *
     * @param category Catégorie recherchée
     * @return Liste des objets de cette catégorie
     */
    public List<Item> getItemsByCategory(ItemCategory category) {
        List<Item> categoryItems = new ArrayList<>();

        for (Item item : items.keySet()) {
            if (item.getCategory() == category) {
                categoryItems.add(item);
            }
        }

        return categoryItems;
    }

    /**
     * Permet à un autre inventaire de copier un objet (pour les objets non consommables).
     *
     * @param item Objet à copier
     * @param targetInventory Inventaire cible
     * @return true si la copie a réussi
     */
    public boolean copyItemTo(Item item, Inventory targetInventory) {
        if (!hasItem(item) || item.isConsumable()) {
            return false; // Ne peut pas copier des objets consommables
        }

        return targetInventory.addItem(item, 1);
    }

    /**
     * Vide complètement l'inventaire.
     */
    public void clear() {
        items.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Inventaire (").append(getTotalItems()).append("/").append(maxCapacity).append("):\n");

        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            sb.append("- ").append(entry.getKey().getName())
                    .append(" x").append(entry.getValue())
                    .append(" (").append(entry.getKey().getCategory().getName()).append(")\n");
        }

        return sb.toString();
    }
}