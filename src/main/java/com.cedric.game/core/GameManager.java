package game.core;

import game.characters.PlayerTrainer;
import game.characters.IATrainer;
import game.core.battle.Battle;
import game.core.battle.BattleManager;
import game.core.creature.Creature;
import game.core.type.Type;
import game.items.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gestionnaire principal du jeu, coordonne les différents systèmes.
 */
public class GameManager {
    private static GameManager instance;

    private PlayerTrainer player;
    private BattleManager battleManager;
    private List<Type> availableTypes;
    private List<IATrainer> npcs;
    private Random random;

    /**
     * Crée une nouvelle instance du gestionnaire de jeu.
     * Constructeur privé pour le singleton.
     */
    private GameManager() {
        this.battleManager = new BattleManager();
        this.availableTypes = Type.createDefaultTypes();
        this.npcs = new ArrayList<>();
        this.random = new Random();
    }

    /**
     * Obtient l'instance unique du gestionnaire.
     *
     * @return Instance du GameManager
     */
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * Initialise une nouvelle partie.
     *
     * @param playerName Nom du joueur
     * @param starterCreature Créature de départ
     */
    public void initializeNewGame(String playerName, Creature starterCreature) {
        this.player = new PlayerTrainer("player_" + System.currentTimeMillis(), playerName);

        // Ajouter la créature de départ
        if (starterCreature != null) {
            player.addCreature(starterCreature);
        }

        // Ajouter des objets de départ
        Item potion = Item.createHealingPotion("potion_small", "Petite Potion", 20, 100);
        player.getInventory().addItem(potion, 3);

        // Initialiser les PNJ
        initializeNPCs();
    }

    /**
     * Initialise les PNJ du jeu.
     */
    private void initializeNPCs() {
        // Créer quelques dresseurs IA pour les tests
        npcs.clear();

        // Exemple: Novice
        IATrainer novice = new IATrainer("trainer_novice", "Novice Nathan",
                2, "Novice", "Aléatoire", false);
        Creature noviceCreature = generateRandomCreature("raccoon_01", "Racoono", 5);
        novice.addCreature(noviceCreature);
        npcs.add(novice);

        // Exemple: Scientifique
        IATrainer scientist = new IATrainer("trainer_scientist", "Scientifique Sophie",
                4, "Scientifique", "Équilibré", false);
        Creature scientistCreature1 = generateRandomCreature("owl_01", "Owloo", 8);
        Creature scientistCreature2 = generateRandomCreature("fox_01", "Foxxy", 7);
        scientist.addCreature(scientistCreature1);
        scientist.addCreature(scientistCreature2);
        npcs.add(scientist);

        // Exemple: Chef de gym
        IATrainer gymLeader = new IATrainer("gym_leader_1", "Chef Cynthia",
                8, "Chef de Gym", "Intelligent", true);
        // Ajouter plusieurs créatures fortes...
        npcs.add(gymLeader);
    }

    /**
     * Génère une créature aléatoire.
     *
     * @param id Identifiant de la créature
     * @param name Nom de la créature
     * @param level Niveau souhaité
     * @return Créature générée
     */
    public Creature generateRandomCreature(String id, String name, int level) {
        // Sélectionner un type aléatoire
        Type primaryType = availableTypes.get(random.nextInt(availableTypes.size()));

        // Créer la créature
        Creature creature = new Creature(id, name, primaryType);

        // Simuler une montée en niveau
        for (int i = 1; i < level; i++) {
            creature.getStats().gainExperience(creature.getStats().getMaxXpNextLevel());
        }

        // Déverrouiller quelques compétences de base
        List<String> rootSkillIds = new ArrayList<>();
        for (game.core.skill.SkillNode node : creature.getSkillTree().getRootNodes()) {
            rootSkillIds.add(node.getId());
        }

        // Débloquer les compétences racines et quelques-unes supplémentaires
        for (String nodeId : rootSkillIds) {
            creature.unlockSkillNode(nodeId);
        }

        // Ajouter des compétences aléatoires selon le niveau
        List<game.core.skill.SkillNode> availableNodes = creature.getSkillTree().getAvailableNodes();
        int additionalSkills = Math.min(level / 3, availableNodes.size());

        for (int i = 0; i < additionalSkills && !availableNodes.isEmpty(); i++) {
            int randomIndex = random.nextInt(availableNodes.size());
            creature.unlockSkillNode(availableNodes.get(randomIndex).getId());

            // Mettre à jour les nœuds disponibles
            availableNodes = creature.getSkillTree().getAvailableNodes();
        }

        // Équiper des compétences (jusqu'à 4)
        List<game.core.skill.Skill> unlockedSkills = creature.getSkillTree().getUnlockedSkills();
        for (int i = 0; i < Math.min(4, unlockedSkills.size()); i++) {
            creature.equipSkill(unlockedSkills.get(i));
        }

        return creature;
    }

