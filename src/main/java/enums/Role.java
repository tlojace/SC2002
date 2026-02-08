package enums;

public enum Role {
	PATIENT("PATIENT"),
    DOCTOR("DOCTOR"),
    PHARMACIST("PHARMACIST"),
    ADMINISTRATOR("ADMINISTRATOR"),;

    private final String displayValue;

    Role (String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
