package solar.network.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

@Data
public abstract class Transaction {

    private String id;

    private Integer version;

    private Integer network;

    private BigInteger nonce;

    private Integer type;

    private Integer headerType;

    private Integer typeGroup;

    private BigInteger fee;

    private BigInteger burnedFee;

    private String senderId;

    private String senderPublicKey;

    private String memo;

    private String signature;

    private String secondSignature;

    private String signSignature;

    private String[] signatures;

    private Boolean verified;

    private String key;

    private BigDecimal staticFee;

    private Boolean isVerified;

    private DeserialiseAddresses addresses;

//    private TransactionData data;

    private byte[] serialised;

    private Long timestamp;

    private Transfers asset;

    public abstract ByteBuffer serialise(SerialiseOptions options);
}
