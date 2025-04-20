package com.cedric.game.ui;

import com.cedric.game.characters.PlayerTrainer;
import com.cedric.game.core.GameManager;
import com.cedric.game.core.creature.Creature;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Interface graphique principale du jeu.
 */
public class GameGUI extends JFrame {
    private GameManager gameManager;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Panneaux différents
    private JPanel titlePanel;
    private JPanel newGamePanel;
    private JPanel mainMenuPanel;
    private JPanel explorePanel;
    private JPanel trainerPanel;
    private JPanel creaturePanel;
    private JPanel inventoryPanel;

    /**
     * Crée une nouvelle interface graphique.
     */
    public GameGUI() {
        super("Jeu de Créatures");
        this.gameManager = GameManager.getInstance();

        // Initialiser l'interface
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Créer le layout principal
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialiser les différents panneaux
        initTitlePanel();
        initNewGamePanel();
        initMainMenuPanel();
        initExplorePanel();
        initTrainerPanel();
        initCreaturePanel();
        initInventoryPanel();

        // Ajouter les panneaux au panel principal
        mainPanel.add(titlePanel, "title");
        mainPanel.add(newGamePanel, "newGame");
        mainPanel.add(mainMenuPanel, "mainMenu");
        mainPanel.add(explorePanel, "explore");
        mainPanel.add(trainerPanel, "trainer");
        mainPanel.add(creaturePanel, "creature");
        mainPanel.add(inventoryPanel, "inventory");

        // Afficher le panneau de titre au démarrage
        cardLayout.show(mainPanel, "title");

        // Ajouter le panel principal au frame
        add(mainPanel);

        // Rendre visible
        setVisible(true);
    }

    /**
     * Initialise le panneau de titre.
     */
    private void initTitlePanel() {
        titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        // Créer des composants
        JLabel titleLabel = new JLabel("JEU DE CRÉATURES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Une aventure captivante de dressage");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton newGameButton = new JButton("Nouvelle Partie");
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameButton.addActionListener(e -> cardLayout.show(mainPanel, "newGame"));

        JButton loadGameButton = new JButton("Charger une Partie");
        loadGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadGameButton.addActionListener(e -> loadGame());

        JButton exitButton = new JButton("Quitter");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> System.exit(0));

        // Ajouter de l'espace
        titlePanel.add(Box.createVerticalGlue());