    /**
     * Commence un combat avec un dresseur IA.
     *
     * @param opponentId Identifiant du dresseur adversaire
     * @return Battle créée ou null si échec
     */
    public Battle startTrainerBattle(String opponentId) {
        IATrainer opponent = null;

        // Trouver le dresseur
        for (IATrainer npc : npcs) {
            if (npc.getId().equals(opponentId)) {
                opponent = npc;
                break;
            }
        }

        if (opponent == null) {
            return null;
        }

        return battleManager.createBattle(player, opponent);
    }

    /**
     * Commence un combat avec une créature sauvage.
     *
     * @param areaLevel Niveau moyen de la zone
     * @return Battle créée
     */
    public Battle startWildBattle(int areaLevel) {
        // Ajouter une variation aléatoire au niveau
        int creatureLevel = Math.max(1, areaLevel + random.nextInt(3) - 1);

        // Générer un ID et un nom aléatoires
        String id = "wild_" + System.currentTimeMillis();
        String names[] = {"Wilder", "Ferocio", "Sauvana", "Nativo", "Wildy"};
        String name = names[random.nextInt(names.length)];

        // Créer la créature
        Creature wildCreature = generateRandomCreature(id, name, creatureLevel);

        return battleManager.createWildBattle(player, wildCreature);
    }

    /**
     * Obtient la liste des types disponibles.
     *
     * @return Liste des types
     */
    public List<Type> getAvailableTypes() {
        return new ArrayList<>(availableTypes);
    }

    /**
     * Obtient le joueur actuel.
     *
     * @return Dresseur joueur
     */
    public PlayerTrainer getPlayer() {
        return player;
    }

    /**
     * Obtient le gestionnaire de combats.
     *
     * @return Gestionnaire de combats
     */
    public BattleManager getBattleManager() {
        return battleManager;
    }

    /**
     * Obtient la liste des PNJ.
     *
     * @return Liste des dresseurs IA
     */
    public List<IATrainer> getNpcs() {
        return new ArrayList<>(npcs);
    }

    /**
     * Sauvegarde l'état du jeu.
     *
     * @return true si la sauvegarde a réussi
     */
    public boolean saveGame() {
        // Implémentation de la sauvegarde à ajouter
        return true;
    }

    /**
     * Charge une sauvegarde.
     *
     * @param saveId Identifiant de la sauvegarde
     * @return true si le chargement a réussi
     */
    public boolean loadGame(String saveId) {
        // Implémentation du chargement à ajouter
        return false;
    }

    /**
     * Sauvegarde l'état du jeu avec un nom spécifique.
     *
     * @param saveName Nom de la sauvegarde
     * @return true si la sauvegarde a réussi
     */
    public boolean saveGame(String saveName) {
        return SaveSystem.saveGame(this, saveName);
    }

    /**
     * Charge une sauvegarde par son nom.
     *
     * @param saveName Nom de la sauvegarde
     * @return true si le chargement a réussi
     */
    public boolean loadGame(String saveName) {
        GameManager loadedManager = SaveSystem.loadGame(saveName);

        if (loadedManager != null) {
            // Transférer les données chargées à l'instance actuelle
            this.player = loadedManager.getPlayer();
            this.npcs = loadedManager.getNpcs();
            return true;
        }

        return false;
    }

    /**
     * Liste toutes les sauvegardes disponibles.
     *
     * @return Liste des noms de sauvegarde
     */
    public List<String> listSaveGames() {
        return SaveSystem.listSaves();
    }

    /**
     * Supprime une sauvegarde.
     *
     * @param saveName Nom de la sauvegarde à supprimer
     * @return true si la suppression a réussi
     */
    public boolean deleteSaveGame(String saveName) {
        return SaveSystem.deleteSave(saveName);
    }

    /**
     * Définit le joueur actuel.
     * Utilisé principalement lors du chargement d'une sauvegarde.
     *
     * @param player Nouveau joueur
     */
    public void setPlayer(PlayerTrainer player) {
        this.player = player;
    }

    /**
     * Définit la liste des PNJ.
     * Utilisé principalement lors du chargement d'une sauvegarde.
     *
     * @param npcs Liste des PNJ
     */
    public void setNpcs(List<IATrainer> npcs) {
        this.npcs = new ArrayList<>(npcs);
    }

