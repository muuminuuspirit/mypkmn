package com.cedric.game.core;

import com.cedric.game.characters.PlayerTrainer;
import com.cedric.game.core.creature.Creature;
import com.cedric.game.items.Item;
import com.cedric.game.items.Inventory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Système de sauvegarde et chargement pour le jeu.
 */
public class SaveSystem {
    private static final String SAVE_DIRECTORY = "saves/";

    /**
     * Initialise le système de sauvegarde.
     */
    public static void initialize() {
        // Créer le répertoire de sauvegarde s'il n'existe pas
        File saveDir = new File(SAVE_DIRECTORY);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
    }

    /**
     * Sauvegarde l'état actuel du jeu.
     *
     * @param gameManager Gestionnaire de jeu à sauvegarder
     * @param saveName Nom de la sauvegarde
     * @return true si la sauvegarde a réussi
     */
    public static boolean saveGame(GameManager gameManager, String saveName) {
        if (gameManager == null || saveName == null || saveName.isEmpty()) {
            return false;
        }

        // Créer un objet de données de sauvegarde
        SaveData saveData = new SaveData();

        // Sauvegarder les données du joueur
        PlayerTrainer player = gameManager.getPlayer();
        saveData.playerName = player.getName();
        saveData.playerID = player.getId();
        saveData.playerMoney = player.getMoney();
        saveData.playerBadges = player.getBadges();
        saveData.playerRank = player.getPlayerRank();
        saveData.playerReputation = player.getReputation();
        saveData.maxCreatures = player.getMaxCreatures();

        // Sauvegarder les créatures du joueur
        saveData.creatures = serializeCreatures(player.getCreatures());

        // Sauvegarder l'inventaire
        saveData.inventory = serializeInventory(player.getInventory());

        // Sauvegarder les PNJ et autres données
        saveData.npcs = serializeNPCs(gameManager.getNpcs());

        // Créer le fichier de sauvegarde
        String filePath = SAVE_DIRECTORY + saveName + ".save";

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(saveData);
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde: " + e.getMessage());
            return false;
        }
    }

    /**
     * Charge une sauvegarde.
     *
     * @param saveName Nom de la sauvegarde à charger
     * @return GameManager initialisé ou null si échec
     */
    public static GameManager loadGame(String saveName) {
        if (saveName == null || saveName.isEmpty()) {
            return null;
        }

        String filePath = SAVE_DIRECTORY + saveName + ".save";
        File saveFile = new File(filePath);

        if (!saveFile.exists() || !saveFile.isFile()) {
            System.err.println("Fichier de sauvegarde introuvable: " + filePath);
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            SaveData saveData = (SaveData) ois.readObject();

            // Créer un nouveau gestionnaire de jeu
            GameManager gameManager = GameManager.getInstance();

            // Recréer le joueur
            PlayerTrainer player = new PlayerTrainer(saveData.playerID, saveData.playerName);
            player.setMoney(saveData.playerMoney);

            // Définir les attributs spécifiques au joueur via la réflexion
            // ou des méthodes spécifiques (simplifié pour l'exemple)

            // Restaurer les créatures
            List<Creature> creatures = deserializeCreatures(saveData.creatures);
            for (Creature creature : creatures) {
                player.addCreature(creature);
            }

            // Restaurer l'inventaire
            Inventory inventory = deserializeInventory(saveData.inventory);
            // Remplacer l'inventaire du joueur (méthode à ajouter à PlayerTrainer)

            // Restaurer les PNJ
            List<game.characters.IATrainer> npcs = deserializeNPCs(saveData.npcs);

            // Configurer le gestionnaire avec les données chargées
            // (Ces méthodes devront être implémentées dans GameManager)
            gameManager.setPlayer(player);
            gameManager.setNpcs(npcs);

            return gameManager;

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement: " + e.getMessage());
            return null;
        }
    }

    /**
     * Liste toutes les sauvegardes disponibles.
     *
     * @return Liste des noms de sauvegarde
     */
    public static List<String> listSaves() {
        List<String> saveNames = new ArrayList<>();
        File saveDir = new File(SAVE_DIRECTORY);

        if (!saveDir.exists() || !saveDir.isDirectory()) {
            return saveNames;
        }

        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".save"));

        if (saveFiles != null) {
            for (File file : saveFiles) {
                String fileName = file.getName();
                saveNames.add(fileName.substring(0, fileName.length() - 5));
            }
        }

        return saveNames;
    }

    /**
     * Supprime une sauvegarde.
     *
     * @param saveName Nom de la sauvegarde à supprimer
     * @return true si la suppression a réussi
     */
    public static boolean deleteSave(String saveName) {
        if (saveName == null || saveName.isEmpty()) {
            return false;
        }

        String filePath = SAVE_DIRECTORY + saveName + ".save";
        File saveFile = new File(filePath);

        return saveFile.exists() && saveFile.isFile() && saveFile.delete();
    }

    /**
     * Sérialise les créatures pour la sauvegarde.
     */
    private static List<Map<String, Object>> serializeCreatures(List<Creature> creatures) {
        List<Map<String, Object>> serializedCreatures = new ArrayList<>();

        for (Creature creature : creatures) {
            Map<String, Object> creatureData = new HashMap<>();
            creatureData.put("id", creature.getId());
            creatureData.put("name", creature.getName());
            creatureData.put("level", creature.getStats().getLevel());
            creatureData.put("health", creature.getStats().getHealth());
            creatureData.put("maxHealth", creature.getStats().getMaxHealth());
            creatureData.put("xp", creature.getStats().getXp());
            creatureData.put("strength", creature.getStats().getStrength());
            creatureData.put("constitution", creature.getStats().getConstitution());
            creatureData.put("spirit", creature.getStats().getSpirit());
            creatureData.put("mental", creature.getStats().getMental());
            creatureData.put("speed", creature.getStats().getSpeed());

            // Sauvegarder les types
            List<String> types = new ArrayList<>();
            for (game.core.type.Type type : creature.getStats().getTypes()) {
                types.add(type.getName());
            }
            creatureData.put("types", types);

            // Sauvegarder les compétences équipées
            List<String> equippedSkills = new ArrayList<>();
            for (game.core.skill.Skill skill : creature.getActiveSkills()) {
                equippedSkills.add(skill.getId());
            }
            creatureData.put("equippedSkills", equippedSkills);

            // Sauvegarder les noeuds de compétence débloqués
            List<String> unlockedNodes = new ArrayList<>();
            for (game.core.skill.SkillNode node : creature.getSkillTree().getAllNodes()) {
                if (node.isUnlocked()) {
                    unlockedNodes.add(node.getId());
                }
            }
            creatureData.put("unlockedNodes", unlockedNodes);

            serializedCreatures.add(creatureData);
        }

        return serializedCreatures;
    }

    /**
     * Sérialise l'inventaire pour la sauvegarde.
     */
    private static Map<String, Integer> serializeInventory(Inventory inventory) {
        Map<String, Integer> itemData = new HashMap<>();

        for (Item item : inventory.getItems()) {
            itemData.put(item.getId(), inventory.getItemQuantity(item));
        }

        return itemData;
    }

    /**
     * Sérialise les PNJ pour la sauvegarde.
     */
    private static List<Map<String, Object>> serializeNPCs(List<game.characters.IATrainer> npcs) {
        List<Map<String, Object>> serializedNPCs = new ArrayList<>();

        for (game.characters.IATrainer npc : npcs) {
            Map<String, Object> npcData = new HashMap<>();
            npcData.put("id", npc.getId());
            npcData.put("name", npc.getName());
            npcData.put("difficulty", npc.getDifficulty());
            npcData.put("trainerType", npc.getTrainerType());
            npcData.put("strategyType", npc.getStrategyType());
            npcData.put("isGymLeader", npc.isGymLeader());

            // Sauvegarder les créatures du PNJ
            npcData.put("creatures", serializeCreatures(npc.getCreatures()));

            serializedNPCs.add(npcData);
        }

        return serializedNPCs;
    }

    /**
     * Désérialise les créatures lors du chargement.
     */
    private static List<Creature> deserializeCreatures(List<Map<String, Object>> serializedCreatures) {
        List<Creature> creatures = new ArrayList<>();

        // Cette implémentation est une simplification
        // Une mise en œuvre complète nécessiterait de recréer exactement
        // toutes les attributs et états des créatures

        for (Map<String, Object> creatureData : serializedCreatures) {
            String id = (String) creatureData.get("id");
            String name = (String) creatureData.get("name");

            // Récupérer le type principal
            List<String> typeNames = (List<String>) creatureData.get("types");
            game.core.type.Type primaryType = null;

            // Chercher le type correspondant
            List<game.core.type.Type> availableTypes = game.core.type.Type.createDefaultTypes();
            for (game.core.type.Type type : availableTypes) {
                if (type.getName().equals(typeNames.get(0))) {
                    primaryType = type;
                    break;
                }
            }

            if (primaryType == null) {
                // Type par défaut si introuvable
                primaryType = availableTypes.get(0);
            }

            // Créer la créature de base
            Creature creature = new Creature(id, name, primaryType);

            // Ajouter les types secondaires
            for (int i = 1; i < typeNames.size(); i++) {
                for (game.core.type.Type type : availableTypes) {
                    if (type.getName().equals(typeNames.get(i))) {
                        creature.getStats().addType(type);
                        break;
                    }
                }
            }

            // Définir les statistiques
            int level = ((Number) creatureData.get("level")).intValue();
            int currentXP = ((Number) creatureData.get("xp")).intValue();

            // Simuler la montée en niveau
            for (int i = 1; i < level; i++) {
                creature.getStats().gainExperience(creature.getStats().getMaxXpNextLevel());
            }

            // Définir l'XP actuel
            // Note: ceci est simplifié et pourrait ne pas être exact

            // Débloquer les nœuds de compétence
            List<String> unlockedNodes = (List<String>) creatureData.get("unlockedNodes");
            for (String nodeId : unlockedNodes) {
                creature.unlockSkillNode(nodeId);
            }

            // Équiper les compétences
            List<String> equippedSkills = (List<String>) creatureData.get("equippedSkills");
            for (String skillId : equippedSkills) {
                for (game.core.skill.Skill skill : creature.getSkillTree().getUnlockedSkills()) {
                    if (skill.getId().equals(skillId)) {
                        creature.equipSkill(skill);
                        break;
                    }
                }
            }

            // Définir les PV actuels
            int health = ((Number) creatureData.get("health")).intValue();
            creature.getStats().setHealth(health);

            creatures.add(creature);
        }

        return creatures;
    }

    /**
     * Désérialise l'inventaire lors du chargement.
     */
    private static Inventory deserializeInventory(Map<String, Integer> itemData) {
        Inventory inventory = new Inventory();

        // Une implémentation complète nécessiterait un registre d'objets
        // pour recréer les objets à partir de leurs ID

        // Exemple simplifié:
        // Pour chaque ID d'objet, créer l'objet correspondant et l'ajouter
        // à l'inventaire avec la quantité spécifiée

        return inventory;
    }

    /**
     * Désérialise les PNJ lors du chargement.
     */
    private static List<game.characters.IATrainer> deserializeNPCs(List<Map<String, Object>> serializedNPCs) {
        List<game.characters.IATrainer> npcs = new ArrayList<>();

        for (Map<String, Object> npcData : serializedNPCs) {
            String id = (String) npcData.get("id");
            String name = (String) npcData.get("name");
            int difficulty = ((Number) npcData.get("difficulty")).intValue();
            String trainerType = (String) npcData.get("trainerType");
            String strategyType = (String) npcData.get("strategyType");
            boolean isGymLeader = (Boolean) npcData.get("isGymLeader");

            game.characters.IATrainer npc = new game.characters.IATrainer(
                    id, name, difficulty, trainerType, strategyType, isGymLeader
            );

            // Ajouter les créatures
            List<Creature> creatures = deserializeCreatures(
                    (List<Map<String, Object>>) npcData.get("creatures")
            );

            for (Creature creature : creatures) {
                npc.addCreature(creature);
            }

            npcs.add(npc);
        }

        return npcs;
    }

    /**
     * Classe interne pour stocker les données de sauvegarde.
     */
    private static class SaveData implements Serializable {
        private static final long serialVersionUID = 1L;

        // Données du joueur
        public String playerName;
        public String playerID;
        public int playerMoney;
        public int playerBadges;
        public int playerRank;
        public int playerReputation;
        public int maxCreatures;

        // Données des créatures
        public List<Map<String, Object>> creatures;

        // Données d'inventaire
        public Map<String, Integer> inventory;

        // Données des PNJ
        public List<Map<String, Object>> npcs;
    }
}