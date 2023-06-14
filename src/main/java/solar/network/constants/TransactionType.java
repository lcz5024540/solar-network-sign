package solar.network.constants;

public class TransactionType {
    public static final CoreTransactionType LegacyTransfer = CoreTransactionType.LegacyTransfer;
    public static final CoreTransactionType SecondSignature = CoreTransactionType.SecondSignature;
    public static final CoreTransactionType DelegateRegistration = CoreTransactionType.DelegateRegistration;
    public static final CoreTransactionType Vote = CoreTransactionType.Vote;
    public static final CoreTransactionType MultiSignature = CoreTransactionType.MultiSignature;
    public static final CoreTransactionType Ipfs = CoreTransactionType.Ipfs;
    public static final CoreTransactionType Transfer = CoreTransactionType.Transfer;
    public static final CoreTransactionType DelegateResignation = CoreTransactionType.DelegateResignation;
    public static final CoreTransactionType HtlcLock = CoreTransactionType.HtlcLock;
    public static final CoreTransactionType HtlcClaim = CoreTransactionType.HtlcClaim;
    public static final CoreTransactionType HtlcRefund = CoreTransactionType.HtlcRefund;

    public static final SolarTransactionType Burn = SolarTransactionType.Burn;
    public static final SolarTransactionType SolarVote = SolarTransactionType.Vote;
}
