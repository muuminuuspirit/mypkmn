package game.core.skill;

import game.core.creature.Creature;
import game.core.type.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Représente l'arbre de compétences d'une créature.
 */
public class SkillTree {
    private List<SkillNode> rootNodes;
    private Map<String, SkillNode> allNodes;
    private Type primaryType;

    /**
     * Crée un nouvel arbre de compétences.
     *
     * @param primaryType Type principal de la créature
     */
    public SkillTree(Type primaryType) {
        this.rootNodes = new ArrayList<>();
        this.allNodes = new HashMap<>();
        this.primaryType = primaryType;

        // Initialiser l'arbre avec des compétences de base
        initializeBaseSkills();
    }

    /**
     * Initialise l'arbre avec des compétences de base selon le type.
     */
    private void initializeBaseSkills() {
        // Compétence de base - Attaque basique
        Skill basicAttack = new Skill(
                "skill_basic_" + primaryType.getName().toLowerCase(),
                "Attaque basique",
                "Une attaque simple qui utilise peu d'énergie.",
                primaryType,
                5, // Puissance
                95, // Précision
                1,  // Coût en PA
                Skill.SkillCategory.PHYSICAL
        );

        SkillNode basicNode = new SkillNode("node_basic_attack", basicAttack, 0);
        addNode(basicNode);
        rootNodes.add(basicNode);

        // Compétence de défense
        Skill basicDefense = new Skill(
                "skill_defense_" + primaryType.getName().toLowerCase(),
                "Défense basique",
                "Renforce temporairement la défense.",
                primaryType,
                0, // Pas de dégâts directs
                100, // Toujours réussi
                1,  // Coût en PA
                Skill.SkillCategory.STATUS
        );

        SkillEffect defenseEffect = new SkillEffect(
                "effect_def_boost",
                "Bouclier",
                SkillEffect.EffectType.STAT_BOOST,
                3, // Dure 3 tours
                5, // +5 en défense
                SkillEffect.StatisticType.CONSTITUTION,
                1.0 // 100% de chance
        );

        basicDefense.addEffect(defenseEffect);

        SkillNode defenseNode = new SkillNode("node_basic_defense", basicDefense, 0);
        addNode(defenseNode);
        rootNodes.add(defenseNode);

        // Ajouter des compétences spécifiques au type
        addTypeSpecificSkills();

        // Ajouter la structure de l'arbre - les nœuds suivants
        buildSkillTreeStructure();
    }

    /**
     * Ajoute des compétences spécifiques au type principal.
     */
    private void addTypeSpecificSkills() {
        String typeName = primaryType.getName();

        // Attaque de type spécifique niveau 1
        Skill typeAttack = new Skill(
                "skill_" + typeName.toLowerCase() + "_1",
                typeName + " niveau 1",
                "Une attaque de type " + typeName + " de niveau basique.",
                primaryType,
                15, // Puissance
                90, // Précision
                2,  // Coût en PA
                Skill.SkillCategory.SPECIAL
        );

        SkillNode typeNode = new SkillNode("node_" + typeName.toLowerCase() + "_1", typeAttack, 1);
        addNode(typeNode);
        rootNodes.add(typeNode);

        // Effet de statut selon le type
        SkillEffect.EffectType effectType = SkillEffect.EffectType.STATUS_EFFECT;
        String effectName = "Effet " + typeName;

        switch (typeName) {
            case "Feu":
                effectName = "Brûlure";
                break;
            case "Eau":
                effectName = "Trempe";
                break;
            case "Nature":
                effectName = "Poison";
                break;
            case "Électrique":
                effectName = "Paralysie";
                break;
            case "Ténèbres":
                effectName = "Peur";
                break;
            // Autres types...
        }

        Skill statusSkill = new Skill(
                "skill_status_" + typeName.toLowerCase(),
                effectName,
                "Applique un effet de statut " + effectName.toLowerCase() + ".",
                primaryType,
                5, // Faible puissance
                85, // Précision moyenne
                2,  // Coût en PA
                Skill.SkillCategory.STATUS
        );

        SkillEffect statusEffect = new SkillEffect(
                "effect_" + typeName.toLowerCase() + "_status",
                effectName,
                effectType,
                3, // Dure 3 tours
                5, // Intensité moyenne
                SkillEffect.StatisticType.HEALTH, // Affecte généralement la santé
                0.7 // 70% de chance
        );

        statusSkill.addEffect(statusEffect);

        SkillNode statusNode = new SkillNode("node_status_" + typeName.toLowerCase(), statusSkill, 2);
        addNode(statusNode);

        // Connecter au nœud racine
        if (!rootNodes.isEmpty()) {
            rootNodes.get(0).addChild(statusNode);
        }
    }

