package solar.network.enums;

public enum TransactionTypeGroup {
    Test(0),
    Core(1),
    Solar(2),
    Reserved(1000);

    private final int value;

    TransactionTypeGroup(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
