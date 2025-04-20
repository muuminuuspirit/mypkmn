package com.cedric.game.items;

/**
 * Énumération des catégories d'objets.
 */
public enum ItemCategory {
    HEALING("Soin", "Objets pour soigner les créatures"),
    COMBAT("Combat", "Objets utilisables en combat"),
    CAPTURE("Capture", "Objets pour capturer des créatures"),
    EVOLUTION("Évolution", "Objets pour faire évoluer les créatures"),
    KEY("Clé", "Objets importants pour la progression"),
    TOOLS("Outils", "Outils divers"),
    HOLD("Support", "Objets qu'une créature peut tenir"),
    TREASURE("Trésor", "Objets de valeur à vendre");

    private final String name;
    private final String description;

    /**
     * Crée une nouvelle catégorie d'objets.
     *
     * @param name Nom de la catégorie
     * @param description Description de la catégorie
     */
    ItemCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Obtient le nom de la catégorie.
     *
     * @return Nom de la catégorie
     */
    public String getName() {
        return name;
    }

    /**
     * Obtient la description de la catégorie.
     *
     * @return Description de la catégorie
     */
    public String getDescription() {
        return description;
    }
}