    /**
     * Construit la structure de l'arbre avec des nœuds connectés.
     */
    private void buildSkillTreeStructure() {
        // Exemple de structure d'arbre plus avancée
        // Niveau 2 - Compétences intermédiaires
        for (SkillNode rootNode : rootNodes) {
            if (rootNode.getSkill().getCategory() == Skill.SkillCategory.PHYSICAL) {
                // Branch out physical skills
                createPhysicalBranch(rootNode);
            } else if (rootNode.getSkill().getCategory() == Skill.SkillCategory.SPECIAL) {
                // Branch out special skills
                createSpecialBranch(rootNode);
            } else if (rootNode.getSkill().getCategory() == Skill.SkillCategory.STATUS) {
                // Branch out status skills
                createStatusBranch(rootNode);
            }
        }
    }

    /**
     * Crée une branche de compétences physiques.
     *
     * @param parentNode Nœud parent
     */
    private void createPhysicalBranch(SkillNode parentNode) {
        // Compétence physique avancée
        Skill improvedPhysical = new Skill(
                "skill_physical_improved",
                "Coup puissant",
                "Une attaque physique plus puissante mais moins précise.",
                primaryType,
                30, // Puissance élevée
                80, // Précision réduite
                3,  // Coût en PA
                Skill.SkillCategory.PHYSICAL
        );

        SkillNode improvedNode = new SkillNode("node_physical_improved", improvedPhysical, 3);
        addNode(improvedNode);
        parentNode.addChild(improvedNode);

        // Compétence physique rapide
        Skill quickPhysical = new Skill(
                "skill_physical_quick",
                "Attaque rapide",
                "Une attaque plus faible mais qui frappe en premier.",
                primaryType,
                10, // Faible puissance
                95, // Haute précision
                1,  // Faible coût en PA
                Skill.SkillCategory.PHYSICAL
        );

        SkillNode quickNode = new SkillNode("node_physical_quick", quickPhysical, 2);
        addNode(quickNode);
        parentNode.addChild(quickNode);
    }

    /**
     * Crée une branche de compétences spéciales.
     *
     * @param parentNode Nœud parent
     */
    private void createSpecialBranch(SkillNode parentNode) {
        // Compétence spéciale avancée
        String typeName = primaryType.getName();

        Skill improvedSpecial = new Skill(
                "skill_" + typeName.toLowerCase() + "_2",
                typeName + " niveau 2",
                "Une attaque de type " + typeName + " plus puissante.",
                primaryType,
                25, // Puissance moyenne-élevée
                85, // Précision
                3,  // Coût en PA
                Skill.SkillCategory.SPECIAL
        );

        SkillNode improvedNode = new SkillNode("node_" + typeName.toLowerCase() + "_2", improvedSpecial, 4);
        addNode(improvedNode);
        parentNode.addChild(improvedNode);

        // Attaque de zone
        Skill areaSpecial = new Skill(
                "skill_" + typeName.toLowerCase() + "_area",
                "Vague de " + typeName,
                "Une attaque de type " + typeName + " qui touche une zone.",
                primaryType,
                20, // Puissance modérée
                80, // Précision un peu réduite
                4,  // Coût en PA élevé
                Skill.SkillCategory.SPECIAL
        );

        SkillNode areaNode = new SkillNode("node_" + typeName.toLowerCase() + "_area", areaSpecial, 5);
        addNode(areaNode);
        parentNode.addChild(areaNode);
    }

    /**
     * Crée une branche de compétences de statut.
     *
     * @param parentNode Nœud parent
     */
    private void createStatusBranch(SkillNode parentNode) {
        // Amélioration de soi
        Skill selfBuff = new Skill(
                "skill_self_buff",
                "Concentration",
                "Augmente temporairement ses propres statistiques.",
                primaryType,
                0, // Pas de dégâts
                100, // Toujours réussi
                2,  // Coût en PA
                Skill.SkillCategory.STATUS
        );

        SkillEffect buffEffect1 = new SkillEffect(
                "effect_str_boost",
                "Force améliorée",
                SkillEffect.EffectType.STAT_BOOST,
                3, // Dure 3 tours
                7, // +7 en force
                SkillEffect.StatisticType.STRENGTH,
                1.0 // 100% de chance
        );

        SkillEffect buffEffect2 = new SkillEffect(
                "effect_spd_boost",
                "Vitesse améliorée",
                SkillEffect.EffectType.STAT_BOOST,
                3, // Dure 3 tours
                5, // +5 en vitesse
                SkillEffect.StatisticType.SPEED,
                1.0 // 100% de chance
        );

        selfBuff.addEffect(buffEffect1);
        selfBuff.addEffect(buffEffect2);

        SkillNode selfBuffNode = new SkillNode("node_self_buff", selfBuff, 3);
        addNode(selfBuffNode);
        parentNode.addChild(selfBuffNode);

        // Soins
        Skill healingSkill = new Skill(
                "skill_healing",
                "Restauration",
                "Soigne partiellement les points de vie.",
                primaryType,
                0, // Pas de dégâts
                100, // Toujours réussi
                3,  // Coût en PA
                Skill.SkillCategory.STATUS
        );

        SkillEffect healEffect = new SkillEffect(
                "effect_healing",
                "Soins",
                SkillEffect.EffectType.HEALING,
                0, // Instantané
                15, // Soigne 15 PV
                SkillEffect.StatisticType.HEALTH,
                1.0 // 100% de chance
        );

        healingSkill.addEffect(healEffect);

        SkillNode healingNode = new SkillNode("node_healing", healingSkill, 4);
        addNode(healingNode);
        parentNode.addChild(healingNode);
    }

