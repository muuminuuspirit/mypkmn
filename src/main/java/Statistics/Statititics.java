package Statistics;

import java.util.ArrayList;

public class Statititics {
    private String name;
    private String description;
    private int level;
    private int xp;
    private int maxXpNextLevel;
    private int health;
    private int maxHealth;
    private int vitality;
    private int maxVitality;
    private int vitalEnergy;
    private int maxVitalEnergy;
    private int strength;
    private int constitution;
    private int spirit;
    private int mental;
    private int speed;
    private List<Type> types;

    public Statistics() {
        this.level = 1;
        this.xp = 0;
        this.maxXpNextLevel = 100;
        this.types = new ArrayList<>();
    }

    public boolean gainExperience(int amount) {
        this.xp += amount;
        if (this.xp >= this.maxXpNextLevel) {
            levelUp();
            return true;
        }
        return false;
    }

    private void levelUp() {
        this.level++;
        this.xp -= this.maxXpNextLevel;
        this.maxXpNextLevel = calculateNextLevelXp();

        // Augmentation des stats
        this.maxHealth += 5 + (constitution / 2);
        this.health = this.maxHealth;
        this.maxVitality += 3 + (spirit / 3);
        this.vitality = this.maxVitality;
        this.maxVitalEnergy += 2 + (mental / 2);
        this.vitalEnergy = this.maxVitalEnergy;

        // Augmentation aléatoire d'autres stats
        this.strength += 1 + (int)(Math.random() * 2);
        this.constitution += 1 + (int)(Math.random() * 2);
        this.spirit += 1 + (int)(Math.random() * 2);
        this.mental += 1 + (int)(Math.random() * 2);
        this.speed += 1 + (int)(Math.random() * 2);
    }

    private int calculateNextLevelXp() {
        return this.maxXpNextLevel += (20 * this.level);
    }

    public void addType(Type type) {
        if (!this.types.contains(type)) {
            this.types.add(type);
        }
    }

    public void generateRandomSecondaryType(List<Type> availableTypes) {
        if (this.types.size() >= 2) {
            return; // Déjà deux types ou plus
        }

        if (this.types.isEmpty()) {
            return; // Pas de type principal défini
        }

        // Filtrer les types disponibles pour exclure le type principal
        List<Type> filteredTypes = new ArrayList<>();
        Type primaryType = this.types.get(0);

        for (Type type : availableTypes) {
            if (!type.equals(primaryType)) {
                filteredTypes.add(type);
            }
        }

        if (!filteredTypes.isEmpty()) {
            // Sélectionner un type aléatoire
            int randomIndex = (int)(Math.random() * filteredTypes.size());
            addType(filteredTypes.get(randomIndex));
        }
    }

    // Getters and setters
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

    public int getLevel() {
        return level;
    }

    public List<Type> getTypes() {
        return new ArrayList<>(types); // Retourne une copie pour encapsulation
    }
}
