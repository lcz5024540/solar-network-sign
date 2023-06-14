package solar.network.constants;

public enum SolarTransactionType {
    Burn(0),
    Vote(2);

    private final int value;

    SolarTransactionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
