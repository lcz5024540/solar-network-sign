package solar.network.enums;

public enum TransactionHeaderType {
    Standard(0),
    Extended(1);

    private final int value;

    private TransactionHeaderType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
