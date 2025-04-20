package game.core.battle;

import game.core.creature.Creature;
import game.characters.Trainer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gère la création et l'exécution des batailles.
 */
public class BattleManager implements Battle.BattleObserver {
    private List<BattleListener> listeners;
    private Battle currentBattle;
    private Random random;

    /**
     * Interface pour écouter les événements de gestion des batailles.
     */
    public interface BattleListener {
        void onBattleCreated(Battle battle);
        void onBattleCompleted(Battle battle, Battle.BattleState result);
        void onBattleMessage(String message);
    }

    /**
     * Crée un nouveau gestionnaire de batailles.
     */
    public BattleManager() {
        this.listeners = new ArrayList<>();
        this.random = new Random();
    }

    /**
     * Ajoute un écouteur pour les événements de bataille.
     *
     * @param listener Écouteur à ajouter
     */
    public void addBattleListener(BattleListener listener) {
        listeners.add(listener);
    }

    /**
     * Retire un écouteur.
     *
     * @param listener Écouteur à retirer
     */
    public void removeBattleListener(BattleListener listener) {
        listeners.remove(listener);
    }

    /**
     * Crée une nouvelle bataille entre deux dresseurs.
     *
     * @param playerTrainer Dresseur joueur
     * @param opponentTrainer Dresseur adversaire
     * @return La bataille créée
     */
    public Battle createBattle(Trainer playerTrainer, Trainer opponentTrainer) {
        List<Creature> playerCreatures = playerTrainer.getActiveCreatures();
        List<Creature> opponentCreatures = opponentTrainer.getActiveCreatures();

        // Vérifier que les deux dresseurs ont des créatures valides
        if (playerCreatures.isEmpty() || opponentCreatures.isEmpty()) {
            notifyMessage("Impossible de créer une bataille: un des dresseurs n'a pas de créatures valides.");
            return null;
        }

        Battle battle = new Battle(playerCreatures, opponentCreatures);
        battle.addObserver(this);
        currentBattle = battle;

        notifyBattleCreated(battle);
        return battle;
    }

    /**
     * Crée une bataille sauvage avec une seule créature adverse.
     *
     * @param playerTrainer Dresseur joueur
     * @param wildCreature Créature sauvage
     * @return La bataille créée
     */
    public Battle createWildBattle(Trainer playerTrainer, Creature wildCreature) {
        List<Creature> playerCreatures = playerTrainer.getActiveCreatures();
        List<Creature> wildTeam = new ArrayList<>();
        wildTeam.add(wildCreature);

        if (playerCreatures.isEmpty()) {
            notifyMessage("Impossible de créer une bataille: le dresseur n'a pas de créatures valides.");
            return null;
        }

        Battle battle = new Battle(playerCreatures, wildTeam);
        battle.addObserver(this);
        currentBattle = battle;

        notifyBattleCreated(battle);
        return battle;
    }

    /**
     * Obtient la bataille en cours.
     *
     * @return Bataille actuelle ou null s'il n'y en a pas
     */
    public Battle getCurrentBattle() {
        return currentBattle;
    }

    /**
     * Notifie les écouteurs de la création d'une bataille.
     *
     * @param battle Bataille créée
     */
    private void notifyBattleCreated(Battle battle) {
        for (BattleListener listener : listeners) {
            listener.onBattleCreated(battle);
        }
    }

    /**
     * Notifie les écouteurs de la fin d'une bataille.
     *
     * @param battle Bataille terminée
     * @param result Résultat de la bataille
     */
    private void notifyBattleCompleted(Battle battle, Battle.BattleState result) {
        for (BattleListener listener : listeners) {
            listener.onBattleCompleted(battle, result);
        }
    }

    /**
     * Notifie les écouteurs d'un message.
     *
     * @param message Message à envoyer
     */
    private void notifyMessage(String message) {
        for (BattleListener listener : listeners) {
            listener.onBattleMessage(message);
        }
    }

    // Implémentation de l'interface BattleObserver

    @Override
    public void onBattleStart(Battle battle) {
        notifyMessage("La bataille commence!");
    }

    @Override
    public void onTurnStart(Battle battle, boolean isPlayerTurn) {
        Creature activeCreature = isPlayerTurn ? battle.getActiveCreatureA() : battle.getActiveCreatureB();
        String turnOwner = isPlayerTurn ? "votre" : "l'adversaire";
        notifyMessage("C'est au tour de " + turnOwner + " créature " + activeCreature.getName() +
                " (PA: " + activeCreature.getCurrentActionPoints() + "/" + activeCreature.getMaxActionPoints() + ")");
    }

    @Override
    public void onAttackPerformed(Battle battle, Creature attacker, Creature defender,
                                  game.core.skill.Skill skill, int damage) {
        String message = attacker.getName() + " utilise " + skill.getName() + "! ";

        // Ajouter des détails sur l'efficacité basée sur les types
        double effectiveness = 1.0;
        for (game.core.type.Type attackerType : attacker.getStats().getTypes()) {
            for (game.core.type.Type defenderType : defender.getStats().getTypes()) {
                effectiveness *= attackerType.getEffectivenessAgainst(defenderType);
            }
        }

        if (effectiveness > 1.5) {
            message += "C'est super efficace! ";
        } else if (effectiveness < 0.5 && effectiveness > 0) {
            message += "Ce n'est pas très efficace... ";
        } else if (effectiveness == 0) {
            message += "Ça n'affecte pas " + defender.getName() + "... ";
        }

        message += defender.getName() + " perd " + damage + " PV!";
        notifyMessage(message);

        // Vérifier si des compétences ont gagné des niveaux
        if (skill.getExperience() >= skill.getExperienceToNextLevel()) {
            notifyMessage(skill.getName() + " monte au niveau " + skill.getLevel() + "!");
        }
    }

    @Override
    public void onCreatureDefeated(Battle battle, Creature creature) {
        notifyMessage(creature.getName() + " est K.O.!");
    }

    @Override
    public void onCreatureSwitched(Battle battle, Creature oldCreature, Creature newCreature, boolean isTeamA) {
        String team = isTeamA ? "Votre équipe" : "L'équipe adverse";
        notifyMessage(team + " change de créature. " + oldCreature.getName() +
                " revient et " + newCreature.getName() + " entre en jeu!");
    }

    @Override
    public void onBattleEnd(Battle battle, Battle.BattleState result) {
        String message;

        switch (result) {
            case TEAM_A_VICTORY:
                message = "Vous avez gagné le combat!";
                break;
            case TEAM_B_VICTORY:
                message = "Vous avez perdu le combat...";
                break;
            case ESCAPED:
                message = "Vous avez fui le combat!";
                break;
            case DRAW:
                message = "Le combat se termine par un match nul.";
                break;
            default:
                message = "Le combat est terminé.";
                break;
        }

        notifyMessage(message);
        notifyBattleCompleted(battle, result);

        // Réinitialiser la bataille courante
        if (currentBattle == battle) {
            currentBattle = null;
        }
    }
}