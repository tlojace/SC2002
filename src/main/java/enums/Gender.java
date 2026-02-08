package enums;

public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE");

    private final String displayValue;

    Gender(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