        // Ajouter les composants au panneau
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        titlePanel.add(subtitleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 50)));
        titlePanel.add(newGameButton);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(loadGameButton);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(exitButton);

        // Ajouter de l'espace
        titlePanel.add(Box.createVerticalGlue());
    }

    /**
     * Initialise le panneau de nouvelle partie.
     */
    private void initNewGamePanel() {
        newGamePanel = new JPanel();
        newGamePanel.setLayout(new BoxLayout(newGamePanel, BoxLayout.Y_AXIS));

        // Créer des composants
        JLabel titleLabel = new JLabel("NOUVELLE PARTIE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel nameLabel = new JLabel("Nom du dresseur: ");
        JTextField nameField = new JTextField(15);
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        JLabel creatureLabel = new JLabel("Choisissez votre créature de départ:");
        creatureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Options de créature
        JPanel creaturePanel = new JPanel();
        creaturePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        ButtonGroup creatureGroup = new ButtonGroup();
        JRadioButton fireButton = new JRadioButton("Flamby (Type: Feu)");
        JRadioButton waterButton = new JRadioButton("Aquali (Type: Eau)");
        JRadioButton natureButton = new JRadioButton("Leafoo (Type: Nature)");

        fireButton.setSelected(true);
        creatureGroup.add(fireButton);
        creatureGroup.add(waterButton);
        creatureGroup.add(natureButton);

        creaturePanel.add(fireButton);
        creaturePanel.add(waterButton);
        creaturePanel.add(natureButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton startButton = new JButton("Commencer l'aventure");
        JButton backButton = new JButton("Retour");

        buttonPanel.add(startButton);
        buttonPanel.add(backButton);

        // Actions des boutons
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = nameField.getText().trim();
                if (playerName.isEmpty()) {
                    JOptionPane.showMessageDialog(GameGUI.this,
                            "Veuillez entrer un nom de dresseur.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Déterminer la créature choisie
                String creatureType;
                if (fireButton.isSelected()) {
                    creatureType = "Feu";
                } else if (waterButton.isSelected()) {
                    creatureType = "Eau";
                } else {
                    creatureType = "Nature";
                }

                // Créer la créature de départ
                Type selectedType = null;
                for (Type type : gameManager.getAvailableTypes()) {
                    if (type.getName().equals(creatureType)) {
                        selectedType = type;
                        break;
                    }
                }

                if (selectedType == null) {
                    JOptionPane.showMessageDialog(GameGUI.this,
                            "Erreur lors de la création de la créature.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String creatureName;
                if (creatureType.equals("Feu")) {
                    creatureName = "Flamby";
                } else if (creatureType.equals("Eau")) {
                    creatureName = "Aquali";
                } else {
                    creatureName = "Leafoo";
                }

                Creature starterCreature = new Creature(
                        creatureType.toLowerCase() + "_starter",
                        creatureName,
                        selectedType
                );

                // Initialiser le jeu
                gameManager.initializeNewGame(playerName, starterCreature);

                // Afficher le menu principal
                updateMainMenu();
                cardLayout.show(mainPanel, "mainMenu");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "title"));

        // Ajouter de l'espace
        newGamePanel.add(Box.createVerticalGlue());

        // Ajouter les composants au panneau
        newGamePanel.add(titleLabel);
        newGamePanel.add(Box.createRigidArea(new Dimension(0, 30)));
        newGamePanel.add(namePanel);
        newGamePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        newGamePanel.add(creatureLabel);
        newGamePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        newGamePanel.add(creaturePanel);
        newGamePanel.add(Box.createRigidArea(new Dimension(0, 30)));
        newGamePanel.add(buttonPanel);

        // Ajouter de l'espace
        newGamePanel.add(Box.createVerticalGlue());
    }

    /**
     * Initialise le panneau de menu principal.
     */
    private void initMainMenuPanel() {
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new BorderLayout());

        // Panel d'info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel playerLabel = new JLabel("Dresseur: ");
        JLabel badgeLabel = new JLabel("Badges: ");
        JLabel moneyLabel = new JLabel("Argent: ");

        infoPanel.add(playerLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(badgeLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(moneyLabel);

        // Panel de boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton exploreButton = new JButton("Explorer la nature sauvage");
        JButton trainerButton = new JButton("Défier un dresseur");
        JButton creatureButton = new JButton("Gérer mes créatures");
        JButton inventoryButton = new JButton("Gérer mon inventaire");
        JButton saveButton = new JButton("Sauvegarder et quitter");

        buttonPanel.add(exploreButton);
        buttonPanel.add(trainerButton);
        buttonPanel.add(creatureButton);
        buttonPanel.add(inventoryButton);
        buttonPanel.add(saveButton);

        // Actions des boutons
        exploreButton.addActionListener(e -> {
            updateExplorePanel();
            cardLayout.show(mainPanel, "explore");
        });

        trainerButton.addActionListener(e -> {
            updateTrainerPanel();
            cardLayout.show(mainPanel, "trainer");
        });

        creatureButton.addActionListener(e -> {
            updateCreaturePanel();
            cardLayout.show(mainPanel, "creature");
        });

        inventoryButton.addActionListener(e -> {
            updateInventoryPanel();
            cardLayout.show(mainPanel, "inventory");
        });

        saveButton.addActionListener(e -> saveAndQuit());

        // Ajouter les panneaux au menu principal
        mainMenuPanel.add(infoPanel, BorderLayout.NORTH);
        mainMenuPanel.add(buttonPanel, BorderLayout.CENTER);
    }

    /**
     * Met à jour les informations du menu principal.
     */
    private void updateMainMenu() {
        PlayerTrainer player = gameManager.getPlayer();

        if (player == null) {
            return;
        }

        JPanel infoPanel = (JPanel) mainMenuPanel.getComponent(0);

        JLabel playerLabel = (JLabel) infoPanel.getComponent(0);
        JLabel badgeLabel = (JLabel) infoPanel.getComponent(2);
        JLabel moneyLabel = (JLabel) infoPanel.getComponent(4);

        playerLabel.setText("Dresseur: " + player.getName() + " (Rang " + player.getPlayerRank() + ")");
        badgeLabel.setText("Badges: " + player.getBadges());
        moneyLabel.setText("Argent: " + player.getMoney() + "¤");
    }

    /**
     * Initialise le panneau d'exploration.
     */
    private void initExplorePanel() {
        explorePanel = new JPanel();
        explorePanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("EXPLORATION", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel areaPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        areaPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton forestButton = new JButton("Forêt Verdoyante (Niv. 1-5)");
        JButton plainsButton = new JButton("Plaine Ensoleillée (Niv. 3-8)");
        JButton caveButton = new JButton("Grotte Humide (Niv. 5-10)");

        areaPanel.add(forestButton);
        areaPanel.add(plainsButton);
        areaPanel.add(caveButton);

        JButton backButton = new JButton("Retour au menu");

        // Actions des boutons
        forestButton.addActionListener(e -> explore(1));
        plainsButton.addActionListener(e -> explore(2));
        caveButton.addActionListener(e -> explore(3));

        backButton.addActionListener(e -> {
            updateMainMenu();
            cardLayout.show(mainPanel, "mainMenu");
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);

        explorePanel.add(titleLabel, BorderLayout.NORTH);
        explorePanel.add(areaPanel, BorderLayout.CENTER);
        explorePanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Met à jour le panneau d'exploration.
     */
    private void updateExplorePanel() {
        // Rien à mettre à jour pour l'instant
    }

    /**
     * Initialise le panneau des dresseurs.
     */
    private void initTrainerPanel() {
        trainerPanel = new JPanel();
        trainerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("DRESSEURS", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(centerPanel);

        JButton backButton = new JButton("Retour au menu");
        backButton.addActionListener(e -> {
            updateMainMenu();
            cardLayout.show(mainPanel, "mainMenu");
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);

        trainerPanel.add(titleLabel, BorderLayout.NORTH);
        trainerPanel.add(scrollPane, BorderLayout.CENTER);
        trainerPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Met à jour le panneau des dresseurs.
     */
    private void updateTrainerPanel() {
        List<game.characters.IATrainer> npcs = gameManager.getNpcs();

        JPanel centerPanel = (JPanel) ((JScrollPane) trainerPanel.getComponent(1)).getViewport().getView();
        centerPanel.removeAll();

        if (npcs.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucun dresseur disponible actuellement.");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(emptyLabel);
        } else {
            for (game.characters.IATrainer trainer : npcs) {
                JPanel trainerItemPanel = new JPanel();
                trainerItemPanel.setLayout(new BoxLayout(trainerItemPanel, BoxLayout.X_AXIS));
                trainerItemPanel.setBorder(BorderFactory.createEtchedBorder());
                trainerItemPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));

                String title = trainer.isGymLeader() ? "Chef de Gym" : "Dresseur";
                JLabel nameLabel = new JLabel(title + " " + trainer.getName() +
                        " (Difficulté: " + trainer.getDifficulty() + ")");
                nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                JButton challengeButton = new JButton("Défier");
                challengeButton.addActionListener(e -> challenge(trainer));

                trainerItemPanel.add(nameLabel);
                trainerItemPanel.add(Box.createHorizontalGlue());
                trainerItemPanel.add(challengeButton);

                centerPanel.add(trainerItemPanel);
                centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    /**
     * Initialise le panneau de gestion des créatures.
     */
    private void initCreaturePanel() {
        creaturePanel = new JPanel();
        creaturePanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("MES CRÉATURES", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(centerPanel);

        JButton backButton = new JButton("Retour au menu");
        backButton.addActionListener(e -> {
            updateMainMenu();
            cardLayout.show(mainPanel, "mainMenu");
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);

        creaturePanel.add(titleLabel, BorderLayout.NORTH);
        creaturePanel.add(scrollPane, BorderLayout.CENTER);
        creaturePanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Affiche les détails d'une créature.
     *
     * @param creature Créature à afficher
     */
    private void showCreatureDetails(Creature creature) {
        JDialog detailsDialog = new JDialog(this, "Détails de " + creature.getName(), true);
        detailsDialog.setSize(400, 500);
        detailsDialog.setLocationRelativeTo(this);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // En-tête
        JLabel nameLabel = new JLabel(creature.getName().toUpperCase());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Types
        String types = "";
        for (Type type : creature.getStats().getTypes()) {
            if (!types.isEmpty()) {
                types += ", ";
            }
            types += type.getName();
        }
        JLabel typeLabel = new JLabel("Types: " + types);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Niveau et XP
        JLabel levelLabel = new JLabel("Niveau: " + creature.getStats().getLevel());
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel xpLabel = new JLabel("XP: " + creature.getStats().getXp() +
                "/" + creature.getStats().getMaxXpNextLevel());
        xpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Stats
        JPanel statsPanel = new JPanel(new GridLayout(6, 2, 10, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistiques"));

        statsPanel.add(new JLabel("Points de Vie:"));
        statsPanel.add(new JLabel(creature.getStats().getHealth() + "/" +
                creature.getStats().getMaxHealth()));

        statsPanel.add(new JLabel("Points d'Action:"));
        statsPanel.add(new JLabel(creature.getCurrentActionPoints() + "/" +
                creature.getMaxActionPoints()));

        statsPanel.add(new JLabel("Force:"));
        statsPanel.add(new JLabel(String.valueOf(creature.getStats().getStrength())));

        statsPanel.add(new JLabel("Constitution:"));
        statsPanel.add(new JLabel(String.valueOf(creature.getStats().getConstitution())));

        statsPanel.add(new JLabel("Esprit:"));
        statsPanel.add(new JLabel(String.valueOf(creature.getStats().getSpirit())));

        statsPanel.add(new JLabel("Mental:"));
        statsPanel.add(new JLabel(String.valueOf(creature.getStats().getMental())));

        statsPanel.add(new JLabel("Vitesse:"));
        statsPanel.add(new JLabel(String.valueOf(creature.getStats().getSpeed())));

        // Compétences
        JPanel skillsPanel = new JPanel();
        skillsPanel.setLayout(new BoxLayout(skillsPanel, BoxLayout.Y_AXIS));
        skillsPanel.setBorder(BorderFactory.createTitledBorder("Compétences"));

        List<game.core.skill.Skill> skills = creature.getActiveSkills();
        if (skills.isEmpty()) {
            JLabel noSkillLabel = new JLabel("Aucune compétence active.");
            noSkillLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            skillsPanel.add(noSkillLabel);
        } else {
            for (game.core.skill.Skill skill : skills) {
                JPanel skillItemPanel = new JPanel();
                skillItemPanel.setLayout(new BoxLayout(skillItemPanel, BoxLayout.X_AXIS));
                skillItemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel skillNameLabel = new JLabel(skill.getName());
                JLabel skillInfoLabel = new JLabel("(Niv." + skill.getLevel() +
                        ", PA: " + skill.getActionPointCost() + ")");

                skillItemPanel.add(skillNameLabel);
                skillItemPanel.add(Box.createRigidArea(new Dimension(10, 0)));
                skillItemPanel.add(skillInfoLabel);

                skillsPanel.add(skillItemPanel);
                skillsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton skillsButton = new JButton("Gérer les compétences");
        JButton closeButton = new JButton("Fermer");

        skillsButton.addActionListener(e -> manageCreatureSkills(creature));
        closeButton.addActionListener(e -> detailsDialog.dispose());

        buttonPanel.add(skillsButton);
        buttonPanel.add(closeButton);

        // Ajouter tous les composants
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(typeLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(levelLabel);
        detailsPanel.add(xpLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        detailsPanel.add(statsPanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        detailsPanel.add(skillsPanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        detailsPanel.add(buttonPanel);

        detailsDialog.add(detailsPanel);
        detailsDialog.setVisible(true);
    }

    /**
     * Ouvre une interface de gestion des compétences pour une créature.
     *
     * @param creature Créature à gérer
     */
    private void manageCreatureSkills(Creature creature) {
        JOptionPane.showMessageDialog(this,
                "La gestion des compétences n'est pas encore implémentée dans cette version.",
                "En développement", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Met à jour le panneau de gestion des créatures.
     */
    private void updateCreaturePanel() {
        PlayerTrainer player = gameManager.getPlayer();
        List<Creature> creatures = player.getCreatures();

        JPanel centerPanel = (JPanel) ((JScrollPane) creaturePanel.getComponent(1)).getViewport().getView();
        centerPanel.removeAll();

        if (creatures.isEmpty()) {
            JLabel emptyLabel = new JLabel("Vous n'avez pas de créatures.");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(emptyLabel);
        } else {
            for (Creature creature : creatures) {
                JPanel creatureItemPanel = new JPanel();
                creatureItemPanel.setLayout(new BoxLayout(creatureItemPanel, BoxLayout.X_AXIS));
                creatureItemPanel.setBorder(BorderFactory.createEtchedBorder());
                creatureItemPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));

                String types = "";
                for (Type type : creature.getStats().getTypes()) {
                    if (!types.isEmpty()) {
                        types += ", ";
                    }
                    types += type.getName();
                }

                JLabel nameLabel = new JLabel(creature.getName() + " (Niv." + creature.getStats().getLevel() +
                        "): " + creature.getStats().getHealth() + "/" + creature.getStats().getMaxHealth() + " PV");
                nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                JLabel typeLabel = new JLabel("Types: " + types);
                typeLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                JButton detailsButton = new JButton("Détails");
                detailsButton.addActionListener(e -> showCreatureDetails(creature));

                creatureItemPanel.add(nameLabel);
                creatureItemPanel.add(typeLabel);
                creatureItemPanel.add(Box.createHorizontalGlue());
                creatureItemPanel.add(detailsButton);

                centerPanel.add(creatureItemPanel);
                centerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        centerPanel.revalidate();
        centerPanel.repaint();