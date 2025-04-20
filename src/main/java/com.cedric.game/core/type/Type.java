package game.core.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Représente un type élémental dans le jeu.
 */
public class Type {
    private String name;
    private String description;
    private Map<String, Double> effectivenessChart; // Stocke l'efficacité contre d'autres types

    /**
     * Crée un nouveau type.
     *
     * @param name Nom du type
     * @param description Description du type
     */
    public Type(String name, String description) {
        this.name = name;
        this.description = description;
        this.effectivenessChart = new HashMap<>();
    }

    /**
     * Définit l'efficacité de ce type contre un autre type.
     *
     * @param targetTypeName Nom du type cible
     * @param multiplier Multiplicateur d'efficacité (2.0 = super efficace, 0.5 = pas très efficace, 0.0 = aucun effet)
     */
    public void setEffectivenessAgainst(String targetTypeName, double multiplier) {
        effectivenessChart.put(targetTypeName, multiplier);
    }

    /**
     * Récupère l'efficacité de ce type contre un autre.
     *
     * @param targetType Type cible
     * @return Multiplicateur d'efficacité (1.0 par défaut si non spécifié)
     */
    public double getEffectivenessAgainst(Type targetType) {
        if (targetType == null) {
            return 1.0;
        }

        Double effectiveness = effectivenessChart.get(targetType.getName());
        return effectiveness != null ? effectiveness : 1.0;
    }

    /**
     * Crée des types prédéfinis avec leurs relations.
     *
     * @return Liste des types de base du jeu
     */
    public static List<Type> createDefaultTypes() {
        List<Type> types = new ArrayList<>();

        // Créer les types de base
        Type fire = new Type("Feu", "Type feu, puissant mais volatile");
        Type water = new Type("Eau", "Type eau, fluide et adaptable");
        Type nature = new Type("Nature", "Type plante, régénératif et patient");
        Type electric = new Type("Électrique", "Type électrique, rapide et imprévisible");
        Type ground = new Type("Sol", "Type sol, stable et résistant");
        Type air = new Type("Air", "Type air, agile et insaisissable");
        Type psychic = new Type("Psychique", "Type psychique, puissant mentalement");
        Type dark = new Type("Ténèbres", "Type ténèbres, furtif et rusé");
        Type light = new Type("Lumière", "Type lumière, perçant et révélateur");
        Type metal = new Type("Métal", "Type métal, solide et résistant");

        // Définir les relations d'efficacité

        // Feu
        fire.setEffectivenessAgainst("Nature", 2.0);
        fire.setEffectivenessAgainst("Glace", 2.0);
        fire.setEffectivenessAgainst("Métal", 2.0);
        fire.setEffectivenessAgainst("Eau", 0.5);
        fire.setEffectivenessAgainst("Sol", 0.5);

        // Eau
        water.setEffectivenessAgainst("Feu", 2.0);
        water.setEffectivenessAgainst("Sol", 2.0);
        water.setEffectivenessAgainst("Nature", 0.5);
        water.setEffectivenessAgainst("Électrique", 0.5);

        // Nature
        nature.setEffectivenessAgainst("Eau", 2.0);
        nature.setEffectivenessAgainst("Sol", 2.0);
        nature.setEffectivenessAgainst("Feu", 0.5);
        nature.setEffectivenessAgainst("Air", 0.5);

        // Électrique
        electric.setEffectivenessAgainst("Eau", 2.0);
        electric.setEffectivenessAgainst("Air", 2.0);
        electric.setEffectivenessAgainst("Sol", 0.0);
        electric.setEffectivenessAgainst("Nature", 0.5);

        // Sol
        ground.setEffectivenessAgainst("Feu", 2.0);
        ground.setEffectivenessAgainst("Électrique", 2.0);
        ground.setEffectivenessAgainst("Métal", 2.0);
        ground.setEffectivenessAgainst("Air", 0.0);
        ground.setEffectivenessAgainst("Nature", 0.5);

        // Air
        air.setEffectivenessAgainst("Nature", 2.0);
        air.setEffectivenessAgainst("Sol", 2.0);
        air.setEffectivenessAgainst("Électrique", 0.5);

        // Psychique
        psychic.setEffectivenessAgainst("Ténèbres", 0.0);
        psychic.setEffectivenessAgainst("Métal", 0.5);
        psychic.setEffectivenessAgainst("Lumière", 0.5);

        // Ténèbres
        dark.setEffectivenessAgainst("Psychique", 2.0);
        dark.setEffectivenessAgainst("Lumière", 0.5);

        // Lumière
        light.setEffectivenessAgainst("Ténèbres", 2.0);
        light.setEffectivenessAgainst("Psychique", 0.5);

        // Métal
        metal.setEffectivenessAgainst("Nature", 2.0);
        metal.setEffectivenessAgainst("Feu", 0.5);
        metal.setEffectivenessAgainst("Sol", 0.5);

        // Ajouter tous les types à la liste
        types.add(fire);
        types.add(water);
        types.add(nature);
        types.add(electric);
        types.add(ground);
        types.add(air);
        types.add(psychic);
        types.add(dark);
        types.add(light);
        types.add(metal);

        return types;
    }

    // Getters et setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Type type = (Type) obj;
        return name.equals(type.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}