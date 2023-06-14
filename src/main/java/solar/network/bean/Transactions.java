package solar.network.bean;

import lombok.Data;

import java.math.BigInteger;

@Data
public class Transactions {

    private String id;

    private Integer version;

    private Integer type;

    private Integer typeGroup;

    private BigInteger nonce;

    private Transfers asset;

    private BigInteger fee;

    private String senderPublicKey;

    private String memo;

    private String signature;
}
