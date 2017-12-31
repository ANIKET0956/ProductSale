package backend.enums;

public enum OperatorType {

    EQUAL("Equal"),
    GREATER("Greater than"),
    LESSER("Lesser than"),
    CONTAINS("Contains");

    private final String label;

    OperatorType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static OperatorType getOperatorFromLabel(String label) {
        OperatorType[] values = OperatorType.values();
        for (OperatorType value : values) {
            if(value.getLabel().equals(label)){
                return value;
            }
        }
        return OperatorType.EQUAL;
    }
}
