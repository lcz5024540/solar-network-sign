package solar.network.constants;

public enum CoreTransactionType {
    LegacyTransfer(0),
    SecondSignature(1),
    DelegateRegistration(2),
    Vote(3),
    MultiSignature(4),
    Ipfs(5),
    Transfer(6),
    DelegateResignation(7),
    HtlcLock(8),
    HtlcClaim(9),
    HtlcRefund(10);

    private final int value;

    CoreTransactionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}