    /**
     * Ajoute un nœud à l'arbre.
     *
     * @param node Nœud à ajouter
     * @return true si l'ajout a réussi
     */
    public boolean addNode(SkillNode node) {
        if (node == null || allNodes.containsKey(node.getId())) {
            return false;
        }

        allNodes.put(node.getId(), node);
        return true;
    }

    /**
     * Obtient un nœud par son ID.
     *
     * @param nodeId ID du nœud
     * @return Nœud correspondant ou null
     */
    public SkillNode getNode(String nodeId) {
        return allNodes.get(nodeId);
    }

    /**
     * Débloque un nœud si les prérequis sont remplis.
     *
     * @param nodeId ID du nœud à débloquer
     * @param creature Créature concernée
     * @param availablePoints Points disponibles
     * @return true si le déblocage a réussi
     */
    public boolean unlockNode(String nodeId, Creature creature, int availablePoints) {
        SkillNode node = getNode(nodeId);

        if (node == null || node.isUnlocked() || availablePoints < node.getPointsRequired()) {
            return false;
        }

        // Vérifier que les prérequis sont remplis
        if (!node.arePrerequisitesMet()) {
            return false;
        }

        // Débloquer le nœud
        node.setUnlocked(true);
        return true;
    }

    /**
     * Obtient tous les nœuds racines.
     *
     * @return Liste des nœuds racines
     */
    public List<SkillNode> getRootNodes() {
        return new ArrayList<>(rootNodes);
    }

    /**
     * Obtient tous les nœuds disponibles pour déblocage.
     *
     * @return Liste des nœuds disponibles
     */
    public List<SkillNode> getAvailableNodes() {
        List<SkillNode> availableNodes = new ArrayList<>();

        for (SkillNode node : allNodes.values()) {
            if (!node.isUnlocked() && node.arePrerequisitesMet()) {
                availableNodes.add(node);
            }
        }

        return availableNodes;
    }

    /**
     * Obtient toutes les compétences débloquées.
     *
     * @return Liste des compétences débloquées
     */
    public List<Skill> getUnlockedSkills() {
        List<Skill> unlockedSkills = new ArrayList<>();

        for (SkillNode node : allNodes.values()) {
            if (node.isUnlocked()) {
                unlockedSkills.add(node.getSkill());
            }
        }

        return unlockedSkills;
    }

    /**
     * Vérifie si une compétence est débloquée.
     *
     * @param skill Compétence à vérifier
     * @return true si la compétence est débloquée
     */
    public boolean isSkillUnlocked(Skill skill) {
        for (SkillNode node : allNodes.values()) {
            if (node.isUnlocked() && node.getSkill().getId().equals(skill.getId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Génère une représentation visuelle de l'arbre.
     *
     * @return Liste des informations visuelles pour chaque nœud
     */
    public List<SkillNode.NodeVisualInfo> generateVisualTree() {
        List<SkillNode.NodeVisualInfo> visualInfo = new ArrayList<>();
        Map<SkillNode, Integer> nodeDepths = new HashMap<>();
        Map<Integer, Integer> depthCounts = new HashMap<>();

        // Calculer les profondeurs en commençant par les racines
        for (SkillNode rootNode : rootNodes) {
            calculateDepths(rootNode, 0, nodeDepths, depthCounts);
        }

        // Générer les informations visuelles
        for (Map.Entry<SkillNode, Integer> entry : nodeDepths.entrySet()) {
            SkillNode node = entry.getKey();
            int depth = entry.getValue();

            // Calculer la position horizontale
            int horizontalPosition = 0;
            if (depthCounts.containsKey(depth)) {
                horizontalPosition = depthCounts.get(depth);
                depthCounts.put(depth, horizontalPosition + 1);
            }

            SkillNode.NodeVisualInfo info = node.generateVisualInfo(depth, horizontalPosition);
            visualInfo.add(info);
        }

        return visualInfo;
    }

    /**
     * Calcule récursivement la profondeur de chaque nœud dans l'arbre.
     *
     * @param node Nœud actuel
     * @param depth Profondeur actuelle
     * @param nodeDepths Map pour stocker les profondeurs calculées
     * @param depthCounts Nombre de nœuds à chaque profondeur
     */
    private void calculateDepths(SkillNode node, int depth,
                                 Map<SkillNode, Integer> nodeDepths,
                                 Map<Integer, Integer> depthCounts) {
        // Mettre à jour la profondeur du nœud
        nodeDepths.put(node, depth);

        // Incrémenter le compteur pour cette profondeur
        int count = depthCounts.getOrDefault(depth, 0);
        depthCounts.put(depth, count + 1);

        // Calculer pour les enfants
        for (SkillNode child : node.getChildren()) {
            calculateDepths(child, depth + 1, nodeDepths, depthCounts);
        }
    }