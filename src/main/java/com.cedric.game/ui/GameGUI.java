package com.cedric.game.ui;

import com.cedric.game.characters.PlayerTrainer;
import com.cedric.game.core.GameManager;
import com.cedric.game.core.creature.Creature;
import com.cedric.game.core.type.Type;
import com.cedric.game.items.Item;
import com.cedric.game.items.ItemCategory;

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
    }

    /**
     * Initialise le panneau d'inventaire.
     */
    private void initInventoryPanel() {
        inventoryPanel = new JPanel();
        inventoryPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("MON INVENTAIRE", JLabel.CENTER);
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

        inventoryPanel.add(titleLabel, BorderLayout.NORTH);
        inventoryPanel.add(scrollPane, BorderLayout.CENTER);
        inventoryPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Met à jour le panneau d'inventaire.
     */
    private void updateInventoryPanel() {
        PlayerTrainer player = gameManager.getPlayer();

        JPanel centerPanel = (JPanel) ((JScrollPane) inventoryPanel.getComponent(1)).getViewport().getView();
        centerPanel.removeAll();

        JLabel infoLabel = new JLabel("Capacité: " + player.getInventory().getTotalItems() +
                "/" + player.getInventory().getMaxCapacity());
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(infoLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        List<Item> items = player.getInventory().getItems();
        if (items.isEmpty()) {
            JLabel emptyLabel = new JLabel("Votre inventaire est vide.");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(emptyLabel);
        } else {
            // Organiser par catégorie
            for (ItemCategory category : ItemCategory.values()) {
                List<Item> categoryItems = player.getInventory().getItemsByCategory(category);

                if (!categoryItems.isEmpty()) {
                    JPanel categoryPanel = new JPanel();
                    categoryPanel.setLayout(new BoxLayout(categoryPanel, BoxLayout.Y_AXIS));
                    categoryPanel.setBorder(BorderFactory.createTitledBorder(category.getName()));
                    categoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                    for (Item item : categoryItems) {
                        JPanel itemPanel = new JPanel();
                        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.X_AXIS));
                        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        itemPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

                        int quantity = player.getInventory().getItemQuantity(item);
                        JLabel nameLabel = new JLabel(item.getName() + " x" + quantity);
                        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                        JButton useButton = new JButton("Utiliser");
                        useButton.setEnabled(false); // Désactivé pour cette version

                        itemPanel.add(nameLabel);
                        itemPanel.add(Box.createHorizontalGlue());
                        itemPanel.add(useButton);

                        categoryPanel.add(itemPanel);
                        categoryPanel.add(Box.createRigidArea(new Dimension(0, 2)));
                    }

                    centerPanel.add(categoryPanel);
                    centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
        }

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    /**
     * Charge une partie sauvegardée.
     */
    private void loadGame() {
        List<String> saveGames = gameManager.listSaveGames();

        if (saveGames.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Aucune sauvegarde trouvée.",
                    "Erreur", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] saveArray = saveGames.toArray(new String[0]);
        String selectedSave = (String) JOptionPane.showInputDialog(
                this,
                "Choisissez une sauvegarde à charger:",
                "Charger une partie",
                JOptionPane.QUESTION_MESSAGE,
                null,
                saveArray,
                saveArray[0]
        );

        if (selectedSave != null) {
            boolean success = gameManager.loadGame(selectedSave);
            if (success) {
                updateMainMenu();
                cardLayout.show(mainPanel, "mainMenu");
                JOptionPane.showMessageDialog(this,
                        "Partie chargée avec succès!",
                        "Chargement réussi", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors du chargement de la partie.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Sauvegarde la partie et quitte.
     */
    private void saveAndQuit() {
        String saveName = JOptionPane.showInputDialog(
                this,
                "Entrez un nom pour votre sauvegarde:",
                "Sauvegarder et quitter",
                JOptionPane.QUESTION_MESSAGE
        );

        if (saveName != null && !saveName.trim().isEmpty()) {
            boolean success = gameManager.saveGame(saveName);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Partie sauvegardée avec succès!",
                        "Sauvegarde réussie", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la sauvegarde de la partie.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Explore une zone sauvage.
     *
     * @param areaId ID de la zone (1=forêt, 2=plaine, 3=grotte)
     */
    private void explore(int areaId) {
        // Déterminer la zone
        String areaName;
        int areaLevel;

        switch (areaId) {
            case 1:
                areaName = "Forêt Verdoyante";
                areaLevel = 3;
                break;
            case 2:
                areaName = "Plaine Ensoleillée";
                areaLevel = 5;
                break;
            case 3:
                areaName = "Grotte Humide";
                areaLevel = 8;
                break;
            default:
                return;
        }

        // Afficher un message d'exploration
        JOptionPane.showMessageDialog(this,
                "Vous explorez " + areaName + "...",
                "Exploration", JOptionPane.INFORMATION_MESSAGE);

        // Chance de rencontre (70%)
        if (Math.random() < 0.7) {
            // Créer un combat sauvage
            com.cedric.game.core.battle.Battle wildBattle = gameManager.startWildBattle(areaLevel);
            if (wildBattle != null) {
                // Lancer le combat
                new BattleDialog(this, wildBattle, true);
            }
        } else {
            // Pas de rencontre, mais peut-être un objet
            JOptionPane.showMessageDialog(this,
                    "Vous avez exploré la zone sans rencontrer de créature.\n" +
                            "Vous trouvez un petit trésor! +50¤",
                    "Exploration", JOptionPane.INFORMATION_MESSAGE);

            // Ajouter de l'argent
            PlayerTrainer player = gameManager.getPlayer();
            player.setMoney(player.getMoney() + 50);

            // Mettre à jour le menu
            updateMainMenu();
        }
    }

    /**
     * Défie un dresseur.
     *
     * @param trainer Dresseur à défier
     */
    private void challenge(game.characters.IATrainer trainer) {
        // Afficher un message de défi
        JOptionPane.showMessageDialog(this,
                "Vous défiez " + trainer.getName() + "!",
                "Combat de dresseurs", JOptionPane.INFORMATION_MESSAGE);

        // Créer le combat
        com.cedric.game.core.battle.Battle battle = gameManager.startTrainerBattle(trainer.getId());
        if (battle != null) {
            // Lancer le combat
            new BattleDialog(this, battle, false);
        }
    }

    /**
     * Dialogue pour gérer un combat.
     */
    private class BattleDialog extends JDialog implements com.cedric.game.core.battle.BattleManager.BattleListener {
        private com.cedric.game.core.battle.Battle battle;
        private boolean isWildBattle;
        private JTextArea battleLog;
        private JPanel actionPanel;
        private JPanel switchPanel;
        private JPanel skillPanel;
        private CardLayout actionCardLayout;

        public BattleDialog(JFrame parent, com.cedric.game.core.battle.Battle battle, boolean isWildBattle) {
            super(parent, "Combat", true);
            this.battle = battle;
            this.isWildBattle = isWildBattle;

            // Enregistrer comme écouteur de bataille
            gameManager.getBattleManager().addBattleListener(this);

            // Initialiser l'interface de combat
            initBattleUI();

            // Démarrer le combat
            battle.start();

            // Afficher la boîte de dialogue
            setSize(600, 500);
            setLocationRelativeTo(parent);
            setVisible(true);
        }

        private void initBattleUI() {
            setLayout(new BorderLayout());

            // Panel du haut - Informations sur les créatures
            JPanel statusPanel = new JPanel(new GridLayout(1, 2, 10, 0));
            statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Créature du joueur
            JPanel playerCreaturePanel = createCreatureStatusPanel(battle.getActiveCreatureA(), true);

            // Créature adverse
            JPanel enemyCreaturePanel = createCreatureStatusPanel(battle.getActiveCreatureB(), false);

            statusPanel.add(playerCreaturePanel);
            statusPanel.add(enemyCreaturePanel);

            // Panel central - Log de combat
            battleLog = new JTextArea();
            battleLog.setEditable(false);
            battleLog.setLineWrap(true);
            battleLog.setWrapStyleWord(true);
            JScrollPane logScrollPane = new JScrollPane(battleLog);
            logScrollPane.setBorder(BorderFactory.createTitledBorder("Déroulement du combat"));

            // Panel du bas - Actions
            JPanel bottomPanel = new JPanel(new BorderLayout());

            // Panel pour les boutons d'action
            actionPanel = new JPanel();
            actionCardLayout = new CardLayout();
            actionPanel.setLayout(actionCardLayout);

            // Panel principal d'action
            JPanel mainActionPanel = new JPanel(new GridLayout(1, 4, 5, 0));

            JButton attackButton = new JButton("Attaquer");
            attackButton.addActionListener(e -> actionCardLayout.show(actionPanel, "skills"));

            JButton switchButton = new JButton("Changer");
            switchButton.addActionListener(e -> actionCardLayout.show(actionPanel, "switch"));

            JButton itemButton = new JButton("Objet");
            itemButton.setEnabled(false); // Non implémenté dans cette version

            JButton fleeButton = new JButton("Fuir");
            fleeButton.setEnabled(isWildBattle);
            fleeButton.addActionListener(e -> tryEscape());

            mainActionPanel.add(attackButton);
            mainActionPanel.add(switchButton);
            mainActionPanel.add(itemButton);
            mainActionPanel.add(fleeButton);

            // Panel de compétences
            skillPanel = new JPanel(new GridLayout(0, 2, 5, 5));
            skillPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            updateSkillPanel();

            // Panel de changement de créature
            switchPanel = new JPanel(new GridLayout(0, 2, 5, 5));
            switchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            updateSwitchPanel();

            // Panel de retour
            JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton backButton = new JButton("Retour");
            backButton.addActionListener(e -> actionCardLayout.show(actionPanel, "main"));
            backPanel.add(backButton);

            // Ajouter les panels d'action
            JPanel skillActionPanel = new JPanel(new BorderLayout());
            skillActionPanel.add(skillPanel, BorderLayout.CENTER);
            skillActionPanel.add(backPanel, BorderLayout.SOUTH);

            JPanel switchActionPanel = new JPanel(new BorderLayout());
            switchActionPanel.add(switchPanel, BorderLayout.CENTER);
            switchActionPanel.add(backPanel, BorderLayout.SOUTH);

            actionPanel.add(mainActionPanel, "main");
            actionPanel.add(skillActionPanel, "skills");
            actionPanel.add(switchActionPanel, "switch");
            actionCardLayout.show(actionPanel, "main");

            bottomPanel.add(actionPanel, BorderLayout.CENTER);

            // Ajouter tous les panels à la boîte de dialogue
            add(statusPanel, BorderLayout.NORTH);
            add(logScrollPane, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);

            // Afficher un message de début
            battleLog.append("Le combat commence!\n");
            if (isWildBattle) {
                battleLog.append("Une créature sauvage " + battle.getActiveCreatureB().getName() +
                        " (Niv." + battle.getActiveCreatureB().getStats().getLevel() + ") apparaît!\n");
            } else {
                battleLog.append("Vous affrontez un dresseur avec " +
                        battle.getTeamB().size() + " créatures!\n");
            }
            battleLog.append("Vous envoyez " + battle.getActiveCreatureA().getName() + "!\n");
        }

        private JPanel createCreatureStatusPanel(Creature creature, boolean isPlayer) {
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createTitledBorder(
                    isPlayer ? "Votre créature" : "Créature adverse"));

            JLabel nameLabel = new JLabel(creature.getName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel levelLabel = new JLabel("Niveau " + creature.getStats().getLevel());
            levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel hpLabel = new JLabel("PV: " + creature.getStats().getHealth() +
                    "/" + creature.getStats().getMaxHealth());
            hpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Ajouter une barre de vie
            JProgressBar hpBar = new JProgressBar(0, creature.getStats().getMaxHealth());
            hpBar.setValue(creature.getStats().getHealth());
            hpBar.setStringPainted(true);
            hpBar.setString(creature.getStats().getHealth() + "/" +
                    creature.getStats().getMaxHealth());
            hpBar.setForeground(new Color(0, 180, 0));
            hpBar.setAlignmentX(Component.CENTER_ALIGNMENT);

            String types = "";
            for (Type type : creature.getStats().getTypes()) {
                if (!types.isEmpty()) types += ", ";
                types += type.getName();
            }
            JLabel typeLabel = new JLabel("Types: " + types);
            typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Uniquement pour la créature du joueur
            if (isPlayer) {
                JLabel apLabel = new JLabel("PA: " + creature.getCurrentActionPoints() +
                        "/" + creature.getMaxActionPoints());
                apLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(Box.createRigidArea(new Dimension(0, 5)));
                panel.add(apLabel);
            }

            panel.add(nameLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(levelLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(hpBar);
            panel.add(Box.createRigidArea(new Dimension(0, 5)));
            panel.add(typeLabel);

            return panel;
        }

        private void updateSkillPanel() {
            skillPanel.removeAll();

            Creature playerCreature = battle.getActiveCreatureA();
            List<game.core.skill.Skill> skills = playerCreature.getActiveSkills();

            for (game.core.skill.Skill skill : skills) {
                JButton skillButton = new JButton(skill.getName());
                skillButton.setToolTipText("Type: " + skill.getType().getName() +
                        " | Puissance: " + skill.getPower() +
                        " | PA: " + skill.getActionPointCost());

                // Désactiver le bouton si pas assez de PA
                boolean enabled = skill.getActionPointCost() <= playerCreature.getCurrentActionPoints();
                skillButton.setEnabled(enabled);

                skillButton.addActionListener(e -> executeAttack(skill));

                skillPanel.add(skillButton);
            }

            skillPanel.revalidate();
            skillPanel.repaint();
        }

        private void updateSwitchPanel() {
            switchPanel.removeAll();

            List<Creature> creatures = battle.getTeamA();
            Creature activeCreature = battle.getActiveCreatureA();

            for (int i = 0; i < creatures.size(); i++) {
                Creature creature = creatures.get(i);
                JButton creatureButton = new JButton(creature.getName());
                creatureButton.setToolTipText("PV: " + creature.getStats().getHealth() +
                        "/" + creature.getStats().getMaxHealth());

                // Désactiver le bouton si c'est la créature active ou K.O.
                boolean enabled = creature != activeCreature && !creature.isDead();
                creatureButton.setEnabled(enabled);

                if (creature == activeCreature) {
                    creatureButton.setText(creature.getName() + " [Actif]");
                } else if (creature.isDead()) {
                    creatureButton.setText(creature.getName() + " [K.O.]");
                }

                final int index = i;
                creatureButton.addActionListener(e -> switchCreature(index));

                switchPanel.add(creatureButton);
            }

            switchPanel.revalidate();
            switchPanel.repaint();
        }

        private void executeAttack(game.core.skill.Skill skill) {
            if (!battle.isPlayerTurn()) {
                return;
            }

            boolean success = battle.executeAttack(skill);
            if (!success) {
                battleLog.append("Impossible d'utiliser cette compétence!\n");
            }

            // Mise à jour des panneaux
            updateSkillPanel();
            updateSwitchPanel();
        }

        private void switchCreature(int index) {
            if (!battle.isPlayerTurn()) {
                return;
            }

            boolean success = battle.switchCreatureA(index);
            if (!success) {
                battleLog.append("Impossible de changer de créature!\n");
            }

            // Mise à jour des panneaux
            updateSkillPanel();
            updateSwitchPanel();
        }

        private void tryEscape() {
            if (!isWildBattle || !battle.isPlayerTurn()) {
                return;
            }

            boolean success = battle.tryEscape();
            if (!success) {
                battleLog.append("Impossible de fuir le combat!\n");
            }
        }

        @Override
        public void onBattleCreated(com.cedric.game.core.battle.Battle battle) {
            // Non utilisé ici
        }

        @Override
        public void onBattleCompleted(com.cedric.game.core.battle.Battle battle,
                                      com.cedric.game.core.battle.Battle.BattleState result) {
            // Fermer la boîte de dialogue à la fin du combat
            dispose();

            // Se désenregistrer comme écouteur
            gameManager.getBattleManager().removeBattleListener(this);

            // Mettre à jour le menu principal
            updateMainMenu();

            // Afficher un message de fin de combat
            String message = "";
            switch (result) {
                case TEAM_A_VICTORY:
                    message = "Vous avez gagné le combat!";
                    if (isWildBattle) {
                        // Offrir la capture pour les combats sauvages
                        offerCapture();
                    } else {
                        // Récompense pour avoir battu un dresseur
                        int rewardMoney = 100 * battle.getCurrentTurn();
                        PlayerTrainer player = gameManager.getPlayer();
                        player.setMoney(player.getMoney() + rewardMoney);
                        JOptionPane.showMessageDialog(GameGUI.this,
                                "Vous avez gagné " + rewardMoney + "¤!",
                                "Victoire!", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;
                case TEAM_B_VICTORY:
                    message = "Vous avez perdu le combat!";
                    JOptionPane.showMessageDialog(GameGUI.this,
                            "Vos créatures sont fatiguées.\nElles récupèrent à moitié de leurs PV.",
                            "Défaite", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case ESCAPED:
                    message = "Vous avez fui le combat!";
                    break;
                default:
                    message = "Le combat est terminé.";
                    break;
            }

            JOptionPane.showMessageDialog(GameGUI.this, message,
                    "Fin du combat", JOptionPane.INFORMATION_MESSAGE);
        }

        @Override
        public void onBattleMessage(String message) {
            // Ajouter les messages au log de combat
            battleLog.append(message + "\n");

            // Faire défiler vers le bas
            battleLog.setCaretPosition(battleLog.getDocument().getLength());
        }

        /**
         * Offre la possibilité de capturer une créature sauvage.
         */
        private void offerCapture() {
            int choice = JOptionPane.showConfirmDialog(GameGUI.this,
                    "Voulez-vous tenter de capturer cette créature?",
                    "Capture", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                Creature wildCreature = battle.getActiveCreatureB();
                boolean captured = gameManager.getPlayer().captureCreature(wildCreature, 50);

                if (captured) {
                    JOptionPane.showMessageDialog(GameGUI.this,
                            "Félicitations! Vous avez capturé " + wildCreature.getName() + "!",
                            "Capture réussie", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(GameGUI.this,
                            "La capture a échoué. La créature s'est enfuie!",
                            "Échec de capture", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
}