package solar.network.bean;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class BlockJson {

    private String blockSignature;
    private String generatorPublicKey;
    private BigInteger height;
    private String id;
    private BigInteger numberOfTransactions;
    private String payloadHash;
    private String previousBlock;
    private String reward;
    private BigInteger timestamp;
    private String totalAmount;
    private String totalFee;
    private List<TransactionJson> transactions;

}
