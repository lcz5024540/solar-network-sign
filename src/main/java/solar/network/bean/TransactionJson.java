package solar.network.bean;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class TransactionJson {
    private Integer version;

    private Integer network;

    private Integer typeGroup;

    private Integer type;

    private BigInteger nonce;

    private String senderId;

    private String senderPublicKey;

    private BigInteger fee;

    private BigInteger burnedFee;

    private BigInteger amount;

    private Integer expiration;

    private String recipientId;

//    private TransactionAsset asset;

    private String memo;

    private String id;

    private String signature;

    private String secondSignature;

    private String signSignature;

    private List<String> signatures;

    private String blockId;

    private Integer blockHeight;

    private String ipfsHash;
}
