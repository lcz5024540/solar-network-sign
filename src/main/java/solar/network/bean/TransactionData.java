package solar.network.bean;

import lombok.Data;

import java.math.BigInteger;

@Data
public class TransactionData {
    Integer version;
    Integer network;
    Integer typeGroup;
    int type;
    BigInteger nonce;
    String senderId;
    String senderPublicKey;
    Integer headerType;
    BigInteger fee;
    BigInteger burnedFee;
    BigInteger amount;
    Integer expiration;
    String recipientId;
    TransactionAsset asset;
    String memo;
    String vendorField;
    String id;
    String signature;
    String secondSignature;
    String signSignature;
    String[] signatures;
    String blockId;
    Integer blockHeight;
    Integer sequence;
}
