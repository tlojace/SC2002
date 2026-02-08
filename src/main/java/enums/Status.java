package enums;

public enum Status {
    FREE("FREE"),               // Doctor create free appointment slots
    PENDING("PENDING"),         // Patient book appointment
    CONFIRMED("CONFIRMED"),     // Doctor accepts appointment
    CANCELLED("CANCELLED"),     // Doctor cancels appointment
    COMPLETED("COMPLETED"),     // Doctor finished creating medical record after appointment
    DISPENSED("DISPENSED");     // Pharmacist dispensed medication

    private final String displayValue;

    Status(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