    /**
     * Crée un nouveau combat de démonstration pour le tutoriel.
     *
     * @return Battle créée
     */
    public Battle createTutorialBattle() {
        // Créer un dresseur IA très simple pour le tutoriel
        IATrainer tutorialTrainer = new IATrainer(
                "tutorial_trainer",
                "Assistant Tutoriel",
                1, // Difficulté minimale
                "Tutoriel",
                "Aléatoire",
                false
        );

        // Créer une créature très simple pour le tutoriel
        Creature tutorialCreature = generateRandomCreature(
                "tutorial_creature",
                "Tutoria",
                Math.max(1, player.getCreatures().get(0).getStats().getLevel() - 2)
        );

        tutorialTrainer.addCreature(tutorialCreature);

        // Créer le combat
        return battleManager.createBattle(player, tutorialTrainer);
    }

    /**
     * Ajoute un PNJ à la liste des PNJ disponibles.
     *
     * @param npc PNJ à ajouter
     */
    public void addNPC(IATrainer npc) {
        if (npc != null && !npcs.contains(npc)) {
            npcs.add(npc);
        }
    }

    /**
     * Supprime un PNJ de la liste des PNJ disponibles.
     *
     * @param npcId Identifiant du PNJ à supprimer
     * @return true si le PNJ a été trouvé et supprimé
     */
    public boolean removeNPC(String npcId) {
        for (int i = 0; i < npcs.size(); i++) {
            if (npcs.get(i).getId().equals(npcId)) {
                npcs.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Trouve un PNJ par son identifiant.
     *
     * @param npcId Identifiant du PNJ
     * @return PNJ trouvé ou null
     */
    public IATrainer findNPCById(String npcId) {
        for (IATrainer npc : npcs) {
            if (npc.getId().equals(npcId)) {
                return npc;
            }
        }
        return null;
    }

    /**
     * Génère une créature avec un type spécifique.
     *
     * @param id Identifiant de la créature
     * @param name Nom de la créature
     * @param level Niveau souhaité
     * @param typeName Nom du type principal
     * @return Créature générée
     */
    public Creature generateCreatureWithType(String id, String name, int level, String typeName) {
        // Trouver le type demandé
        Type selectedType = null;
        for (Type type : availableTypes) {
            if (type.getName().equalsIgnoreCase(typeName)) {
                selectedType = type;
                break;
            }
        }

        // Si le type n'est pas trouvé, utiliser le premier disponible
        if (selectedType == null && !availableTypes.isEmpty()) {
            selectedType = availableTypes.get(0);
        }

        // Créer la créature avec le type spécifié
        Creature creature = new Creature(id, name, selectedType);

        // Simuler une montée en niveau
        for (int i = 1; i < level; i++) {
            creature.getStats().gainExperience(creature.getStats().getMaxXpNextLevel());
        }

        // Déverrouiller quelques compétences de base
        List<String> rootSkillIds = new ArrayList<>();
        for (game.core.skill.SkillNode node : creature.getSkillTree().getRootNodes()) {
            rootSkillIds.add(node.getId());
        }

        // Débloquer les compétences racines et quelques-unes supplémentaires
        for (String nodeId : rootSkillIds) {
            creature.unlockSkillNode(nodeId);
        }

        // Ajouter des compétences aléatoires selon le niveau
        List<game.core.skill.SkillNode> availableNodes = creature.getSkillTree().getAvailableNodes();
        int additionalSkills = Math.min(level / 3, availableNodes.size());

        for (int i = 0; i < additionalSkills && !availableNodes.isEmpty(); i++) {
            int randomIndex = random.nextInt(availableNodes.size());
            creature.unlockSkillNode(availableNodes.get(randomIndex).getId());

            // Mettre à jour les nœuds disponibles
            availableNodes = creature.getSkillTree().getAvailableNodes();
        }

        // Équiper des compétences (jusqu'à 4)
        List<game.core.skill.Skill> unlockedSkills = creature.getSkillTree().getUnlockedSkills();
        for (int i = 0; i < Math.min(4, unlockedSkills.size()); i++) {
            creature.equipSkill(unlockedSkills.get(i));
        }

        return creature;
    }

    /**
     * Génère un objet personnalisé.
     *
     * @param id Identifiant de l'objet
     * @param name Nom de l'objet
     * @param description Description de l'objet
     * @param value Valeur de l'objet
     * @param category Catégorie de l'objet
     * @param consumable Si l'objet est consommable
     * @return Objet créé
     */
    public Item createCustomItem(String id, String name, String description,
                                 int value, ItemCategory category, boolean consumable) {
        return new Item(id, name, description, value, category, consumable);
    }

    /**
     * Initialise une nouvelle partie avec des options avancées.
     *
     * @param playerName Nom du joueur
     * @param starterCreature Créature de départ
     * @param startingMoney Argent de départ
     * @param difficulty Niveau de difficulté (1-3)
     */
    public void initializeNewGameAdvanced(String playerName, Creature starterCreature,
                                          int startingMoney, int difficulty) {
        this.player = new PlayerTrainer("player_" + System.currentTimeMillis(), playerName);

        // Ajouter la créature de départ
        if (starterCreature != null) {
            player.addCreature(starterCreature);
        }

        // Définir l'argent de départ
        player.setMoney(startingMoney);

        // Ajouter des objets de départ
        Item potion = Item.createHealingPotion("potion_small", "Petite Potion", 20, 100);
        player.getInventory().addItem(potion, 3);

        // Ajouter des objets supplémentaires selon la difficulté
        if (difficulty == 1) { // Facile
            Item bigPotion = Item.createHealingPotion("potion_big", "Grande Potion", 50, 250);
            player.getInventory().addItem(bigPotion, 2);

            // Donner plus d'argent en mode facile
            player.setMoney(player.getMoney() + 500);
        }

        // Initialiser les PNJ avec une difficulté adaptée
        initializeNPCsWithDifficulty(difficulty);
    }

    /**
     * Initialise les PNJ avec un niveau de difficulté spécifique.
     *
     * @param difficulty Niveau de difficulté (1-3)
     */
    private void initializeNPCsWithDifficulty(int difficulty) {
        // Effacer les PNJ existants
        npcs.clear();

        // Facteur de difficulté
        int difficultyFactor = difficulty;

        // Créer plusieurs types de PNJ avec des niveaux adaptés

        // Novices
        for (int i = 0; i < 3; i++) {
            IATrainer novice = new IATrainer(
                    "trainer_novice_" + i,
                    "Novice " + getRandomName(),
                    1 + difficultyFactor,
                    "Novice",
                    "Aléatoire",
                    false
            );

            // Ajouter 1-2 créatures de niveau bas
            int creatureCount = 1 + random.nextInt(2);
            for (int j = 0; j < creatureCount; j++) {
                Creature noviceCreature = generateRandomCreature(
                        "creature_novice_" + i + "_" + j,
                        getRandomCreatureName(),
                        3 + difficultyFactor
                );
                novice.addCreature(noviceCreature);
            }

            npcs.add(novice);
        }

        // Dresseurs intermédiaires
        for (int i = 0; i < 4; i++) {
            IATrainer intermediate = new IATrainer(
                    "trainer_med_" + i,
                    getRandomTitle() + " " + getRandomName(),
                    3 + difficultyFactor,
                    getRandomTrainerType(),
                    "Équilibré",
                    false
            );

            // Ajouter 2-3 créatures de niveau moyen
            int creatureCount = 2 + random.nextInt(2);
            for (int j = 0; j < creatureCount; j++) {
                Creature intermediateCreature = generateRandomCreature(
                        "creature_med_" + i + "_" + j,
                        getRandomCreatureName(),
                        6 + difficultyFactor
                );
                intermediate.addCreature(intermediateCreature);
            }

            npcs.add(intermediate);
        }

        // Dresseurs expérimentés
        for (int i = 0; i < 3; i++) {
            IATrainer expert = new IATrainer(
                    "trainer_expert_" + i,
                    "Maître " + getRandomName(),
                    5 + difficultyFactor,
                    "Expert",
                    "Intelligent",
                    false
            );

            // Ajouter 3-4 créatures de niveau élevé
            int creatureCount = 3 + random.nextInt(2);
            for (int j = 0; j < creatureCount; j++) {
                Creature expertCreature = generateRandomCreature(
                        "creature_expert_" + i + "_" + j,
                        getRandomCreatureName(),
                        10 + difficultyFactor
                );
                expert.addCreature(expertCreature);
            }

            npcs.add(expert);
        }

        // Chefs de gym
        for (int i = 0; i < 8; i++) {
            IATrainer gymLeader = new IATrainer(
                    "gym_leader_" + i,
                    "Champion " + getRandomName(),
                    7 + difficultyFactor,
                    "Chef de Gym",
                    "Intelligent",/**
             * Sauvegarde l'état du jeu avec un nom spécifique.
             *
             * @param saveName Nom de la sauvegarde
             * @return true si la sauvegarde a réussi
             */
            public boolean saveGame(String saveName) {
                return SaveSystem.saveGame(this, saveName);
            }

            /**
             * Charge une sauvegarde par son nom.
             *
             * @param saveName Nom de la sauvegarde
             * @return true si le chargement a réussi
             */
            public boolean loadGame(String saveName) {
                GameManager loadedManager = SaveSystem.loadGame(saveName);

                if (loadedManager != null) {
                    // Transférer les données chargées à l'instance actuelle
                    this.player = loadedManager.getPlayer();
                    this.npcs = loadedManager.getNpcs();
                    return true;
                }

                return false;
            }

            /**
             * Liste toutes les sauvegardes disponibles.
             *
             * @return Liste des noms de sauvegarde
             */
            public List<String> listSaveGames() {
                return SaveSystem.listSaves();
            }

            /**
             * Supprime une sauvegarde.
             *
             * @param saveName Nom de la sauvegarde à supprimer
             * @return true si la suppression a réussi
             */
            public boolean deleteSaveGame(String saveName) {
                return SaveSystem.deleteSave(saveName);
            }

            /**
             * Définit le joueur actuel.
             * Utilisé principalement lors du chargement d'une sauvegarde.
             *
             * @param player Nouveau joueur
             */
            public void setPlayer(PlayerTrainer player) {
                this.player = player;
            }

            /**
             * Définit la liste des PNJ.
             * Utilisé principalement lors du chargement d'une sauvegarde.
             *
             * @param npcs Liste des PNJ
             */
            public void setNpcs(List<IATrainer> npcs) {
                this.npcs = new ArrayList<>(npcs);
            }

            /**
             * Crée un nouveau combat de démonstration pour le tutoriel.
             *
             * @return Battle créée
             */
            public Battle createTutorialBattle() {
                // Créer un dresseur IA très simple pour le tutoriel
                IATrainer tutorialTrainer = new IATrainer(
                        "tutorial_trainer",
                        "Assistant Tutoriel",
                        1, // Difficulté minimale
                        "Tutoriel",
                        "Aléatoire",
                        false
                );

                // Créer une créature très simple pour le tutoriel
                Creature tutorialCreature = generateRandomCreature(
                        "tutorial_creature",
                        "Tutoria",
                        Math.max(1, player.getCreatures().get(0).getStats().getLevel() - 2)
                );

                tutorialTrainer.addCreature(tutorialCreature);

                // Créer le combat
                return battleManager.createBattle(player, tutorialTrainer);
            }

            /**
             * Ajoute un PNJ à la liste des PNJ disponibles.
             *
             * @param npc PNJ à ajouter
             */
            public void addNPC(IATrainer npc) {
                if (npc != null && !npcs.contains(npc)) {
                    npcs.add(npc);
                }
            }

            /**
             * Supprime un PNJ de la liste des PNJ disponibles.
             *
             * @param npcId Identifiant du PNJ à supprimer
             * @return true si le PNJ a été trouvé et supprimé
             */
            public boolean removeNPC(String npcId) {
                for (int i = 0; i < npcs.size(); i++) {
                    if (npcs.get(i).getId().equals(npcId)) {
                        npcs.remove(i);
                        return true;
                    }
                }
                return false;
            }

            /**
             * Trouve un PNJ par son identifiant.
             *
             * @param npcId Identifiant du PNJ
             * @return PNJ trouvé ou null
             */
            public IATrainer findNPCById(String npcId) {
                for (IATrainer npc : npcs) {
                    if (npc.getId().equals(npcId)) {
                        return npc;
                    }
                }
                return null;
            }

            /**
             * Génère une créature avec un type spécifique.
             *
             * @param id Identifiant de la créature
             * @param name Nom de la créature
             * @param level Niveau souhaité
             * @param typeName Nom du type principal
             * @return Créature générée
             */
            public Creature generateCreatureWithType(String id, String name, int level, String typeName) {
                // Trouver le type demandé
                Type selectedType = null;
                for (Type type : availableTypes) {
                    if (type.getName().equalsIgnoreCase(typeName)) {
                        selectedType = type;
                        break;
                    }
                }

                // Si le type n'est pas trouvé, utiliser le premier disponible
                if (selectedType == null && !availableTypes.isEmpty()) {
                    selectedType = availableTypes.get(0);
                }

                // Créer la créature avec le type spécifié
                Creature creature = new Creature(id, name, selectedType);

                // Simuler une montée en niveau
                for (int i = 1; i < level; i++) {
                    creature.getStats().gainExperience(creature.getStats().getMaxXpNextLevel());
                }

                // Déverrouiller quelques compétences de base
                List<String> rootSkillIds = new ArrayList<>();
                for (game.core.skill.SkillNode node : creature.getSkillTree().getRootNodes()) {
                    rootSkillIds.add(node.getId());
                }

                // Débloquer les compétences racines et quelques-unes supplémentaires
                for (String nodeId : rootSkillIds) {
                    creature.unlockSkillNode(nodeId);
                }

                // Ajouter des compétences aléatoires selon le niveau
                List<game.core.skill.SkillNode> availableNodes = creature.getSkillTree().getAvailableNodes();
                int additionalSkills = Math.min(level / 3, availableNodes.size());

                for (int i = 0; i < additionalSkills && !availableNodes.isEmpty(); i++) {
                    int randomIndex = random.nextInt(availableNodes.size());
                    creature.unlockSkillNode(availableNodes.get(randomIndex).getId());

                    // Mettre à jour les nœuds disponibles
                    availableNodes = creature.getSkillTree().getAvailableNodes();
                }

                // Équiper des compétences (jusqu'à 4)
                List<game.core.skill.Skill> unlockedSkills = creature.getSkillTree().getUnlockedSkills();
                for (int i = 0; i < Math.min(4, unlockedSkills.size()); i++) {
                    creature.equipSkill(unlockedSkills.get(i));
                }

                return creature;
            }

            /**
             * Génère un objet personnalisé.
             *
             * @param id Identifiant de l'objet
             * @param name Nom de l'objet
             * @param description Description de l'objet
             * @param value Valeur de l'objet
             * @param category Catégorie de l'objet
             * @param consumable Si l'objet est consommable
             * @return Objet créé
             */
            public Item createCustomItem(String id, String name, String description,
            int value, ItemCategory category, boolean consumable) {
                return new Item(id, name, description, value, category, consumable);
            }

            /**
             * Initialise une nouvelle partie avec des options avancées.
             *
             * @param playerName Nom du joueur
             * @param starterCreature Créature de départ
             * @param startingMoney Argent de départ
             * @param difficulty Niveau de difficulté (1-3)
             */
            public void initializeNewGameAdvanced(String playerName, Creature starterCreature,
            int startingMoney, int difficulty) {
                this.player = new PlayerTrainer("player_" + System.currentTimeMillis(), playerName);

                // Ajouter la créature de départ
                if (starterCreature != null) {
                    player.addCreature(starterCreature);
                }

                // Définir l'argent de départ
                player.setMoney(startingMoney);

                // Ajouter des objets de départ
                Item potion = Item.createHealingPotion("potion_small", "Petite Potion", 20, 100);
                player.getInventory().addItem(potion, 3);

                // Ajouter des objets supplémentaires selon la difficulté
                if (difficulty == 1) { // Facile
                    Item bigPotion = Item.createHealingPotion("potion_big", "Grande Potion", 50, 250);
                    player.getInventory().addItem(bigPotion, 2);

                    // Donner plus d'argent en mode facile
                    player.setMoney(player.getMoney() + 500);
                }

                // Initialiser les PNJ avec une difficulté adaptée
                initializeNPCsWithDifficulty(difficulty);
            }

            /**
             * Initialise les PNJ avec un niveau de difficulté spécifique.
             *
             * @param difficulty Niveau de difficulté (1-3)
             */
            private void initializeNPCsWithDifficulty(int difficulty) {
                // Effacer les PNJ existants
                npcs.clear();

                // Facteur de difficulté
                int difficultyFactor = difficulty;

                // Créer plusieurs types de PNJ avec des niveaux adaptés

                // Novices
                for (int i = 0; i < 3; i++) {
                    IATrainer novice = new IATrainer(
                            "trainer_novice_" + i,
                            "Novice " + getRandomName(),
                            1 + difficultyFactor,
                            "Novice",
                            "Aléatoire",
                            false
                    );

                    // Ajouter 1-2 créatures de niveau bas
                    int creatureCount = 1 + random.nextInt(2);
                    for (int j = 0; j < creatureCount; j++) {
                        Creature noviceCreature = generateRandomCreature(
                                "creature_novice_" + i + "_" + j,
                                getRandomCreatureName(),
                                3 + difficultyFactor
                        );
                        novice.addCreature(noviceCreature);
                    }

                    npcs.add(novice);
                }

                // Dresseurs intermédiaires
                for (int i = 0; i < 4; i++) {
                    IATrainer intermediate = new IATrainer(
                            "trainer_med_" + i,
                            getRandomTitle() + " " + getRandomName(),
                            3 + difficultyFactor,
                            getRandomTrainerType(),
                            "Équilibré",
                            false
                    );

                    // Ajouter 2-3 créatures de niveau moyen
                    int creatureCount = 2 + random.nextInt(2);
                    for (int j = 0; j < creatureCount; j++) {
                        Creature intermediateCreature = generateRandomCreature(
                                "creature_med_" + i + "_" + j,
                                getRandomCreatureName(),
                                6 + difficultyFactor
                        );
                        intermediate.addCreature(intermediateCreature);
                    }

                    npcs.add(intermediate);
                }

                // Dresseurs expérimentés
                for (int i = 0; i < 3; i++) {
                    IATrainer expert = new IATrainer(
                            "trainer_expert_" + i,
                            "Maître " + getRandomName(),
                            5 + difficultyFactor,
                            "Expert",
                            "Intelligent",
                            false
                    );

                    // Ajouter 3-4 créatures de niveau élevé
                    int creatureCount = 3 + random.nextInt(2);
                    for (int j = 0; j < creatureCount; j++) {
                        Creature expertCreature = generateRandomCreature(
                                "creature_expert_" + i + "_" + j,
                                getRandomCreatureName(),
                                10 + difficultyFactor
                        );
                        expert.addCreature(expertCreature);
                    }

                    npcs.add(expert);
                }

                // Chefs de gym
                for (int i = 0; i < 8; i++) {
                    IATrainer gymLeader = new IATrainer(
                            "gym_leader_" + i,
                            "Champion " + getRandomName(),
                            7 + difficultyFactor,
                            "Chef de Gym",
                            "Intelligent",
                            true
                    );

                    // Ajouter 4-6 créatures de niveau élevé
                    int creatureCount = 4 + random.nextInt(3);
                    for (int j = 0; j < creatureCount; j++) {
                        // Choisir un type spécifique pour chaque gym
                        String gymType = getGymTypeForIndex(i);

                        Creature gymCreature = generateCreatureWithType(
                                "creature_gym_" + i + "_" + j,
                                getRandomCreatureName(),
                                12 + difficultyFactor + i, // Niveau croissant pour chaque gym
                                gymType
                        );
                        gymLeader.addCreature(gymCreature);
                    }

                    npcs.add(gymLeader);
                }
            }

            /**
             * Obtient un type pour chaque gym.
             *
             * @param gymIndex Index de la gym (0-7)
             * @return Nom du type
             */
            private String getGymTypeForIndex(int gymIndex) {
                switch (gymIndex) {
                    case 0: return "Feu";
                    case 1: return "Eau";
                    case 2: return "Nature";
                    case 3: return "Électrique";
                    case 4: return "Sol";
                    case 5: return "Air";
                    case 6: return "Psychique";
                    case 7: return "Ténèbres";
                    default: return "Feu";
                }
            }

            /**
             * Génère un prénom aléatoire pour les PNJ.
             *
             * @return Prénom généré
             */
            private String getRandomName() {
                String[] names = {
                        "Alex", "Morgan", "Casey", "Jordan", "Taylor", "Robin", "Sam", "Jamie",
                        "Riley", "Skyler", "Quinn", "Avery", "Charlie", "Frankie", "Ashley"
                };

                return names[random.nextInt(names.length)];
            }

            /**
             * Génère un titre aléatoire pour les PNJ.
             *
             * @return Titre généré
             */
            private String getRandomTitle() {
                String[] titles = {
                        "Professeur", "Docteur", "Ranger", "Explorateur", "Scientifique",
                        "Savant", "Botaniste", "Archéologue", "Chercheur", "Étudiant"
                };

                return titles[random.nextInt(titles.length)];
            }

            /**
             * Génère un type de dresseur aléatoire.
             *
             * @return Type de dresseur
             */
            private String getRandomTrainerType() {
                String[] types = {
                        "Scientifique", "Éleveur", "Randonneur", "Pêcheur", "Athlète",
                        "Aventurier", "Explorateur", "Guitariste", "Collectionneur"
                };

                return types[random.nextInt(types.length)];
            }

            /**
             * Génère un nom aléatoire pour les créatures.
             *
             * @return Nom généré
             */
            private String getRandomCreatureName() {
                String[] prefixes = {
                        "Pico", "Zar", "Flam", "Aqua", "Terra", "Aero", "Spark", "Shad",
                        "Luna", "Sol", "Cryo", "Pyro", "Herba", "Veno", "Mystic"
                };

                String[] suffixes = {
                        "mon", "ter", "zard", "saur", "tuna", "dile", "king", "chu",
                        "mander", "puff", "meleon", "oise", "burn", "fox", "claw"
                };

                return prefixes[random.nextInt(prefixes.length)] +
                        suffixes[random.nextInt(suffixes.length)];
            }

            /**
             * Crée un événement spécial dans le jeu.
             *
             * @param eventType Type d'événement
             * @return true si l'événement a été créé
             */
            public boolean createSpecialEvent(String eventType) {
                switch (eventType) {
                    case "tournament":
                        return createTournament();

                    case "wild_swarm":
                        return createWildSwarm();

                    case "rare_creature":
                        return createRareCreatureEvent();

                    case "sale":
                        return createSaleEvent();

                    default:
                        return false;
                }
            }

            /**
             * Crée un événement de tournoi.
             *
             * @return true si le tournoi a été créé
             */
            private boolean createTournament() {
                // Cette méthode pourrait être développée pour implémenter
                // un système complet de tournoi, avec des matchs à élimination directe, etc.

                // Version simplifiée pour l'exemple
                IATrainer[] tournamentTrainers = new IATrainer[7]; // 7 adversaires

                // Créer des dresseurs de niveau tournoi
                for (int i = 0; i < tournamentTrainers.length; i++) {
                    tournamentTrainers[i] = new IATrainer(
                            "tournament_trainer_" + i,
                            "Challenger " + getRandomName(),
                            5 + i/2, // Difficulté croissante
                            "Tournoi",
                            i < 3 ? "Équilibré" : "Intelligent",
                            false
                    );

                    // Ajouter 3-6 créatures par dresseur
                    int creatureCount = 3 + random.nextInt(4);
                    for (int j = 0; j < creatureCount; j++) {
                        Creature tournamentCreature = generateRandomCreature(
                                "tournament_creature_" + i + "_" + j,
                                getRandomCreatureName(),
                                8 + i // Niveau croissant
                        );
                        tournamentTrainers[i].addCreature(tournamentCreature);
                    }
                }

                // Stocker les dresseurs de tournoi quelque part
                // (dans cet exemple, nous les ajoutons simplement aux NPCs)
                for (IATrainer trainer : tournamentTrainers) {
                    addNPC(trainer);
                }

                return true;
            }

            /**
             * Crée un événement d'essaim de créatures sauvages.
             *
             * @return true si l'événement a été créé
             */
            private boolean createWildSwarm() {
                // Cet événement pourrait augmenter temporairement le taux de rencontre
                // et faire apparaître un certain type de créatures plus fréquemment

                // Ici, nous ne pouvons qu'implémenter le squelette
                System.out.println("Un essaim de créatures sauvages est apparu dans la région!");
                return true;
            }

            /**
             * Crée un événement de créature rare.
             *
             * @return true si l'événement a été créé
             */
            private boolean createRareCreatureEvent() {
                // Cet événement ferait apparaître une créature rare à capturer

                // Créer une créature rare
                Type[] rareTypes = {
                        findTypeByName("Lumière"),
                        findTypeByName("Ténèbres"),
                        findTypeByName("Psychique")
                };

                Type rareType = rareTypes[random.nextInt(rareTypes.length)];
                if (rareType == null && !availableTypes.isEmpty()) {
                    rareType = availableTypes.get(random.nextInt(availableTypes.size()));
                }

                Creature rareCreature = new Creature(
                        "rare_creature_" + System.currentTimeMillis(),
                        "Légendaire" + getRandomCreatureName(),
                        rareType
                );

                // Niveau élevé
                for (int i = 1; i < 20; i++) {
                    rareCreature.getStats().gainExperience(rareCreature.getStats().getMaxXpNextLevel());
                }

                // Ajouter des compétences spéciales
                // (cette partie nécessiterait une implémentation plus complexe)

                // Ici, nous ne pouvons que retourner true pour indiquer la création
                System.out.println("Une créature rare est apparue: " + rareCreature.getName() + "!");
                return true;
            }

            /**
             * Crée un événement de solde dans les boutiques.
             *
             * @return true si l'événement a été créé
             */
            private boolean createSaleEvent() {
                // Cet événement réduirait temporairement le prix des objets

                // Ici, nous ne pouvons qu'implémenter le squelette
                System.out.println("Soldes spéciales dans toutes les boutiques!");
                return true;
            }

            /**
             * Trouve un type par son nom.
             *
             * @param name Nom du type
             * @return Type trouvé ou null
             */
            private Type findTypeByName(String name) {
                for (Type type : availableTypes) {
                    if (type.getName().equalsIgnoreCase(name)) {
                        return type;
                    }
                }
                return null;
            }
}