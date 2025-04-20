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
}