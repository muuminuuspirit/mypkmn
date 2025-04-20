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
        if (index < 0 || index >= teamA.size() || teamA.get(index).isDead()) {
            return false;
        }

        Creature oldCreature = activeCreatureA;
        activeCreatureA = teamA.get(index);

        // Notifier les observateurs
        for (BattleObserver observer : observers) {
            observer.onCreatureSwitched(this, oldCreature, activeCreatureA, true);
        }

        // Passer au tour suivant si ce n'était pas suite à un K.O.
        if (!oldCreature.isDead()) {
            isPlayerTurn = !isPlayerTurn;
            startNextTurn();
        }

        return true;
    }

    /**
     * Change de créature pour l'équipe B.
     *
     * @param index Index de la nouvelle créature
     * @return true si le changement a réussi
     */
    public boolean switchCreatureB(int index) {
        if (index < 0 || index >= teamB.size() || teamB.get(index).isDead()) {
            return false;
        }

        Creature oldCreature = activeCreatureB;
        activeCreatureB = teamB.get(index);

        // Notifier les observateurs
        for (BattleObserver observer : observers) {
            observer.onCreatureSwitched(this, oldCreature, activeCreatureB, false);
        }

        // Passer au tour suivant si ce n'était pas suite à un K.O.
        if (!oldCreature.isDead()) {
            isPlayerTurn = !isPlayerTurn;
            startNextTurn();
        }

        return true;
    }

    /**
     * Passe automatiquement à la prochaine créature viable de l'équipe A.
     *
     * @return true si une créature viable a été trouvée
     */
    public boolean switchToNextCreatureA() {
        for (int i = 0; i < teamA.size(); i++) {
            Creature creature = teamA.get(i);
            if (!creature.isDead() && creature != activeCreatureA) {
                return switchCreatureA(i);
            }
        }
        return false; // Aucune créature viable
    }

    /**
     * Passe automatiquement à la prochaine créature viable de l'équipe B.
     *
     * @return true si une créature viable a été trouvée
     */
    public boolean switchToNextCreatureB() {
        for (int i = 0; i < teamB.size(); i++) {
            Creature creature = teamB.get(i);
            if (!creature.isDead() && creature != activeCreatureB) {
                return switchCreatureB(i);
            }
        }
        return false; // Aucune créature viable
    }

    /**
     * Tente de fuir le combat (uniquement pour les combats sauvages).
     *
     * @return true si la fuite a réussi
     */
    public boolean tryEscape() {
        // Dans un combat contre un dresseur, la fuite est impossible
        if (teamB.size() > 1) {
            return false;
        }

        // Calcul de la chance de fuite basé sur la vitesse
        int escapeFactor = activeCreatureA.getStats().getSpeed() - activeCreatureB.getStats().getSpeed() + 30;
        double escapeChance = Math.min(0.95, Math.max(0.1, escapeFactor / 100.0));

        if (Math.random() < escapeChance) {
            endBattle(BattleState.ESCAPED);
            return true;
        }

        // Échec de la fuite, passage au tour suivant
        isPlayerTurn = !isPlayerTurn;
        startNextTurn();
        return false;
    }

    /**
     * Termine le combat avec un résultat spécifique.
     *
     * @param result Résultat du combat
     */
    public void endBattle(BattleState result) {
        if (state != BattleState.IN_PROGRESS) {
            return;
        }

        state = result;

        // Distribuer l'expérience en cas de victoire
        if (result == BattleState.TEAM_A_VICTORY) {
            distributeExperience(teamA, teamB);
        } else if (result == BattleState.TEAM_B_VICTORY) {
            distributeExperience(teamB, teamA);
        }

        // Notifier les observateurs
        for (BattleObserver observer : observers) {
            observer.onBattleEnd(this, result);
        }
    }

    /**
     * Distribue l'expérience aux créatures victorieuses.
     *
     * @param winners Équipe gagnante
     * @param losers Équipe perdante
     */
    private void distributeExperience(List<Creature> winners, List<Creature> losers) {
        int totalLosersLevel = 0;
        for (Creature loser : losers) {
            totalLosersLevel += loser.getStats().getLevel();
        }

        int baseXP = totalLosersLevel * 5 + currentTurn * 2;

        // Partager l'XP entre les participants
        List<Creature> participants = new ArrayList<>();
        for (Creature winner : winners) {
            if (!winner.isDead()) {
                participants.add(winner);
            }
        }

        if (participants.isEmpty()) {
            return;
        }

        int xpPerCreature = baseXP / participants.size();

        for (Creature creature : participants) {
            // Bonus pour créature active
            int bonus = (creature == activeCreatureA || creature == activeCreatureB) ? 10 : 0;

            // Gagner l'XP et vérifier le niveau
            boolean leveledUp = creature.getStats().gainExperience(xpPerCreature + bonus);

            if (leveledUp) {
                creature.onLevelUp();
            }
        }
    }

    // Getters

    public Creature getActiveCreatureA() {
        return activeCreatureA;
    }

    public Creature getActiveCreatureB() {
        return activeCreatureB;
    }

    public List<Creature> getTeamA() {
        return new ArrayList<>(teamA);
    }

    public List<Creature> getTeamB() {
        return new ArrayList<>(teamB);
    }

    public BattleState getState() {
        return state;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }
}