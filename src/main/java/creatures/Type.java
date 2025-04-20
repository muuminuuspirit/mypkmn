package creatures;

public class Type {
    private String name;
    private String description;
    private ElementalAffinity[] strengths;
    private ElementalAffinity[] weaknesses;
    private ElementalAffinity[] immunities;

    public Type(String name, String description) {
        this.name = name;
        this.description = description;
        this.strengths = new ElementalAffinity[0];
        this.weaknesses = new ElementalAffinity[0];
        this.immunities = new ElementalAffinity[0];
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getEffectivenessAgainst(Type targetType) {

    }

    public static class ElementalAffinity {
        private Type type;
        private double multiplier;

        public ElementalAffinity(Type type, double multiplier) {
            this.type = type;
            this.multiplier = multiplier;
        }

        public Type getType() {
            return type;
        }
        public void setType(Type type) {
            this.type = type;
        }
    }
}
