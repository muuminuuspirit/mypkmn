package game.core.battle;

import game.core.creature.Creature;
import game.core.skill.Skill;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère un combat entre des créatures.
 */
public class Battle {
    private List<Creature> teamA;
    private List<Creature> teamB;
    private Creature activeCreatureA;
    private Creature activeCreatureB;
    private List<BattleObserver> observers;
    private BattleState state;
    private int currentTurn;
    private boolean isPlayerTurn;

    /**
     * État possible d'une bataille.
     */
    public enum BattleState {
        NOT_STARTED,
        IN_PROGRESS,
        TEAM_A_VICTORY,
        TEAM_B_VICTORY,
        DRAW,
        ESCAPED
    }

    /**
     * Interface pour observer les événements d'une bataille.
     */
    public interface BattleObserver {
        void onBattleStart(Battle battle);
        void onTurnStart(Battle battle, boolean isPlayerTurn);
        void onAttackPerformed(Battle battle, Creature attacker, Creature defender, Skill skill, int damage);
        void onCreatureDefeated(Battle battle, Creature creature);
        void onCreatureSwitched(Battle battle, Creature oldCreature, Creature newCreature, boolean isTeamA);
        void onBattleEnd(Battle battle, BattleState result);
    }

    /**
     * Crée une nouvelle bataille.
     *
     * @param teamA Équipe A (généralement le joueur)
     * @param teamB Équipe B (généralement l'adversaire)
     */
    public Battle(List<Creature> teamA, List<Creature> teamB) {
        this.teamA = new ArrayList<>(teamA);
        this.teamB = new ArrayList<>(teamB);
        this.observers = new ArrayList<>();
        this.state = BattleState.NOT_STARTED;
        this.currentTurn = 0;

        if (!teamA.isEmpty()) {
            this.activeCreatureA = teamA.get(0);
        }

        if (!teamB.isEmpty()) {
            this.activeCreatureB = teamB.get(0);
        }
    }

    /**
     * Ajoute un observateur à la bataille.
     *
     * @param observer Observateur à ajouter
     */
    public void addObserver(BattleObserver observer) {
        observers.add(observer);
    }

    /**
     * Démarre la bataille.
     */
    public void start() {
        if (state != BattleState.NOT_STARTED) {
            return;
        }

        state = BattleState.IN_PROGRESS;

        // Déterminer qui commence (basé sur la vitesse)
        isPlayerTurn = activeCreatureA.getStats().getSpeed() >= activeCreatureB.getStats().getSpeed();

        // Notifier les observateurs
        for (BattleObserver observer : observers) {
            observer.onBattleStart(this);
        }

        startNextTurn();
    }

    /**
     * Commence le tour suivant.
     */
    private void startNextTurn() {
        currentTurn++;

        // Restaurer une partie des PA à chaque tour
        if (isPlayerTurn) {
            activeCreatureA.restoreActionPoints(2); // Valeur paramétrable
            // Mise à jour des effets actifs
            activeCreatureA.updateEffects();
        } else {
            activeCreatureB.restoreActionPoints(2);
            // Mise à jour des effets actifs
            activeCreatureB.updateEffects();
        }

        // Notifier les observateurs
        for (BattleObserver observer : observers) {
            observer.onTurnStart(this, isPlayerTurn);
        }
    }

    /**
     * Exécute une attaque.
     *
     * @param skill Compétence à utiliser
     * @return true si l'attaque a été effectuée
     */
    public boolean executeAttack(Skill skill) {
        Creature attacker = isPlayerTurn ? activeCreatureA : activeCreatureB;
        Creature defender = isPlayerTurn ? activeCreatureB : activeCreatureA;

        int damage = attacker.attack(defender, skill);

        if (damage > 0) {
            // Attaque réussie
            for (BattleObserver observer : observers) {
                observer.onAttackPerformed(this, attacker, defender, skill, damage);
            }

            // Vérifier si la créature défenseuse est vaincue
            if (defender.isDead()) {
                for (BattleObserver observer : observers) {
                    observer.onCreatureDefeated(this, defender);
                }

                // Essayer de remplacer la créature vaincue
                if (isPlayerTurn) {
                    if (!switchToNextCreatureB()) {
                        endBattle(BattleState.TEAM_A_VICTORY);
                        return true;
                    }
                } else {
                    if (!switchToNextCreatureA()) {
                        endBattle(BattleState.TEAM_B_VICTORY);
                        return true;
                    }
                }
            }

            // Passer au tour suivant
            isPlayerTurn = !isPlayerTurn;
            startNextTurn();
            return true;
        }

        return false; // Attaque échouée (pas assez de PA, etc.)
    }

    /**
     * Change de créature pour l'équipe A.
     *
     * @param index Index de la nouvelle créature
     * @return true si le changement a réussi
     */
    public boolean switchCreatureA(int index) {
        if (index < 0 || index