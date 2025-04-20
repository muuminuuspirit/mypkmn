package game.core;

import game.core.creature.Creature;
import game.core.type.Type;
import game.core.battle.Battle;
import game.core.battle.BattleManager;
import game.characters.PlayerTrainer;

import java.util.List;
import java.util.Scanner;

/**
 * Point d'entrée principal du jeu.
 */
public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static GameManager gameManager;

    /**
     * Méthode principale.
     *
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        System.out.println("Bienvenue dans le jeu de créatures!");
        gameManager = GameManager.getInstance();

        // Initialiser le jeu
        initializeGame();

        // Boucle principale du jeu
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput(1, 5);

            switch (choice) {
                case 1:
                    exploreWildArea();
                    break;
                case 2:
                    challengeTrainer();
                    break;
                case 3:
                    manageCreatures();
                    break;
                case 4:
                    manageInventory();
                    break;
                case 5:
                    running = false;
                    System.out.println("Merci d'avoir joué! À bientôt.");
                    break;
            }
        }

        scanner.close();
    }

    /**
     * Initialise une nouvelle partie.
     */
    private static void initializeGame() {
        System.out.println("Commençons une nouvelle aventure!");
        System.out.print("Quel est votre nom? ");
        String playerName = scanner.nextLine();

        // Sélection de la créature de départ
        Creature starterCreature = selectStarterCreature();

        gameManager.initializeNewGame(playerName, starterCreature);

        System.out.println("\nBienvenue, " + playerName + "! Votre aventure commence avec " +
                starterCreature.getName() + ".");
        System.out.println("Explorez le monde, capturez des créatures et devenez le meilleur dresseur!");
    }

    /**
     * Permet au joueur de sélectionner sa créature de départ.
     *
     * @return Créature sélectionnée
     */
    private static Creature selectStarterCreature() {
        System.out.println("\nChoisissez votre créature de départ:");

        List<Type> types = gameManager.getAvailableTypes();
        Type fireType = null;
        Type waterType = null;
        Type natureType = null;

        // Trouver les types élémentaires de base
        for (Type type : types) {
            if (type.getName().equals("Feu")) {
                fireType = type;
            } else if (type.getName().equals("Eau")) {
                waterType = type;
            } else if (type.getName().equals("Nature")) {
                natureType = type;
            }
        }

        // Créer les créatures de départ
        Creature fireStarter = null;
        Creature waterStarter = null;
        Creature natureStarter = null;

        if (fireType != null) {
            fireStarter = new Creature("fire_starter", "Flamby", fireType);
            System.out.println("1. Flamby (Type: Feu) - Une créature énergique avec des flammes sur le dos");
        }

        if (waterType != null) {
            waterStarter = new Creature("water_starter", "Aquali", waterType);
            System.out.println("2. Aquali (Type: Eau) - Une créature calme qui peut respirer sous l'eau");
        }

        if (natureType != null) {
            natureStarter = new Creature("nature_starter", "Leafoo", natureType);
            System.out.println("3. Leafoo (Type: Nature) - Une créature paisible qui aime les forêts");
        }

        System.out.print("\nVotre choix (1-3): ");
        int choice = getIntInput(1, 3);

        switch (choice) {
            case 1:
                return fireStarter;
            case 2:
                return waterStarter;
            case 3:
                return natureStarter;
            default:
                // Par défaut, retourner la créature de feu
                return fireStarter;
        }
    }

    /**
     * Affiche le menu principal.
     */
    private static void displayMainMenu() {
        PlayerTrainer player = gameManager.getPlayer();

        System.out.println("\n===== MENU PRINCIPAL =====");
        System.out.println("Dresseur: " + player.getName() + " | Badges: " + player.getBadges() +
                " | Argent: " + player.getMoney() + "¤");
        System.out.println("1. Explorer la nature sauvage");
        System.out.println("2. Défier un dresseur");
        System.out.println("3. Gérer mes créatures");
        System.out.println("4. Gérer mon inventaire");
        System.out.println("5. Quitter le jeu");
        System.out.print("Votre choix: ");
    }

    /**
     * Exploration d'une zone sauvage avec rencontre aléatoire.
     */
    private static void exploreWildArea() {
        System.out.println("\n===== EXPLORATION =====");
        System.out.println("Choisissez une zone à explorer:");
        System.out.println("1. Forêt Verdoyante (Niv. 1-5)");
        System.out.println("2. Plaine Ensoleillée (Niv. 3-8)");
        System.out.println("3. Grotte Humide (Niv. 5-10)");
        System.out.println("4. Retour");
        System.out.print("Votre choix: ");

        int choice = getIntInput(1, 4);

        if (choice == 4) {
            return;
        }

        int areaLevel;
        switch (choice) {
            case 1:
                areaLevel = 3;
                System.out.println("Vous explorez la Forêt Verdoyante...");
                break;
            case 2:
                areaLevel = 5;
                System.out.println("Vous explorez la Plaine Ensoleillée...");
                break;
            case 3:
                areaLevel = 8;
                System.out.println("Vous explorez la Grotte Humide...");
                break;
            default:
                return;
        }

        // Chance de rencontre (70%)
        if (Math.random() < 0.7) {
            System.out.println("Une créature sauvage apparaît!");
            Battle wildBattle = gameManager.startWildBattle(areaLevel);
            handleBattle(wildBattle, true);
        } else {
            System.out.println("Vous avez exploré la zone sans rencontrer de créature.");
            System.out.println("Vous trouvez un petit trésor! +50¤");
            gameManager.getPlayer().setMoney(gameManager.getPlayer().getMoney() + 50);
        }
    }

    /**
     * Défi contre un dresseur IA.
     */
    private static void challengeTrainer() {
        System.out.println("\n===== DRESSEURS =====");
        List<game.characters.IATrainer> npcs = gameManager.getNpcs();

        if (npcs.isEmpty()) {
            System.out.println("Aucun dresseur disponible actuellement.");
            return;
        }

        System.out.println("Choisissez un dresseur à défier:");

        for (int i = 0; i < npcs.size(); i++) {
            game.characters.IATrainer trainer = npcs.get(i);
            String title = trainer.isGymLeader() ? "Chef de Gym" : "Dresseur";
            System.out.println((i + 1) + ". " + title + " " + trainer.getName() +
                    " (Difficulté: " + trainer.getDifficulty() + ")");
        }

        System.out.println((npcs.size() + 1) + ". Retour");
        System.out.print("Votre choix: ");

        int choice = getIntInput(1, npcs.size() + 1);

        if (choice == npcs.size() + 1) {
            return;
        }

        game.characters.IATrainer opponent = npcs.get(choice - 1);
        System.out.println("Vous défiez " + opponent.getName() + "!");

        Battle trainerBattle = gameManager.startTrainerBattle(opponent.getId());
        handleBattle(trainerBattle, false);
    }

    /**
     * Gère un combat.
     *
     * @param battle Combat à gérer
     * @param isWildBattle true si c'est un combat contre une créature sauvage
     */
    private static void handleBattle(Battle battle, boolean isWildBattle) {
        if (battle == null) {
            System.out.println("Impossible de commencer le combat.");
            return;
        }

        // Démarrer le combat
        battle.start();

        // Boucle de combat
        while (battle.getState() == Battle.BattleState.IN_PROGRESS) {
            if (battle.isPlayerTurn()) {
                // Tour du joueur
                displayBattleStatus(battle);
                displayBattleActions(battle, isWildBattle);

                int choice = getIntInput(1, isWildBattle ? 4 : 3);

                switch (choice) {
                    case 1:
                        // Attaquer
                        executePlayerAttack(battle);
                        break;
                    case 2:
                        // Changer de créature
                        switchPlayerCreature(battle);
                        break;
                    case 3:
                        // Utiliser un objet
                        useItemInBattle(battle);
                        break;
                    case 4:
                        // Tenter de fuir (uniquement pour les combats sauvages)
                        if (isWildBattle) {
                            if (battle.tryEscape()) {
                                System.out.println("Vous avez fui le combat!");
                            } else {
                                System.out.println("Impossible de fuir!");
                            }
                        }
                        break;
                }
            } else {
                // Tour de l'adversaire (IA)
                System.out.println("\nC'est au tour de l'adversaire...");

                // Simuler un délai
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Si c'est un dresseur, on utilise son IA
                if (!isWildBattle) {
                    // L'IA choisit une action (simplifié)
                    Creature enemyCreature = battle.getActiveCreatureB();
                    List<game.core.skill.Skill> enemySkills = enemyCreature.getActiveSkills();

                    if (!enemySkills.isEmpty()) {
                        for (game.core.skill.Skill skill : enemySkills) {
                            if (skill.getActionPointCost() <= enemyCreature.getCurrentActionPoints()) {
                                battle.executeAttack(skill);
                                break;
                            }
                        }
                    }
                } else {
                    // Combat sauvage, action aléatoire simple
                    Creature wildCreature = battle.getActiveCreatureB();
                    List<game.core.skill.Skill> wildSkills = wildCreature.getActiveSkills();

                    if (!wildSkills.isEmpty()) {
                        int randomIndex = (int)(Math.random() * wildSkills.size());
                        game.core.skill.Skill selectedSkill = wildSkills.get(randomIndex);

                        if (selectedSkill.getActionPointCost() <= wildCreature.getCurrentActionPoints()) {
                            battle.executeAttack(selectedSkill);
                        }
                    }
                }
            }
        }

        // Fin du combat
        handleBattleEnd(battle, isWildBattle);
    }

    /**
     * Affiche l'état du combat.
     *
     * @param battle Combat en cours
     */
    private static void displayBattleStatus(Battle battle) {
        Creature playerCreature = battle.getActiveCreatureA();
        Creature enemyCreature = battle.getActiveCreatureB();

        System.out.println("\n----- ÉTAT DU COMBAT -----");
        System.out.println("Votre " + playerCreature.getName() + " (Nv." + playerCreature.getStats().getLevel() +
                "): " + playerCreature.getStats().getHealth() + "/" + playerCreature.getStats().getMaxHealth() +
                " PV | PA: " + playerCreature.getCurrentActionPoints() + "/" + playerCreature.getMaxActionPoints());

        System.out.println("Adversaire " + enemyCreature.getName() + " (Nv." + enemyCreature.getStats().getLevel() +
                "): " + enemyCreature.getStats().getHealth() + "/" + enemyCreature.getStats().getMaxHealth() + " PV");

        // Afficher les types
        System.out.print("Types - Votre créature: ");
        for (Type type : playerCreature.getStats().getTypes()) {
            System.out.print(type.getName() + " ");
        }

        System.out.print(" | Adversaire: ");
        for (Type type : enemyCreature.getStats().getTypes()) {
            System.out.print(type.getName() + " ");
        }
        System.out.println();
    }

    /**
     * Affiche les actions possibles en combat.
     *
     * @param battle Combat en cours
     * @param isWildBattle true si c'est un combat contre une créature sauvage
     */
    private static void displayBattleActions(Battle battle, boolean isWildBattle) {
        System.out.println("\nQue souhaitez-vous faire?");
        System.out.println("1. Attaquer");
        System.out.println("2. Changer de créature");
        System.out.println("3. Utiliser un objet");
        if (isWildBattle) {
            System.out.println("4. Tenter de fuir");
        }
        System.out.print("Votre choix: ");
    }

    /**
     * Exécute une attaque du joueur.
     *
     * @param battle Combat en cours
     */
    private static void executePlayerAttack(Battle battle) {
        Creature playerCreature = battle.getActiveCreatureA();
        List<game.core.skill.Skill> skills = playerCreature.getActiveSkills();

        if (skills.isEmpty()) {
            System.out.println("Votre créature n'a pas de compétences!");
            return;
        }

        System.out.println("\nChoisissez une compétence:");

        for (int i = 0; i < skills.size(); i++) {
            game.core.skill.Skill skill = skills.get(i);
            String skillInfo = (i + 1) + ". " + skill.getName() + " (Type: " + skill.getType().getName() +
                    " | Puissance: " + skill.getPower() + " | Précision: " + skill.getAccuracy() +
                    " | PA: " + skill.getActionPointCost() + " | Niv: " + skill.getLevel() + ")";

            if (skill.getActionPointCost() > playerCreature.getCurrentActionPoints()) {
                skillInfo += " [PAS ASSEZ DE PA]";
            }

            System.out.println(skillInfo);
        }

        System.out.println((skills.size() + 1) + ". Retour");
        System.out.print("Votre choix: ");

        int choice = getIntInput(1, skills.size() + 1);

        if (choice == skills.size() + 1) {
            return;
        }

        game.core.skill.Skill selectedSkill = skills.get(choice - 1);

        if (selectedSkill.getActionPointCost() > playerCreature.getCurrentActionPoints()) {
            System.out.println("Pas assez de Points d'Action pour utiliser cette compétence!");
            return;
        }

        battle.executeAttack(selectedSkill);
    }

    /**
     * Change la créature active du joueur.
     *
     * @param battle Combat en cours
     */
    private static void switchPlayerCreature(Battle battle) {
        List<Creature> creatures = gameManager.getPlayer().getCreatures();
        Creature currentCreature = battle.getActiveCreatureA();

        System.out.println("\nChoisissez une créature:");

        for (int i = 0; i < creatures.size(); i++) {
            Creature creature = creatures.get(i);
            String creatureInfo = (i + 1) + ". " + creature.getName() + " (Nv." + creature.getStats().getLevel() +
                    "): " + creature.getStats().getHealth() + "/" + creature.getStats().getMaxHealth() + " PV";

            if (creature == currentCreature) {
                creatureInfo += " [ACTIF]";
            } else if (creature.isDead()) {
                creatureInfo += " [K.O.]";
            }

            System.out.println(creatureInfo);
        }

        System.out.println((creatures.size() + 1) + ". Retour");
        System.out.print("Votre choix: ");

        int choice = getIntInput(1, creatures.size() + 1);

        if (choice == creatures.size() + 1) {
            return;
        }

        if (choice - 1 < creatures.size()) {
            if (battle.switchCreatureA(choice - 1)) {
                System.out.println("Changement de créature réussi!");
            } else {
                System.out.println("Impossible de changer de créature!");
            }
        }
    }

    /**
     * Utilise un objet en combat.
     *
     * @param battle Combat en cours
     */
    private static void useItemInBattle(Battle battle) {
        System.out.println("\nFonctionnalité non implémentée dans cette démo simplifiée.");
    }

    /**
     * Gère la fin d'un combat.
     *
     * @param battle Combat terminé
     * @param isWildBattle true si c'était un combat contre une créature sauvage
     */
    private static void handleBattleEnd(Battle battle, boolean isWildBattle) {
        switch (battle.getState()) {
            case TEAM_A_VICTORY:
                System.out.println("\nVictoire! Vous avez gagné le combat!");

                if (isWildBattle) {
                    // Possibilité de capture
                    System.out.println("Souhaitez-vous tenter de capturer cette créature?");
                    System.out.println("1. Oui");
                    System.out.println("2. Non");
                    System.out.print("Votre choix: ");

                    int choice = getIntInput(1, 2);

                    if (choice == 1) {
                        Creature wildCreature = battle.getActiveCreatureB();
                        boolean captured = gameManager.getPlayer().captureCreature(wildCreature, 50);

                        if (captured) {
                            System.out.println("Félicitations! Vous avez capturé " + wildCreature.getName() + "!");
                        } else {
                            System.out.println("La capture a échoué. La créature s'est enfuie!");
                        }
                    }
                } else {
                    // Récompenses pour avoir battu un dresseur
                    int rewardMoney = 100 * battle.getCurrentTurn();
                    gameManager.getPlayer().setMoney(gameManager.getPlayer().getMoney() + rewardMoney);
                    System.out.println("Vous avez gagné " + rewardMoney + "¤!");
                }

                // Distribution d'XP gérée par le BattleManager
                break;

            case TEAM_B_VICTORY:
                System.out.println("\nDéfaite! Vous avez perdu le combat.");
                System.out.println("Vos créatures sont fatiguées. Elles récupèrent à moitié de leurs PV.");

                // Restaurer partiellement les créatures
                for (Creature creature : gameManager.getPlayer().getCreatures()) {
                    int halfHealth = creature.getStats().getMaxHealth() / 2;
                    creature.getStats().setHealth(halfHealth);
                }
                break;

            case ESCAPED:
                System.out.println("\nVous avez fui le combat!");
                break;

            default:
                System.out.println("\nFin du combat.");
                break;
        }

        // Pause pour lire le résultat
        System.out.println("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }

    /**
     * Menu de gestion des créatures.
     */
    private static void manageCreatures() {
        boolean managing = true;

        while (managing) {
            System.out.println("\n===== MES CRÉATURES =====");
            List<Creature> creatures = gameManager.getPlayer().getCreatures();

            if (creatures.isEmpty()) {
                System.out.println("Vous n'avez pas de créatures.");
                return;
            }

            System.out.println("Vos créatures:");
            for (int i = 0; i < creatures.size(); i++) {
                Creature creature = creatures.get(i);
                System.out.println((i + 1) + ". " + creature.getName() + " (Niv." + creature.getStats().getLevel() +
                        "): " + creature.getStats().getHealth() + "/" + creature.getStats().getMaxHealth() + " PV");
            }

            System.out.println("\nOptions:");
            System.out.println("1. Voir les détails d'une créature");
            System.out.println("2. Gérer les compétences d'une créature");
            System.out.println("3. Retour");
            System.out.print("Votre choix: ");

            int choice = getIntInput(1, 3);

            switch (choice) {
                case 1:
                    viewCreatureDetails();
                    break;
                case 2:
                    manageCreatureSkills();
                    break;
                case 3:
                    managing = false;
                    break;
            }
        }
    }

    /**
     * Affiche les détails d'une créature.
     */
    private static void viewCreatureDetails() {
        List<Creature> creatures = gameManager.getPlayer().getCreatures();

        System.out.println("\nChoisissez une créature:");
        for (int i = 0; i < creatures.size(); i++) {
            System.out.println((i + 1) + ". " + creatures.get(i).getName());
        }
        System.out.println((creatures.size() + 1) + ". Retour");
        System.out.print("Votre choix: ");

        int choice = getIntInput(1, creatures.size() + 1);

        if (choice <= creatures.size()) {
            Creature creature = creatures.get(choice - 1);

            System.out.println("\n----- DÉTAILS DE " + creature.getName().toUpperCase() + " -----");
            System.out.println("Niveau: " + creature.getStats().getLevel());
            System.out.println("XP: " + creature.getStats().getXp() + "/" + creature.getStats().getMaxXpNextLevel());
            System.out.println("Points de Vie: " + creature.getStats().getHealth() + "/" + creature.getStats().getMaxHealth());
            System.out.println("Points d'Action: " + creature.getCurrentActionPoints() + "/" + creature.getMaxActionPoints());

            System.out.print("Types: ");
            for (Type type : creature.getStats().getTypes()) {
                System.out.print(type.getName() + " ");
            }
            System.out.println();

            System.out.println("\nStatistiques:");
            System.out.println("Force: " + creature.getStats().getStrength());
            System.out.println("Constitution: " + creature.getStats().getConstitution());
            System.out.println("Esprit: " + creature.getStats().getSpirit());
            System.out.println("Mental: " + creature.getStats().getMental());
            System.out.println("Vitesse: " + creature.getStats().getSpeed());

            System.out.println("\nCompétences actives:");
            List<game.core.skill.Skill> skills = creature.getActiveSkills();
            if (skills.isEmpty()) {
                System.out.println("Aucune compétence active.");
            } else {
                for (game.core.skill.Skill skill : skills) {
                    System.out.println("- " + skill.getName() + " (Niv." + skill.getLevel() + ", XP: " +
                            skill.getExperience() + "/" + skill.getExperienceToNextLevel() + ")");
                }
            }

            System.out.println("\nAppuyez sur Entrée pour continuer...");
            scanner.nextLine();
        }
    }

    /**
     * Gère les compétences d'une créature.
     */
    private static void manageCreatureSkills() {
        // Simplifié pour l'exemple
        System.out.println("\nFonctionnalité complète de gestion des compétences non implémentée dans cette démo.");
        System.out.println("Cette fonctionnalité permettrait de débloquer et équiper des compétences avec les points de compétence.");
        System.out.println("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }

    /**
     * Menu de gestion de l'inventaire.
     */
    private static void manageInventory() {
        System.out.println("\n===== INVENTAIRE =====");
        System.out.println("Fonctionnalité non implémentée dans cette démo simplifiée.");
        System.out.println("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }

    /**
     * Récupère un entier entré par l'utilisateur dans un intervalle donné.
     *
     * @param min Valeur minimale
     * @param max Valeur maximale
     * @return Entier entré
     */
    private static int getIntInput(int min, int max) {
        int choice = min;
        boolean validInput = false;

        while (!validInput) {
            try {
                String input = scanner.nextLine();
                choice = Integer.parseInt(input);

                if (choice >= min && choice <= max) {
                    validInput = true;
                } else {
                    System.out.print("Entrée invalide. Veuillez choisir entre " + min + " et " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Entrée invalide. Veuillez entrer un nombre: ");
            }
        }

        return choice;
    }
}